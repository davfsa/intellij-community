// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.quickfix

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.command.impl.FinishMarkAction
import com.intellij.openapi.command.impl.StartMarkAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.search.searches.MethodReferencesSearch
import org.jetbrains.kotlin.asJava.toLightMethods
import org.jetbrains.kotlin.descriptors.ClassDescriptorWithResolutionScopes
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.idea.KotlinBundle
import org.jetbrains.kotlin.idea.core.CollectingNameValidator
import org.jetbrains.kotlin.idea.base.fe10.codeInsight.newDeclaration.Fe10KotlinNameSuggester
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.codeInsight.shorten.runRefactoringAndKeepDelayedRequests
import org.jetbrains.kotlin.idea.core.appendElement
import org.jetbrains.kotlin.idea.core.getOrCreateBody
import org.jetbrains.kotlin.idea.core.util.CodeInsightUtils
import org.jetbrains.kotlin.idea.refactoring.CompositeRefactoringRunner
import org.jetbrains.kotlin.idea.refactoring.changeSignature.*
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.resolve.descriptorUtil.secondaryConstructors
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.source.getPsi
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

object InitializePropertyQuickFixFactory : KotlinIntentionActionsFactory() {
    class AddInitializerFix(property: KtProperty) : KotlinQuickFixAction<KtProperty>(property) {
        override fun getText() = KotlinBundle.message("add.initializer")
        override fun getFamilyName() = text

        override fun invoke(project: Project, editor: Editor?, file: KtFile) {
            val element = element ?: return
            val descriptor = element.resolveToDescriptorIfAny() as? PropertyDescriptor ?: return
            val initializerText = CodeInsightUtils.defaultInitializer(descriptor.type) ?: "TODO()"
            val initializer = element.setInitializer(KtPsiFactory(project).createExpression(initializerText))!!
            if (editor != null) {
                PsiDocumentManager.getInstance(project).commitDocument(editor.document)
                editor.selectionModel.setSelection(initializer.startOffset, initializer.endOffset)
                editor.caretModel.moveToOffset(initializer.endOffset)
            }
        }
    }

    class MoveToConstructorParameters(property: KtProperty) : KotlinQuickFixAction<KtProperty>(property) {
        override fun getText() = KotlinBundle.message("move.to.constructor.parameters")
        override fun getFamilyName() = text

        override fun startInWriteAction(): Boolean = false

        private fun configureChangeSignature(
            property: KtProperty,
            propertyDescriptor: PropertyDescriptor
        ): KotlinChangeSignatureConfiguration {
            return object : KotlinChangeSignatureConfiguration {
                override fun configure(originalDescriptor: KotlinMethodDescriptor): KotlinMethodDescriptor {
                    return originalDescriptor.modify {
                        val initializerText = CodeInsightUtils.defaultInitializer(propertyDescriptor.type) ?: "null"
                        val newParam = KotlinParameterInfo(
                            callableDescriptor = originalDescriptor.baseDescriptor,
                            name = propertyDescriptor.name.asString(),
                            originalTypeInfo = KotlinTypeInfo(false, propertyDescriptor.type),
                            valOrVar = property.valOrVarKeyword.toValVar(),
                            modifierList = property.modifierList,
                            defaultValueForCall = KtPsiFactory(property.project).createExpression(initializerText)
                        )
                        it.addParameter(newParam)
                    }
                }

                override fun performSilently(affectedFunctions: Collection<PsiElement>) = noUsagesExist(affectedFunctions)
            }
        }

        override fun invoke(project: Project, editor: Editor?, file: KtFile) {
            val element = element ?: return
            val klass = element.containingClassOrObject ?: return
            val propertyDescriptor = element.resolveToDescriptorIfAny() as? PropertyDescriptor ?: return

            StartMarkAction.canStart(editor)?.let { return }
            val startMarkAction = StartMarkAction.start(editor, project, text)

            try {
                val parameterToInsert = KtPsiFactory(project).createParameter(element.text)
                runWriteAction { element.delete() }

                val classDescriptor = klass.resolveToDescriptorIfAny() as? ClassDescriptorWithResolutionScopes ?: return
                val constructorDescriptor = classDescriptor.unsubstitutedPrimaryConstructor ?: return
                val contextElement = constructorDescriptor.source.getPsi() ?: return
                val constructorPointer = contextElement.createSmartPointer()
                val config = configureChangeSignature(element, propertyDescriptor)
                object : CompositeRefactoringRunner(project, "refactoring.changeSignature") {
                    override fun runRefactoring() {
                        runChangeSignature(project, editor, constructorDescriptor, config, contextElement, text)
                    }

                    override fun onRefactoringDone() {
                        val constructorOrClass = constructorPointer.element
                        val constructor = constructorOrClass as? KtConstructor<*> ?: (constructorOrClass as? KtClass)?.primaryConstructor
                        constructor?.valueParameters?.lastOrNull()?.replace(parameterToInsert)
                    }
                }.run()
            } finally {
                FinishMarkAction.finish(project, editor, startMarkAction)
            }
        }
    }

    class InitializeWithConstructorParameter(property: KtProperty) : KotlinQuickFixAction<KtProperty>(property) {
        override fun getText() = KotlinBundle.message("initialize.with.constructor.parameter")
        override fun getFamilyName() = text

        override fun startInWriteAction(): Boolean = false

