// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
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
import com.intellij.workspaceModel.storage.impl.extractOneToManyChildren
import com.intellij.workspaceModel.storage.impl.extractOneToOneChild
import com.intellij.workspaceModel.storage.impl.indices.WorkspaceMutableIndex
import com.intellij.workspaceModel.storage.impl.updateOneToManyChildrenOfParent
import com.intellij.workspaceModel.storage.impl.updateOneToOneChildOfParent
import com.intellij.workspaceModel.storage.referrersx
import com.intellij.workspaceModel.storage.url.VirtualFileUrl
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import org.jetbrains.deft.ObjBuilder
import org.jetbrains.deft.Type
import org.jetbrains.deft.annotations.Child

@GeneratedCodeApiVersion(1)
@GeneratedCodeImplVersion(1)
open class ModuleEntityImpl: ModuleEntity, WorkspaceEntityBase() {
    
    companion object {
        internal val CONTENTROOTS_CONNECTION_ID: ConnectionId = ConnectionId.create(ModuleEntity::class.java, ContentRootEntity::class.java, ConnectionId.ConnectionType.ONE_TO_MANY, false)
        internal val CUSTOMIMLDATA_CONNECTION_ID: ConnectionId = ConnectionId.create(ModuleEntity::class.java, ModuleCustomImlDataEntity::class.java, ConnectionId.ConnectionType.ONE_TO_ONE, false)
        internal val GROUPPATH_CONNECTION_ID: ConnectionId = ConnectionId.create(ModuleEntity::class.java, ModuleGroupPathEntity::class.java, ConnectionId.ConnectionType.ONE_TO_ONE, false)
        internal val JAVASETTINGS_CONNECTION_ID: ConnectionId = ConnectionId.create(ModuleEntity::class.java, JavaModuleSettingsEntity::class.java, ConnectionId.ConnectionType.ONE_TO_ONE, false)
        internal val EXMODULEOPTIONS_CONNECTION_ID: ConnectionId = ConnectionId.create(ModuleEntity::class.java, ExternalSystemModuleOptionsEntity::class.java, ConnectionId.ConnectionType.ONE_TO_ONE, false)
        internal val FACETS_CONNECTION_ID: ConnectionId = ConnectionId.create(ModuleEntity::class.java, FacetEntity::class.java, ConnectionId.ConnectionType.ONE_TO_MANY, false)
        
        val connections = listOf<ConnectionId>(
            CONTENTROOTS_CONNECTION_ID,
            CUSTOMIMLDATA_CONNECTION_ID,
            GROUPPATH_CONNECTION_ID,
            JAVASETTINGS_CONNECTION_ID,
            EXMODULEOPTIONS_CONNECTION_ID,
            FACETS_CONNECTION_ID,
        )

    }
        
    @JvmField var _name: String? = null
    override val name: String
        get() = _name!!
                        
    @JvmField var _type: String? = null
    override val type: String?
        get() = _type
                        
    @JvmField var _dependencies: List<ModuleDependencyItem>? = null
    override val dependencies: List<ModuleDependencyItem>
        get() = _dependencies!!   
    
    override val contentRoots: List<ContentRootEntity>
        get() = snapshot.extractOneToManyChildren<ContentRootEntity>(CONTENTROOTS_CONNECTION_ID, this)!!.toList()
    
    override val customImlData: ModuleCustomImlDataEntity?
        get() = snapshot.extractOneToOneChild(CUSTOMIMLDATA_CONNECTION_ID, this)           
        
    override val groupPath: ModuleGroupPathEntity?
        get() = snapshot.extractOneToOneChild(GROUPPATH_CONNECTION_ID, this)           
        
    override val javaSettings: JavaModuleSettingsEntity?
        get() = snapshot.extractOneToOneChild(JAVASETTINGS_CONNECTION_ID, this)           
        
    override val exModuleOptions: ExternalSystemModuleOptionsEntity?
        get() = snapshot.extractOneToOneChild(EXMODULEOPTIONS_CONNECTION_ID, this)           
        
    override val facets: List<FacetEntity>
        get() = snapshot.extractOneToManyChildren<FacetEntity>(FACETS_CONNECTION_ID, this)!!.toList()
    
    override fun connectionIdList(): List<ConnectionId> {
        return connections
    }

    class Builder(val result: ModuleEntityData?): ModifiableWorkspaceEntityBase<ModuleEntity>(), ModuleEntity.Builder {
        constructor(): this(ModuleEntityData())
        
