// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.vcs.log.data.index

import com.intellij.concurrency.ConcurrentCollectionFactory
import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Attachment
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.progress.EmptyProgressIndicator
import com.intellij.openapi.progress.util.BackgroundTaskUtil
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.Consumer
import com.intellij.util.containers.HashingStrategy
import com.intellij.vcs.log.data.CommitDetailsGetter
import com.intellij.vcs.log.data.DataPack
import com.intellij.vcs.log.data.VcsLogStorage
import com.intellij.vcs.log.data.index.IndexDiagnostic.getDiffFor
import com.intellij.vcs.log.data.index.IndexDiagnostic.getFirstCommits
import com.intellij.vcs.log.impl.FatalErrorHandler

internal class IndexDiagnosticRunner(private val index: VcsLogModifiableIndex,
                                     private val storage: VcsLogStorage,
                                     private val roots: Collection<VirtualFile>,
                                     private val dataPackGetter: () -> DataPack,
                                     private val commitDetailsGetter: CommitDetailsGetter,
                                     private val errorHandler: FatalErrorHandler,
                                     parent: Disposable) : Disposable {
  private val indexingListener = VcsLogIndex.IndexingFinishedListener { root -> runDiagnostic(listOf(root)) }
  private val checkedRoots = ConcurrentCollectionFactory.createConcurrentSet<VirtualFile>(HashingStrategy.canonical())

  init {
    index.addListener(indexingListener)
    Disposer.register(parent, this)
  }

  private fun runDiagnostic(rootsToCheck: Collection<VirtualFile>) {
    val dataGetter = index.dataGetter ?: return

    val dataPack = dataPackGetter()
    if (!dataPack.isFull) return

    val uncheckedRoots = rootsToCheck - checkedRoots
    if (uncheckedRoots.isEmpty()) return

    thisLogger().info("Running index diagnostic for $uncheckedRoots")
    checkedRoots.addAll(uncheckedRoots)

    val commits = dataPack.getFirstCommits(storage, uncheckedRoots)
    commitDetailsGetter.loadCommitsData(commits, Consumer { commitDetails ->
      BackgroundTaskUtil.executeOnPooledThread(this) {
        val diffReport = dataGetter.getDiffFor(commits, commitDetails)
        if (diffReport.isNotBlank()) {
          val exception = RuntimeException("Index is corrupted")
          thisLogger().error(exception.message, exception, Attachment("VcsLogIndexDiagnosticReport.txt", diffReport))
          index.markCorrupted()
          errorHandler.consume(this, exception)
        }
      }
    }, thisLogger()::error, EmptyProgressIndicator())
  }

  fun onDataPackChange() {
    runDiagnostic(roots.filter(index::isIndexed))
  }

  override fun dispose() {
    index.removeListener(indexingListener)
  }
}