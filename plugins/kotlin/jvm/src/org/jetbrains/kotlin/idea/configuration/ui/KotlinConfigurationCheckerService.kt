// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.kotlin.idea.configuration.ui

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationsConfiguration
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.module.Module
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.util.IntellijInternalApi
import org.jetbrains.annotations.TestOnly
import org.jetbrains.kotlin.config.KotlinFacetSettingsProvider
import org.jetbrains.kotlin.idea.KotlinJvmBundle
import org.jetbrains.kotlin.idea.compiler.configuration.KotlinCommonCompilerArgumentsHolder
import org.jetbrains.kotlin.idea.compiler.configuration.KotlinJpsPluginSettings
import org.jetbrains.kotlin.idea.compiler.configuration.isKotlinLanguageVersionConfigured
import org.jetbrains.kotlin.idea.configuration.getModulesWithKotlinFiles
import org.jetbrains.kotlin.idea.facet.KotlinFacet
import org.jetbrains.kotlin.idea.facet.getLibraryLanguageLevel
import org.jetbrains.kotlin.idea.util.projectStructure.allModules
import org.jetbrains.kotlin.platform.idePlatformKind
import java.util.concurrent.atomic.AtomicInteger

class KotlinConfigurationCheckerStartupActivity : StartupActivity.Background {
    override fun runActivity(project: Project) {
        NotificationsConfiguration.getNotificationsConfiguration().register(
            KotlinConfigurationCheckerService.CONFIGURE_NOTIFICATION_GROUP_ID,
            NotificationDisplayType.STICKY_BALLOON,
            true,
        )

        KotlinConfigurationCheckerService.getInstance(project).performProjectPostOpenActions()
    }
}

class KotlinConfigurationCheckerService(val project: Project) {
    private val syncDepth = AtomicInteger()

    fun performProjectPostOpenActions() {
        val task = object : Task.Backgroundable(project, KotlinJvmBundle.message("configure.kotlin.language.settings"), false) {
            override fun run(indicator: ProgressIndicator) {
                val kotlinLanguageVersionConfigured = runReadAction { isKotlinLanguageVersionConfigured(project) }

                val ktModules = if (kotlinLanguageVersionConfigured) {
                    // we already have `.idea/kotlinc` so it's ok to add the jps version there
                    KotlinJpsPluginSettings.validateSettings(project)

                    // pick up modules with kotlin faces those use custom (non project) settings
                    val modulesWithKotlinFacets = runReadAction {
                        project.allModules()
                    }.filter {
                        val facetSettings = KotlinFacet.get(it)?.configuration?.settings ?: return@filter false
                        // module uses custom (not a project-wide) kotlin facet settings and LV or ApiVersion is missed
                        !facetSettings.useProjectSettings && (facetSettings.languageLevel == null || facetSettings.apiLevel == null)
                    }

                    if (modulesWithKotlinFacets.isEmpty()) return

                    getModulesWithKotlinFiles(project, modulesWithKotlinFacets)
                } else {
                    getModulesWithKotlinFiles(project)
                }

                if (ktModules.isEmpty()) return
                if (!kotlinLanguageVersionConfigured) {
                    KotlinJpsPluginSettings.validateSettings(project)
                }

                indicator.isIndeterminate = false
                for ((idx, module) in ktModules.withIndex()) {
                    indicator.checkCanceled()
                    if (project.isDisposed) return
                    indicator.fraction = 1.0 * idx / ktModules.size
                    runReadAction {
                        if (module.isDisposed) return@runReadAction
                        indicator.text2 = KotlinJvmBundle.message("configure.kotlin.language.settings.0.module", module.name)
                        getAndCacheLanguageLevelByDependencies(module)
                    }
                }
            }
        }
        ProgressManager.getInstance().run(task)
    }

    @IntellijInternalApi
    @TestOnly
    fun getAndCacheLanguageLevelByDependencies(module: Module) {
        val facetSettings = KotlinFacetSettingsProvider.getInstance(project)?.getInitializedSettings(module) ?: return
        val newLanguageLevel = getLibraryLanguageLevel(module, null, facetSettings.targetPlatform?.idePlatformKind)

        // Preserve inferred version in facet/project settings
        if (facetSettings.useProjectSettings) {
            KotlinCommonCompilerArgumentsHolder.getInstance(project).takeUnless { isKotlinLanguageVersionConfigured(it) }?.update {
                if (languageVersion == null) {
                    languageVersion = newLanguageLevel.versionString
                }

                if (apiVersion == null) {
                    apiVersion = newLanguageLevel.versionString
                }
            }
        } else {
            with(facetSettings) {
                if (languageLevel == null) {
                    languageLevel = newLanguageLevel
                }

                if (apiLevel == null) {
                    apiLevel = newLanguageLevel
                }
            }
        }
    }


    val isSyncing: Boolean get() = syncDepth.get() > 0

    fun syncStarted() {
        syncDepth.incrementAndGet()
    }

    fun syncDone() {
        syncDepth.decrementAndGet()
    }

    companion object {
        const val CONFIGURE_NOTIFICATION_GROUP_ID = "Configure Kotlin in Project"

        fun getInstance(project: Project): KotlinConfigurationCheckerService = project.service()

    }
}
