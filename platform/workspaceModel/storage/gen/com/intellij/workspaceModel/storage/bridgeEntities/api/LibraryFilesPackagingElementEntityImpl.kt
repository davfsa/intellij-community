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
import com.intellij.workspaceModel.storage.PersistentEntityId
import com.intellij.workspaceModel.storage.WorkspaceEntity
import com.intellij.workspaceModel.storage.impl.ConnectionId
import com.intellij.workspaceModel.storage.impl.EntityLink
import com.intellij.workspaceModel.storage.impl.ModifiableWorkspaceEntityBase
import com.intellij.workspaceModel.storage.impl.SoftLinkable
import com.intellij.workspaceModel.storage.impl.WorkspaceEntityBase
import com.intellij.workspaceModel.storage.impl.WorkspaceEntityData
import com.intellij.workspaceModel.storage.impl.extractOneToAbstractManyParent
import com.intellij.workspaceModel.storage.impl.indices.WorkspaceMutableIndex
import com.intellij.workspaceModel.storage.impl.updateOneToAbstractManyParentOfChild
import com.intellij.workspaceModel.storage.referrersx
import com.intellij.workspaceModel.storage.url.VirtualFileUrl
import org.jetbrains.deft.ObjBuilder
import org.jetbrains.deft.Type
import org.jetbrains.deft.annotations.Abstract
import org.jetbrains.deft.annotations.Child

@GeneratedCodeApiVersion(1)
@GeneratedCodeImplVersion(1)
open class LibraryFilesPackagingElementEntityImpl: LibraryFilesPackagingElementEntity, WorkspaceEntityBase() {
    
    companion object {
        internal val PARENTENTITY_CONNECTION_ID: ConnectionId = ConnectionId.create(CompositePackagingElementEntity::class.java, PackagingElementEntity::class.java, ConnectionId.ConnectionType.ONE_TO_ABSTRACT_MANY, true)
        
        val connections = listOf<ConnectionId>(
            PARENTENTITY_CONNECTION_ID,
        )

    }
        
    override val parentEntity: CompositePackagingElementEntity?
        get() = snapshot.extractOneToAbstractManyParent(PARENTENTITY_CONNECTION_ID, this)           
        
    @JvmField var _library: LibraryId? = null
    override val library: LibraryId?
        get() = _library
    
    override fun connectionIdList(): List<ConnectionId> {
        return connections
    }

    class Builder(val result: LibraryFilesPackagingElementEntityData?): ModifiableWorkspaceEntityBase<LibraryFilesPackagingElementEntity>(), LibraryFilesPackagingElementEntity.Builder {
        constructor(): this(LibraryFilesPackagingElementEntityData())
        
