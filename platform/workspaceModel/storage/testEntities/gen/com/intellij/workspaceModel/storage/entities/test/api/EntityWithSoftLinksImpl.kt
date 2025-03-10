// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
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
import com.intellij.workspaceModel.storage.impl.SoftLinkable
import com.intellij.workspaceModel.storage.impl.WorkspaceEntityBase
import com.intellij.workspaceModel.storage.impl.WorkspaceEntityData
import com.intellij.workspaceModel.storage.impl.extractOneToManyChildren
import com.intellij.workspaceModel.storage.impl.indices.WorkspaceMutableIndex
import com.intellij.workspaceModel.storage.impl.updateOneToManyChildrenOfParent
import org.jetbrains.deft.ObjBuilder
import org.jetbrains.deft.Type
import org.jetbrains.deft.annotations.Child
import org.jetbrains.deft.annotations.Open

@GeneratedCodeApiVersion(1)
@GeneratedCodeImplVersion(1)
open class EntityWithSoftLinksImpl: EntityWithSoftLinks, WorkspaceEntityBase() {
    
    companion object {
        internal val CHILDREN_CONNECTION_ID: ConnectionId = ConnectionId.create(EntityWithSoftLinks::class.java, SoftLinkReferencedChild::class.java, ConnectionId.ConnectionType.ONE_TO_MANY, false)
        
        val connections = listOf<ConnectionId>(
            CHILDREN_CONNECTION_ID,
        )

    }
        
    @JvmField var _link: OnePersistentId? = null
    override val link: OnePersistentId
        get() = _link!!
                        
    @JvmField var _manyLinks: List<OnePersistentId>? = null
    override val manyLinks: List<OnePersistentId>
        get() = _manyLinks!!   
    
    @JvmField var _optionalLink: OnePersistentId? = null
    override val optionalLink: OnePersistentId?
        get() = _optionalLink
                        
    @JvmField var _inContainer: Container? = null
    override val inContainer: Container
        get() = _inContainer!!
                        
    @JvmField var _inOptionalContainer: Container? = null
    override val inOptionalContainer: Container?
        get() = _inOptionalContainer
                        
    @JvmField var _inContainerList: List<Container>? = null
    override val inContainerList: List<Container>
        get() = _inContainerList!!   
    
    @JvmField var _deepContainer: List<TooDeepContainer>? = null
    override val deepContainer: List<TooDeepContainer>
        get() = _deepContainer!!   
    
    @JvmField var _sealedContainer: SealedContainer? = null
    override val sealedContainer: SealedContainer
        get() = _sealedContainer!!
                        
    @JvmField var _listSealedContainer: List<SealedContainer>? = null
    override val listSealedContainer: List<SealedContainer>
        get() = _listSealedContainer!!   
    
    @JvmField var _justProperty: String? = null
    override val justProperty: String
        get() = _justProperty!!
                        
    @JvmField var _justNullableProperty: String? = null
    override val justNullableProperty: String?
        get() = _justNullableProperty
                        
    @JvmField var _justListProperty: List<String>? = null
    override val justListProperty: List<String>
        get() = _justListProperty!!   
    
    @JvmField var _deepSealedClass: DeepSealedOne? = null
    override val deepSealedClass: DeepSealedOne
        get() = _deepSealedClass!!
                        
    override val children: List<SoftLinkReferencedChild>
        get() = snapshot.extractOneToManyChildren<SoftLinkReferencedChild>(CHILDREN_CONNECTION_ID, this)!!.toList()
    
    override fun connectionIdList(): List<ConnectionId> {
        return connections
    }

    class Builder(val result: EntityWithSoftLinksData?): ModifiableWorkspaceEntityBase<EntityWithSoftLinks>(), EntityWithSoftLinks.Builder {
        constructor(): this(EntityWithSoftLinksData())
        
