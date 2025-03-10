// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.plugins.gradle.service.resolve

import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.InheritanceUtil
import groovy.lang.Closure
import org.jetbrains.plugins.gradle.service.resolve.GradleCommonClassNames.GRADLE_API_PROJECT
import org.jetbrains.plugins.gradle.settings.GradleExtensionsSettings
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.expressions.TypesUtil.createType
import org.jetbrains.plugins.groovy.lang.psi.impl.synthetic.GrLightMethodBuilder
import org.jetbrains.plugins.groovy.lang.psi.util.GroovyCommonClassNames.GROOVY_LANG_CLOSURE
import org.jetbrains.plugins.groovy.lang.resolve.NonCodeMembersContributor
import org.jetbrains.plugins.groovy.lang.resolve.delegatesTo.DELEGATES_TO_KEY
import org.jetbrains.plugins.groovy.lang.resolve.delegatesTo.DelegatesToInfo
import org.jetbrains.plugins.groovy.lang.resolve.getName
import org.jetbrains.plugins.groovy.lang.resolve.shouldProcessMethods
import org.jetbrains.plugins.groovy.lang.resolve.shouldProcessProperties

class GradleProjectExtensionContributor : NonCodeMembersContributor() {

  override fun getParentClassName(): String = GRADLE_API_PROJECT

  override fun processDynamicElements(qualifierType: PsiType,
                                      aClass: PsiClass?,
                                      processor: PsiScopeProcessor,
                                      place: PsiElement,
                                      state: ResolveState) {
    if (qualifierType !is GradleProjectAwareType) return

    val processMethods = processor.shouldProcessMethods()
    val processProperties = processor.shouldProcessProperties()
    if (!processMethods && !processProperties) {
      return
    }

    val containingFile = place.containingFile
    val extensionsData = GradleExtensionsContributor.getExtensionsFor(containingFile) ?: return

    val name = processor.getName(state)
    val allExtensions = extensionsData.extensions
    val extensions = if (name == null) allExtensions.values else listOf(allExtensions[name] ?: return)
    if (extensions.isEmpty()) return

    val factory = PsiElementFactory.getInstance(containingFile.project)
    val manager = containingFile.manager

    for (extension in extensions) {
      val delegateType = factory.createTypeFromText(extension.rootTypeFqn, place)
      if (delegateType !is PsiClassType) {
        continue
      }
      val type = GradleExtensionType(delegateType)
      if (processProperties) {
        val extensionProperty = GradleExtensionProperty(extension.name, type, containingFile)
        if (!processor.execute(extensionProperty, state)) {
          return
        }
      }
      if (processMethods) {
        val extensionMethod = GrLightMethodBuilder(manager, extension.name).apply {
          returnType = type
          containingClass = aClass
          if (shouldAddConfiguration(extension, place)) {
            addAndGetParameter("configuration", createType(GROOVY_LANG_CLOSURE, containingFile))
              .putUserData(DELEGATES_TO_KEY, DelegatesToInfo(type, Closure.DELEGATE_FIRST))
          }
        }
        if (!processor.execute(extensionMethod, state)) {
          return
        }
      }
    }
  }

  private fun shouldAddConfiguration(extension: GradleExtensionsSettings.GradleExtension, context: PsiElement): Boolean {
    val clazz = JavaPsiFacade.getInstance(context.project).findClass(extension.rootTypeFqn, context.resolveScope) ?: return true
    return !InheritanceUtil.isInheritor(clazz, "org.gradle.api.internal.catalog.AbstractExternalDependencyFactory")
  }
}
