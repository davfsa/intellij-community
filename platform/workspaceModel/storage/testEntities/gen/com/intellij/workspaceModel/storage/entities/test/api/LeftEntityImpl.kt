package com.intellij.workspaceModel.storage.entities.test.api

import com.intellij.workspaceModel.storage.*
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
import com.intellij.workspaceModel.storage.impl.extractOneToAbstractManyChildren
import com.intellij.workspaceModel.storage.impl.extractOneToAbstractManyParent
import com.intellij.workspaceModel.storage.impl.extractOneToManyChildren
import com.intellij.workspaceModel.storage.impl.updateOneToAbstractManyChildrenOfParent
import com.intellij.workspaceModel.storage.impl.updateOneToAbstractManyParentOfChild
import org.jetbrains.deft.ObjBuilder
import org.jetbrains.deft.Type
import org.jetbrains.deft.annotations.Abstract
import org.jetbrains.deft.annotations.Child

@GeneratedCodeApiVersion(1)
@GeneratedCodeImplVersion(1)
open class LeftEntityImpl: LeftEntity, WorkspaceEntityBase() {
    
    companion object {
        internal val PARENTENTITY_CONNECTION_ID: ConnectionId = ConnectionId.create(CompositeBaseEntity::class.java, BaseEntity::class.java, ConnectionId.ConnectionType.ONE_TO_ABSTRACT_MANY, true)
        internal val CHILDREN_CONNECTION_ID: ConnectionId = ConnectionId.create(CompositeBaseEntity::class.java, BaseEntity::class.java, ConnectionId.ConnectionType.ONE_TO_ABSTRACT_MANY, true)
        
        val connections = listOf<ConnectionId>(
            PARENTENTITY_CONNECTION_ID,
            CHILDREN_CONNECTION_ID,
        )

    }
        
    override val parentEntity: CompositeBaseEntity?
        get() = snapshot.extractOneToAbstractManyParent(PARENTENTITY_CONNECTION_ID, this)           
        
    override val children: List<BaseEntity>
        get() = snapshot.extractOneToAbstractManyChildren<BaseEntity>(CHILDREN_CONNECTION_ID, this)!!.toList()
    
    override fun connectionIdList(): List<ConnectionId> {
        return connections
    }

    class Builder(val result: LeftEntityData?): ModifiableWorkspaceEntityBase<LeftEntity>(), LeftEntity.Builder {
        constructor(): this(LeftEntityData())
        
        override fun applyToBuilder(builder: MutableEntityStorage) {
            if (this.diff != null) {
                if (existsInBuilder(builder)) {
                    this.diff = builder
                    return
                }
                else {
                    error("Entity LeftEntity is already created in a different builder")
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
            // Check initialization for list with ref type
            if (_diff != null) {
                if (_diff.extractOneToManyChildren<WorkspaceEntityBase>(CHILDREN_CONNECTION_ID, this) == null) {
                    error("Field CompositeBaseEntity#children should be initialized")
                }
            }
            else {
                if (this.entityLinks[CHILDREN_CONNECTION_ID] == null) {
                    error("Field CompositeBaseEntity#children should be initialized")
                }
            }
            if (!getEntityData().isEntitySourceInitialized()) {
                error("Field CompositeBaseEntity#entitySource should be initialized")
            }
        }
        
        override fun connectionIdList(): List<ConnectionId> {
            return connections
        }
    
        
        override var parentEntity: CompositeBaseEntity?
            get() {
                val _diff = diff
                return if (_diff != null) {
                    _diff.extractOneToAbstractManyParent(PARENTENTITY_CONNECTION_ID, this) ?: this.entityLinks[PARENTENTITY_CONNECTION_ID]?.entity as? CompositeBaseEntity
                } else {
                    this.entityLinks[PARENTENTITY_CONNECTION_ID]?.entity as? CompositeBaseEntity
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
                    _diff.updateOneToAbstractManyParentOfChild(PARENTENTITY_CONNECTION_ID, this, value)
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
        
            override var children: List<BaseEntity>
                get() {
                    val _diff = diff
                    return if (_diff != null) {
                        _diff.extractOneToAbstractManyChildren<BaseEntity>(CHILDREN_CONNECTION_ID, this)!!.toList() + (this.entityLinks[CHILDREN_CONNECTION_ID]?.entity as? List<BaseEntity> ?: emptyList())
                    } else {
                        this.entityLinks[CHILDREN_CONNECTION_ID]?.entity!! as List<BaseEntity>
                    }
                }
                set(value) {
                    checkModificationAllowed()
                    val _diff = diff
                    if (_diff != null) {
                        for (item_value in value) {
                            if (item_value is ModifiableWorkspaceEntityBase<*> && (item_value as? ModifiableWorkspaceEntityBase<*>)?.diff == null) {
                                _diff.addEntity(item_value)
                            }
                        }
                        _diff.updateOneToAbstractManyChildrenOfParent(CHILDREN_CONNECTION_ID, this, value.asSequence())
                    }
                    else {
                        for (item_value in value) {
                            if (item_value is ModifiableWorkspaceEntityBase<*>) {
                                item_value.entityLinks[CHILDREN_CONNECTION_ID] = EntityLink(false, this)
                            }
                            // else you're attaching a new entity to an existing entity that is not modifiable
                        }
                        
                        this.entityLinks[CHILDREN_CONNECTION_ID] = EntityLink(true, value)
                    }
                    changedProperty.add("children")
                }
        
        override var entitySource: EntitySource
            get() = getEntityData().entitySource
            set(value) {
                checkModificationAllowed()
                getEntityData().entitySource = value
                changedProperty.add("entitySource")
                
            }
        
        override fun getEntityData(): LeftEntityData = result ?: super.getEntityData() as LeftEntityData
        override fun getEntityClass(): Class<LeftEntity> = LeftEntity::class.java
    }
}
    
class LeftEntityData : WorkspaceEntityData<LeftEntity>() {


    override fun wrapAsModifiable(diff: MutableEntityStorage): ModifiableWorkspaceEntity<LeftEntity> {
        val modifiable = LeftEntityImpl.Builder(null)
        modifiable.allowModifications {
          modifiable.diff = diff
          modifiable.snapshot = diff
          modifiable.id = createEntityId()
          modifiable.entitySource = this.entitySource
        }
        modifiable.changedProperty.clear()
        return modifiable
    }

    override fun createEntity(snapshot: EntityStorage): LeftEntity {
        val entity = LeftEntityImpl()
        entity.entitySource = entitySource
        entity.snapshot = snapshot
        entity.id = createEntityId()
        return entity
    }

    override fun getEntityInterface(): Class<out WorkspaceEntity> {
        return LeftEntity::class.java
    }

    override fun serialize(ser: EntityInformation.Serializer) {
    }

    override fun deserialize(de: EntityInformation.Deserializer) {
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this::class != other::class) return false
        
        other as LeftEntityData
        
        if (this.entitySource != other.entitySource) return false
        return true
    }

    override fun equalsIgnoringEntitySource(other: Any?): Boolean {
        if (other == null) return false
        if (this::class != other::class) return false
        
        other as LeftEntityData
        
        return true
    }

    override fun hashCode(): Int {
        var result = entitySource.hashCode()
        return result
    }
}