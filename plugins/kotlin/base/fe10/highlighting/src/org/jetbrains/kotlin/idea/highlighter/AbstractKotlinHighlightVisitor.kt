// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.kotlin.idea.highlighter

import com.intellij.codeInsight.daemon.impl.Divider
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder
import com.intellij.codeInsight.daemon.impl.analysis.HighlightingLevelManager
import com.intellij.codeInsight.problems.ProblemImpl
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.diagnostic.ControlFlowException
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.registry.Registry
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.problems.Problem
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.CommonProcessors
import org.jetbrains.kotlin.asJava.getJvmSignatureDiagnostics
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.diagnostics.rendering.RenderingContext
import org.jetbrains.kotlin.diagnostics.rendering.parameters
import org.jetbrains.kotlin.idea.base.facet.platform.platform
import org.jetbrains.kotlin.idea.base.highlighting.shouldHighlightErrors
import org.jetbrains.kotlin.idea.base.fe10.highlighting.suspender.KotlinHighlightingSuspender
import org.jetbrains.kotlin.idea.base.projectStructure.RootKindFilter
import org.jetbrains.kotlin.idea.base.projectStructure.matches
import org.jetbrains.kotlin.idea.base.projectStructure.moduleInfo
import org.jetbrains.kotlin.idea.caches.resolve.analyzeWithAllCompilerChecks
import org.jetbrains.kotlin.idea.util.actionUnderSafeAnalyzeBlock
import org.jetbrains.kotlin.platform.jvm.isJvm
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.forEachDescendantOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.diagnostics.Diagnostics
import java.util.*

abstract class AbstractKotlinHighlightVisitor: HighlightVisitor {
    private var afterAnalysisVisitor: Array<AfterAnalysisHighlightingVisitor>? = null

    override fun suitableForFile(file: PsiFile) = file is KtFile

    override fun visit(element: PsiElement) {
        afterAnalysisVisitor?.forEach(element::accept)
    }

    override fun analyze(psiFile: PsiFile, updateWholeFile: Boolean, holder: HighlightInfoHolder, action: Runnable): Boolean {
        val file = psiFile as? KtFile ?: return false
        val highlightingLevelManager = HighlightingLevelManager.getInstance(file.project)
        if (highlightingLevelManager.runEssentialHighlightingOnly(file)) {
            return true
        }

        try {
            analyze(file, holder)

            action.run()
        } catch (e: Throwable) {
            if (e is ControlFlowException) throw e

            if (KotlinHighlightingSuspender.getInstance(file.project).suspend(file.virtualFile)) {
                throw e
            } else {
                LOG.warn(e)
            }
        } finally {
          afterAnalysisVisitor = null
        }

        return true
    }

