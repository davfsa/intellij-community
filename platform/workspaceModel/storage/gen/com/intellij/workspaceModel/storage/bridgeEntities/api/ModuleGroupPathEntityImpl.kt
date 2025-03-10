package com.intellij.workspaceModel.storage.bridgeEntities.api

import com.intellij.workspaceModel.storage.*
import com.intellij.workspaceModel.storage.EntityInformation
import com.intellij.workspaceModel.storage.EntitySource
import com.intellij.workspaceModel.storage.EntityStorage
import com.intellij.workspaceModel.storage.GeneratedCodeApiVersion
import com.intellij.workspaceModel.storage.GeneratedCodeImplVersion
import com.intellij.workspaceModel.storage.ModifiableReferableWorkspaceEntity
import com.intellij.workspaceModel.storage.ModifiableWorkspaceEntity
import com.intellij.workspaceModel.storage.MutableEntityStorage
import com.intellij.workspaceModel.storage.WorkspaceEntity
import com.intellij.workspaceModel.storage.impl.ConnectionId
import com.intellij.workspaceModel.storage.impl.EntityLink
import com.intellij.workspaceModel.storage.impl.ModifiableWorkspaceEntityBase
import com.intellij.workspaceModel.storage.impl.WorkspaceEntityBase
import com.intellij.workspaceModel.storage.impl.WorkspaceEntityData
import com.intellij.workspaceModel.storage.impl.extractOneToOneParent
import com.intellij.workspaceModel.storage.impl.updateOneToOneParentOfChild
import com.intellij.workspaceModel.storage.referrersx
import com.intellij.workspaceModel.storage.url.VirtualFileUrl
import org.jetbrains.deft.ObjBuilder
import org.jetbrains.deft.Type
import org.jetbrains.deft.annotations.Child

@GeneratedCodeApiVersion(1)
@GeneratedCodeImplVersion(1)
open class ModuleGroupPathEntityImpl: ModuleGroupPathEntity, WorkspaceEntityBase() {
    
    companion object {
        internal val MODULE_CONNECTION_ID: ConnectionId = ConnectionId.create(ModuleEntity::class.java, ModuleGroupPathEntity::class.java, ConnectionId.ConnectionType.ONE_TO_ONE, false)
        
        val connections = listOf<ConnectionId>(
            MODULE_CONNECTION_ID,
        )

    }
        
    override val module: ModuleEntity
        get() = snapshot.extractOneToOneParent(MODULE_CONNECTION_ID, this)!!           
        
    @JvmField var _path: List<String>? = null
    override val path: List<String>
        get() = _path!!
    
    override fun connectionIdList(): List<ConnectionId> {
        return connections
    }

    class Builder(val result: ModuleGroupPathEntityData?): ModifiableWorkspaceEntityBase<ModuleGroupPathEntity>(), ModuleGroupPathEntity.Builder {
        constructor(): this(ModuleGroupPathEntityData())
        
        override fun applyToBuilder(builder: MutableEntityStorage) {
            if (this.diff != null) {
                if (existsInBuilder(builder)) {
                    this.diff = builder
                    return
                }
                else {
                    error("Entity ModuleGroupPathEntity is already created in a different builder")
                }
            }
            
            this.diff = builder
            this.snapshot = builder
            addToBuilder()
            this.id = getEntityData().createEntityId()
            
            // Process linked entities that are connected without a builder
            processLinkedEntities(builder)
            checkInitialization() // TODO uncomment and check failed tests
        }
    
        fun checkInitialization() {
            val _diff = diff
            if (_diff != null) {
                if (_diff.extractOneToOneParent<WorkspaceEntityBase>(MODULE_CONNECTION_ID, this) == null) {
                    error("Field ModuleGroupPathEntity#module should be initialized")
                }
            }
            else {
                if (this.entityLinks[MODULE_CONNECTION_ID] == null) {
                    error("Field ModuleGroupPathEntity#module should be initialized")
                }
            }
            if (!getEntityData().isEntitySourceInitialized()) {
                error("Field ModuleGroupPathEntity#entitySource should be initialized")
            }
            if (!getEntityData().isPathInitialized()) {
                error("Field ModuleGroupPathEntity#path should be initialized")
            }
        }
        
