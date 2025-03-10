// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.kotlin.idea.base.util.caching

import com.intellij.openapi.project.Project
import com.intellij.workspaceModel.ide.WorkspaceModelChangeListener
import com.intellij.workspaceModel.storage.EntityChange
import com.intellij.workspaceModel.storage.EntityStorage
import com.intellij.workspaceModel.storage.VersionedStorageChange
import com.intellij.workspaceModel.storage.WorkspaceEntity

abstract class WorkspaceEntityChangeListener<Entity : WorkspaceEntity, Value: Any>(protected val project: Project): WorkspaceModelChangeListener {
    protected abstract val entityClass: Class<Entity>

    protected abstract fun map(storage: EntityStorage, entity: Entity): Value?

    protected abstract fun entitiesChanged(outdated: List<Value>)

    final override fun changed(event: VersionedStorageChange) {
        val storageBefore = event.storageBefore
        val changes = event.getChanges(entityClass).ifEmpty { return }

        val outdatedEntities: List<Value> = changes.asSequence()
            .mapNotNull { change ->
                when (change) {
                    is EntityChange.Added -> null
                    is EntityChange.Removed -> change.entity
                    is EntityChange.Replaced -> change.oldEntity
                }
            }
            .mapNotNull { map(storageBefore, it) }
            .toList()

        if (outdatedEntities.isNotEmpty()) {
            entitiesChanged(outdatedEntities)
        }
    }
}