        override fun applyToBuilder(builder: MutableEntityStorage) {
            if (this.diff != null) {
                if (existsInBuilder(builder)) {
                    this.diff = builder
                    return
                }
                else {
                    error("Entity ModuleEntity is already created in a different builder")
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
            if (!getEntityData().isNameInitialized()) {
                error("Field ModuleEntity#name should be initialized")
            }
            if (!getEntityData().isEntitySourceInitialized()) {
                error("Field ModuleEntity#entitySource should be initialized")
            }
            if (!getEntityData().isDependenciesInitialized()) {
                error("Field ModuleEntity#dependencies should be initialized")
            }
            // Check initialization for list with ref type
            if (_diff != null) {
                if (_diff.extractOneToManyChildren<WorkspaceEntityBase>(CONTENTROOTS_CONNECTION_ID, this) == null) {
                    error("Field ModuleEntity#contentRoots should be initialized")
                }
            }
            else {
                if (this.entityLinks[CONTENTROOTS_CONNECTION_ID] == null) {
                    error("Field ModuleEntity#contentRoots should be initialized")
                }
            }
            // Check initialization for list with ref type
            if (_diff != null) {
                if (_diff.extractOneToManyChildren<WorkspaceEntityBase>(FACETS_CONNECTION_ID, this) == null) {
                    error("Field ModuleEntity#facets should be initialized")
                }
            }
            else {
                if (this.entityLinks[FACETS_CONNECTION_ID] == null) {
                    error("Field ModuleEntity#facets should be initialized")
                }
            }
        }
        
        override fun connectionIdList(): List<ConnectionId> {
            return connections
        }
    
        
        override var name: String
            get() = getEntityData().name
            set(value) {
                checkModificationAllowed()
                getEntityData().name = value
                changedProperty.add("name")
            }
            
        override var entitySource: EntitySource
            get() = getEntityData().entitySource
            set(value) {
                checkModificationAllowed()
                getEntityData().entitySource = value
                changedProperty.add("entitySource")
                
            }
            
        override var type: String?
            get() = getEntityData().type
            set(value) {
                checkModificationAllowed()
                getEntityData().type = value
                changedProperty.add("type")
            }
            
        override var dependencies: List<ModuleDependencyItem>
            get() = getEntityData().dependencies
            set(value) {
                checkModificationAllowed()
                getEntityData().dependencies = value
                
                changedProperty.add("dependencies")
            }
            
        // List of non-abstract referenced types
        var _contentRoots: List<ContentRootEntity>? = emptyList()
        override var contentRoots: List<ContentRootEntity>
            get() {
                // Getter of the list of non-abstract referenced types
                val _diff = diff
                return if (_diff != null) {
                    _diff.extractOneToManyChildren<ContentRootEntity>(CONTENTROOTS_CONNECTION_ID, this)!!.toList() + (this.entityLinks[CONTENTROOTS_CONNECTION_ID]?.entity as? List<ContentRootEntity> ?: emptyList())
                } else {
                    this.entityLinks[CONTENTROOTS_CONNECTION_ID]?.entity!! as List<ContentRootEntity>
                }
            }
            set(value) {
                // Setter of the list of non-abstract referenced types
                checkModificationAllowed()
                val _diff = diff
                if (_diff != null) {
                    for (item_value in value) {
                        if (item_value is ModifiableWorkspaceEntityBase<*> && (item_value as? ModifiableWorkspaceEntityBase<*>)?.diff == null) {
                            _diff.addEntity(item_value)
                        }
                    }
                    _diff.updateOneToManyChildrenOfParent(CONTENTROOTS_CONNECTION_ID, this, value)
                }
                else {
                    for (item_value in value) {
                        if (item_value is ModifiableWorkspaceEntityBase<*>) {
                            item_value.entityLinks[CONTENTROOTS_CONNECTION_ID] = EntityLink(false, this)
                        }
                        // else you're attaching a new entity to an existing entity that is not modifiable
                    }
                    
                    this.entityLinks[CONTENTROOTS_CONNECTION_ID] = EntityLink(true, value)
                }
                changedProperty.add("contentRoots")
            }
        
        override var customImlData: ModuleCustomImlDataEntity?
            get() {
                val _diff = diff
                return if (_diff != null) {
                    _diff.extractOneToOneChild(CUSTOMIMLDATA_CONNECTION_ID, this) ?: this.entityLinks[CUSTOMIMLDATA_CONNECTION_ID]?.entity as? ModuleCustomImlDataEntity
                } else {
                    this.entityLinks[CUSTOMIMLDATA_CONNECTION_ID]?.entity as? ModuleCustomImlDataEntity
                }
            }
            set(value) {
                checkModificationAllowed()
                val _diff = diff
                if (_diff != null && value is ModifiableWorkspaceEntityBase<*> && value.diff == null) {
                    if (value is ModifiableWorkspaceEntityBase<*>) {
                        value.entityLinks[CUSTOMIMLDATA_CONNECTION_ID] = EntityLink(false, this)
                    }
                    // else you're attaching a new entity to an existing entity that is not modifiable
                    _diff.addEntity(value)
                }
                if (_diff != null && (value !is ModifiableWorkspaceEntityBase<*> || value.diff != null)) {
                    _diff.updateOneToOneChildOfParent(CUSTOMIMLDATA_CONNECTION_ID, this, value)
                }
                else {
                    if (value is ModifiableWorkspaceEntityBase<*>) {
                        value.entityLinks[CUSTOMIMLDATA_CONNECTION_ID] = EntityLink(false, this)
                    }
                    // else you're attaching a new entity to an existing entity that is not modifiable
                    
                    this.entityLinks[CUSTOMIMLDATA_CONNECTION_ID] = EntityLink(true, value)
                }
                changedProperty.add("customImlData")
            }
        
        override var groupPath: ModuleGroupPathEntity?
            get() {
                val _diff = diff
                return if (_diff != null) {
                    _diff.extractOneToOneChild(GROUPPATH_CONNECTION_ID, this) ?: this.entityLinks[GROUPPATH_CONNECTION_ID]?.entity as? ModuleGroupPathEntity
                } else {
                    this.entityLinks[GROUPPATH_CONNECTION_ID]?.entity as? ModuleGroupPathEntity
                }
            }
            set(value) {
                checkModificationAllowed()
                val _diff = diff
                if (_diff != null && value is ModifiableWorkspaceEntityBase<*> && value.diff == null) {
                    if (value is ModifiableWorkspaceEntityBase<*>) {
                        value.entityLinks[GROUPPATH_CONNECTION_ID] = EntityLink(false, this)
                    }
                    // else you're attaching a new entity to an existing entity that is not modifiable
                    _diff.addEntity(value)
                }
                if (_diff != null && (value !is ModifiableWorkspaceEntityBase<*> || value.diff != null)) {
                    _diff.updateOneToOneChildOfParent(GROUPPATH_CONNECTION_ID, this, value)
                }
                else {
                    if (value is ModifiableWorkspaceEntityBase<*>) {
                        value.entityLinks[GROUPPATH_CONNECTION_ID] = EntityLink(false, this)
                    }
                    // else you're attaching a new entity to an existing entity that is not modifiable
                    
                    this.entityLinks[GROUPPATH_CONNECTION_ID] = EntityLink(true, value)
                }
                changedProperty.add("groupPath")
            }
        
        override var javaSettings: JavaModuleSettingsEntity?
            get() {
                val _diff = diff
                return if (_diff != null) {
                    _diff.extractOneToOneChild(JAVASETTINGS_CONNECTION_ID, this) ?: this.entityLinks[JAVASETTINGS_CONNECTION_ID]?.entity as? JavaModuleSettingsEntity
                } else {
                    this.entityLinks[JAVASETTINGS_CONNECTION_ID]?.entity as? JavaModuleSettingsEntity
                }
            }
            set(value) {
                checkModificationAllowed()
                val _diff = diff
                if (_diff != null && value is ModifiableWorkspaceEntityBase<*> && value.diff == null) {
                    if (value is ModifiableWorkspaceEntityBase<*>) {
                        value.entityLinks[JAVASETTINGS_CONNECTION_ID] = EntityLink(false, this)
                    }
                    // else you're attaching a new entity to an existing entity that is not modifiable
                    _diff.addEntity(value)
                }
                if (_diff != null && (value !is ModifiableWorkspaceEntityBase<*> || value.diff != null)) {
                    _diff.updateOneToOneChildOfParent(JAVASETTINGS_CONNECTION_ID, this, value)
                }
                else {
                    if (value is ModifiableWorkspaceEntityBase<*>) {
                        value.entityLinks[JAVASETTINGS_CONNECTION_ID] = EntityLink(false, this)
                    }
                    // else you're attaching a new entity to an existing entity that is not modifiable
                    
                    this.entityLinks[JAVASETTINGS_CONNECTION_ID] = EntityLink(true, value)
                }
                changedProperty.add("javaSettings")
            }
        
        override var exModuleOptions: ExternalSystemModuleOptionsEntity?
            get() {
                val _diff = diff
                return if (_diff != null) {
                    _diff.extractOneToOneChild(EXMODULEOPTIONS_CONNECTION_ID, this) ?: this.entityLinks[EXMODULEOPTIONS_CONNECTION_ID]?.entity as? ExternalSystemModuleOptionsEntity
                } else {
                    this.entityLinks[EXMODULEOPTIONS_CONNECTION_ID]?.entity as? ExternalSystemModuleOptionsEntity
                }
            }
            set(value) {
                checkModificationAllowed()
                val _diff = diff
                if (_diff != null && value is ModifiableWorkspaceEntityBase<*> && value.diff == null) {
                    if (value is ModifiableWorkspaceEntityBase<*>) {
                        value.entityLinks[EXMODULEOPTIONS_CONNECTION_ID] = EntityLink(false, this)
                    }
                    // else you're attaching a new entity to an existing entity that is not modifiable
                    _diff.addEntity(value)
                }
                if (_diff != null && (value !is ModifiableWorkspaceEntityBase<*> || value.diff != null)) {
                    _diff.updateOneToOneChildOfParent(EXMODULEOPTIONS_CONNECTION_ID, this, value)
                }
                else {
                    if (value is ModifiableWorkspaceEntityBase<*>) {
                        value.entityLinks[EXMODULEOPTIONS_CONNECTION_ID] = EntityLink(false, this)
                    }
                    // else you're attaching a new entity to an existing entity that is not modifiable
                    
                    this.entityLinks[EXMODULEOPTIONS_CONNECTION_ID] = EntityLink(true, value)
                }
                changedProperty.add("exModuleOptions")
            }
        
        // List of non-abstract referenced types
        var _facets: List<FacetEntity>? = emptyList()
        override var facets: List<FacetEntity>
            get() {
                // Getter of the list of non-abstract referenced types
                val _diff = diff
                return if (_diff != null) {
                    _diff.extractOneToManyChildren<FacetEntity>(FACETS_CONNECTION_ID, this)!!.toList() + (this.entityLinks[FACETS_CONNECTION_ID]?.entity as? List<FacetEntity> ?: emptyList())
                } else {
                    this.entityLinks[FACETS_CONNECTION_ID]?.entity!! as List<FacetEntity>
                }
            }
            set(value) {
                // Setter of the list of non-abstract referenced types
                checkModificationAllowed()
                val _diff = diff
                if (_diff != null) {
                    for (item_value in value) {
                        if (item_value is ModifiableWorkspaceEntityBase<*> && (item_value as? ModifiableWorkspaceEntityBase<*>)?.diff == null) {
                            _diff.addEntity(item_value)
                        }
                    }
                    _diff.updateOneToManyChildrenOfParent(FACETS_CONNECTION_ID, this, value)
                }
                else {
                    for (item_value in value) {
                        if (item_value is ModifiableWorkspaceEntityBase<*>) {
                            item_value.entityLinks[FACETS_CONNECTION_ID] = EntityLink(false, this)
                        }
                        // else you're attaching a new entity to an existing entity that is not modifiable
                    }
                    
                    this.entityLinks[FACETS_CONNECTION_ID] = EntityLink(true, value)
                }
                changedProperty.add("facets")
            }
        
        override fun getEntityData(): ModuleEntityData = result ?: super.getEntityData() as ModuleEntityData
        override fun getEntityClass(): Class<ModuleEntity> = ModuleEntity::class.java
    }
}
    
class ModuleEntityData : WorkspaceEntityData.WithCalculablePersistentId<ModuleEntity>(), SoftLinkable {
    lateinit var name: String
    var type: String? = null
    lateinit var dependencies: List<ModuleDependencyItem>

