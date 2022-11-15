package com.github.fmaxx.stopwatch

import com.github.fmaxx.stopwatch.state.StateCalculator
import com.github.fmaxx.stopwatch.state.StateHolder
import com.github.fmaxx.stopwatch.time.ElapsedTime
import com.github.fmaxx.stopwatch.time.TimestampMillisecondsFormatter
import com.github.fmaxx.stopwatch.time.TimestampProviderImpl
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Created by Maxim Firsov on 31.10.2022.
 * firsoffmaxim@gmail.com
 */
class StopwatchProcessor(
        private val scope: CoroutineScope,
        private val tickDelayMilliseconds: Long = 20,
        private val startMilliseconds: Long = 0,
        holder: StateHolder? = null
) {

    private val stateHolder: StateHolder
    private var job: Job? = null
    private val _ticker = MutableStateFlow(0L)
    val ticker: Flow<Long> = _ticker

    init {
        require(tickDelayMilliseconds > 0)
        require(startMilliseconds >= 0)
        stateHolder = holder ?: createStateHolder(startMilliseconds)
    }

    private fun createStateHolder(startMilliseconds: Long): StateHolder {
        val timestampProvider = TimestampProviderImpl()
        val elapsedTime = ElapsedTime(timestampProvider = timestampProvider)
        val calculator = StateCalculator(
                timestampProvider = timestampProvider,
                elapsedTime = elapsedTime)

        return StateHolder(
                stateCalculator = calculator,
                elapsedTime = elapsedTime,
                formatter = TimestampMillisecondsFormatter(),
                startMilliseconds
        )
    }

    fun start() {
        if (job == null) startJob()
        stateHolder.start()
    }

    private fun startJob() {
        scope.launch {
            while (isActive) {
                _ticker.value = stateHolder.elapsedTimeMillis
                delay(tickDelayMilliseconds)
                println("stateHolder.elapsedTimeMillis: ${stateHolder.elapsedTimeMillis}, delay: $tickDelayMilliseconds")
            }
        }
    }

    fun pause() {
        stateHolder.pause()
        stopJob()
    }

    fun stop() {
        stateHolder.stop()
        stopJob()
        clearValue()
    }

    private fun stopJob() {
        scope.coroutineContext.cancelChildren()
        job = null
    }

    private fun clearValue() {
        _ticker.value = startMilliseconds
    }
}