        override fun applyToBuilder(builder: MutableEntityStorage) {
            if (this.diff != null) {
                if (existsInBuilder(builder)) {
                    this.diff = builder
                    return
                }
                else {
                    error("Entity EntityWithSoftLinks is already created in a different builder")
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
            if (!getEntityData().isLinkInitialized()) {
                error("Field EntityWithSoftLinks#link should be initialized")
            }
            if (!getEntityData().isEntitySourceInitialized()) {
                error("Field EntityWithSoftLinks#entitySource should be initialized")
            }
            if (!getEntityData().isManyLinksInitialized()) {
                error("Field EntityWithSoftLinks#manyLinks should be initialized")
            }
            if (!getEntityData().isInContainerInitialized()) {
                error("Field EntityWithSoftLinks#inContainer should be initialized")
            }
            if (!getEntityData().isInContainerListInitialized()) {
                error("Field EntityWithSoftLinks#inContainerList should be initialized")
            }
            if (!getEntityData().isDeepContainerInitialized()) {
                error("Field EntityWithSoftLinks#deepContainer should be initialized")
            }
            if (!getEntityData().isSealedContainerInitialized()) {
                error("Field EntityWithSoftLinks#sealedContainer should be initialized")
            }
            if (!getEntityData().isListSealedContainerInitialized()) {
                error("Field EntityWithSoftLinks#listSealedContainer should be initialized")
            }
            if (!getEntityData().isJustPropertyInitialized()) {
                error("Field EntityWithSoftLinks#justProperty should be initialized")
            }
            if (!getEntityData().isJustListPropertyInitialized()) {
                error("Field EntityWithSoftLinks#justListProperty should be initialized")
            }
            if (!getEntityData().isDeepSealedClassInitialized()) {
                error("Field EntityWithSoftLinks#deepSealedClass should be initialized")
            }
            // Check initialization for list with ref type
            if (_diff != null) {
                if (_diff.extractOneToManyChildren<WorkspaceEntityBase>(CHILDREN_CONNECTION_ID, this) == null) {
                    error("Field EntityWithSoftLinks#children should be initialized")
                }
            }
            else {
                if (this.entityLinks[CHILDREN_CONNECTION_ID] == null) {
                    error("Field EntityWithSoftLinks#children should be initialized")
                }
            }
        }
        
        override fun connectionIdList(): List<ConnectionId> {
            return connections
        }
    
        
        override var link: OnePersistentId
            get() = getEntityData().link
            set(value) {
                checkModificationAllowed()
                getEntityData().link = value
                changedProperty.add("link")
                
            }
            
        override var entitySource: EntitySource
            get() = getEntityData().entitySource
            set(value) {
                checkModificationAllowed()
                getEntityData().entitySource = value
                changedProperty.add("entitySource")
                
            }
            
        override var manyLinks: List<OnePersistentId>
            get() = getEntityData().manyLinks
            set(value) {
                checkModificationAllowed()
                getEntityData().manyLinks = value
                
                changedProperty.add("manyLinks")
            }
            
        override var optionalLink: OnePersistentId?
            get() = getEntityData().optionalLink
            set(value) {
                checkModificationAllowed()
                getEntityData().optionalLink = value
                changedProperty.add("optionalLink")
                
            }
            
        override var inContainer: Container
            get() = getEntityData().inContainer
            set(value) {
                checkModificationAllowed()
                getEntityData().inContainer = value
                changedProperty.add("inContainer")
                
            }
            
        override var inOptionalContainer: Container?
            get() = getEntityData().inOptionalContainer
            set(value) {
                checkModificationAllowed()
                getEntityData().inOptionalContainer = value
                changedProperty.add("inOptionalContainer")
                
            }
            
        override var inContainerList: List<Container>
            get() = getEntityData().inContainerList
            set(value) {
                checkModificationAllowed()
                getEntityData().inContainerList = value
                
                changedProperty.add("inContainerList")
            }
            
        override var deepContainer: List<TooDeepContainer>
            get() = getEntityData().deepContainer
            set(value) {
                checkModificationAllowed()
                getEntityData().deepContainer = value
                
                changedProperty.add("deepContainer")
            }
            
        override var sealedContainer: SealedContainer
            get() = getEntityData().sealedContainer
            set(value) {
                checkModificationAllowed()
                getEntityData().sealedContainer = value
                changedProperty.add("sealedContainer")
                
            }
            
        override var listSealedContainer: List<SealedContainer>
            get() = getEntityData().listSealedContainer
            set(value) {
                checkModificationAllowed()
                getEntityData().listSealedContainer = value
                
                changedProperty.add("listSealedContainer")
            }
            
        override var justProperty: String
            get() = getEntityData().justProperty
            set(value) {
                checkModificationAllowed()
                getEntityData().justProperty = value
                changedProperty.add("justProperty")
            }
            
        override var justNullableProperty: String?
            get() = getEntityData().justNullableProperty
            set(value) {
                checkModificationAllowed()
                getEntityData().justNullableProperty = value
                changedProperty.add("justNullableProperty")
            }
            
        override var justListProperty: List<String>
            get() = getEntityData().justListProperty
            set(value) {
                checkModificationAllowed()
                getEntityData().justListProperty = value
                
                changedProperty.add("justListProperty")
            }
            
        override var deepSealedClass: DeepSealedOne
            get() = getEntityData().deepSealedClass
            set(value) {
                checkModificationAllowed()
                getEntityData().deepSealedClass = value
                changedProperty.add("deepSealedClass")
                
            }
            
        // List of non-abstract referenced types
        var _children: List<SoftLinkReferencedChild>? = emptyList()
        override var children: List<SoftLinkReferencedChild>
            get() {
                // Getter of the list of non-abstract referenced types
                val _diff = diff
                return if (_diff != null) {
                    _diff.extractOneToManyChildren<SoftLinkReferencedChild>(CHILDREN_CONNECTION_ID, this)!!.toList() + (this.entityLinks[CHILDREN_CONNECTION_ID]?.entity as? List<SoftLinkReferencedChild> ?: emptyList())
                } else {
                    this.entityLinks[CHILDREN_CONNECTION_ID]?.entity!! as List<SoftLinkReferencedChild>
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
                    _diff.updateOneToManyChildrenOfParent(CHILDREN_CONNECTION_ID, this, value)
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
        
        override fun getEntityData(): EntityWithSoftLinksData = result ?: super.getEntityData() as EntityWithSoftLinksData
        override fun getEntityClass(): Class<EntityWithSoftLinks> = EntityWithSoftLinks::class.java
    }
}
    
class EntityWithSoftLinksData : WorkspaceEntityData<EntityWithSoftLinks>(), SoftLinkable {
    lateinit var link: OnePersistentId
    lateinit var manyLinks: List<OnePersistentId>
    var optionalLink: OnePersistentId? = null
    lateinit var inContainer: Container
    var inOptionalContainer: Container? = null
    lateinit var inContainerList: List<Container>
    lateinit var deepContainer: List<TooDeepContainer>
    lateinit var sealedContainer: SealedContainer
    lateinit var listSealedContainer: List<SealedContainer>
    lateinit var justProperty: String
    var justNullableProperty: String? = null
    lateinit var justListProperty: List<String>
    lateinit var deepSealedClass: DeepSealedOne

    fun isLinkInitialized(): Boolean = ::link.isInitialized
    fun isManyLinksInitialized(): Boolean = ::manyLinks.isInitialized
    fun isInContainerInitialized(): Boolean = ::inContainer.isInitialized
    fun isInContainerListInitialized(): Boolean = ::inContainerList.isInitialized
    fun isDeepContainerInitialized(): Boolean = ::deepContainer.isInitialized
    fun isSealedContainerInitialized(): Boolean = ::sealedContainer.isInitialized
    fun isListSealedContainerInitialized(): Boolean = ::listSealedContainer.isInitialized
    fun isJustPropertyInitialized(): Boolean = ::justProperty.isInitialized
    fun isJustListPropertyInitialized(): Boolean = ::justListProperty.isInitialized
    fun isDeepSealedClassInitialized(): Boolean = ::deepSealedClass.isInitialized

    override fun getLinks(): Set<PersistentEntityId<*>> {
        val result = HashSet<PersistentEntityId<*>>()
        result.add(link)
        for (item in manyLinks) {
            result.add(item)
        }
        val optionalLink_optionalLink = optionalLink
        if (optionalLink_optionalLink != null) {
            result.add(optionalLink_optionalLink)
        }
        result.add(inContainer.id)
        for (item in inContainerList) {
            result.add(item.id)
        }
        for (item in deepContainer) {
            for (item in item.goDeeper) {
                for (item in item.goDeep) {
                    result.add(item.id)
                }
                val optionalLink_item_optionalId = item.optionalId
                if (optionalLink_item_optionalId != null) {
                    result.add(optionalLink_item_optionalId)
                }
            }
        }
        val _sealedContainer = sealedContainer
        when (_sealedContainer) {
            is SealedContainer.BigContainer ->  {
                result.add(_sealedContainer.id)
            }
            is SealedContainer.SmallContainer ->  {
                result.add(_sealedContainer.notId)
            }
            is SealedContainer.ContainerContainer ->  {
                for (item in _sealedContainer.container) {
                    result.add(item.id)
                }
            }
          is SealedContainer.EmptyContainer -> {}
        }
        for (item in listSealedContainer) {
            when (item) {
                is SealedContainer.BigContainer ->  {
                    result.add(item.id)
                }
                is SealedContainer.SmallContainer ->  {
                    result.add(item.notId)
                }
                is SealedContainer.ContainerContainer ->  {
                    for (item in item.container) {
                        result.add(item.id)
                    }
                }
              is SealedContainer.EmptyContainer -> {}
            }
        }
        for (item in justListProperty) {
        }
        val _deepSealedClass = deepSealedClass
        when (_deepSealedClass) {
            is DeepSealedOne.DeepSealedTwo ->  {
                val __deepSealedClass = _deepSealedClass
                when (__deepSealedClass) {
                    is DeepSealedOne.DeepSealedTwo.DeepSealedThree ->  {
                        val ___deepSealedClass = __deepSealedClass
                        when (___deepSealedClass) {
                            is DeepSealedOne.DeepSealedTwo.DeepSealedThree.DeepSealedFour ->  {
                                result.add(___deepSealedClass.id)
                            }
                        }
                    }
                }
            }
        }
        return result
    }

    override fun index(index: WorkspaceMutableIndex<PersistentEntityId<*>>) {
        index.index(this, link)
        for (item in manyLinks) {
            index.index(this, item)
        }
        val optionalLink_optionalLink = optionalLink
        if (optionalLink_optionalLink != null) {
            index.index(this, optionalLink_optionalLink)
        }
        index.index(this, inContainer.id)
        for (item in inContainerList) {
            index.index(this, item.id)
        }
        for (item in deepContainer) {
            for (item in item.goDeeper) {
                for (item in item.goDeep) {
                    index.index(this, item.id)
                }
                val optionalLink_item_optionalId = item.optionalId
                if (optionalLink_item_optionalId != null) {
                    index.index(this, optionalLink_item_optionalId)
                }
            }
        }
        val _sealedContainer = sealedContainer
        when (_sealedContainer) {
            is SealedContainer.BigContainer ->  {
                index.index(this, _sealedContainer.id)
            }
            is SealedContainer.SmallContainer ->  {
                index.index(this, _sealedContainer.notId)
            }
            is SealedContainer.ContainerContainer ->  {
                for (item in _sealedContainer.container) {
                    index.index(this, item.id)
                }
            }
          is SealedContainer.EmptyContainer -> {}
        }
        for (item in listSealedContainer) {
            when (item) {
                is SealedContainer.BigContainer ->  {
                    index.index(this, item.id)
                }
                is SealedContainer.SmallContainer ->  {
                    index.index(this, item.notId)
                }
                is SealedContainer.ContainerContainer ->  {
                    for (item in item.container) {
                        index.index(this, item.id)
                    }
                }
                is SealedContainer.EmptyContainer -> {}
            }
        }
        for (item in justListProperty) {
        }
        val _deepSealedClass = deepSealedClass
        when (_deepSealedClass) {
            is DeepSealedOne.DeepSealedTwo ->  {
                val __deepSealedClass = _deepSealedClass
                when (__deepSealedClass) {
                    is DeepSealedOne.DeepSealedTwo.DeepSealedThree ->  {
                        val ___deepSealedClass = __deepSealedClass
                        when (___deepSealedClass) {
                            is DeepSealedOne.DeepSealedTwo.DeepSealedThree.DeepSealedFour ->  {
                                index.index(this, ___deepSealedClass.id)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun updateLinksIndex(prev: Set<PersistentEntityId<*>>, index: WorkspaceMutableIndex<PersistentEntityId<*>>) {
        // TODO verify logic
        val mutablePreviousSet = HashSet(prev)
        val removedItem_link = mutablePreviousSet.remove(link)
        if (!removedItem_link) {
            index.index(this, link)
        }
        for (item in manyLinks) {
            val removedItem_item = mutablePreviousSet.remove(item)
            if (!removedItem_item) {
                index.index(this, item)
            }
        }
        val optionalLink_optionalLink = optionalLink
        if (optionalLink_optionalLink != null) {
            val removedItem_optionalLink_optionalLink = mutablePreviousSet.remove(optionalLink_optionalLink)
            if (!removedItem_optionalLink_optionalLink) {
                index.index(this, optionalLink_optionalLink)
            }
        }
        val removedItem_inContainer_id = mutablePreviousSet.remove(inContainer.id)
        if (!removedItem_inContainer_id) {
            index.index(this, inContainer.id)
        }
        for (item in inContainerList) {
            val removedItem_item_id = mutablePreviousSet.remove(item.id)
            if (!removedItem_item_id) {
                index.index(this, item.id)
            }
        }
        for (item in deepContainer) {
            for (item in item.goDeeper) {
                for (item in item.goDeep) {
                    val removedItem_item_id = mutablePreviousSet.remove(item.id)
                    if (!removedItem_item_id) {
                        index.index(this, item.id)
                    }
                }
                val optionalLink_item_optionalId = item.optionalId
                if (optionalLink_item_optionalId != null) {
                    val removedItem_optionalLink_item_optionalId = mutablePreviousSet.remove(optionalLink_item_optionalId)
                    if (!removedItem_optionalLink_item_optionalId) {
                        index.index(this, optionalLink_item_optionalId)
                    }
                }
            }
        }
        val _sealedContainer = sealedContainer
        when (_sealedContainer) {
            is SealedContainer.BigContainer ->  {
                val removedItem__sealedContainer_id = mutablePreviousSet.remove(_sealedContainer.id)
                if (!removedItem__sealedContainer_id) {
                    index.index(this, _sealedContainer.id)
                }
            }
            is SealedContainer.SmallContainer ->  {
                val removedItem__sealedContainer_notId = mutablePreviousSet.remove(_sealedContainer.notId)
                if (!removedItem__sealedContainer_notId) {
                    index.index(this, _sealedContainer.notId)
                }
            }
            is SealedContainer.ContainerContainer ->  {
                for (item in _sealedContainer.container) {
                    val removedItem_item_id = mutablePreviousSet.remove(item.id)
                    if (!removedItem_item_id) {
                        index.index(this, item.id)
                    }
                }
            }
            is SealedContainer.EmptyContainer -> {}
        }
        for (item in listSealedContainer) {
            when (item) {
                is SealedContainer.BigContainer ->  {
                    val removedItem_item_id = mutablePreviousSet.remove(item.id)
                    if (!removedItem_item_id) {
                        index.index(this, item.id)
                    }
                }
                is SealedContainer.SmallContainer ->  {
                    val removedItem_item_notId = mutablePreviousSet.remove(item.notId)
                    if (!removedItem_item_notId) {
                        index.index(this, item.notId)
                    }
                }
                is SealedContainer.ContainerContainer ->  {
                    for (item in item.container) {
                        val removedItem_item_id = mutablePreviousSet.remove(item.id)
                        if (!removedItem_item_id) {
                            index.index(this, item.id)
                        }
                    }
                }
                is SealedContainer.EmptyContainer -> {}
            }
        }
        for (item in justListProperty) {
        }
        val _deepSealedClass = deepSealedClass
        when (_deepSealedClass) {
            is DeepSealedOne.DeepSealedTwo ->  {
                val __deepSealedClass = _deepSealedClass
                when (__deepSealedClass) {
                    is DeepSealedOne.DeepSealedTwo.DeepSealedThree ->  {
                        val ___deepSealedClass = __deepSealedClass
                        when (___deepSealedClass) {
                            is DeepSealedOne.DeepSealedTwo.DeepSealedThree.DeepSealedFour ->  {
                                val removedItem____deepSealedClass_id = mutablePreviousSet.remove(___deepSealedClass.id)
                                if (!removedItem____deepSealedClass_id) {
                                    index.index(this, ___deepSealedClass.id)
                                }
                            }
                        }
                    }
                }
            }
        }
        for (removed in mutablePreviousSet) {
            index.remove(this, removed)
        }
    }

    override fun updateLink(oldLink: PersistentEntityId<*>, newLink: PersistentEntityId<*>): Boolean {
        var changed = false
        val link_data =         if (link == oldLink) {
            changed = true
            newLink as OnePersistentId
        }
        else {
            null
        }
        if (link_data != null) {
            link = link_data
        }
        val manyLinks_data = manyLinks.map {
            val it_data =             if (it == oldLink) {
                changed = true
                newLink as OnePersistentId
            }
            else {
                null
            }
            if (it_data != null) {
                it_data
            }
            else {
                it
            }
        }
        if (manyLinks_data != null) {
            manyLinks = manyLinks_data
        }
        var optionalLink_data_optional =         if (optionalLink != null) {
            val optionalLink___data =             if (optionalLink!! == oldLink) {
                changed = true
                newLink as OnePersistentId
            }
            else {
                null
            }
            optionalLink___data
        }
        else {
            null
        }
        if (optionalLink_data_optional != null) {
            optionalLink = optionalLink_data_optional
        }
        val inContainer_id_data =         if (inContainer.id == oldLink) {
            changed = true
            newLink as OnePersistentId
        }
        else {
            null
        }
        var inContainer_data = inContainer
        if (inContainer_id_data != null) {
            inContainer_data = inContainer_data.copy(id = inContainer_id_data)
        }
        if (inContainer_data != null) {
            inContainer = inContainer_data
        }
        var inOptionalContainer_data_optional =         if (inOptionalContainer != null) {
            val inOptionalContainer___id_data =             if (inOptionalContainer!!.id == oldLink) {
                changed = true
                newLink as OnePersistentId
            }
            else {
                null
            }
            var inOptionalContainer___data = inOptionalContainer!!
            if (inOptionalContainer___id_data != null) {
                inOptionalContainer___data = inOptionalContainer___data.copy(id = inOptionalContainer___id_data)
            }
            inOptionalContainer___data
        }
        else {
            null
        }
        if (inOptionalContainer_data_optional != null) {
            inOptionalContainer = inOptionalContainer_data_optional
        }
        val inContainerList_data = inContainerList.map {
            val it_id_data =             if (it.id == oldLink) {
                changed = true
                newLink as OnePersistentId
            }
            else {
                null
            }
            var it_data = it
            if (it_id_data != null) {
                it_data = it_data.copy(id = it_id_data)
            }
            if (it_data != null) {
                it_data
            }
            else {
                it
            }
        }
        if (inContainerList_data != null) {
            inContainerList = inContainerList_data
        }
        val deepContainer_data = deepContainer.map {
            val it_goDeeper_data = it.goDeeper.map {
                val it_goDeep_data = it.goDeep.map {
                    val it_id_data =                     if (it.id == oldLink) {
                        changed = true
                        newLink as OnePersistentId
                    }
                    else {
                        null
                    }
                    var it_data = it
                    if (it_id_data != null) {
                        it_data = it_data.copy(id = it_id_data)
                    }
                    if (it_data != null) {
                        it_data
                    }
                    else {
                        it
                    }
                }
                var it_optionalId_data_optional =                 if (it.optionalId != null) {
                    val it_optionalId___data =                     if (it.optionalId!! == oldLink) {
                        changed = true
                        newLink as OnePersistentId
                    }
                    else {
                        null
                    }
                    it_optionalId___data
                }
                else {
                    null
                }
                var it_data = it
                if (it_goDeep_data != null) {
                    it_data = it_data.copy(goDeep = it_goDeep_data)
                }
                if (it_optionalId_data_optional != null) {
                    it_data = it_data.copy(optionalId = it_optionalId_data_optional)
                }
                if (it_data != null) {
                    it_data
                }
                else {
                    it
                }
            }
            var it_data = it
            if (it_goDeeper_data != null) {
                it_data = it_data.copy(goDeeper = it_goDeeper_data)
            }
            if (it_data != null) {
                it_data
            }
            else {
                it
            }
        }
        if (deepContainer_data != null) {
            deepContainer = deepContainer_data
        }
        val _sealedContainer = sealedContainer
        val res_sealedContainer =         when (_sealedContainer) {
            is SealedContainer.BigContainer ->  {
                val _sealedContainer_id_data =                 if (_sealedContainer.id == oldLink) {
                    changed = true
                    newLink as OnePersistentId
                }
                else {
                    null
                }
                var _sealedContainer_data = _sealedContainer
                if (_sealedContainer_id_data != null) {
                    _sealedContainer_data = _sealedContainer_data.copy(id = _sealedContainer_id_data)
                }
                _sealedContainer_data
            }
            is SealedContainer.SmallContainer ->  {
                val _sealedContainer_notId_data =                 if (_sealedContainer.notId == oldLink) {
                    changed = true
                    newLink as OnePersistentId
                }
                else {
                    null
                }
                var _sealedContainer_data = _sealedContainer
                if (_sealedContainer_notId_data != null) {
                    _sealedContainer_data = _sealedContainer_data.copy(notId = _sealedContainer_notId_data)
                }
                _sealedContainer_data
            }
            is SealedContainer.EmptyContainer ->  {
                _sealedContainer
            }
            is SealedContainer.ContainerContainer ->  {
                val _sealedContainer_container_data = _sealedContainer.container.map {
                    val it_id_data =                     if (it.id == oldLink) {
                        changed = true
                        newLink as OnePersistentId
                    }
                    else {
                        null
                    }
                    var it_data = it
                    if (it_id_data != null) {
                        it_data = it_data.copy(id = it_id_data)
                    }
                    if (it_data != null) {
                        it_data
                    }
                    else {
                        it
                    }
                }
                var _sealedContainer_data = _sealedContainer
                if (_sealedContainer_container_data != null) {
                    _sealedContainer_data = _sealedContainer_data.copy(container = _sealedContainer_container_data)
                }
                _sealedContainer_data
            }
        }
        if (res_sealedContainer != null) {
            sealedContainer = res_sealedContainer
        }
        val listSealedContainer_data = listSealedContainer.map {
            val _it = it
            val res_it =             when (_it) {
                is SealedContainer.BigContainer ->  {
                    val _it_id_data =                     if (_it.id == oldLink) {
                        changed = true
                        newLink as OnePersistentId
                    }
                    else {
                        null
                    }
                    var _it_data = _it
                    if (_it_id_data != null) {
                        _it_data = _it_data.copy(id = _it_id_data)
                    }
                    _it_data
                }
                is SealedContainer.SmallContainer ->  {
                    val _it_notId_data =                     if (_it.notId == oldLink) {
                        changed = true
                        newLink as OnePersistentId
                    }
                    else {
                        null
                    }
                    var _it_data = _it
                    if (_it_notId_data != null) {
                        _it_data = _it_data.copy(notId = _it_notId_data)
                    }
                    _it_data
                }
                is SealedContainer.EmptyContainer ->  {
                    _it
                }
                is SealedContainer.ContainerContainer ->  {
                    val _it_container_data = _it.container.map {
                        val it_id_data =                         if (it.id == oldLink) {
                            changed = true
                            newLink as OnePersistentId
                        }
                        else {
                            null
                        }
                        var it_data = it
                        if (it_id_data != null) {
                            it_data = it_data.copy(id = it_id_data)
                        }
                        if (it_data != null) {
                            it_data
                        }
                        else {
                            it
                        }
                    }
                    var _it_data = _it
                    if (_it_container_data != null) {
                        _it_data = _it_data.copy(container = _it_container_data)
                    }
                    _it_data
                }
            }
            if (res_it != null) {
                res_it
            }
            else {
                it
            }
        }
        if (listSealedContainer_data != null) {
            listSealedContainer = listSealedContainer_data
        }
        val _deepSealedClass = deepSealedClass
        val res_deepSealedClass =         when (_deepSealedClass) {
            is DeepSealedOne.DeepSealedTwo ->  {
                val __deepSealedClass = _deepSealedClass
                val res__deepSealedClass =                 when (__deepSealedClass) {
                    is DeepSealedOne.DeepSealedTwo.DeepSealedThree ->  {
                        val ___deepSealedClass = __deepSealedClass
                        val res___deepSealedClass =                         when (___deepSealedClass) {
                            is DeepSealedOne.DeepSealedTwo.DeepSealedThree.DeepSealedFour ->  {
                                val ___deepSealedClass_id_data =                                 if (___deepSealedClass.id == oldLink) {
                                    changed = true
                                    newLink as OnePersistentId
                                }
                                else {
                                    null
                                }
                                var ___deepSealedClass_data = ___deepSealedClass
                                if (___deepSealedClass_id_data != null) {
                                    ___deepSealedClass_data = ___deepSealedClass_data.copy(id = ___deepSealedClass_id_data)
                                }
                                ___deepSealedClass_data
                            }
                        }
                        res___deepSealedClass
                    }
                }
                res__deepSealedClass
            }
        }
        if (res_deepSealedClass != null) {
            deepSealedClass = res_deepSealedClass
        }
        return changed
    }

    override fun wrapAsModifiable(diff: MutableEntityStorage): ModifiableWorkspaceEntity<EntityWithSoftLinks> {
        val modifiable = EntityWithSoftLinksImpl.Builder(null)
        modifiable.allowModifications {
          modifiable.diff = diff
          modifiable.snapshot = diff
          modifiable.id = createEntityId()
          modifiable.entitySource = this.entitySource
        }
        modifiable.changedProperty.clear()
        return modifiable
    }

    override fun createEntity(snapshot: EntityStorage): EntityWithSoftLinks {
        val entity = EntityWithSoftLinksImpl()
        entity._link = link
        entity._manyLinks = manyLinks
        entity._optionalLink = optionalLink
        entity._inContainer = inContainer
        entity._inOptionalContainer = inOptionalContainer
        entity._inContainerList = inContainerList
        entity._deepContainer = deepContainer
        entity._sealedContainer = sealedContainer
        entity._listSealedContainer = listSealedContainer
        entity._justProperty = justProperty
        entity._justNullableProperty = justNullableProperty
        entity._justListProperty = justListProperty
        entity._deepSealedClass = deepSealedClass
        entity.entitySource = entitySource
        entity.snapshot = snapshot
        entity.id = createEntityId()
        return entity
    }

    override fun getEntityInterface(): Class<out WorkspaceEntity> {
        return EntityWithSoftLinks::class.java
    }

    override fun serialize(ser: EntityInformation.Serializer) {
    }

    override fun deserialize(de: EntityInformation.Deserializer) {
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this::class != other::class) return false
        
        other as EntityWithSoftLinksData
        
        if (this.link != other.link) return false
        if (this.entitySource != other.entitySource) return false
        if (this.manyLinks != other.manyLinks) return false
        if (this.optionalLink != other.optionalLink) return false
        if (this.inContainer != other.inContainer) return false
        if (this.inOptionalContainer != other.inOptionalContainer) return false
        if (this.inContainerList != other.inContainerList) return false
        if (this.deepContainer != other.deepContainer) return false
        if (this.sealedContainer != other.sealedContainer) return false
        if (this.listSealedContainer != other.listSealedContainer) return false
        if (this.justProperty != other.justProperty) return false
        if (this.justNullableProperty != other.justNullableProperty) return false
        if (this.justListProperty != other.justListProperty) return false
        if (this.deepSealedClass != other.deepSealedClass) return false
        return true
    }

    override fun equalsIgnoringEntitySource(other: Any?): Boolean {
        if (other == null) return false
        if (this::class != other::class) return false
        
        other as EntityWithSoftLinksData
        
        if (this.link != other.link) return false
        if (this.manyLinks != other.manyLinks) return false
        if (this.optionalLink != other.optionalLink) return false
        if (this.inContainer != other.inContainer) return false
        if (this.inOptionalContainer != other.inOptionalContainer) return false
        if (this.inContainerList != other.inContainerList) return false
        if (this.deepContainer != other.deepContainer) return false
        if (this.sealedContainer != other.sealedContainer) return false
        if (this.listSealedContainer != other.listSealedContainer) return false
        if (this.justProperty != other.justProperty) return false
        if (this.justNullableProperty != other.justNullableProperty) return false
        if (this.justListProperty != other.justListProperty) return false
        if (this.deepSealedClass != other.deepSealedClass) return false
        return true
    }

    override fun hashCode(): Int {
        var result = entitySource.hashCode()
        result = 31 * result + link.hashCode()
        result = 31 * result + manyLinks.hashCode()
        result = 31 * result + optionalLink.hashCode()
        result = 31 * result + inContainer.hashCode()
        result = 31 * result + inOptionalContainer.hashCode()
        result = 31 * result + inContainerList.hashCode()
        result = 31 * result + deepContainer.hashCode()
        result = 31 * result + sealedContainer.hashCode()
        result = 31 * result + listSealedContainer.hashCode()
        result = 31 * result + justProperty.hashCode()
        result = 31 * result + justNullableProperty.hashCode()
        result = 31 * result + justListProperty.hashCode()
        result = 31 * result + deepSealedClass.hashCode()
        return result
    }
}