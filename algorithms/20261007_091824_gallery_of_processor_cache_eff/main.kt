package main

import core.performCacheTests
import core.CacheTestResult
import kotlin.math.abs

fun testArrayGeneration() {
    val size = 10
    val arr = core.generateIntArray(size, initValue = 5)
    check(arr.size == size)
    for (v in arr) {
        check(v == 5)
    }
}

fun testSequentialTiming() {
    val size = 1_000
    val arr = core.generateIntArray(size, initValue = 1)
    val duration = core.runSequential(arr)
    check(duration > 0L)
}

fun testStrideTiming() {
    val size = 1_000
    val stride = 4
    val arr = core.generateIntArray(size, initValue = 1)
    val duration = core.runStride(arr, stride)
    check(duration > 0L)
}

fun testRandomTiming() {
    val size = 1_000
    val arr = core.generateIntArray(size, initValue = 1)
    val duration = core.runRandom(arr, seed = 123)
    check(duration > 0L)
}

fun testResultOrdering() {
    val size = 10_000
    val stride = 8
    val results = performCacheTests(size, stride)
    // Ensure we have three distinct results
    check(results.size == 3)
    // Simple sanity: durations should be positive
    for (res in results) {
        check(res.durationNs > 0L)
    }
    // Sequential should not be slower than stride by more than an order of magnitude in typical runs
    val seq = results.first { it.pattern == "Sequential" }.durationNs
    val strideRes = results.first { it.pattern.startsWith("Stride") }.durationNs
    check(abs(seq - strideRes) < seq * 10L) // loosely bound
}

fun runAllTests() {
    testArrayGeneration()
    testSequentialTiming()
    testStrideTiming()
    testRandomTiming()
    testResultOrdering()
    println("All tests passed.")
}

fun printSampleResults() {
    val sizes = listOf(10_000, 100_000, 1_000_000)
    val stride = 16
    for (size in sizes) {
        val results = performCacheTests(size, stride)
        println("Array size: $size")
        for (res in results) {
            println("  ${res.pattern}: ${res.durationNs} ns")
        }
    }
}

fun main() {
    runAllTests()
    printSampleResults()
}