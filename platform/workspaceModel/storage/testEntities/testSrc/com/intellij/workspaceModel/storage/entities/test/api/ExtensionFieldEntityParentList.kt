package com.intellij.workspaceModel.storage.entities.test.api

import com.intellij.workspaceModel.storage.*
import org.jetbrains.deft.ObjBuilder
import org.jetbrains.deft.Type
import org.jetbrains.deft.annotations.Child
import com.intellij.workspaceModel.storage.EntitySource
import com.intellij.workspaceModel.storage.GeneratedCodeApiVersion
import com.intellij.workspaceModel.storage.ModifiableReferableWorkspaceEntity
import com.intellij.workspaceModel.storage.ModifiableWorkspaceEntity
import com.intellij.workspaceModel.storage.MutableEntityStorage
import com.intellij.workspaceModel.storage.WorkspaceEntity
import com.intellij.workspaceModel.storage.referrersy



interface MainEntityParentList : WorkspaceEntity {
  val x: String
  val children: List<@Child AttachedEntityParentList>


  //region generated code
  //@formatter:off
  @GeneratedCodeApiVersion(1)
  interface Builder: MainEntityParentList, ModifiableWorkspaceEntity<MainEntityParentList>, ObjBuilder<MainEntityParentList> {
      override var x: String
      override var entitySource: EntitySource
      override var children: List<AttachedEntityParentList>
  }
  
  companion object: Type<MainEntityParentList, Builder>() {
      operator fun invoke(x: String, entitySource: EntitySource, init: (Builder.() -> Unit)? = null): MainEntityParentList {
          val builder = builder()
          builder.x = x
          builder.entitySource = entitySource
          init?.invoke(builder)
          return builder
      }
  }
  //@formatter:on
  //endregion

}
//region generated code
fun MutableEntityStorage.modifyEntity(entity: MainEntityParentList, modification: MainEntityParentList.Builder.() -> Unit) = modifyEntity(MainEntityParentList.Builder::class.java, entity, modification)
//endregion

interface AttachedEntityParentList : WorkspaceEntity {
  val data: String


  //region generated code
  //@formatter:off
  @GeneratedCodeApiVersion(1)
  interface Builder: AttachedEntityParentList, ModifiableWorkspaceEntity<AttachedEntityParentList>, ObjBuilder<AttachedEntityParentList> {
      override var data: String
      override var entitySource: EntitySource
  }
  
  companion object: Type<AttachedEntityParentList, Builder>() {
      operator fun invoke(data: String, entitySource: EntitySource, init: (Builder.() -> Unit)? = null): AttachedEntityParentList {
          val builder = builder()
          builder.data = data
          builder.entitySource = entitySource
          init?.invoke(builder)
          return builder
      }
  }
  //@formatter:on
  //endregion

}
//region generated code
fun MutableEntityStorage.modifyEntity(entity: AttachedEntityParentList, modification: AttachedEntityParentList.Builder.() -> Unit) = modifyEntity(AttachedEntityParentList.Builder::class.java, entity, modification)
var AttachedEntityParentList.Builder.ref: MainEntityParentList?
    get() {
        return referrersy(MainEntityParentList::children).singleOrNull()
    }
    set(value) {
        (this as ModifiableReferableWorkspaceEntity).linkExternalEntity(MainEntityParentList::class, if (value is List<*>) value as List<WorkspaceEntity?> else listOf(value) as List<WorkspaceEntity?> )
    }

//endregion

val AttachedEntityParentList.ref: MainEntityParentList?
  get() = referrersy(MainEntityParentList::children).singleOrNull()