    fun isNameInitialized(): Boolean = ::name.isInitialized
    fun isDependenciesInitialized(): Boolean = ::dependencies.isInitialized

    override fun getLinks(): Set<PersistentEntityId<*>> {
        val result = HashSet<PersistentEntityId<*>>()
        for (item in dependencies) {
            when (item) {
                is ModuleDependencyItem.Exportable ->  {
                    when (item) {
                        is ModuleDependencyItem.Exportable.ModuleDependency ->  {
                            result.add(item.module)
                        }
                        is ModuleDependencyItem.Exportable.LibraryDependency ->  {
                            result.add(item.library)
                        }
                    }
                }
              else -> {}
            }
        }
        return result
    }

    override fun index(index: WorkspaceMutableIndex<PersistentEntityId<*>>) {
        for (item in dependencies) {
            when (item) {
                is ModuleDependencyItem.Exportable ->  {
                    when (item) {
                        is ModuleDependencyItem.Exportable.ModuleDependency ->  {
                            index.index(this, item.module)
                        }
                        is ModuleDependencyItem.Exportable.LibraryDependency ->  {
                            index.index(this, item.library)
                        }
                    }
                }
              else -> {}
            }
        }
    }

    override fun updateLinksIndex(prev: Set<PersistentEntityId<*>>, index: WorkspaceMutableIndex<PersistentEntityId<*>>) {
        // TODO verify logic
        val mutablePreviousSet = if (prev is ObjectOpenHashSet<PersistentEntityId<*>>) prev.clone() else HashSet(prev)
        for (item in dependencies) {
            when (item) {
                is ModuleDependencyItem.Exportable.ModuleDependency -> {
                    val removedItem_item_module = mutablePreviousSet.remove(item.module)
                    if (!removedItem_item_module) {
                        index.index(this, item.module)
                    }
                }
                is ModuleDependencyItem.Exportable.LibraryDependency -> {
                    val removedItem_item_library = mutablePreviousSet.remove(item.library)
                    if (!removedItem_item_library) {
                        index.index(this, item.library)
                    }
                }
              else -> {}
            }
        }
        for (removed in mutablePreviousSet) {
            index.remove(this, removed)
        }
    }

