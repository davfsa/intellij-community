package com.intellij.workspaceModel.storage.entities.test.api

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
import com.intellij.workspaceModel.storage.impl.extractOneToManyParent
import com.intellij.workspaceModel.storage.impl.updateOneToManyParentOfChild
import com.intellij.workspaceModel.storage.referrersx
import org.jetbrains.deft.ObjBuilder
import org.jetbrains.deft.Type
import org.jetbrains.deft.annotations.Child

@GeneratedCodeApiVersion(1)
@GeneratedCodeImplVersion(1)
open class ChildWithNullsOppositeMultipleImpl: ChildWithNullsOppositeMultiple, WorkspaceEntityBase() {
    
    companion object {
        internal val PARENTENTITY_CONNECTION_ID: ConnectionId = ConnectionId.create(ParentWithNullsOppositeMultiple::class.java, ChildWithNullsOppositeMultiple::class.java, ConnectionId.ConnectionType.ONE_TO_MANY, true)
        
        val connections = listOf<ConnectionId>(
            PARENTENTITY_CONNECTION_ID,
        )

    }
        
    @JvmField var _childData: String? = null
    override val childData: String
        get() = _childData!!
                        
    override val parentEntity: ParentWithNullsOppositeMultiple?
        get() = snapshot.extractOneToManyParent(PARENTENTITY_CONNECTION_ID, this)
    
    override fun connectionIdList(): List<ConnectionId> {
        return connections
    }

    class Builder(val result: ChildWithNullsOppositeMultipleData?): ModifiableWorkspaceEntityBase<ChildWithNullsOppositeMultiple>(), ChildWithNullsOppositeMultiple.Builder {
        constructor(): this(ChildWithNullsOppositeMultipleData())
        
        override fun applyToBuilder(builder: MutableEntityStorage) {
            if (this.diff != null) {
                if (existsInBuilder(builder)) {
                    this.diff = builder
                    return
                }
                else {
                    error("Entity ChildWithNullsOppositeMultiple is already created in a different builder")
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
            if (!getEntityData().isChildDataInitialized()) {
                error("Field ChildWithNullsOppositeMultiple#childData should be initialized")
            }
            if (!getEntityData().isEntitySourceInitialized()) {
                error("Field ChildWithNullsOppositeMultiple#entitySource should be initialized")
            }
        }
        
        override fun connectionIdList(): List<ConnectionId> {
            return connections
        }
    
        
        override var childData: String
            get() = getEntityData().childData
            set(value) {
                checkModificationAllowed()
                getEntityData().childData = value
                changedProperty.add("childData")
            }
            
        override var entitySource: EntitySource
            get() = getEntityData().entitySource
            set(value) {
                checkModificationAllowed()
                getEntityData().entitySource = value
                changedProperty.add("entitySource")
                
            }
            
        override var parentEntity: ParentWithNullsOppositeMultiple?
            get() {
                val _diff = diff
                return if (_diff != null) {
                    _diff.extractOneToManyParent(PARENTENTITY_CONNECTION_ID, this) ?: this.entityLinks[PARENTENTITY_CONNECTION_ID]?.entity as? ParentWithNullsOppositeMultiple
                } else {
                    this.entityLinks[PARENTENTITY_CONNECTION_ID]?.entity as? ParentWithNullsOppositeMultiple
                }
            }
            set(value) {
                checkModificationAllowed()
                val _diff = diff
                if (_diff != null && value is ModifiableWorkspaceEntityBase<*> && value.diff == null) {
                    // Setting backref of the list
                    if (value is ModifiableWorkspaceEntityBase<*>) {
                        val data = (value.entityLinks[PARENTENTITY_CONNECTION_ID]?.entity as? List<Any> ?: emptyList()) + this
                        value.entityLinks[PARENTENTITY_CONNECTION_ID] = EntityLink(true, data)
                    }
                    // else you're attaching a new entity to an existing entity that is not modifiable
                    _diff.addEntity(value)
                }
                if (_diff != null && (value !is ModifiableWorkspaceEntityBase<*> || value.diff != null)) {
                    _diff.updateOneToManyParentOfChild(PARENTENTITY_CONNECTION_ID, this, value)
                }
                else {
                    // Setting backref of the list
                    if (value is ModifiableWorkspaceEntityBase<*>) {
                        val data = (value.entityLinks[PARENTENTITY_CONNECTION_ID]?.entity as? List<Any> ?: emptyList()) + this
                        value.entityLinks[PARENTENTITY_CONNECTION_ID] = EntityLink(true, data)
                    }
                    // else you're attaching a new entity to an existing entity that is not modifiable
                    
                    this.entityLinks[PARENTENTITY_CONNECTION_ID] = EntityLink(false, value)
                }
                changedProperty.add("parentEntity")
            }
        
        override fun getEntityData(): ChildWithNullsOppositeMultipleData = result ?: super.getEntityData() as ChildWithNullsOppositeMultipleData
        override fun getEntityClass(): Class<ChildWithNullsOppositeMultiple> = ChildWithNullsOppositeMultiple::class.java
    }
}
    
class ChildWithNullsOppositeMultipleData : WorkspaceEntityData<ChildWithNullsOppositeMultiple>() {
    lateinit var childData: String

    fun isChildDataInitialized(): Boolean = ::childData.isInitialized

    override fun wrapAsModifiable(diff: MutableEntityStorage): ModifiableWorkspaceEntity<ChildWithNullsOppositeMultiple> {
        val modifiable = ChildWithNullsOppositeMultipleImpl.Builder(null)
        modifiable.allowModifications {
          modifiable.diff = diff
          modifiable.snapshot = diff
          modifiable.id = createEntityId()
          modifiable.entitySource = this.entitySource
        }
        modifiable.changedProperty.clear()
        return modifiable
    }

    override fun createEntity(snapshot: EntityStorage): ChildWithNullsOppositeMultiple {
        val entity = ChildWithNullsOppositeMultipleImpl()
        entity._childData = childData
        entity.entitySource = entitySource
        entity.snapshot = snapshot
        entity.id = createEntityId()
        return entity
    }

    override fun getEntityInterface(): Class<out WorkspaceEntity> {
        return ChildWithNullsOppositeMultiple::class.java
    }

    override fun serialize(ser: EntityInformation.Serializer) {
    }

    override fun deserialize(de: EntityInformation.Deserializer) {
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this::class != other::class) return false
        
        other as ChildWithNullsOppositeMultipleData
        
        if (this.childData != other.childData) return false
        if (this.entitySource != other.entitySource) return false
        return true
    }

    override fun equalsIgnoringEntitySource(other: Any?): Boolean {
        if (other == null) return false
        if (this::class != other::class) return false
        
        other as ChildWithNullsOppositeMultipleData
        
        if (this.childData != other.childData) return false
        return true
    }

    override fun hashCode(): Int {
        var result = entitySource.hashCode()
        result = 31 * result + childData.hashCode()
        return result
    }
}