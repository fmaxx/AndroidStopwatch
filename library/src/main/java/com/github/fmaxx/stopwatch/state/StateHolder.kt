package com.github.fmaxx.stopwatch.state

import com.github.fmaxx.stopwatch.time.ElapsedTime
import com.github.fmaxx.stopwatch.time.TimestampMillisecondsFormatter


/**
 * Created by Maxim Firsov on 31.10.2022.
 * firsoffmaxim@gmail.com
 */
class StateHolder(private val stateCalculator: StateCalculator,
                  private val elapsedTime: ElapsedTime,
                  private val formatter: TimestampMillisecondsFormatter,
                  private val startMilliseconds: Long = 0) {

    var state: State = getDefaultState()
        private set

    fun start() {
        state = stateCalculator.calculateRunningState(state)
    }

    fun pause() {
        state = stateCalculator.calculatePausedState(state)
    }

    fun stop() {
        state = getDefaultState()
    }

    private fun getDefaultState() = Paused(startMilliseconds)

    val elapsedTimeString: String
        get() = formatter.format(elapsedTimeMillis)

    val elapsedTimeMillis: Long
        get() = when (val currentState = state) {
            is Paused -> currentState.elapsedTime
            is Running -> elapsedTime.calculate(currentState)
        }
}