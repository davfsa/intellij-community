// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.util.concurrency;

import com.intellij.codeWithMe.ClientId;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.ConcurrencyUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * An {@link ExecutorService} implementation which
 * delegates tasks to the EDT for execution.
 */
final class EdtScheduledExecutorServiceImpl extends SchedulingWrapper implements EdtScheduledExecutorService {
  private EdtScheduledExecutorServiceImpl() {
    super(EdtExecutorServiceImpl.INSTANCE, ((AppScheduledExecutorService)AppExecutorUtil.getAppScheduledExecutorService()).delayQueue);
  }

  @NotNull
  @Override
  public ScheduledFuture<?> schedule(@NotNull Runnable command, @NotNull ModalityState modalityState, long delay, TimeUnit unit) {
    MyScheduledFutureTask<?> task = new MyScheduledFutureTask<Void>(ClientId.decorateRunnable(command), null, triggerTime(delayQueue, delay, unit)){
      @Override
      void executeMeInBackendExecutor() {
        EdtExecutorService.getInstance().execute(this, modalityState, (o) -> {
          Application application = ApplicationManager.getApplication();
          return this.isCancelled() || application == null || application.isDisposed();
        });
      }
    };
    return delayedExecute(task);
  }

  @Override
  void futureDone(@NotNull Future<?> task) {
    if (EdtExecutorServiceImpl.shouldManifestExceptionsImmediately()) {
      ConcurrencyUtil.manifestExceptionsIn(task);
    }
  }

  // stubs
  @Override
  public void shutdown() {
    AppScheduledExecutorService.error();
  }

  @NotNull
  @Override
  public List<Runnable> shutdownNow() {
    return AppScheduledExecutorService.error();
  }

  @Override
  public boolean isShutdown() {
    return false;
  }

  @Override
  public boolean isTerminated() {
    return false;
  }

  @Override
  public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) {
    AppScheduledExecutorService.error();
    return false;
  }

  static final EdtScheduledExecutorService INSTANCE = new EdtScheduledExecutorServiceImpl();
}
