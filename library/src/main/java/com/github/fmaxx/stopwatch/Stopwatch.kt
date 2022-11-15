package com.github.fmaxx.stopwatch

import kotlinx.coroutines.CoroutineScope

/**
 * Created by Maxim Firsov on 14.11.2022.
 * firsoffmaxim@gmail.com
 */
class Stopwatch private constructor() {
    class Builder(private val scope: CoroutineScope) {
        private var tickDelay: Long = 100
        private var startMilliseconds: Long = 0

        fun setTickDelay(milliseconds: Long): Builder {
            require(milliseconds > 0) { "Should be more than zero" }
            tickDelay = milliseconds
            return this
        }

        fun setStartMilliseconds(milliseconds: Long): Builder {
            require(milliseconds > 0) { "Should be more than zero" }
            startMilliseconds = milliseconds
            return this
        }

        fun build(): StopwatchProcessor =
            StopwatchProcessor(
                scope = scope,
                tickDelayMilliseconds = tickDelay,
                startMilliseconds = startMilliseconds)
    }
}