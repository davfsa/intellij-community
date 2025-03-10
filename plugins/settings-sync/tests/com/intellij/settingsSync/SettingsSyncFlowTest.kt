package com.intellij.settingsSync

import com.intellij.util.io.readText
import com.intellij.util.io.write
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.TreeWalk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.nio.charset.StandardCharsets
import kotlin.io.path.div
import kotlin.io.path.writeText

@RunWith(JUnit4::class)
internal class SettingsSyncFlowTest : SettingsSyncTestBase() {

  private lateinit var ideMediator: MockSettingsSyncIdeMediator

  @Before
  internal fun initFields() {
    ideMediator = MockSettingsSyncIdeMediator()
  }

  private fun initSettingsSync(initMode: SettingsSyncBridge.InitMode = SettingsSyncBridge.InitMode.JustInit) {
    val controls = SettingsSyncMain.init(application, disposable, settingsSyncStorage, configDir, remoteCommunicator, ideMediator)
    updateChecker = controls.updateChecker
    bridge = controls.bridge
    bridge.initialize(initMode)
  }
  
  @Test fun `existing settings should be copied on initialization`() {
    val fileName = "options/laf.xml"
    val initialContent = "LaF Initial"
    configDir.resolve(fileName).write(initialContent)

    initSettingsSync(SettingsSyncBridge.InitMode.PushToServer)

    val pushedSnapshot = remoteCommunicator.pushed
    assertNotNull("Nothing has been pushed", pushedSnapshot)
    pushedSnapshot!!.assertSettingsSnapshot {
      fileState(fileName, initialContent)
    }
  }

  @Test fun `settings modified between IDE sessions should be logged`() {
    // emulate first session with initialization
    val fileName = "options/laf.xml"
    val file = configDir.resolve(fileName).write("LaF Initial")
    val log = GitSettingsLog(settingsSyncStorage, configDir, disposable, MockSettingsSyncIdeMediator.getAllFilesFromSettings(configDir))
    log.initialize()
    log.logExistingSettings()

    // modify between sessions
    val contentBetweenSessions = "LaF Between Sessions"
    file.writeText(contentBetweenSessions)

    initSettingsSync()

    val pushedSnapshot = remoteCommunicator.pushed
    assertNotNull("Nothing has been pushed", pushedSnapshot)
    pushedSnapshot!!.assertSettingsSnapshot {
      fileState(fileName, contentBetweenSessions)
    }
  }

  @Test fun `enable settings via Take from Server should log existing settings`() {
    val fileName = "options/laf.xml"
    val initialContent = "LaF Initial"
    configDir.resolve(fileName).write(initialContent)

    val cloudContent = "LaF from Server"
    val snapshot = settingsSnapshot {
      fileState(fileName, cloudContent)
    }

    initSettingsSync(SettingsSyncBridge.InitMode.TakeFromServer(SyncSettingsEvent.CloudChange(snapshot)))

    assertEquals("Incorrect content", cloudContent, (settingsSyncStorage / "options" / "laf.xml").readText())

    assertAppliedToIde("options/laf.xml", cloudContent)

    val dotGit = settingsSyncStorage.resolve(".git")
    FileRepositoryBuilder.create(dotGit.toFile()).use { repository ->
      val git = Git(repository)
      val commits = git.log().all().call().toList()
      assertEquals("Unexpected number of commits. Commits: ${commits.joinToString { "'${it.shortMessage}'" }}", 3, commits.size)
      val (applyCloud, copyExistingSettings, initial) = commits

      assertEquals("Unexpected content in the 'copy existing settings' commit", initialContent, getContent(repository, copyExistingSettings, fileName))
      assertEquals("Unexpected content in the 'apply from cloud' commit", cloudContent, getContent(repository, applyCloud, fileName))
    }
  }

  private fun assertAppliedToIde(fileSpec: String, expectedContent: String) {
    val value = ideMediator.files[fileSpec]
    assertNotNull("$fileSpec was not applied to the IDE", value)
    assertEquals("Incorrect content of the applied $fileSpec", expectedContent, value)
  }

  private fun getContent(repository: Repository, commit: RevCommit, path: String): String {
    TreeWalk.forPath(repository, path, commit.tree).use { treeWalk ->
      repository.newObjectReader().use { objectReader ->
        val blob = treeWalk.getObjectId(0)
        return String(objectReader.open(blob).bytes, StandardCharsets.UTF_8)
      }
    }
  }
}