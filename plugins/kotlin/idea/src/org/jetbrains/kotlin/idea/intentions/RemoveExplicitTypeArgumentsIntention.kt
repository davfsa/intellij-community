// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.kotlin.idea.intentions

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ValueDescriptor
import org.jetbrains.kotlin.idea.KotlinBundle
import org.jetbrains.kotlin.idea.caches.resolve.analyzeInContext
import org.jetbrains.kotlin.idea.caches.resolve.getResolutionFacade
import org.jetbrains.kotlin.idea.base.psi.copied
import org.jetbrains.kotlin.idea.inspections.IntentionBasedInspection
import org.jetbrains.kotlin.idea.project.builtIns
import org.jetbrains.kotlin.idea.util.getResolutionScope
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForSelector
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.parentsWithSelf
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.DelegatingBindingTrace
import org.jetbrains.kotlin.resolve.bindingContextUtil.getDataFlowInfoBefore
import org.jetbrains.kotlin.resolve.bindingContextUtil.isUsedAsExpression
import org.jetbrains.kotlin.resolve.bindingContextUtil.isUsedAsStatement
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker

@Suppress("DEPRECATION")
class RemoveExplicitTypeArgumentsInspection : IntentionBasedInspection<KtTypeArgumentList>(RemoveExplicitTypeArgumentsIntention::class) {
    override fun problemHighlightType(element: KtTypeArgumentList): ProblemHighlightType = ProblemHighlightType.LIKE_UNUSED_SYMBOL

    override fun additionalFixes(element: KtTypeArgumentList): List<LocalQuickFix>? {
        val declaration = element.getStrictParentOfType<KtCallableDeclaration>() ?: return null
        if (!RemoveExplicitTypeIntention.isApplicableTo(declaration)) return null
        return listOf(RemoveExplicitTypeFix(declaration.nameAsSafeName.asString()))
    }

    private class RemoveExplicitTypeFix(private val declarationName: String) : LocalQuickFix {
        override fun getName() = KotlinBundle.message("remove.explicit.type.specification.from.0", declarationName)

        override fun getFamilyName() = name

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val element = descriptor.psiElement as? KtTypeArgumentList ?: return
            val declaration = element.getStrictParentOfType<KtCallableDeclaration>() ?: return
            RemoveExplicitTypeIntention.removeExplicitType(declaration)
        }
    }
}

class RemoveExplicitTypeArgumentsIntention : SelfTargetingOffsetIndependentIntention<KtTypeArgumentList>(
    KtTypeArgumentList::class.java,
    KotlinBundle.lazyMessage("remove.explicit.type.arguments")
) {
    companion object {
        fun isApplicableTo(element: KtTypeArgumentList, approximateFlexible: Boolean): Boolean {
            val callExpression = element.parent as? KtCallExpression ?: return false
            val typeArguments = callExpression.typeArguments
            if (typeArguments.isEmpty()) return false
            if (typeArguments.any { it.typeReference?.isAnnotatedDeep() == true }) return false

            val resolutionFacade = callExpression.getResolutionFacade()
            val bindingContext = resolutionFacade.analyze(callExpression, BodyResolveMode.PARTIAL_WITH_CFA)
            val originalCall = callExpression.getResolvedCall(bindingContext) ?: return false

            val (contextExpression, expectedType) = findContextToAnalyze(callExpression, bindingContext)
            val resolutionScope = contextExpression.getResolutionScope(bindingContext, resolutionFacade)

            val key = Key<Unit>("RemoveExplicitTypeArgumentsIntention")
            callExpression.putCopyableUserData(key, Unit)
            val expressionToAnalyze = contextExpression.copied()
            callExpression.putCopyableUserData(key, null)

            val newCallExpression = expressionToAnalyze.findDescendantOfType<KtCallExpression> { it.getCopyableUserData(key) != null }!!
            newCallExpression.typeArgumentList!!.delete()

            val newBindingContext = expressionToAnalyze.analyzeInContext(
                resolutionScope,
                contextExpression,
                trace = DelegatingBindingTrace(bindingContext, "Temporary trace"),
                dataFlowInfo = bindingContext.getDataFlowInfoBefore(contextExpression),
                expectedType = expectedType ?: TypeUtils.NO_EXPECTED_TYPE,
                isStatement = contextExpression.isUsedAsStatement(bindingContext)
            )

            val newCall = newCallExpression.getResolvedCall(newBindingContext) ?: return false

            val args = originalCall.typeArguments
            val newArgs = newCall.typeArguments

            fun equalTypes(type1: KotlinType, type2: KotlinType): Boolean {
                return if (approximateFlexible) {
                    KotlinTypeChecker.DEFAULT.equalTypes(type1, type2)
                } else {
                    type1 == type2
                }
            }

            return args.size == newArgs.size && args.values.zip(newArgs.values).all { (argType, newArgType) ->
                equalTypes(argType, newArgType)
            }
        }

        private fun findContextToAnalyze(expression: KtExpression, bindingContext: BindingContext): Pair<KtExpression, KotlinType?> {
            for (element in expression.parentsWithSelf) {
                if (element !is KtExpression) continue

                if (element.getQualifiedExpressionForSelector() != null) continue
                if (element is KtFunctionLiteral) continue
                if (!element.isUsedAsExpression(bindingContext)) return element to null

                when (val parent = element.parent) {
                    is KtNamedFunction -> {
                        val expectedType = if (element == parent.bodyExpression && !parent.hasBlockBody() && parent.hasDeclaredReturnType())
                            (bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, parent] as? FunctionDescriptor)?.returnType
                        else
                            null
                        return element to expectedType
                    }

                    is KtVariableDeclaration -> {
                        val expectedType = if (element == parent.initializer && parent.typeReference != null)
                            (bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, parent] as? ValueDescriptor)?.type
                        else
                            null
                        return element to expectedType
                    }

                    is KtParameter -> {
                        val expectedType = if (element == parent.defaultValue)
                            (bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, parent] as? ValueDescriptor)?.type
                        else
                            null
                        return element to expectedType
                    }

                    is KtPropertyAccessor -> {
                        val property = parent.parent as KtProperty
                        val expectedType = when {
                            element != parent.bodyExpression || parent.hasBlockBody() -> null
                            parent.isSetter -> parent.builtIns.unitType
                            property.typeReference == null -> null
                            else -> (bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, parent] as? FunctionDescriptor)?.returnType
                        }
                        return element to expectedType
                    }
                }
            }

            return expression to null
        }
    }

    override fun isApplicableTo(element: KtTypeArgumentList): Boolean = isApplicableTo(element, approximateFlexible = false)

    override fun applyTo(element: KtTypeArgumentList, editor: Editor?) = element.delete()
}