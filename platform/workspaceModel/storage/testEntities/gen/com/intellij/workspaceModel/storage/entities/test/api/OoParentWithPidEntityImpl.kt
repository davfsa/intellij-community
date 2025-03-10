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
import com.intellij.workspaceModel.storage.impl.extractOneToOneChild
import com.intellij.workspaceModel.storage.impl.updateOneToOneChildOfParent
import org.jetbrains.deft.ObjBuilder
import org.jetbrains.deft.Type
import org.jetbrains.deft.annotations.Child

@GeneratedCodeApiVersion(1)
@GeneratedCodeImplVersion(1)
open class OoParentWithPidEntityImpl: OoParentWithPidEntity, WorkspaceEntityBase() {
    
    companion object {
        internal val CHILDONE_CONNECTION_ID: ConnectionId = ConnectionId.create(OoParentWithPidEntity::class.java, OoChildForParentWithPidEntity::class.java, ConnectionId.ConnectionType.ONE_TO_ONE, false)
        internal val CHILDTHREE_CONNECTION_ID: ConnectionId = ConnectionId.create(OoParentWithPidEntity::class.java, OoChildAlsoWithPidEntity::class.java, ConnectionId.ConnectionType.ONE_TO_ONE, false)
        
        val connections = listOf<ConnectionId>(
            CHILDONE_CONNECTION_ID,
            CHILDTHREE_CONNECTION_ID,
        )

    }
        
    @JvmField var _parentProperty: String? = null
    override val parentProperty: String
        get() = _parentProperty!!
                        
    override val childOne: OoChildForParentWithPidEntity?
        get() = snapshot.extractOneToOneChild(CHILDONE_CONNECTION_ID, this)           
        
    override val childThree: OoChildAlsoWithPidEntity?
        get() = snapshot.extractOneToOneChild(CHILDTHREE_CONNECTION_ID, this)
    
    override fun connectionIdList(): List<ConnectionId> {
        return connections
    }

    class Builder(val result: OoParentWithPidEntityData?): ModifiableWorkspaceEntityBase<OoParentWithPidEntity>(), OoParentWithPidEntity.Builder {
        constructor(): this(OoParentWithPidEntityData())
        
