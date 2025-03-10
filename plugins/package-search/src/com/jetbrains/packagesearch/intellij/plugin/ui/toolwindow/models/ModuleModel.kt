/*******************************************************************************
 * Copyright 2000-2022 JetBrains s.r.o. and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.jetbrains.packagesearch.intellij.plugin.ui.toolwindow.models

import com.intellij.openapi.application.readAction
import com.jetbrains.packagesearch.intellij.plugin.extensibility.ProjectModule
import com.jetbrains.packagesearch.intellij.plugin.extensibility.ProjectModuleOperationProvider
import com.jetbrains.packagesearch.intellij.plugin.extensibility.RepositoryDeclaration

data class ModuleModel(
    val projectModule: ProjectModule,
    val declaredRepositories: List<RepositoryDeclaration>,
) {

    companion object {

        suspend operator fun invoke(projectModule: ProjectModule) = readAction {
            ModuleModel(
                projectModule = projectModule,
                declaredRepositories = ProjectModuleOperationProvider.forProjectModuleType(projectModule.moduleType)
                    ?.listRepositoriesInModule(projectModule)
                    ?.map { RepositoryDeclaration(it.id, it.name, it.url, projectModule) }
                    ?: emptyList()
            )
        }
    }
}
