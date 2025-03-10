// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.plugins.gradle.testFramework

import com.intellij.openapi.externalSystem.util.runReadAction
import com.intellij.openapi.externalSystem.util.runWriteActionAndWait
import com.intellij.testFramework.runInEdtAndWait
import org.jetbrains.plugins.groovy.util.ExpressionTest
import org.junit.jupiter.api.Assertions.assertTrue

abstract class GradleCodeInsightTestCase : GradleCodeInsightBaseTestCase(), ExpressionTest {

  fun testBuildscript(decorator: String, expression: String, test: () -> Unit) {
    if (decorator.isEmpty()) {
      testBuildscript(expression, test)
    }
    else {
      testBuildscript("$decorator { $expression }", test)
    }
  }

  fun testBuildscript(expression: String, test: () -> Unit) {
    checkCaret(expression)
    updateProjectFile(expression)
    runReadAction {
      test()
    }
  }

  private fun checkCaret(expression: String) {
    assertTrue("<caret>" in expression, "Please define caret position in build script.")
  }

  fun testHighlighting(expression: String) = testHighlighting("build.gradle", expression)
  fun testHighlighting(relativePath: String, expression: String) {
    val file = findOrCreateFile(relativePath, expression)
    runInEdtAndWait {
      codeInsightFixture.testHighlighting(true, false, true, file)
    }
  }

  fun testCompletion(expression: String, vararg completionCandidates: String) {
    checkCaret(expression)
    val file = findOrCreateFile("build.gradle", expression)
    runInEdtAndWait {
      codeInsightFixture.configureFromExistingVirtualFile(file)
      val lookup = listOf(*codeInsightFixture.completeBasic())
      var startIndex = 0
      for (candidate in completionCandidates) {
        val newIndex = lookup.subList(startIndex, lookup.size).indexOfFirst { it.lookupString == candidate }
        assertTrue(newIndex != -1, "Element '$candidate' must be in the lookup")
        startIndex = newIndex + 1
      }
    }
  }

  fun updateProjectFile(content: String) {
    val file = findOrCreateFile("build.gradle", content)
    runWriteActionAndWait {
      codeInsightFixture.configureFromExistingVirtualFile(file)
    }
  }

  fun getDistributionBaseNameMethod(): String {
    return when {
      isGradleAtLeast("7.0") -> "getDistributionBaseName()"
      else -> "getBaseName()"
    }
  }

  fun getDistributionContainerFqn(): String {
    return when {
      isGradleAtLeast("3.5") -> "org.gradle.api.NamedDomainObjectContainer<org.gradle.api.distribution.Distribution>"
      else -> "org.gradle.api.distribution.internal.DefaultDistributionContainer"
    }
  }

  fun getExtraPropertiesExtensionFqn(): String {
    return when {
      isGradleOlderThan("5.2") -> "org.gradle.api.internal.plugins.DefaultExtraPropertiesExtension"
      else -> "org.gradle.internal.extensibility.DefaultExtraPropertiesExtension"
    }
  }

  fun getPublishingExtensionFqn(): String {
    return when {
      isGradleOlderThan("4.8") -> "org.gradle.api.publish.internal.DefaultPublishingExtension"
      isGradleAtLeast("5.0") -> "org.gradle.api.publish.internal.DefaultPublishingExtension"
      else -> "org.gradle.api.publish.internal.DeferredConfigurablePublishingExtension"
    }
  }

  companion object {
    const val DECORATORS = """
      "",
      project(':'), 
      allprojects, 
      subprojects, 
      configure(project(':'))
    """
  }
}
