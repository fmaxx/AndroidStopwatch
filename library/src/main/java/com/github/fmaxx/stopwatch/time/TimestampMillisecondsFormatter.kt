package com.github.fmaxx.stopwatch.time

/**
 * Created by Maxim Firsov on 14.10.2022.
 * firsoffmaxim@gmail.com
 */
class TimestampMillisecondsFormatter {
    companion object {
        const val DEFAULT_TIME = "00:00:000"
    }

    fun format(timestampMillis: Long): String {
        val millisecondsFormatted = (timestampMillis % 1000).pad(3)
        val seconds = timestampMillis / 1000
        val secondsFormatted = (seconds % 60).pad(2)
        val minutes = seconds / 60
        val minutesFormatted = (minutes % 60).pad(2)
        val hours = minutes / 60
        return if (hours > 0) {
            val hoursFormatted = (minutes / 60).pad(2)
            "$hoursFormatted:$minutesFormatted:$secondsFormatted"
        } else {
            "$minutesFormatted:$secondsFormatted:$millisecondsFormatted"
        }
    }

    private fun Long.pad(desiredLength: Int) =
            this.toString().padStart(desiredLength, '0')
}