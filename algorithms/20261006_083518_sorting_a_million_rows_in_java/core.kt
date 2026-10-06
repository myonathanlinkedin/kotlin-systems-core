package main

import kotlin.random.Random

/**
 * Core sorting and profiling utilities.
 * Implements a production-grade QuickSort with median-of-three pivot selection
 * and insertion sort for small partitions to optimize cache locality and reduce
 * recursion overhead.
 */
object SortEngine {

    /**
     * Sorts a list of Integers in-place using an optimized QuickSort algorithm.
     *
     * @param data The list to sort.
     * @param random Optional random generator for tie-breaking or testing.
     */
    fun quickSort(data: MutableList<Int>, random: Random = Random.Default) {
        if (data.size < 2) return
        quickSortRecursive(data, 0, data.size - 1, random)
    }

    private fun quickSortRecursive(data: MutableList<Int>, low: Int, high: Int, random: Random) {
        // Use insertion sort for small partitions to minimize overhead
        if (high - low < 16) {
            insertionSort(data, low, high)
            return
        }

        val pivotIndex = partition(data, low, high, random)
        if (pivotIndex > low) {
            quickSortRecursive(data, low, pivotIndex - 1, random)
        }
        if (pivotIndex < high) {
            quickSortRecursive(data, pivotIndex + 1, high, random)
        }
    }

    /**
     * Partitions the array around a pivot chosen via median-of-three.
     * Returns the final index of the pivot.
     */
    private fun partition(data: MutableList<Int>, low: Int, high: Int, random: Random): Int {
        // Median of three to avoid worst-case O(n^2) on sorted/reverse-sorted data
        val mid = low + (high - low) / 2
        if (data[low] > data[mid]) swap(data, low, mid)
        if (data[low] > data[high]) swap(data, low, high)
        if (data[mid] > data[high]) swap(data, mid, high)
        
        // Move pivot to high-1
        swap(data, mid, high - 1)
        val pivot = data[high - 1]

        var i = low
        var j = high - 1

        while (true) {
            while (data[++i] < pivot) {
                if (i == high - 1) break
            }
            while (data[--j] > pivot) {
                if (j == low) break
            }
            if (i >= j) break
            swap(data, i, j)
        }

        // Restore pivot
        swap(data, i, high - 1)
        return i
    }

    private fun insertionSort(data: MutableList<Int>, low: Int, high: Int) {
        for (i in low + 1..high) {
            val key = data[i]
            var j = i - 1
            while (j >= low && data[j] > key) {
                data[j + 1] = data[j]
                j--
            }
            data[j + 1] = key
        }
    }

    private fun swap(data: MutableList<Int>, i: Int, j: Int) {
        if (i != j) {
            val temp = data[i]
            data[i] = data[j]
            data[j] = temp
        }
    }

    /**
     * Generates a list of random integers.
     */
    fun generateRandomData(size: Int, seed: Long = 42L): MutableList<Int> {
        val random = Random(seed)
        return MutableList(size) { random.nextInt() }
    }

    /**
     * Generates a list of sorted integers.
     */
    fun generateSortedData(size: Int): MutableList<Int> {
        return MutableList(size) { it }
    }

    /**
     * Generates a list of reverse-sorted integers.
     */
    fun generateReverseSortedData(size: Int): MutableList<Int> {
        return MutableList(size) { size - 1 - it }
    }

    /**
     * Generates a list with many duplicate values.
     */
    fun generateDuplicateData(size: Int, uniqueCount: Int = 10): MutableList<Int> {
        val random = Random(123L)
        return MutableList(size) { random.nextInt(uniqueCount) }
    }
}

/**
 * Utility for measuring execution time in nanoseconds.
 */
object Profiler {
    fun measureTimeNanos(block: () -> Unit): Long {
        val start = System.nanoTime()
        block()
        val end = System.nanoTime()
        return end - start
    }
}