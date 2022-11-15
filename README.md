# AndroidStopwatch

Simple realisation of Stopwatch.

![demo](/images/screencast.gif)

# Using

## Common case

```kotlin
// initialization
val stopwatch = Stopwatch.Builder(scope = YOUR_SCOPE).build()

// collect data with Flow
YOUR_SCOPE.launch {
    stopwatch.ticker.collect { elapsedMilliseconds ->
        println("elapsed: $elapsedMilliseconds")
    }
}
```

## Activity case

```kotlin
 override fun onCreate(savedInstanceState: Bundle?) {
    val stopwatch = Stopwatch.Builder(scope = lifecycleScope).build()
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            stopwatch.ticker.collect { elapsedMilliseconds ->
                println("elapsed: $elapsedMilliseconds")
            }
        }
    }
}
```




