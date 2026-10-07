package core

import kotlin.random.Random
import kotlin.system.measureNanoTime

data class CacheTestResult(val pattern: String, val durationNs: Long)

fun generateIntArray(size: Int, initValue: Int = 0): IntArray {
    return IntArray(size) { initValue }
}

fun runSequential(array: IntArray): Long {
    var sum = 0L
    val duration = measureNanoTime {
        for (i in array.indices) {
            sum += array[i].toLong()
        }
    }
    // Prevent compiler optimizing away the loop
    check(sum >= 0L)
    return duration
}

fun runStride(array: IntArray, stride: Int): Long {
    require(stride > 0) { "Stride must be positive" }
    var sum = 0L
    val duration = measureNanoTime {
        var i = 0
        while (i < array.size) {
            sum += array[i].toLong()
            i += stride
        }
    }
    check(sum >= 0L)
    return duration
}

fun runRandom(array: IntArray, seed: Int = 42): Long {
    val rnd = Random(seed)
    var sum = 0L
    val indices = array.indices.toMutableList()
    indices.shuffle(rnd)
    val duration = measureNanoTime {
        for (i in indices) {
            sum += array[i].toLong()
        }
    }
    check(sum >= 0L)
    return duration
}

fun performCacheTests(size: Int, stride: Int, seed: Int = 42): List<CacheTestResult> {
    val array = generateIntArray(size, initValue = 1)
    val seqTime = runSequential(array)
    val strideTime = runStride(array, stride)
    val randomTime = runRandom(array, seed)
    return listOf(
        CacheTestResult("Sequential", seqTime),
        CacheTestResult("Stride_$stride", strideTime),
        CacheTestResult("Random", randomTime)
    )
}