    override fun updateLink(oldLink: PersistentEntityId<*>, newLink: PersistentEntityId<*>): Boolean {
        var changed = false
        val dependencies_data = dependencies.map {
            val _it = it
            val res_it =             when (_it) {
                is ModuleDependencyItem.Exportable ->  {
                    val __it = _it
                    val res__it =                     when (__it) {
                        is ModuleDependencyItem.Exportable.ModuleDependency ->  {
                            val __it_module_data =                             if (__it.module == oldLink) {
                                changed = true
                                newLink as ModuleId
                            }
                            else {
                                null
                            }
                            var __it_data = __it
                            if (__it_module_data != null) {
                                __it_data = __it_data.copy(module = __it_module_data)
                            }
                            __it_data
                        }
                        is ModuleDependencyItem.Exportable.LibraryDependency ->  {
                            val __it_library_data =                             if (__it.library == oldLink) {
                                changed = true
                                newLink as LibraryId
                            }
                            else {
                                null
                            }
                            var __it_data = __it
                            if (__it_library_data != null) {
                                __it_data = __it_data.copy(library = __it_library_data)
                            }
                            __it_data
                        }
                    }
                    res__it
                }
                is ModuleDependencyItem.SdkDependency ->  {
                    _it
                }
                is ModuleDependencyItem.InheritedSdkDependency ->  {
                    _it
                }
                is ModuleDependencyItem.ModuleSourceDependency ->  {
                    _it
                }
            }
            if (res_it != null) {
                res_it
            }
            else {
                it
            }
        }
        if (dependencies_data != null) {
            dependencies = dependencies_data
        }
        return changed
    }

