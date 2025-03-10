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
import com.intellij.workspaceModel.storage.impl.extractOneToAbstractManyParent
import com.intellij.workspaceModel.storage.impl.updateOneToAbstractManyParentOfChild
import org.jetbrains.deft.ObjBuilder
import org.jetbrains.deft.Type
import org.jetbrains.deft.annotations.Abstract
import org.jetbrains.deft.annotations.Child

@GeneratedCodeApiVersion(1)
@GeneratedCodeImplVersion(1)
open class SimpleChildAbstractEntityImpl: SimpleChildAbstractEntity, WorkspaceEntityBase() {
    
    companion object {
        internal val PARENTINLIST_CONNECTION_ID: ConnectionId = ConnectionId.create(CompositeAbstractEntity::class.java, SimpleAbstractEntity::class.java, ConnectionId.ConnectionType.ONE_TO_ABSTRACT_MANY, false)
        
        val connections = listOf<ConnectionId>(
            PARENTINLIST_CONNECTION_ID,
        )

    }
        
    override val parentInList: CompositeAbstractEntity
        get() = snapshot.extractOneToAbstractManyParent(PARENTINLIST_CONNECTION_ID, this)!!
    
    override fun connectionIdList(): List<ConnectionId> {
        return connections
    }

    class Builder(val result: SimpleChildAbstractEntityData?): ModifiableWorkspaceEntityBase<SimpleChildAbstractEntity>(), SimpleChildAbstractEntity.Builder {
        constructor(): this(SimpleChildAbstractEntityData())
        
        override fun applyToBuilder(builder: MutableEntityStorage) {
            if (this.diff != null) {
                if (existsInBuilder(builder)) {
                    this.diff = builder
                    return
                }
                else {
                    error("Entity SimpleChildAbstractEntity is already created in a different builder")
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
                if (_diff.extractOneToAbstractManyParent<WorkspaceEntityBase>(PARENTINLIST_CONNECTION_ID, this) == null) {
                    error("Field SimpleAbstractEntity#parentInList should be initialized")
                }
            }
            else {
                if (this.entityLinks[PARENTINLIST_CONNECTION_ID] == null) {
                    error("Field SimpleAbstractEntity#parentInList should be initialized")
                }
            }
            if (!getEntityData().isEntitySourceInitialized()) {
                error("Field SimpleAbstractEntity#entitySource should be initialized")
            }
        }
        
        override fun connectionIdList(): List<ConnectionId> {
            return connections
        }
    
        
        override var parentInList: CompositeAbstractEntity
            get() {
                val _diff = diff
                return if (_diff != null) {
                    _diff.extractOneToAbstractManyParent(PARENTINLIST_CONNECTION_ID, this) ?: this.entityLinks[PARENTINLIST_CONNECTION_ID]?.entity!! as CompositeAbstractEntity
                } else {
                    this.entityLinks[PARENTINLIST_CONNECTION_ID]?.entity!! as CompositeAbstractEntity
                }
            }
            set(value) {
                checkModificationAllowed()
                val _diff = diff
                if (_diff != null && value is ModifiableWorkspaceEntityBase<*> && value.diff == null) {
                    // Setting backref of the list
                    if (value is ModifiableWorkspaceEntityBase<*>) {
                        val data = (value.entityLinks[PARENTINLIST_CONNECTION_ID]?.entity as? List<Any> ?: emptyList()) + this
                        value.entityLinks[PARENTINLIST_CONNECTION_ID] = EntityLink(true, data)
                    }
                    // else you're attaching a new entity to an existing entity that is not modifiable
                    _diff.addEntity(value)
                }
                if (_diff != null && (value !is ModifiableWorkspaceEntityBase<*> || value.diff != null)) {
                    _diff.updateOneToAbstractManyParentOfChild(PARENTINLIST_CONNECTION_ID, this, value)
                }
                else {
                    // Setting backref of the list
                    if (value is ModifiableWorkspaceEntityBase<*>) {
                        val data = (value.entityLinks[PARENTINLIST_CONNECTION_ID]?.entity as? List<Any> ?: emptyList()) + this
                        value.entityLinks[PARENTINLIST_CONNECTION_ID] = EntityLink(true, data)
                    }
                    // else you're attaching a new entity to an existing entity that is not modifiable
                    
                    this.entityLinks[PARENTINLIST_CONNECTION_ID] = EntityLink(false, value)
                }
                changedProperty.add("parentInList")
            }
        
        override var entitySource: EntitySource
            get() = getEntityData().entitySource
            set(value) {
                checkModificationAllowed()
                getEntityData().entitySource = value
                changedProperty.add("entitySource")
                
            }
        
        override fun getEntityData(): SimpleChildAbstractEntityData = result ?: super.getEntityData() as SimpleChildAbstractEntityData
        override fun getEntityClass(): Class<SimpleChildAbstractEntity> = SimpleChildAbstractEntity::class.java
    }
}
    
class SimpleChildAbstractEntityData : WorkspaceEntityData<SimpleChildAbstractEntity>() {


    override fun wrapAsModifiable(diff: MutableEntityStorage): ModifiableWorkspaceEntity<SimpleChildAbstractEntity> {
        val modifiable = SimpleChildAbstractEntityImpl.Builder(null)
        modifiable.allowModifications {
          modifiable.diff = diff
          modifiable.snapshot = diff
          modifiable.id = createEntityId()
          modifiable.entitySource = this.entitySource
        }
        modifiable.changedProperty.clear()
        return modifiable
    }

    override fun createEntity(snapshot: EntityStorage): SimpleChildAbstractEntity {
        val entity = SimpleChildAbstractEntityImpl()
        entity.entitySource = entitySource
        entity.snapshot = snapshot
        entity.id = createEntityId()
        return entity
    }

    override fun getEntityInterface(): Class<out WorkspaceEntity> {
        return SimpleChildAbstractEntity::class.java
    }

    override fun serialize(ser: EntityInformation.Serializer) {
    }

    override fun deserialize(de: EntityInformation.Deserializer) {
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this::class != other::class) return false
        
        other as SimpleChildAbstractEntityData
        
        if (this.entitySource != other.entitySource) return false
        return true
    }

    override fun equalsIgnoringEntitySource(other: Any?): Boolean {
        if (other == null) return false
        if (this::class != other::class) return false
        
        other as SimpleChildAbstractEntityData
        
        return true
    }

    override fun hashCode(): Int {
        var result = entitySource.hashCode()
        return result
    }
}