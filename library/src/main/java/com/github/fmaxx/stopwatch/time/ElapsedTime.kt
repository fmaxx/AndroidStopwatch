package com.github.fmaxx.stopwatch.time

import com.github.fmaxx.stopwatch.state.Running

/**
 * Created by Maxim Firsov on 14.10.2022.
 * firsoffmaxim@gmail.com
 */
class ElapsedTime(
    private val timestampProvider: TimestampProvider
) {
    fun calculate(state: Running): Long {
        val current = timestampProvider.milliseconds
        val passedSinceStart = if (current > state.startTime) {
            current - state.startTime
        } else {
            0
        }
        return passedSinceStart + state.elapsedTime
    }
}