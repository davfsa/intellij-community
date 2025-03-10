// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.openapi.editor.toolbar.floating

import com.intellij.openapi.Disposable
import com.intellij.openapi.observable.util.whenDisposed
import com.intellij.openapi.ui.isComponentUnderMouse
import com.intellij.util.ui.TimerUtil
import com.intellij.util.ui.UIUtil.invokeLaterIfNeeded
import org.jetbrains.annotations.ApiStatus
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

@Suppress("SameParameterValue")
@ApiStatus.Internal
class TransparentComponentAnimator(
  private val component: TransparentComponent,
  parentDisposable: Disposable
) {

  private val isDisposed = AtomicBoolean()
  private val executor = ExecutorWithThrottling(THROTTLING_DELAY)
  private val clk = TimerUtil.createNamedTimer("CLK", CLK_DELAY)
  private val state = AtomicReference<State>(State.Invisible)

  private fun startTimerIfNeeded() {
    if (!isDisposed.get() && !clk.isRunning) {
      clk.start()
    }
  }

  private fun stopTimerIfNeeded() {
    if (clk.isRunning) {
      clk.stop()
    }
  }

  fun scheduleShow() = executor.executeOrSkip {
    state.updateAndGet(::nextShowingState)
    startTimerIfNeeded()
  }

  fun scheduleHide() {
    state.updateAndGet(::nextHidingState)
    startTimerIfNeeded()
  }

  private fun updateState() {
    val state = state.updateAndGet(::nextState)
    if (state is State.Invisible ||
        state is State.Visible && !component.autoHideable) {
      stopTimerIfNeeded()
    }
    updateComponent(state)
  }

  private fun updateComponent(state: State) {
    invokeLaterIfNeeded {
      component.opacity = getOpacity(state)
      when (isVisible(state)) {
        true -> component.showComponent()
        else -> component.hideComponent()
      }
      component.component.repaint()
    }
  }

  private fun nextShowingState(state: State): State {
    return when (state) {
      is State.Invisible -> State.Showing(0)
      is State.Visible -> State.Visible(0)
      is State.Hiding -> State.Showing(SHOWING_COUNT - SHOWING_COUNT * state.count / HIDING_COUNT)
      is State.Showing -> state
    }
  }

  private fun nextHidingState(state: State): State {
    return when (state) {
      is State.Invisible -> State.Invisible
      is State.Visible -> State.Hiding(0)
      is State.Hiding -> state
      is State.Showing -> State.Hiding(HIDING_COUNT - HIDING_COUNT * state.count / SHOWING_COUNT)
    }
  }

  private fun nextState(state: State): State {
    return when (state) {
      is State.Invisible -> State.Invisible
      is State.Visible -> when {
        !component.autoHideable -> State.Visible(0)
        component.isComponentUnderMouse() -> State.Visible(0)
        state.count >= RETENTION_COUNT -> State.Hiding(0)
        else -> State.Visible(state.count + 1)
      }
      is State.Hiding -> when {
        state.count >= HIDING_COUNT -> State.Invisible
        else -> State.Hiding(state.count + 1)
      }
      is State.Showing -> when {
        state.count >= SHOWING_COUNT -> State.Visible(0)
        else -> State.Showing(state.count + 1)
      }
    }
  }

  private fun TransparentComponent.isComponentUnderMouse(): Boolean {
    return component.parent?.isComponentUnderMouse() == true
  }

  private fun getOpacity(state: State): Float {
    return when (state) {
      is State.Invisible -> 0.0f
      is State.Visible -> 1.0f
      is State.Hiding -> 1.0f - getOpacity(state.count, HIDING_COUNT)
      is State.Showing -> getOpacity(state.count, SHOWING_COUNT)
    }
  }

  private fun getOpacity(count: Int, maxCount: Int): Float {
    return maxOf(0.0f, minOf(1.0f, count / maxCount.toFloat()))
  }

  private fun isVisible(state: State): Boolean {
    return state !is State.Invisible
  }

  init {
    clk.isRepeats = true
    clk.addActionListener { updateState() }
    parentDisposable.whenDisposed { isDisposed.set(true) }
    parentDisposable.whenDisposed { stopTimerIfNeeded() }
  }

  private sealed interface State {
    object Invisible : State
    data class Visible(val count: Int) : State
    data class Hiding(val count: Int) : State
    data class Showing(val count: Int) : State
  }

  companion object {
    private const val CLK_FREQUENCY = 60
    private const val CLK_DELAY = 1000 / CLK_FREQUENCY
    private const val RETENTION_COUNT = 1500 / CLK_DELAY
    private const val SHOWING_COUNT = 500 / CLK_DELAY
    private const val HIDING_COUNT = 1000 / CLK_DELAY

    private const val THROTTLING_DELAY = 1000
  }
}