package com.intellij.workspaceModel.storage.entities.test.api

import com.intellij.workspaceModel.storage.EntityInformation
import com.intellij.workspaceModel.storage.EntitySource
import com.intellij.workspaceModel.storage.EntityStorage
import com.intellij.workspaceModel.storage.GeneratedCodeApiVersion
import com.intellij.workspaceModel.storage.GeneratedCodeImplVersion
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
import org.jetbrains.deft.ObjBuilder
import org.jetbrains.deft.Type
import org.jetbrains.deft.annotations.Child

@GeneratedCodeApiVersion(1)
@GeneratedCodeImplVersion(1)
open class ChildSubSubEntityImpl: ChildSubSubEntity, WorkspaceEntityBase() {
    
    companion object {
        internal val PARENTENTITY_CONNECTION_ID: ConnectionId = ConnectionId.create(ChildSubEntity::class.java, ChildSubSubEntity::class.java, ConnectionId.ConnectionType.ONE_TO_ONE, false)
        
        val connections = listOf<ConnectionId>(
            PARENTENTITY_CONNECTION_ID,
        )

    }
        
    override val parentEntity: ChildSubEntity
        get() = snapshot.extractOneToOneParent(PARENTENTITY_CONNECTION_ID, this)!!           
        
    @JvmField var _childData: String? = null
    override val childData: String
        get() = _childData!!
    
    override fun connectionIdList(): List<ConnectionId> {
        return connections
    }

    class Builder(val result: ChildSubSubEntityData?): ModifiableWorkspaceEntityBase<ChildSubSubEntity>(), ChildSubSubEntity.Builder {
        constructor(): this(ChildSubSubEntityData())
        
        override fun applyToBuilder(builder: MutableEntityStorage) {
            if (this.diff != null) {
                if (existsInBuilder(builder)) {
                    this.diff = builder
                    return
                }
                else {
                    error("Entity ChildSubSubEntity is already created in a different builder")
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
                if (_diff.extractOneToOneParent<WorkspaceEntityBase>(PARENTENTITY_CONNECTION_ID, this) == null) {
                    error("Field ChildSubSubEntity#parentEntity should be initialized")
                }
            }
            else {
                if (this.entityLinks[PARENTENTITY_CONNECTION_ID] == null) {
                    error("Field ChildSubSubEntity#parentEntity should be initialized")
                }
            }
            if (!getEntityData().isEntitySourceInitialized()) {
                error("Field ChildSubSubEntity#entitySource should be initialized")
            }
            if (!getEntityData().isChildDataInitialized()) {
                error("Field ChildSubSubEntity#childData should be initialized")
            }
        }
        
        override fun connectionIdList(): List<ConnectionId> {
            return connections
        }
    
        
        override var parentEntity: ChildSubEntity
            get() {
                val _diff = diff
                return if (_diff != null) {
                    _diff.extractOneToOneParent(PARENTENTITY_CONNECTION_ID, this) ?: this.entityLinks[PARENTENTITY_CONNECTION_ID]?.entity!! as ChildSubEntity
                } else {
                    this.entityLinks[PARENTENTITY_CONNECTION_ID]?.entity!! as ChildSubEntity
                }
            }
            set(value) {
                checkModificationAllowed()
                val _diff = diff
                if (_diff != null && value is ModifiableWorkspaceEntityBase<*> && value.diff == null) {
                    if (value is ModifiableWorkspaceEntityBase<*>) {
                        value.entityLinks[PARENTENTITY_CONNECTION_ID] = EntityLink(true, this)
                    }
                    // else you're attaching a new entity to an existing entity that is not modifiable
                    _diff.addEntity(value)
                }
                if (_diff != null && (value !is ModifiableWorkspaceEntityBase<*> || value.diff != null)) {
                    _diff.updateOneToOneParentOfChild(PARENTENTITY_CONNECTION_ID, this, value)
                }
                else {
                    if (value is ModifiableWorkspaceEntityBase<*>) {
                        value.entityLinks[PARENTENTITY_CONNECTION_ID] = EntityLink(true, this)
                    }
                    // else you're attaching a new entity to an existing entity that is not modifiable
                    
                    this.entityLinks[PARENTENTITY_CONNECTION_ID] = EntityLink(false, value)
                }
                changedProperty.add("parentEntity")
            }
        
        override var entitySource: EntitySource
            get() = getEntityData().entitySource
            set(value) {
                checkModificationAllowed()
                getEntityData().entitySource = value
                changedProperty.add("entitySource")
                
            }
            
        override var childData: String
            get() = getEntityData().childData
            set(value) {
                checkModificationAllowed()
                getEntityData().childData = value
                changedProperty.add("childData")
            }
        
        override fun getEntityData(): ChildSubSubEntityData = result ?: super.getEntityData() as ChildSubSubEntityData
        override fun getEntityClass(): Class<ChildSubSubEntity> = ChildSubSubEntity::class.java
    }
}
    
class ChildSubSubEntityData : WorkspaceEntityData<ChildSubSubEntity>() {
    lateinit var childData: String

    fun isChildDataInitialized(): Boolean = ::childData.isInitialized

    override fun wrapAsModifiable(diff: MutableEntityStorage): ModifiableWorkspaceEntity<ChildSubSubEntity> {
        val modifiable = ChildSubSubEntityImpl.Builder(null)
        modifiable.allowModifications {
          modifiable.diff = diff
          modifiable.snapshot = diff
          modifiable.id = createEntityId()
          modifiable.entitySource = this.entitySource
        }
        modifiable.changedProperty.clear()
        return modifiable
    }

    override fun createEntity(snapshot: EntityStorage): ChildSubSubEntity {
        val entity = ChildSubSubEntityImpl()
        entity._childData = childData
        entity.entitySource = entitySource
        entity.snapshot = snapshot
        entity.id = createEntityId()
        return entity
    }

    override fun getEntityInterface(): Class<out WorkspaceEntity> {
        return ChildSubSubEntity::class.java
    }

    override fun serialize(ser: EntityInformation.Serializer) {
    }

    override fun deserialize(de: EntityInformation.Deserializer) {
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this::class != other::class) return false
        
        other as ChildSubSubEntityData
        
        if (this.entitySource != other.entitySource) return false
        if (this.childData != other.childData) return false
        return true
    }

    override fun equalsIgnoringEntitySource(other: Any?): Boolean {
        if (other == null) return false
        if (this::class != other::class) return false
        
        other as ChildSubSubEntityData
        
        if (this.childData != other.childData) return false
        return true
    }

    override fun hashCode(): Int {
        var result = entitySource.hashCode()
        result = 31 * result + childData.hashCode()
        return result
    }
}