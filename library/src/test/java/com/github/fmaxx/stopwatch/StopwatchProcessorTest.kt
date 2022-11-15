package com.github.fmaxx.stopwatch

import com.github.fmaxx.stopwatch.time.TimestampProviderImpl
import com.github.fmaxx.stopwatch.state.StateCalculator
import com.github.fmaxx.stopwatch.state.StateHolder
import com.github.fmaxx.stopwatch.time.ElapsedTime
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

/**
 * Created by Maxim Firsov on 31.10.2022.
 * firsoffmaxim@gmail.com
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class StopwatchProcessorTest {

    private val stateHolder: StateHolder = mockk(relaxed = true) {
        every { elapsedTimeMillis } returns 0
    }

    @Test
    fun `Initially the stopwatch value is zero`() =
            runTest {
                val sut = processor {
                    scope(testScheduler)
                }
                assertEquals(0, sut.ticker.first())
            }

    @Test
    fun `After starting a stopwatch its value is emitted`() =
            runTest {
                givenStateHolderReturnsTime(0)
                val sut = processor {
                    scope(testScheduler)
                }
                sut.start()
                runCurrentBy(1)
                val result = sut.ticker.first()
                expectThat(result).isEqualTo(0)
                sut.stop()
            }

    @Test
    fun `When a stopwatch is running its value updated accordingly`() =
            runTest {
                givenStateHolderReturnsTime(0, 1, 2)
                val sut = processor {
                    scope(testScheduler)
                }
                sut.start()
                runCurrentBy(5)
                expectThat(sut.ticker.first()).isEqualTo(2)
                sut.stop()
            }

    @Test
    fun `When a stopwatch is stopped the value is zero`() =
            runTest {
                givenStateHolderReturnsTime(0)
                val sut = processor {
                    scope(testScheduler)
                }
                sut.start()
                runCurrentBy(1)
                sut.stop()
                runCurrentBy(1)
                val result = sut.ticker.first()
                assertEquals(0, result)
            }

    @Test
    fun `When started with startMilliseconds`() =
            runTest {
                val startMilliseconds = 1_000L
                val sut = processor(
                        startMilliseconds = startMilliseconds,
                        stateHolder = holder(
                                startMilliseconds,
                                1_001, 1_002, 1_003
                        )
                ) {
                    scope(testScheduler)
                }
                sut.start()
                runCurrentBy(10)
                assertEquals(sut.ticker.first(), 1_003)
                sut.stop()
                assertEquals(sut.ticker.first(), 0)
            }

    private fun TestScope.runCurrentBy(delayTimeMillis: Long) {
        advanceTimeBy(delayTimeMillis)
        runCurrent()
    }

    private fun processor(
            tickDelayMs: Long = 1,
            startMilliseconds: Long = 0,
            stateHolder: StateHolder? = null,
            scopeBlock: () -> CoroutineScope): StopwatchProcessor =
            StopwatchProcessor(
                    holder = stateHolder ?: this.stateHolder,
                    scope = scopeBlock(),
                    tickDelayMilliseconds = tickDelayMs,
                    startMilliseconds = startMilliseconds
            )

    private fun holder(milliseconds: Long,
                       vararg timeValue: Long): StateHolder {
        val timestampProvider = TimestampProviderImpl()
        val elapsedTime = ElapsedTime(timestampProvider)
        return spyk(
                StateHolder(
                        StateCalculator(timestampProvider, elapsedTime),
                        elapsedTime,
                        mockk(),
                        milliseconds
                )) {
            every { elapsedTimeMillis } returnsMany timeValue.toList()
        }
    }

    private fun scope(scheduler: TestCoroutineScheduler): CoroutineScope =
            CoroutineScope(StandardTestDispatcher(scheduler))

    private fun givenStateHolderReturnsTime(vararg timeValue: Long) =
            every { stateHolder.elapsedTimeMillis } returnsMany timeValue.toList()
}