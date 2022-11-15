package com.github.fmaxx.stopwatch.time

/**
 * Created by Maxim Firsov on 14.11.2022.
 * firsoffmaxim@gmail.com
 */
class TimestampProviderImpl : TimestampProvider {
    override val milliseconds: Long
        get() = System.currentTimeMillis()
}