    override fun wrapAsModifiable(diff: MutableEntityStorage): ModifiableWorkspaceEntity<ModuleEntity> {
        val modifiable = ModuleEntityImpl.Builder(null)
        modifiable.allowModifications {
          modifiable.diff = diff
          modifiable.snapshot = diff
          modifiable.id = createEntityId()
          modifiable.entitySource = this.entitySource
        }
        modifiable.changedProperty.clear()
        return modifiable
    }

    override fun createEntity(snapshot: EntityStorage): ModuleEntity {
        val entity = ModuleEntityImpl()
        entity._name = name
        entity._type = type
        entity._dependencies = dependencies
        entity.entitySource = entitySource
        entity.snapshot = snapshot
        entity.id = createEntityId()
        return entity
    }

    override fun persistentId(): PersistentEntityId<*> {
        return ModuleId(name)
    }

    override fun getEntityInterface(): Class<out WorkspaceEntity> {
        return ModuleEntity::class.java
    }

    override fun serialize(ser: EntityInformation.Serializer) {
    }

    override fun deserialize(de: EntityInformation.Deserializer) {
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this::class != other::class) return false
        
        other as ModuleEntityData
        
        if (this.name != other.name) return false
        if (this.entitySource != other.entitySource) return false
        if (this.type != other.type) return false
        if (this.dependencies != other.dependencies) return false
        return true
    }

    override fun equalsIgnoringEntitySource(other: Any?): Boolean {
        if (other == null) return false
        if (this::class != other::class) return false
        
        other as ModuleEntityData
        
        if (this.name != other.name) return false
        if (this.type != other.type) return false
        if (this.dependencies != other.dependencies) return false
        return true
    }

    override fun hashCode(): Int {
        var result = entitySource.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + dependencies.hashCode()
        return result
    }
}