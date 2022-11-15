package com.github.fmaxx.stopwatch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        test()
    }

    private fun test() {
        val test = Stopwatch.Builder(scope = lifecycleScope)
            .setTickDelay(100)
            .setStartMilliseconds(10_000)
            .build()
    }
}