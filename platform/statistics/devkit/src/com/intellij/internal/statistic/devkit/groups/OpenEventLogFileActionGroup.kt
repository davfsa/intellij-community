// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.internal.statistic.devkit.groups

import com.intellij.internal.statistic.devkit.StatisticsDevKitUtil.getLogProvidersInTestMode
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

internal class OpenEventLogFileActionGroup : ActionGroup() {

  override fun getActionUpdateThread() = ActionUpdateThread.BGT

  override fun getChildren(e: AnActionEvent?): Array<AnAction> {
    return getLogProvidersInTestMode()
      .map { logger ->
        val recorder = logger.recorderId
        com.intellij.internal.statistic.devkit.actions.OpenEventLogFileAction(recorder)
      }
      .toTypedArray()
  }

  override fun isPopup(): Boolean {
    return getLogProvidersInTestMode().size > 1
  }
}