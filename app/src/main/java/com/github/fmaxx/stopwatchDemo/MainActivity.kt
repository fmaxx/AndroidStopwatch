package com.github.fmaxx.stopwatchDemo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.fmaxx.stopwatch.Stopwatch
import com.github.fmaxx.stopwatch.StopwatchProcessor
import com.github.fmaxx.stopwatch.time.TimestampMillisecondsFormatter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var display: TextView
    private lateinit var stopwatch: StopwatchProcessor
    private val formatter = TimestampMillisecondsFormatter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        display = findViewById(R.id.textView)
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            stopwatch.stop()
        }
        findViewById<Button>(R.id.pauseButton).setOnClickListener {
            stopwatch.pause()
        }
        findViewById<Button>(R.id.startButton).setOnClickListener {
            stopwatch.start()
        }

        stopwatch = createStopWatch()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                stopwatch.collect(::displayMillisecond)
            }
        }
    }

    private fun displayMillisecond(value: Long) {
        display.text = formatter.format(value)
    }

    private fun createStopWatch() =
        Stopwatch.Builder(scope = lifecycleScope)
//            .setTickDelay(20)
//            .setStartMilliseconds(10_000)
            .build()
}