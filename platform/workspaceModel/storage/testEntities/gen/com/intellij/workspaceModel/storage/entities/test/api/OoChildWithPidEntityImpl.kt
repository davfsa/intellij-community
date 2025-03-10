package com.intellij.workspaceModel.storage.entities.test.api

import com.intellij.workspaceModel.storage.*
import com.intellij.workspaceModel.storage.EntityInformation
import com.intellij.workspaceModel.storage.EntitySource
import com.intellij.workspaceModel.storage.EntityStorage
import com.intellij.workspaceModel.storage.GeneratedCodeApiVersion
import com.intellij.workspaceModel.storage.GeneratedCodeImplVersion
import com.intellij.workspaceModel.storage.ModifiableWorkspaceEntity
import com.intellij.workspaceModel.storage.MutableEntityStorage
import com.intellij.workspaceModel.storage.PersistentEntityId
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
open class OoChildWithPidEntityImpl: OoChildWithPidEntity, WorkspaceEntityBase() {
    
    companion object {
        internal val PARENTENTITY_CONNECTION_ID: ConnectionId = ConnectionId.create(OoParentWithoutPidEntity::class.java, OoChildWithPidEntity::class.java, ConnectionId.ConnectionType.ONE_TO_ONE, false)
        
        val connections = listOf<ConnectionId>(
            PARENTENTITY_CONNECTION_ID,
        )

    }
        
    @JvmField var _childProperty: String? = null
    override val childProperty: String
        get() = _childProperty!!
                        
    override val parentEntity: OoParentWithoutPidEntity
        get() = snapshot.extractOneToOneParent(PARENTENTITY_CONNECTION_ID, this)!!
    
    override fun connectionIdList(): List<ConnectionId> {
        return connections
    }

    class Builder(val result: OoChildWithPidEntityData?): ModifiableWorkspaceEntityBase<OoChildWithPidEntity>(), OoChildWithPidEntity.Builder {
        constructor(): this(OoChildWithPidEntityData())
        
        override fun applyToBuilder(builder: MutableEntityStorage) {
            if (this.diff != null) {
                if (existsInBuilder(builder)) {
                    this.diff = builder
                    return
                }
                else {
                    error("Entity OoChildWithPidEntity is already created in a different builder")
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
            if (!getEntityData().isChildPropertyInitialized()) {
                error("Field OoChildWithPidEntity#childProperty should be initialized")
            }
            if (!getEntityData().isEntitySourceInitialized()) {
                error("Field OoChildWithPidEntity#entitySource should be initialized")
            }
            if (_diff != null) {
                if (_diff.extractOneToOneParent<WorkspaceEntityBase>(PARENTENTITY_CONNECTION_ID, this) == null) {
                    error("Field OoChildWithPidEntity#parentEntity should be initialized")
                }
            }
            else {
                if (this.entityLinks[PARENTENTITY_CONNECTION_ID] == null) {
                    error("Field OoChildWithPidEntity#parentEntity should be initialized")
                }
            }
        }
        
        override fun connectionIdList(): List<ConnectionId> {
            return connections
        }
    
        
        override var childProperty: String
            get() = getEntityData().childProperty
            set(value) {
                checkModificationAllowed()
                getEntityData().childProperty = value
                changedProperty.add("childProperty")
            }
            
        override var entitySource: EntitySource
            get() = getEntityData().entitySource
            set(value) {
                checkModificationAllowed()
                getEntityData().entitySource = value
                changedProperty.add("entitySource")
                
            }
            
        override var parentEntity: OoParentWithoutPidEntity
            get() {
                val _diff = diff
                return if (_diff != null) {
                    _diff.extractOneToOneParent(PARENTENTITY_CONNECTION_ID, this) ?: this.entityLinks[PARENTENTITY_CONNECTION_ID]?.entity!! as OoParentWithoutPidEntity
                } else {
                    this.entityLinks[PARENTENTITY_CONNECTION_ID]?.entity!! as OoParentWithoutPidEntity
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
        
        override fun getEntityData(): OoChildWithPidEntityData = result ?: super.getEntityData() as OoChildWithPidEntityData
        override fun getEntityClass(): Class<OoChildWithPidEntity> = OoChildWithPidEntity::class.java
    }
}
    
class OoChildWithPidEntityData : WorkspaceEntityData.WithCalculablePersistentId<OoChildWithPidEntity>() {
    lateinit var childProperty: String

    fun isChildPropertyInitialized(): Boolean = ::childProperty.isInitialized

    override fun wrapAsModifiable(diff: MutableEntityStorage): ModifiableWorkspaceEntity<OoChildWithPidEntity> {
        val modifiable = OoChildWithPidEntityImpl.Builder(null)
        modifiable.allowModifications {
          modifiable.diff = diff
          modifiable.snapshot = diff
          modifiable.id = createEntityId()
          modifiable.entitySource = this.entitySource
        }
        modifiable.changedProperty.clear()
        return modifiable
    }

    override fun createEntity(snapshot: EntityStorage): OoChildWithPidEntity {
        val entity = OoChildWithPidEntityImpl()
        entity._childProperty = childProperty
        entity.entitySource = entitySource
        entity.snapshot = snapshot
        entity.id = createEntityId()
        return entity
    }

    override fun persistentId(): PersistentEntityId<*> {
        return OoChildEntityId(childProperty)
    }

    override fun getEntityInterface(): Class<out WorkspaceEntity> {
        return OoChildWithPidEntity::class.java
    }

    override fun serialize(ser: EntityInformation.Serializer) {
    }

    override fun deserialize(de: EntityInformation.Deserializer) {
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this::class != other::class) return false
        
        other as OoChildWithPidEntityData
        
        if (this.childProperty != other.childProperty) return false
        if (this.entitySource != other.entitySource) return false
        return true
    }

    override fun equalsIgnoringEntitySource(other: Any?): Boolean {
        if (other == null) return false
        if (this::class != other::class) return false
        
        other as OoChildWithPidEntityData
        
        if (this.childProperty != other.childProperty) return false
        return true
    }

    override fun hashCode(): Int {
        var result = entitySource.hashCode()
        result = 31 * result + childProperty.hashCode()
        return result
    }
}