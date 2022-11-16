package com.github.fmaxx.stopwatch

import com.github.fmaxx.stopwatch.state.StateCalculator
import com.github.fmaxx.stopwatch.state.StateHolder
import com.github.fmaxx.stopwatch.time.ElapsedTime
import com.github.fmaxx.stopwatch.time.TimestampMillisecondsFormatter
import com.github.fmaxx.stopwatch.time.TimestampProviderImpl
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Created by Maxim Firsov on 31.10.2022.
 * firsoffmaxim@gmail.com
 */
class StopwatchProcessorImpl(
    private val scope: CoroutineScope,
    private val tickDelayMilliseconds: Long = 20,
    startMilliseconds: Long = 0,
    holder: StateHolder? = null,
): StopwatchProcessor {
    private val stateHolder: StateHolder
    private var job: Job? = null
    private val _ticker = MutableStateFlow(0L)
    val ticker: Flow<Long> = _ticker

    init {
        require(tickDelayMilliseconds > 0)
        require(startMilliseconds >= 0)
        stateHolder = holder ?: createStateHolder(startMilliseconds)
    }

    override suspend fun collect(collector: FlowCollector<Long>) {
        ticker.collect(collector)
    }

    override fun start() {
        if (job == null) {
            job = startJob()
        }
        stateHolder.start()
    }

    override fun pause() {
        stateHolder.pause()
        stopJob()
    }

    override fun stop() {
        stateHolder.stop()
        stopJob()
        _ticker.value = 0
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

    private fun startJob(): Job {
        return scope.launch {
            while (isActive) {
                _ticker.value = stateHolder.elapsedTimeMillis
                delay(tickDelayMilliseconds)
            }
        }
    }

    private fun stopJob() {
        job?.cancel()
        job = null
    }
}