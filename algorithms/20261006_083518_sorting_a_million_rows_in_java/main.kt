package main

import kotlin.random.Random

/**
 * Unit tests and benchmarks for the sorting engine.
 */
fun main() {
    println("=== Sorting Engine Test Suite ===")
    
    // Unit Tests
    testEmptyList()
    testSingleElement()
    testTwoElements()
    testAlreadySorted()
    testReverseSorted()
    testDuplicates()
    testRandomData()
    testLargeRandomData()
    
    // Benchmarks
    runBenchmarks()
    
    println("=== All Tests Passed ===")
}

private fun testEmptyList() {
    val data = mutableListOf<Int>()
    SortEngine.quickSort(data)
    check(data.isEmpty()) { "Empty list should remain empty" }
}

private fun testSingleElement() {
    val data = mutableListOf(42)
    SortEngine.quickSort(data)
    check(data == listOf(42)) { "Single element list should remain unchanged" }
}

private fun testTwoElements() {
    val data = mutableListOf(2, 1)
    SortEngine.quickSort(data)
    check(data == listOf(1, 2)) { "Two elements should be sorted" }
}

private fun testAlreadySorted() {
    val data = mutableListOf(1, 2, 3, 4, 5)
    SortEngine.quickSort(data)
    check(data == listOf(1, 2, 3, 4, 5)) { "Already sorted list should remain sorted" }
}

private fun testReverseSorted() {
    val data = mutableListOf(5, 4, 3, 2, 1)
    SortEngine.quickSort(data)
    check(data == listOf(1, 2, 3, 4, 5)) { "Reverse sorted list should be sorted" }
}

private fun testDuplicates() {
    val data = mutableListOf(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5)
    SortEngine.quickSort(data)
    val expected = data.sorted()
    check(data == expected) { "List with duplicates should be sorted correctly" }
}

private fun testRandomData() {
    val data = SortEngine.generateRandomData(1000, seed = 42L)
    val expected = data.sorted()
    SortEngine.quickSort(data)
    check(data == expected) { "Random data should be sorted correctly" }
}

private fun testLargeRandomData() {
    val data = SortEngine.generateRandomData(10000, seed = 123L)
    val expected = data.sorted()
    SortEngine.quickSort(data)
    check(data == expected) { "Large random data should be sorted correctly" }
}

private fun runBenchmarks() {
    val sizes = listOf(1000, 10000, 100000, 1000000)
    
    println("\n=== Benchmarks ===")
    println("Size\t\tTime (ms)\tOps/sec")
    
    for (size in sizes) {
        val data = SortEngine.generateRandomData(size, seed = 42L)
        
        val timeNanos = Profiler.measureTimeNanos {
            SortEngine.quickSort(data)
        }
        
        val timeMs = timeNanos / 1_000_000.0
        val opsPerSec = size / (timeNanos / 1_000_000_000.0)
        
        println("$size\t\t${"%.2f".format(timeMs)}\t\t${"%.0f".format(opsPerSec)}")
        
        // Verify correctness
        val expected = data.sorted()
        check(data == expected) { "Benchmark data should be sorted correctly" }
    }
    
    // Test worst-case scenarios
    println("\n=== Worst-Case Scenarios ===")
    
    val sortedData = SortEngine.generateSortedData(100000)
    val sortedTime = Profiler.measureTimeNanos {
        SortEngine.quickSort(sortedData)
    }
    println("Sorted (100k):\t${"%.2f".format(sortedTime / 1_000_000.0)} ms")
    
    val reverseData = SortEngine.generateReverseSortedData(100000)
    val reverseTime = Profiler.measureTimeNanos {
        SortEngine.quickSort(reverseData)
    }
    println("Reverse (100k):\t${"%.2f".format(reverseTime / 1_000_000.0)} ms")
    
    val dupData = SortEngine.generateDuplicateData(100000, uniqueCount = 10)
    val dupTime = Profiler.measureTimeNanos {
        SortEngine.quickSort(dupData)
    }
    println("Duplicates (100k):\t${"%.2f".format(dupTime / 1_000_000.0)} ms")
}