        override fun applyToBuilder(builder: MutableEntityStorage) {
            if (this.diff != null) {
                if (existsInBuilder(builder)) {
                    this.diff = builder
                    return
                }
                else {
                    error("Entity LibraryFilesPackagingElementEntity is already created in a different builder")
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
            if (!getEntityData().isEntitySourceInitialized()) {
                error("Field LibraryFilesPackagingElementEntity#entitySource should be initialized")
            }
        }
        
        override fun connectionIdList(): List<ConnectionId> {
            return connections
        }
    
        
        override var parentEntity: CompositePackagingElementEntity?
            get() {
                val _diff = diff
                return if (_diff != null) {
                    _diff.extractOneToAbstractManyParent(PARENTENTITY_CONNECTION_ID, this) ?: this.entityLinks[PARENTENTITY_CONNECTION_ID]?.entity as? CompositePackagingElementEntity
                } else {
                    this.entityLinks[PARENTENTITY_CONNECTION_ID]?.entity as? CompositePackagingElementEntity
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
        
        override var library: LibraryId?
            get() = getEntityData().library
            set(value) {
                checkModificationAllowed()
                getEntityData().library = value
                changedProperty.add("library")
                
            }
            
        override var entitySource: EntitySource
            get() = getEntityData().entitySource
            set(value) {
                checkModificationAllowed()
                getEntityData().entitySource = value
                changedProperty.add("entitySource")
                
            }
        
        override fun getEntityData(): LibraryFilesPackagingElementEntityData = result ?: super.getEntityData() as LibraryFilesPackagingElementEntityData
        override fun getEntityClass(): Class<LibraryFilesPackagingElementEntity> = LibraryFilesPackagingElementEntity::class.java
    }
}
    
class LibraryFilesPackagingElementEntityData : WorkspaceEntityData<LibraryFilesPackagingElementEntity>(), SoftLinkable {
    var library: LibraryId? = null


    override fun getLinks(): Set<PersistentEntityId<*>> {
        val result = HashSet<PersistentEntityId<*>>()
        val optionalLink_library = library
        if (optionalLink_library != null) {
            result.add(optionalLink_library)
        }
        return result
    }

    override fun index(index: WorkspaceMutableIndex<PersistentEntityId<*>>) {
        val optionalLink_library = library
        if (optionalLink_library != null) {
            index.index(this, optionalLink_library)
        }
    }

    override fun updateLinksIndex(prev: Set<PersistentEntityId<*>>, index: WorkspaceMutableIndex<PersistentEntityId<*>>) {
        // TODO verify logic
        val mutablePreviousSet = HashSet(prev)
        val optionalLink_library = library
        if (optionalLink_library != null) {
            val removedItem_optionalLink_library = mutablePreviousSet.remove(optionalLink_library)
            if (!removedItem_optionalLink_library) {
                index.index(this, optionalLink_library)
            }
        }
        for (removed in mutablePreviousSet) {
            index.remove(this, removed)
        }
    }

    override fun updateLink(oldLink: PersistentEntityId<*>, newLink: PersistentEntityId<*>): Boolean {
        var changed = false
        var library_data_optional =         if (library != null) {
            val library___data =             if (library!! == oldLink) {
                changed = true
                newLink as LibraryId
            }
            else {
                null
            }
            library___data
        }
        else {
            null
        }
        if (library_data_optional != null) {
            library = library_data_optional
        }
        return changed
    }

    override fun wrapAsModifiable(diff: MutableEntityStorage): ModifiableWorkspaceEntity<LibraryFilesPackagingElementEntity> {
        val modifiable = LibraryFilesPackagingElementEntityImpl.Builder(null)
        modifiable.allowModifications {
          modifiable.diff = diff
          modifiable.snapshot = diff
          modifiable.id = createEntityId()
          modifiable.entitySource = this.entitySource
        }
        modifiable.changedProperty.clear()
        return modifiable
    }

    override fun createEntity(snapshot: EntityStorage): LibraryFilesPackagingElementEntity {
        val entity = LibraryFilesPackagingElementEntityImpl()
        entity._library = library
        entity.entitySource = entitySource
        entity.snapshot = snapshot
        entity.id = createEntityId()
        return entity
    }

    override fun getEntityInterface(): Class<out WorkspaceEntity> {
        return LibraryFilesPackagingElementEntity::class.java
    }

    override fun serialize(ser: EntityInformation.Serializer) {
    }

    override fun deserialize(de: EntityInformation.Deserializer) {
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this::class != other::class) return false
        
        other as LibraryFilesPackagingElementEntityData
        
        if (this.library != other.library) return false
        if (this.entitySource != other.entitySource) return false
        return true
    }

    override fun equalsIgnoringEntitySource(other: Any?): Boolean {
        if (other == null) return false
        if (this::class != other::class) return false
        
        other as LibraryFilesPackagingElementEntityData
        
        if (this.library != other.library) return false
        return true
    }

    override fun hashCode(): Int {
        var result = entitySource.hashCode()
        result = 31 * result + library.hashCode()
        return result
    }
}