    private fun analyze(file: KtFile, holder: HighlightInfoHolder) {
        val dividedElements: List<Divider.DividedElements> = ArrayList()
        Divider.divideInsideAndOutsideAllRoots(
            file, file.textRange, file.textRange, { true },
            CommonProcessors.CollectProcessor(dividedElements)
        )
        // TODO: for the sake of check that element belongs to the file
        //  for some reason analyzeWithAllCompilerChecks could return psiElements those do not belong to the file
        //  see [ScriptConfigurationHighlightingTestGenerated$Highlighting.testCustomExtension]
        val elements = dividedElements.flatMap(Divider.DividedElements::inside).toSet()

        // annotate diagnostics on fly: show diagnostics as soon as front-end reports them
        // don't create quick fixes as it could require some resolve
        val highlightInfoByDiagnostic = mutableMapOf<Diagnostic, HighlightInfo>()
        val highlightInfoByTextRange = mutableMapOf<TextRange, HighlightInfo>()

        // render of on-fly diagnostics with descriptors could lead to recursion
        fun checkIfDescriptor(candidate: Any?): Boolean =
            candidate is DeclarationDescriptor || candidate is Collection<*> && candidate.any(::checkIfDescriptor)

        val shouldHighlightErrors = file.shouldHighlightErrors()

        val analysisResult =
            if (shouldHighlightErrors) {
                file.analyzeWithAllCompilerChecks(
                    {
                        val element = it.psiElement
                        if (element in elements &&
                            it !in highlightInfoByDiagnostic &&
                            !RenderingContext.parameters(it).any(::checkIfDescriptor)
                        ) {
                            annotateDiagnostic(
                                element,
                                holder,
                                it,
                                highlightInfoByDiagnostic,
                                highlightInfoByTextRange
                            )
                        }
                    }
                )
            } else {
                file.analyzeWithAllCompilerChecks()
            }
        // resolve is done!

        val bindingContext =
            file.actionUnderSafeAnalyzeBlock(
                {
                    analysisResult.throwIfError()
                    analysisResult.bindingContext
                },
                { BindingContext.EMPTY }
            )


        afterAnalysisVisitor = getAfterAnalysisVisitor(holder, bindingContext)

        cleanUpCalculatingAnnotations(highlightInfoByTextRange)
        if (!shouldHighlightErrors) return

        if (!Registry.`is`("kotlin.ultra.light.classes.error.on.old.backend", false)) {
            annotateDuplicateJvmSignature(file, holder, bindingContext.diagnostics)
        }

        for (diagnostic in bindingContext.diagnostics) {
            val psiElement = diagnostic.psiElement
            if (psiElement !in elements) continue
            // has been processed earlier e.g. on-fly or for some reasons it could be duplicated diagnostics for the same factory
            //  see [PsiCheckerTestGenerated$Checker.testRedeclaration]
            if (diagnostic in highlightInfoByDiagnostic) continue

            // annotate diagnostics those were not possible to report (and therefore render) on-the-fly
            annotateDiagnostic(psiElement, holder, diagnostic, highlightInfoByDiagnostic, calculatingInProgress = false)
        }

        // apply quick fixes for all diagnostics grouping by element
        highlightInfoByDiagnostic.keys.groupBy { it.psiElement }.forEach {
            annotateQuickFixes(it.key, it.value, highlightInfoByDiagnostic)
        }
    }

    private fun annotateDuplicateJvmSignature(file: KtFile, holder: HighlightInfoHolder, otherDiagnostics: Diagnostics) {
        if (!RootKindFilter.projectSources.matches(file) || !file.platform.isJvm()) return

        val duplicateJvmSignatureAnnotator =
            DuplicateJvmSignatureAnnotator(file, holder, otherDiagnostics, file.moduleInfo.contentScope)
        duplicateJvmSignatureAnnotator.visit()
    }

    private fun annotateDiagnostic(
        element: PsiElement,
        holder: HighlightInfoHolder,
        diagnostic: Diagnostic,
        highlightInfoByDiagnostic: MutableMap<Diagnostic, HighlightInfo>? = null,
        highlightInfoByTextRange: MutableMap<TextRange, HighlightInfo>? = null,
        calculatingInProgress: Boolean = true
    ) = annotateDiagnostics(
        element,
        holder,
        listOf(diagnostic),
        highlightInfoByDiagnostic,
        highlightInfoByTextRange,
        true,
        calculatingInProgress
    )

    private fun cleanUpCalculatingAnnotations(highlightInfoByTextRange: Map<TextRange, HighlightInfo>) {
        highlightInfoByTextRange.values.forEach { annotation ->
            annotation.unregisterQuickFix {
                it is CalculatingIntentionAction
            }
        }
    }

    private fun annotateDiagnostics(
        element: PsiElement,
        holder: HighlightInfoHolder,
        diagnostics: List<Diagnostic>,
        highlightInfoByDiagnostic: MutableMap<Diagnostic, HighlightInfo>? = null,
        highlightInfoByTextRange: MutableMap<TextRange, HighlightInfo>? = null,
        noFixes: Boolean = false,
        calculatingInProgress: Boolean = false
    ) = annotateDiagnostics(
        element, holder, diagnostics, highlightInfoByDiagnostic, highlightInfoByTextRange, ::shouldSuppressUnusedParameter,
        noFixes = noFixes,
        calculatingInProgress = calculatingInProgress
    )

    /**
     * [diagnostics] has to belong to the same element
     */
    private fun annotateQuickFixes(
        element: PsiElement,
        diagnostics: List<Diagnostic>,
        highlightInfoByDiagnostic: MutableMap<Diagnostic, HighlightInfo>
    ) {
        if (diagnostics.isEmpty()) return

        assertBelongsToTheSameElement(element, diagnostics)

        ElementAnnotator(element) { param ->
            shouldSuppressUnusedParameter(param)
        }.registerDiagnosticsQuickFixes(diagnostics, highlightInfoByDiagnostic)
    }

