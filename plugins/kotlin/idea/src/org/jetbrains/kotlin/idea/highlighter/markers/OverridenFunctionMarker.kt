// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.kotlin.idea.highlighter.markers

import com.intellij.codeInsight.daemon.DaemonBundle
import com.intellij.codeInsight.navigation.BackgroundUpdaterTask
import com.intellij.ide.IdeDeprecatedMessagesBundle
import com.intellij.ide.util.MethodCellRenderer
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.keymap.KeymapUtil
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbService
import com.intellij.psi.*
import com.intellij.psi.presentation.java.ClassPresentationUtil
import com.intellij.psi.search.PsiElementProcessor
import com.intellij.psi.search.PsiElementProcessorAdapter
import com.intellij.psi.search.searches.ClassInheritorsSearch
import com.intellij.psi.search.searches.FunctionalExpressionSearch
import com.intellij.util.CommonProcessors
import org.jetbrains.kotlin.asJava.classes.KtLightClass
import org.jetbrains.kotlin.asJava.elements.KtLightMethod
import org.jetbrains.kotlin.asJava.elements.isTraitFakeOverride
import org.jetbrains.kotlin.idea.base.util.module
import org.jetbrains.kotlin.idea.KotlinBundle
import org.jetbrains.kotlin.idea.presentation.DeclarationByModuleRenderer
import org.jetbrains.kotlin.idea.search.declarationsSearch.forEachDeclaredMemberOverride
import org.jetbrains.kotlin.idea.search.declarationsSearch.forEachOverridingMethod
import org.jetbrains.kotlin.idea.search.declarationsSearch.toPossiblyFakeLightMethods
import org.jetbrains.kotlin.idea.util.application.runReadAction
import org.jetbrains.kotlin.idea.util.isExpectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.hasActualModifier
import java.awt.event.MouseEvent
import javax.swing.JComponent

private fun PsiMethod.isMethodWithDeclarationInOtherClass(): Boolean {
    return this is KtLightMethod && this.isTraitFakeOverride()
}

internal fun <T> getOverriddenDeclarations(mappingToJava: MutableMap<PsiElement, T>, classes: Set<PsiClass>): Set<T> {
    val overridden = HashSet<T>()

    for (aClass in classes) {
        aClass.forEachDeclaredMemberOverride { superMember, overridingMember ->
            ProgressManager.checkCanceled()
            val possiblyFakeLightMethods = overridingMember.toPossiblyFakeLightMethods()
            possiblyFakeLightMethods.find { !it.isMethodWithDeclarationInOtherClass() }?.let {
                mappingToJava.remove(superMember)?.let { declaration ->
                    // Light methods points to same methods
                    // and no reason to keep searching those methods
                    // those originals are found
                    if (mappingToJava.remove(it) == null) {
                        mappingToJava.values.removeIf(superMember::equals)
                    }
                    overridden.add(declaration)
                }
                false
            }

            mappingToJava.isNotEmpty()
        }
    }

    return overridden
}

// Module-specific version of MarkerType.getSubclassedClassTooltip
fun getSubclassedClassTooltip(klass: PsiClass): String? {
    val processor = PsiElementProcessor.CollectElementsWithLimit(5, HashSet<PsiClass>())
    ClassInheritorsSearch.search(klass).forEach(PsiElementProcessorAdapter(processor))

    if (processor.isOverflow) {
        return if (klass.isInterface) IdeDeprecatedMessagesBundle.message("interface.is.implemented.too.many") else DaemonBundle.message("class.is.subclassed.too.many")
    }

    val subclasses = processor.toArray(PsiClass.EMPTY_ARRAY)
    if (subclasses.isEmpty()) {
        val functionalImplementations = PsiElementProcessor.CollectElementsWithLimit(2, HashSet<PsiFunctionalExpression>())
        FunctionalExpressionSearch.search(klass).forEach(PsiElementProcessorAdapter(functionalImplementations))
        return if (functionalImplementations.collection.isNotEmpty())
            KotlinBundle.message("highlighter.text.has.functional.implementations")
        else
            null
    }

    val start = IdeDeprecatedMessagesBundle.message(if (klass.isInterface) "interface.is.implemented.by.header" else "class.is.subclassed.by.header")
    val shortcuts = ActionManager.getInstance().getAction(IdeActions.ACTION_GOTO_IMPLEMENTATION).shortcutSet.shortcuts
    val shortcut = shortcuts.firstOrNull()
    val shortCutText = if (shortcut != null)
        KotlinBundle.message("highlighter.text.or.press", KeymapUtil.getShortcutText(shortcut))
    else
        ""

    val postfix = "<br><div style=''margin-top: 5px''><font size=''2''>" + KotlinBundle.message(
        "highlighter.text.click.for.navigate",
        shortCutText
    ) + "</font></div>"

    val renderer = DeclarationByModuleRenderer()
    val comparator = renderer.comparator
    return subclasses.toList().sortedWith(comparator).joinToString(
        prefix = "<html><body>$start", postfix = "$postfix</body</html>", separator = "<br>"
    ) { clazz ->
        val moduleNameRequired = if (clazz is KtLightClass) {
            val origin = clazz.kotlinOrigin
            origin?.hasActualModifier() == true || origin?.isExpectDeclaration() == true
        } else false
        val moduleName = clazz.module?.name
        val elementText = renderer.getElementText(clazz) + (moduleName?.takeIf { moduleNameRequired }?.let { " [$it]" } ?: "")
        val refText = (moduleName?.let { "$it:" } ?: "") + ClassPresentationUtil.getNameForClass(clazz, /* qualified = */ true)
        "&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"#kotlinClass/$refText\">$elementText</a>"
    }
}

