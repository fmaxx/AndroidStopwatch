package com.github.fmaxx.stopwatch

import kotlinx.coroutines.flow.FlowCollector

/**
 * Created by Maxim Firsov on 16.11.2022.
 * firsoffmaxim@gmail.com
 */
interface StopwatchProcessor {
    suspend fun collect(collector: FlowCollector<Long>)
    fun start()
    fun pause()
    fun stop()
}