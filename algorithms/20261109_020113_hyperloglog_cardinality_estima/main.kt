package main

import core.HyperLogLog
import kotlin.math.abs

fun assertApproximatelyEqual(expected: Double, actual: Double, tolerance: Double = 0.05) {
    val diff = abs(expected - actual) / expected
    check(diff <= tolerance) { "Expected $expected, got $actual (diff $diff exceeds $tolerance)" }
}

fun testEmptySketch() {
    val hll = HyperLogLog(p = 10)
    check(hll.estimate() == 0.0) { "Empty sketch should estimate 0" }
}

fun testSingleElement() {
    val hll = HyperLogLog(p = 10)
    hll.add("apple")
    val est = hll.estimate()
    assertApproximatelyEqual(1.0, est, 0.5) // high tolerance for single element
}

fun testExactSmallCardinality() {
    val hll = HyperLogLog(p = 12)
    val n = 1000
    for (i in 0 until n) {
        hll.add(i)
    }
    val est = hll.estimate()
    assertApproximatelyEqual(n.toDouble(), est, 0.1)
}

fun testMerge() {
    val h1 = HyperLogLog(p = 12)
    val h2 = HyperLogLog(p = 12)
    val set = mutableSetOf<Int>()
    for (i in 0 until 500) {
        h1.add(i)
        set.add(i)
    }
    for (i in 250 until 750) {
        h2.add(i)
        set.add(i)
    }
    h1.merge(h2)
    val est = h1.estimate()
    assertApproximatelyEqual(set.size.toDouble(), est, 0.1)
}

fun testLargeCardinality() {
    val hll = HyperLogLog(p = 14)
    val n = 200_000
    for (i in 0 until n) {
        hll.add(i)
    }
    val est = hll.estimate()
    assertApproximatelyEqual(n.toDouble(), est, 0.07)
}

fun main() {
    testEmptySketch()
    testSingleElement()
    testExactSmallCardinality()
    testMerge()
    testLargeCardinality()
    println("All HyperLogLog tests passed.")
}