package main

import core.OptimisticList
import kotlin.random.Random
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch

fun testSingleThread() {
    val list = OptimisticList()
    check(list.add(10))
    check(!list.add(10))
    check(list.contains(10))
    check(list.remove(10))
    check(!list.contains(10))
    check(!list.remove(10))
    // Insert multiple keys
    for (i in 0 until 20) {
        check(list.add(i * 5))
    }
    // Verify ordering
    val snapshot = list.toList()
    check(snapshot.size == 20)
    for (i in 0 until 19) {
        check(snapshot[i] < snapshot[i + 1])
    }
    // Remove some keys
    for (i in 0 until 20 step 2) {
        check(list.remove(i * 5))
    }
    val afterRemoval = list.toList()
    check(afterRemoval.size == 10)
    for (i in 0 until afterRemoval.size - 1) {
        check(afterRemoval[i] < afterRemoval[i + 1])
    }
}

fun testConcurrent() {
    val list = OptimisticList()
    val netCounts = ConcurrentHashMap<Int, Int>()
    val threadCount = 4
    val opsPerThread = 1000
    val latch = CountDownLatch(threadCount)

    repeat(threadCount) {
        Thread {
            repeat(opsPerThread) {
                val key = Random.nextInt(0, 100)
                when (Random.nextInt(0, 3)) {
                    0 -> { // add
                        if (list.add(key)) {
                            netCounts.merge(key, 1) { a, b -> a + b }
                        }
                    }
                    1 -> { // remove
                        if (list.remove(key)) {
                            netCounts.merge(key, -1) { a, b -> a + b }
                        }
                    }
                    else -> { // contains (no effect on netCounts)
                        list.contains(key)
                    }
                }
            }
            latch.countDown()
        }.start()
    }

    latch.await()

    // Build expected set from netCounts (positive net means present)
    val expected = netCounts.filter { it.value > 0 }.keys

    // Verify list contains exactly the expected keys
    val actual = list.toList().toSet()
    check(actual == expected)

    // Verify ordering and uniqueness
    val sorted = list.toList()
    for (i in 0 until sorted.size - 1) {
        check(sorted[i] < sorted[i + 1])
    }
    // Ensure no duplicates
    check(sorted.toSet().size == sorted.size)
}

fun main() {
    testSingleThread()
    testConcurrent()
    println("All tests passed.")
}