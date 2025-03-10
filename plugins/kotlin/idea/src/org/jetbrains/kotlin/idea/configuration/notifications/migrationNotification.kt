// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.kotlin.idea.configuration.notifications

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsSafe
import org.jetbrains.kotlin.idea.KotlinBundle
import org.jetbrains.kotlin.idea.KotlinIcons
import org.jetbrains.kotlin.idea.migration.CodeMigrationAction
import org.jetbrains.kotlin.idea.migration.KotlinMigrationProjectFUSCollector
import org.jetbrains.kotlin.idea.migration.MigrationInfo

internal fun showMigrationNotification(project: Project, migrationInfo: MigrationInfo) {
    @NlsSafe
    val detectedChangeMessage = buildString {
        appendBr(KotlinBundle.message("configuration.migration.text.detected.migration"))

        if (migrationInfo.oldLanguageVersion != migrationInfo.newLanguageVersion) {
            appendIndentBr(
                KotlinBundle.message(
                    "configuration.migration.text.language.version",
                    migrationInfo.oldLanguageVersion,
                    migrationInfo.newLanguageVersion
                )
            )
        }

        if (migrationInfo.oldApiVersion != migrationInfo.newApiVersion) {
            appendIndentBr(
                KotlinBundle.message(
                    "configuration.migration.text.api.version",
                    migrationInfo.oldApiVersion,
                    migrationInfo.newApiVersion
                )
            )
        }
    }

    KotlinMigrationProjectFUSCollector.logNotification(migrationInfo)
    NotificationGroupManager.getInstance()
        .getNotificationGroup("Kotlin Migration")
        .createNotification(
            KotlinBundle.message("configuration.migration.title.kotlin.migration"),
            "${KotlinBundle.message("configuration.migration.text.migrations.for.kotlin.code.are.available")}<br/><br/>$detectedChangeMessage",
            NotificationType.WARNING
        )
        .setSuggestionType(true)
        .addAction(NotificationAction.createExpiring(KotlinBundle.message("configuration.migration.text.run.migrations")) { notificationAction, notification ->
            val notificationProject = notificationAction.project ?: return@createExpiring
            val projectContext = SimpleDataContext.getProjectContext(notificationProject)
            val migrationAction = ActionManager.getInstance().getAction(CodeMigrationAction.ACTION_ID)
            Notification.fire(notification, migrationAction, projectContext)
            KotlinMigrationProjectFUSCollector.logRun()
        })
        .setImportant(true)
        .setIcon(KotlinIcons.SMALL_LOGO)
        .notify(project)
}

private fun StringBuilder.appendBr(line: String) = this.append("$line<br/>")
private fun StringBuilder.appendIndentBr(line: String) = appendBr("&nbsp;&nbsp;$line")