        private fun configureChangeSignature(propertyDescriptor: PropertyDescriptor): KotlinChangeSignatureConfiguration {
            return object : KotlinChangeSignatureConfiguration {
                override fun configure(originalDescriptor: KotlinMethodDescriptor): KotlinMethodDescriptor {
                    return originalDescriptor.modify { methodDescriptor ->
                        val classDescriptor = propertyDescriptor.containingDeclaration as ClassDescriptorWithResolutionScopes
                        val constructorScope = classDescriptor.scopeForClassHeaderResolution
                        val validator = CollectingNameValidator(originalDescriptor.parameters.map { it.name }) { name ->
                            constructorScope.getContributedDescriptors(DescriptorKindFilter.VARIABLES).all {
                                it !is VariableDescriptor || it.name.asString() != name
                            }
                        }
                        val initializerText = CodeInsightUtils.defaultInitializer(propertyDescriptor.type) ?: "null"
                        val newParam = KotlinParameterInfo(
                            callableDescriptor = originalDescriptor.baseDescriptor,
                            name = Fe10KotlinNameSuggester.suggestNameByName(propertyDescriptor.name.asString(), validator),
                            originalTypeInfo = KotlinTypeInfo(false, propertyDescriptor.type),
                            defaultValueForCall = KtPsiFactory(element!!.project).createExpression(initializerText)
                        )
                        methodDescriptor.addParameter(newParam)
                    }
                }

                override fun performSilently(affectedFunctions: Collection<PsiElement>): Boolean = noUsagesExist(affectedFunctions)
            }
        }

        // TODO: Allow processing of multiple functions in Change Signature so that Start/Finish Mark can be used here
        private fun processConstructors(
            project: Project,
            editor: Editor?,
            configuration: KotlinChangeSignatureConfiguration,
            constructorDescriptor: ConstructorDescriptor,
            visitedElements: MutableSet<PsiElement>
        ) {
            val element = element!!

            val constructorPointer = constructorDescriptor.source.getPsi()?.createSmartPointer()
            object : CompositeRefactoringRunner(project, "refactoring.changeSignature") {
                override fun runRefactoring() {
                    val containingClassOrObject = element.containingClassOrObject ?: return
                    runChangeSignature(project, editor, constructorDescriptor, configuration, containingClassOrObject, text)
                }

                override fun onRefactoringDone() {
                    val constructorOrClass = constructorPointer?.element
                    val constructor = constructorOrClass as? KtConstructor<*>
                        ?: (constructorOrClass as? KtClass)?.primaryConstructor
                        ?: return

                    if (!visitedElements.add(constructor)) return
                    constructor.valueParameters.lastOrNull()?.let { newParam ->
                        val psiFactory = KtPsiFactory(project)
                        val name = newParam.name ?: return
                        constructor.safeAs<KtSecondaryConstructor>()?.getOrCreateBody()
                            ?.appendElement(psiFactory.createExpression("this.${element.name} = $name"))
                            ?: element.setInitializer(psiFactory.createExpression(name))
                    }
                }
            }.run()
        }

        override fun invoke(project: Project, editor: Editor?, file: KtFile) {
            val element = element ?: return
            val propertyDescriptor = element.resolveToDescriptorIfAny() as? PropertyDescriptor ?: return
            val classDescriptor = propertyDescriptor.containingDeclaration as? ClassDescriptorWithResolutionScopes ?: return
            val klass = element.containingClassOrObject ?: return
            val constructorDescriptors = if (klass.hasExplicitPrimaryConstructor() || klass.secondaryConstructors.isEmpty()) {
                listOf(classDescriptor.unsubstitutedPrimaryConstructor!!)
            } else {
                classDescriptor.secondaryConstructors.filter {
                    val constructor = it.source.getPsi() as? KtSecondaryConstructor
                    constructor != null && !constructor.getDelegationCall().isCallToThis
                }
            }

            val config = configureChangeSignature(propertyDescriptor)
            val visitedElements = mutableSetOf<PsiElement>()
            project.runRefactoringAndKeepDelayedRequests {
                for (constructorDescriptor in constructorDescriptors) {
                    processConstructors(project, editor, config, constructorDescriptor, visitedElements)
                }
            }
        }
    }

    private fun noUsagesExist(affectedFunctions: Collection<PsiElement>): Boolean = affectedFunctions.flatMap { it.toLightMethods() }.all {
        MethodReferencesSearch.search(it).findFirst() == null
    }

    override fun doCreateActions(diagnostic: Diagnostic): List<IntentionAction> {
        val property = diagnostic.psiElement as? KtProperty ?: return emptyList()
        if (property.receiverTypeReference != null) return emptyList()

        val actions = ArrayList<IntentionAction>(2)

        actions.add(AddInitializerFix(property))

        property.containingClassOrObject.safeAs<KtClass>()?.let { klass ->
            if (klass.isAnnotation() || klass.isInterface()) return@let
            if (klass.primaryConstructor?.hasActualModifier() == true) return@let

            val secondaryConstructors by lazy { klass.secondaryConstructors.filter { it.getDelegationCallOrNull()?.isCallToThis != true } }
            if (property.accessors.isNotEmpty() || secondaryConstructors.isNotEmpty()) {
                if (secondaryConstructors.none { it.hasActualModifier() }) {
                    actions.add(InitializeWithConstructorParameter(property))
                }
            } else {
                actions.add(MoveToConstructorParameters(property))
            }
        }

        return actions
    }
}
