package com.github.fmaxx.stopwatch.state

import com.github.fmaxx.stopwatch.time.ElapsedTime
import com.github.fmaxx.stopwatch.time.TimestampProvider

/**
 * Created by Maxim Firsov on 14.10.2022.
 * firsoffmaxim@gmail.com
 */
class StateCalculator(
    private val timestampProvider: TimestampProvider,
    private val elapsedTime: ElapsedTime
) {
    fun calculateRunningState(oldState: State): Running =
        when (oldState) {
            is Paused -> {
                Running(
                        startTime = timestampProvider.milliseconds,
                        elapsedTime = oldState.elapsedTime
                )
            }
            is Running -> oldState
        }

    fun calculatePausedState(oldState: State): Paused {
        return when (oldState) {
            is Paused -> oldState
            is Running -> {
                val elapsedTime = elapsedTime.calculate(oldState)
                Paused(elapsedTime)
            }
        }
    }
}