package com.github.fmaxx.stopwatch.state

/**
 * Created by Maxim Firsov on 14.10.2022.
 * firsoffmaxim@gmail.com
 */
sealed interface State
data class Paused(val elapsedTime: Long) : State
data class Running(val startTime: Long, val elapsedTime: Long) : State