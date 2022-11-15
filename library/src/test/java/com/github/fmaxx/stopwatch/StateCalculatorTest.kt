package com.github.fmaxx.stopwatch

import com.github.fmaxx.stopwatch.*
import com.github.fmaxx.stopwatch.time.ElapsedTime
import com.github.fmaxx.stopwatch.state.*
import com.github.fmaxx.stopwatch.time.TimestampMillisecondsFormatter
import com.github.fmaxx.stopwatch.time.TimestampProvider
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.stream.Stream

class StateCalculatorTest {

    private val timestampProvider: TimestampProvider = mockk()
    private val elapsedTime = ElapsedTime(timestampProvider)
    private var stateCalculator: StateCalculator = StateCalculator(
            timestampProvider = timestampProvider,
            elapsedTime = elapsedTime
    )
    private val formatter = TimestampMillisecondsFormatter()
    private lateinit var sut: StateHolder

    @BeforeEach
    fun setUp() {
        sut = StateHolder(
                stateCalculator = stateCalculator,
                elapsedTime = elapsedTime,
                formatter = formatter
        )
    }

    @ParameterizedTest
    @ArgumentsSource(ToRunningStateArgumentsProvider::class)
    fun `Correctly calculates running state`(
            startingTimestamp: Long,
            oldState: State,
            expectedState: Running,
    ) {
        every { timestampProvider.milliseconds } returns startingTimestamp
        val result = stateCalculator.calculateRunningState(oldState)
        expectThat(result).isEqualTo(expectedState)
    }

    class ToRunningStateArgumentsProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> =
                Stream.of(
                        args(
                                startingTimestamp = 0,
                                oldState = Paused(elapsedTime = 0),
                                expectedState = Running(
                                        startTime = 0,
                                        elapsedTime = 0,
                                )
                        ),
                        args(
                                startingTimestamp = 1000,
                                oldState = Paused(elapsedTime = 200),
                                expectedState = Running(
                                        startTime = 1000,
                                        elapsedTime = 200,
                                )
                        ),
                        args(
                                startingTimestamp = 0,
                                oldState = Running(
                                        startTime = 1000,
                                        elapsedTime = 200,
                                ),
                                expectedState = Running(
                                        startTime = 1000,
                                        elapsedTime = 200,
                                )
                        )
                )

        private fun args(
                startingTimestamp: Long,
                oldState: State,
                expectedState: Running,
        ) = Arguments.of(startingTimestamp, oldState, expectedState)
    }

    @ParameterizedTest
    @ArgumentsSource(ToPausedStateArgumentsProvider::class)
    fun `Correctly calculates paused state`(
            currentTimestamp: Long,
            oldState: State,
            expectedState: Paused,
    ) {
        every { timestampProvider.milliseconds } returns currentTimestamp
        val result = stateCalculator.calculatePausedState(oldState)
        expectThat(result).isEqualTo(expectedState)
    }

    class ToPausedStateArgumentsProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
            return Stream.of(
                    args(
                            currentTimestamp = 1500,
                            oldState = Running(
                                    startTime = 1000,
                                    elapsedTime = 0,
                            ),
                            expectedState = Paused(
                                    elapsedTime = (1500 - 1000)
                            )
                    ),
                    args(
                            currentTimestamp = 1500,
                            oldState = Running(
                                    startTime = 1000,
                                    elapsedTime = 300,
                            ),
                            expectedState = Paused(
                                    elapsedTime = (1500 - 1000 + 300)
                            )
                    ),
                    args(
                            currentTimestamp = 500,
                            oldState = Running(
                                    startTime = 1000,
                                    elapsedTime = 300,
                            ),
                            expectedState = Paused(
                                    elapsedTime = 300
                            )
                    ),
                    args(
                            currentTimestamp = 500,
                            oldState = Running(
                                    startTime = 1000,
                                    elapsedTime = 0,
                            ),
                            expectedState = Paused(
                                    elapsedTime = 0
                            )
                    ),
                    args(
                            currentTimestamp = 500,
                            oldState = Paused(
                                    elapsedTime = 200,
                            ),
                            expectedState = Paused(
                                    elapsedTime = 200,
                            ),
                    )
            )
        }

        private fun args(
                currentTimestamp: Long,
                oldState: State,
                expectedState: Paused,
        ) = Arguments.of(currentTimestamp, oldState, expectedState)
    }

    @Test
    fun `timestampProvider test`() {
        val timestampProvider: TimestampProvider = mockk()
        val elapsedTime = ElapsedTime(timestampProvider)
        val stateCalculator = StateCalculator(
                timestampProvider = timestampProvider,
                elapsedTime = elapsedTime
        )

        every { timestampProvider.milliseconds } returns 0
        val initialState = Paused(0L)
        val firstStart = stateCalculator.calculateRunningState(initialState)
        expectThat(firstStart.startTime).isEqualTo(0L)
        expectThat(firstStart.elapsedTime).isEqualTo(0L)

        every { timestampProvider.milliseconds } returns 100
        val firstPause = stateCalculator.calculatePausedState(firstStart)
        expectThat(firstPause.elapsedTime).isEqualTo(100L)

        every { timestampProvider.milliseconds } returns 1000
        val secondStart = stateCalculator.calculateRunningState(firstPause)
        expectThat(secondStart.startTime).isEqualTo(1000L)
        expectThat(secondStart.elapsedTime).isEqualTo(100L)

        every { timestampProvider.milliseconds } returns 1500
        val secondPause = stateCalculator.calculatePausedState(secondStart)
        expectThat(secondPause.elapsedTime).isEqualTo(600L)
    }
}