fun getOverriddenMethodTooltip(method: PsiMethod): String? {
    val processor = PsiElementProcessor.CollectElementsWithLimit<PsiMethod>(5)
    method.forEachOverridingMethod(processor = PsiElementProcessorAdapter(processor)::process)

    val isAbstract = method.hasModifierProperty(PsiModifier.ABSTRACT)

    if (processor.isOverflow) {
        return if (isAbstract) DaemonBundle.message("method.is.implemented.too.many") else DaemonBundle.message("method.is.overridden.too.many")
    }

    val comparator = MethodCellRenderer(false).comparator

    val overridingJavaMethods = processor.collection.filter { !it.isMethodWithDeclarationInOtherClass() }.sortedWith(comparator)
    if (overridingJavaMethods.isEmpty()) return null

    val start = if (isAbstract) DaemonBundle.message("method.is.implemented.header") else DaemonBundle.message("method.is.overriden.header")

    return com.intellij.codeInsight.daemon.impl.GutterIconTooltipHelper.composeText(
        overridingJavaMethods,
        start,
        "&nbsp;&nbsp;&nbsp;&nbsp;{1}"
    )
}

fun buildNavigateToOverriddenMethodPopup(e: MouseEvent?, element: PsiElement?): NavigationPopupDescriptor? {
    val method = getPsiMethod(element) ?: return null

    if (DumbService.isDumb(method.project)) {
        DumbService.getInstance(method.project)
            ?.showDumbModeNotification(KotlinBundle.message("highlighter.notification.text.navigation.to.overriding.classes.is.not.possible.during.index.update"))
        return null
    }

    val processor = PsiElementProcessor.CollectElementsWithLimit(2, HashSet<PsiMethod>())
    if (!ProgressManager.getInstance().runProcessWithProgressSynchronously(
            {
                method.forEachOverridingMethod {
                    runReadAction {
                        processor.execute(it)
                    }
                }
            },
            KotlinBundle.message("highlighter.title.searching.for.overriding.declarations"),
            true,
            method.project,
            e?.component as JComponent?
        )
    ) {
        return null
    }

    var overridingJavaMethods = processor.collection.filter { !it.isMethodWithDeclarationInOtherClass() }
    if (overridingJavaMethods.isEmpty()) return null

    val renderer = MethodCellRenderer(false)
    overridingJavaMethods = overridingJavaMethods.sortedWith(renderer.comparator)

    val methodsUpdater = OverridingMethodsUpdater(method, renderer.comparator)
    return NavigationPopupDescriptor(
        overridingJavaMethods,
        methodsUpdater.getCaption(overridingJavaMethods.size),
        KotlinBundle.message("highlighter.title.overriding.declarations.of", method.name),
        renderer,
        methodsUpdater
    )
}

private class OverridingMethodsUpdater(
    private val myMethod: PsiMethod,
    comparator: Comparator<PsiMethod>,
) : BackgroundUpdaterTask(
    myMethod.project,
    KotlinBundle.message("highlighter.title.searching.for.overriding.methods"),
    createComparatorWrapper { o1: PsiElement, o2: PsiElement ->
        if (o1 is PsiMethod && o2 is PsiMethod) comparator.compare(o1, o2) else 0
    }
) {
    @Suppress("DialogTitleCapitalization")
    override fun getCaption(size: Int): String {
        return if (myMethod.hasModifierProperty(PsiModifier.ABSTRACT))
            DaemonBundle.message("navigation.title.implementation.method", myMethod.name, size)
        else
            DaemonBundle.message("navigation.title.overrider.method", myMethod.name, size)
    }

    override fun run(indicator: ProgressIndicator) {
        super.run(indicator)
        val processor = object : CommonProcessors.CollectProcessor<PsiMethod>() {
            override fun process(psiMethod: PsiMethod): Boolean {
                if (!updateComponent(psiMethod)) {
                    indicator.cancel()
                }
                indicator.checkCanceled()
                return super.process(psiMethod)
            }
        }
        myMethod.forEachOverridingMethod { processor.process(it) }
    }
}