        override fun applyToBuilder(builder: MutableEntityStorage) {
            if (this.diff != null) {
                if (existsInBuilder(builder)) {
                    this.diff = builder
                    return
                }
                else {
                    error("Entity OoParentWithPidEntity is already created in a different builder")
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
            if (!getEntityData().isParentPropertyInitialized()) {
                error("Field OoParentWithPidEntity#parentProperty should be initialized")
            }
            if (!getEntityData().isEntitySourceInitialized()) {
                error("Field OoParentWithPidEntity#entitySource should be initialized")
            }
        }
        
        override fun connectionIdList(): List<ConnectionId> {
            return connections
        }
    
        
        override var parentProperty: String
            get() = getEntityData().parentProperty
            set(value) {
                checkModificationAllowed()
                getEntityData().parentProperty = value
                changedProperty.add("parentProperty")
            }
            
        override var entitySource: EntitySource
            get() = getEntityData().entitySource
            set(value) {
                checkModificationAllowed()
                getEntityData().entitySource = value
                changedProperty.add("entitySource")
                
            }
            
        override var childOne: OoChildForParentWithPidEntity?
            get() {
                val _diff = diff
                return if (_diff != null) {
                    _diff.extractOneToOneChild(CHILDONE_CONNECTION_ID, this) ?: this.entityLinks[CHILDONE_CONNECTION_ID]?.entity as? OoChildForParentWithPidEntity
                } else {
                    this.entityLinks[CHILDONE_CONNECTION_ID]?.entity as? OoChildForParentWithPidEntity
                }
            }
            set(value) {
                checkModificationAllowed()
                val _diff = diff
                if (_diff != null && value is ModifiableWorkspaceEntityBase<*> && value.diff == null) {
                    if (value is ModifiableWorkspaceEntityBase<*>) {
                        value.entityLinks[CHILDONE_CONNECTION_ID] = EntityLink(false, this)
                    }
                    // else you're attaching a new entity to an existing entity that is not modifiable
                    _diff.addEntity(value)
                }
                if (_diff != null && (value !is ModifiableWorkspaceEntityBase<*> || value.diff != null)) {
                    _diff.updateOneToOneChildOfParent(CHILDONE_CONNECTION_ID, this, value)
                }
                else {
                    if (value is ModifiableWorkspaceEntityBase<*>) {
                        value.entityLinks[CHILDONE_CONNECTION_ID] = EntityLink(false, this)
                    }
                    // else you're attaching a new entity to an existing entity that is not modifiable
                    
                    this.entityLinks[CHILDONE_CONNECTION_ID] = EntityLink(true, value)
                }
                changedProperty.add("childOne")
            }
        
        override var childThree: OoChildAlsoWithPidEntity?
            get() {
                val _diff = diff
                return if (_diff != null) {
                    _diff.extractOneToOneChild(CHILDTHREE_CONNECTION_ID, this) ?: this.entityLinks[CHILDTHREE_CONNECTION_ID]?.entity as? OoChildAlsoWithPidEntity
                } else {
                    this.entityLinks[CHILDTHREE_CONNECTION_ID]?.entity as? OoChildAlsoWithPidEntity
                }
            }
            set(value) {
                checkModificationAllowed()
                val _diff = diff
                if (_diff != null && value is ModifiableWorkspaceEntityBase<*> && value.diff == null) {
                    if (value is ModifiableWorkspaceEntityBase<*>) {
                        value.entityLinks[CHILDTHREE_CONNECTION_ID] = EntityLink(false, this)
                    }
                    // else you're attaching a new entity to an existing entity that is not modifiable
                    _diff.addEntity(value)
                }
                if (_diff != null && (value !is ModifiableWorkspaceEntityBase<*> || value.diff != null)) {
                    _diff.updateOneToOneChildOfParent(CHILDTHREE_CONNECTION_ID, this, value)
                }
                else {
                    if (value is ModifiableWorkspaceEntityBase<*>) {
                        value.entityLinks[CHILDTHREE_CONNECTION_ID] = EntityLink(false, this)
                    }
                    // else you're attaching a new entity to an existing entity that is not modifiable
                    
                    this.entityLinks[CHILDTHREE_CONNECTION_ID] = EntityLink(true, value)
                }
                changedProperty.add("childThree")
            }
        
        override fun getEntityData(): OoParentWithPidEntityData = result ?: super.getEntityData() as OoParentWithPidEntityData
        override fun getEntityClass(): Class<OoParentWithPidEntity> = OoParentWithPidEntity::class.java
    }
}
    
class OoParentWithPidEntityData : WorkspaceEntityData.WithCalculablePersistentId<OoParentWithPidEntity>() {
    lateinit var parentProperty: String

    fun isParentPropertyInitialized(): Boolean = ::parentProperty.isInitialized

    override fun wrapAsModifiable(diff: MutableEntityStorage): ModifiableWorkspaceEntity<OoParentWithPidEntity> {
        val modifiable = OoParentWithPidEntityImpl.Builder(null)
        modifiable.allowModifications {
          modifiable.diff = diff
          modifiable.snapshot = diff
          modifiable.id = createEntityId()
          modifiable.entitySource = this.entitySource
        }
        modifiable.changedProperty.clear()
        return modifiable
    }

    override fun createEntity(snapshot: EntityStorage): OoParentWithPidEntity {
        val entity = OoParentWithPidEntityImpl()
        entity._parentProperty = parentProperty
        entity.entitySource = entitySource
        entity.snapshot = snapshot
        entity.id = createEntityId()
        return entity
    }

    override fun persistentId(): PersistentEntityId<*> {
        return OoParentEntityId(parentProperty)
    }

    override fun getEntityInterface(): Class<out WorkspaceEntity> {
        return OoParentWithPidEntity::class.java
    }

    override fun serialize(ser: EntityInformation.Serializer) {
    }

    override fun deserialize(de: EntityInformation.Deserializer) {
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this::class != other::class) return false
        
        other as OoParentWithPidEntityData
        
        if (this.parentProperty != other.parentProperty) return false
        if (this.entitySource != other.entitySource) return false
        return true
    }

    override fun equalsIgnoringEntitySource(other: Any?): Boolean {
        if (other == null) return false
        if (this::class != other::class) return false
        
        other as OoParentWithPidEntityData
        
        if (this.parentProperty != other.parentProperty) return false
        return true
    }

    override fun hashCode(): Int {
        var result = entitySource.hashCode()
        result = 31 * result + parentProperty.hashCode()
        return result
    }
}