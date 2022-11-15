package com.github.fmaxx.stopwatch

import com.github.fmaxx.stopwatch.state.Paused
import com.github.fmaxx.stopwatch.state.StateCalculator
import com.github.fmaxx.stopwatch.state.StateHolder
import com.github.fmaxx.stopwatch.time.ElapsedTime
import com.github.fmaxx.stopwatch.time.TimestampMillisecondsFormatter
import com.github.fmaxx.stopwatch.time.TimestampProvider
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

/**
 * Created by Maxim Firsov on 31.10.2022.
 * firsoffmaxim@gmail.com
 */
internal class StateHolderTest {
    private val timestampProvider: TimestampProvider = mockk()
    private val elapsedTime = ElapsedTime(timestampProvider)
    private val stateCalculator = StateCalculator(
            timestampProvider = timestampProvider,
            elapsedTime = elapsedTime
    )
    private val timestampMillisecondsFormatter = TimestampMillisecondsFormatter()
    private lateinit var sut: StateHolder

    @BeforeEach
    fun setUp() {
        sut = StateHolder(
                stateCalculator = stateCalculator,
                elapsedTime = elapsedTime,
                formatter = timestampMillisecondsFormatter
        )
    }

    @Nested
    inner class StartedState {
        @Test
        fun `correctly formatted after no time passes`() {
            zeroTimePasses()
            sut.start()
            val result = sut.elapsedTimeString
            expectThat(result).isEqualTo("00:00:000")
        }

        @Test
        fun `correctly formatted after some time passes from start`() {
            timePassesAfterStart(49999)
            sut.start()
            val result = sut.elapsedTimeString
            expectThat(result).isEqualTo("00:49:999")
        }
    }

    @Nested
    inner class PausedState {
        @Test
        fun `correctly formatted after no time passes`() {
            zeroTimePasses()
            sut.start()
            sut.pause()
            val result = sut.elapsedTimeString
            expectThat(result).isEqualTo("00:00:000")
        }

        @Test
        fun `correctly formatted after some time passes from start`() {
            timePassesAfterStart(49999)
            sut.start()
            sut.pause()
            val result = sut.elapsedTimeString
            expectThat(result).isEqualTo("00:49:999")
        }
    }

    @Test
    fun `test startMilliseconds`() {
        val startMilliseconds = 1_000L
        val sut = StateHolder(
                startMilliseconds = startMilliseconds,
                stateCalculator = stateCalculator,
                elapsedTime = elapsedTime,
                formatter = timestampMillisecondsFormatter
        )
        assertEquals(Paused(startMilliseconds), sut.state)
        timePassesAfterStart(5_000)
        sut.start()
        sut.pause()
        val result = sut.elapsedTimeMillis
        expectThat(result).isEqualTo(6_000)
    }

    private fun zeroTimePasses() {
        every { timestampProvider.milliseconds } returns 0
    }

    private fun timePassesAfterStart(amount: Long) {
        every { timestampProvider.milliseconds }
                .returnsMany(listOf(0L, amount))
    }
}