# AndroidStopwatch

Simple realisation of Stopwatch.

![demo](/images/screencast.gif)

# Install

add jitpack.io repository to the top build.gradle or settings.gradle
```groovy
repositories {
    // ...
    maven { url 'https://jitpack.io' }
}
```

add the dependency to your module build.gradle
```groovy
dependencies {
    //...
    implementation('com.github.fmaxx:AndroidStopwatch:0.0.0')
}
```

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

## Settings

Stopwatch.Builder has additional methods
`setTickDelay(milliseconds: Long)` - milliseconds between each time tick (should be more than zero)
`setStartMilliseconds(milliseconds: Long)` - milliseconds started at (should be more than zero)

```kotlin
val stopwatch = Stopwatch.Builder(scope = YOUR_SCOPE)
    .setTickDelay(20)
    .setStartMilliseconds(10_000)
    .build()
```