        override fun connectionIdList(): List<ConnectionId> {
            return connections
        }
    
        
        override var module: ModuleEntity
            get() {
                val _diff = diff
                return if (_diff != null) {
                    _diff.extractOneToOneParent(MODULE_CONNECTION_ID, this) ?: this.entityLinks[MODULE_CONNECTION_ID]?.entity!! as ModuleEntity
                } else {
                    this.entityLinks[MODULE_CONNECTION_ID]?.entity!! as ModuleEntity
                }
            }
            set(value) {
                checkModificationAllowed()
                val _diff = diff
                if (_diff != null && value is ModifiableWorkspaceEntityBase<*> && value.diff == null) {
                    if (value is ModifiableWorkspaceEntityBase<*>) {
                        value.entityLinks[MODULE_CONNECTION_ID] = EntityLink(true, this)
                    }
                    // else you're attaching a new entity to an existing entity that is not modifiable
                    _diff.addEntity(value)
                }
                if (_diff != null && (value !is ModifiableWorkspaceEntityBase<*> || value.diff != null)) {
                    _diff.updateOneToOneParentOfChild(MODULE_CONNECTION_ID, this, value)
                }
                else {
                    if (value is ModifiableWorkspaceEntityBase<*>) {
                        value.entityLinks[MODULE_CONNECTION_ID] = EntityLink(true, this)
                    }
                    // else you're attaching a new entity to an existing entity that is not modifiable
                    
                    this.entityLinks[MODULE_CONNECTION_ID] = EntityLink(false, value)
                }
                changedProperty.add("module")
            }
        
        override var entitySource: EntitySource
            get() = getEntityData().entitySource
            set(value) {
                checkModificationAllowed()
                getEntityData().entitySource = value
                changedProperty.add("entitySource")
                
            }
            
        override var path: List<String>
            get() = getEntityData().path
            set(value) {
                checkModificationAllowed()
                getEntityData().path = value
                
                changedProperty.add("path")
            }
        
        override fun getEntityData(): ModuleGroupPathEntityData = result ?: super.getEntityData() as ModuleGroupPathEntityData
        override fun getEntityClass(): Class<ModuleGroupPathEntity> = ModuleGroupPathEntity::class.java
    }
}
    
class ModuleGroupPathEntityData : WorkspaceEntityData<ModuleGroupPathEntity>() {
    lateinit var path: List<String>

    fun isPathInitialized(): Boolean = ::path.isInitialized

    override fun wrapAsModifiable(diff: MutableEntityStorage): ModifiableWorkspaceEntity<ModuleGroupPathEntity> {
        val modifiable = ModuleGroupPathEntityImpl.Builder(null)
        modifiable.allowModifications {
          modifiable.diff = diff
          modifiable.snapshot = diff
          modifiable.id = createEntityId()
          modifiable.entitySource = this.entitySource
        }
        modifiable.changedProperty.clear()
        return modifiable
    }

    override fun createEntity(snapshot: EntityStorage): ModuleGroupPathEntity {
        val entity = ModuleGroupPathEntityImpl()
        entity._path = path
        entity.entitySource = entitySource
        entity.snapshot = snapshot
        entity.id = createEntityId()
        return entity
    }

    override fun getEntityInterface(): Class<out WorkspaceEntity> {
        return ModuleGroupPathEntity::class.java
    }

    override fun serialize(ser: EntityInformation.Serializer) {
    }

    override fun deserialize(de: EntityInformation.Deserializer) {
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this::class != other::class) return false
        
        other as ModuleGroupPathEntityData
        
        if (this.entitySource != other.entitySource) return false
        if (this.path != other.path) return false
        return true
    }

    override fun equalsIgnoringEntitySource(other: Any?): Boolean {
        if (other == null) return false
        if (this::class != other::class) return false
        
        other as ModuleGroupPathEntityData
        
        if (this.path != other.path) return false
        return true
    }

    override fun hashCode(): Int {
        var result = entitySource.hashCode()
        result = 31 * result + path.hashCode()
        return result
    }
}