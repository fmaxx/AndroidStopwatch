package com.github.fmaxx.stopwatch

import com.github.fmaxx.stopwatch.time.TimestampMillisecondsFormatter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.stream.Stream

/**
 * Created by Maxim Firsov on 31.10.2022.
 * firsoffmaxim@gmail.com
 */
internal class TimestampMillisecondsFormatterTest {

    private lateinit var sut: TimestampMillisecondsFormatter

    @BeforeEach
    fun setUp() {
        sut = TimestampMillisecondsFormatter()
    }

    @ParameterizedTest
    @ArgumentsSource(TimestampArgumentsProvider::class)
    fun `formats the timestamp`(
            timestamp: Long,
            expectedString: String
    ) {
        val result = sut.format(timestamp)
        expectThat(result).isEqualTo(expectedString)
    }

    private class TimestampArgumentsProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
            return Stream.of(
                    args(timestamp = 0, expectedString = "00:00:000"),
                    args(timestamp = 500, expectedString = "00:00:500"),
                    args(timestamp = 59999, expectedString = "00:59:999"),
                    args(timestamp = 60000, expectedString = "01:00:000"),
                    args(timestamp = 90000, expectedString = "01:30:000"),
                    args(timestamp = 3599999, expectedString = "59:59:999"),
                    args(timestamp = 3600000, expectedString = "01:00:00"),
                    args(timestamp = 86399999, expectedString = "23:59:59"),
                    args(timestamp = 86400000, expectedString = "24:00:00"),
                    args(timestamp = 86401000, expectedString = "24:00:01"),
            )
        }

        private fun args(timestamp: Long, expectedString: String): Arguments =
                Arguments.of(timestamp, expectedString)
    }
}