    protected open fun shouldSuppressUnusedParameter(parameter: KtParameter): Boolean = false

    private inner class DuplicateJvmSignatureAnnotator(
        private val file: KtFile,
        private val holder: HighlightInfoHolder,
        private val otherDiagnostics: Diagnostics,
        private val moduleScope: GlobalSearchScope
    ) {
        fun visit() {
            file.accept(object : PsiRecursiveElementVisitor() {
                override fun visitElement(element: PsiElement) {
                    annotate(element)
                    super.visitElement(element)
                }
            })
        }

        private fun annotate(element: PsiElement) {
            if (element !is KtFile && element !is KtDeclaration) return

            val diagnostics = getJvmSignatureDiagnostics(element, otherDiagnostics, moduleScope) ?: return

            val diagnosticsForElement = diagnostics.forElement(element).toSet()

            annotateDiagnostics(element, holder, diagnosticsForElement)
        }
    }

    private fun convertToProblems(
        infos: Collection<HighlightInfo>,
        file: VirtualFile,
        hasErrorElement: Boolean = true
    ): List<Problem> =
        infos.filter { it.severity == HighlightSeverity.ERROR }.map { ProblemImpl(file, it, hasErrorElement) }

    companion object {
        private val LOG = Logger.getInstance(AbstractKotlinHighlightVisitor::class.java)

        private val UNRESOLVED_KEY = Key<Unit>("KotlinHighlightVisitor.UNRESOLVED_KEY")

        private val DO_NOT_HIGHLIGHT_KEY = Key<Unit>("DO_NOT_HIGHLIGHT_KEY")

        @JvmStatic
        fun KtElement.suppressHighlight() {
            putUserData(DO_NOT_HIGHLIGHT_KEY, Unit)
            forEachDescendantOfType<KtElement> {
                it.putUserData(DO_NOT_HIGHLIGHT_KEY, Unit)
            }
        }

        @JvmStatic
        fun KtElement.unsuppressHighlight() {
            putUserData(DO_NOT_HIGHLIGHT_KEY, null)
            forEachDescendantOfType<KtElement> {
                it.putUserData(DO_NOT_HIGHLIGHT_KEY, null)
            }
        }

        fun getAfterAnalysisVisitor(holder: HighlightInfoHolder, bindingContext: BindingContext) = arrayOf(
            PropertiesHighlightingVisitor(holder, bindingContext),
            FunctionsHighlightingVisitor(holder, bindingContext),
            VariablesHighlightingVisitor(holder, bindingContext),
            TypeKindHighlightingVisitor(holder, bindingContext)
        )

        fun wasUnresolved(element: KtNameReferenceExpression) = element.getUserData(UNRESOLVED_KEY) != null

        internal fun assertBelongsToTheSameElement(element: PsiElement, diagnostics: Collection<Diagnostic>) {
            assert(diagnostics.all { it.psiElement == element })
        }

        fun annotateDiagnostics(
            element: PsiElement,
            holder: HighlightInfoHolder,
            diagnostics: Collection<Diagnostic>,
            highlightInfoByDiagnostic: MutableMap<Diagnostic, HighlightInfo>? = null,
            highlightInfoByTextRange: MutableMap<TextRange, HighlightInfo>? = null,
            shouldSuppressUnusedParameter: (KtParameter) -> Boolean = { false },
            noFixes: Boolean = false,
            calculatingInProgress: Boolean = false
        ) {
            if (diagnostics.isEmpty()) return
            element.getUserData(DO_NOT_HIGHLIGHT_KEY)?.let { return }

            assertBelongsToTheSameElement(element, diagnostics)

            if (element is KtNameReferenceExpression) {
                val unresolved = diagnostics.any { it.factory == Errors.UNRESOLVED_REFERENCE }
                element.putUserData(UNRESOLVED_KEY, if (unresolved) Unit else null)
            }

            ElementAnnotator(element) { param ->
                shouldSuppressUnusedParameter(param)
            }.registerDiagnosticsAnnotations(
                holder, diagnostics, highlightInfoByDiagnostic,
                highlightInfoByTextRange,
                noFixes = noFixes, calculatingInProgress = calculatingInProgress
            )
        }
    }
}

