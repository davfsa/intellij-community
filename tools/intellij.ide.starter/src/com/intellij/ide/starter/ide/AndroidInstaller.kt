package com.intellij.ide.starter.ide

import com.intellij.ide.starter.downloadAndroidStudio
import com.intellij.ide.starter.models.IdeInfo
import com.intellij.ide.starter.system.SystemInfo
import java.io.File
import java.nio.file.Path

class AndroidInstaller: IdeInstallator {

  override fun install(ideInfo: IdeInfo): Pair<String, InstalledIDE> {

    val installDir: Path
    val installerFile: File

    downloadAndroidStudio().also {
      installDir = it.first
      installerFile = it.second
    }
    unpackIDEIfNeeded(installerFile, installDir.toFile())
    val installationPath = when (!SystemInfo.isMac) {
      true -> installDir.resolve("android-studio")
      false -> installDir
    }
    val ide = installIDE(installationPath.toFile(), ideInfo.executableFileName)
    return Pair(ide.build, ide)
  }
}