package hll

import kotlin.math.*

fun main() {
    // Basic sanity check: empty estimator should give 0
    val empty = HyperLogLog(precision = 10)
    check(empty.estimate() == 0.0)

    // Test with known cardinalities
    val hll = HyperLogLog(precision = 12) // m = 4096 registers
    var n = 100_000
    for (i in 0 until n) {
        hll.add(i)
    }
    val estimate = hll.estimate()
    val error = kotlin.math.abs(estimate - n) / n
    // HyperLogLog typical error < 2%
    check(error < 0.05) { "Error $error exceeds tolerance for n=$n" }

    // Test merging two estimators
    val hllA = HyperLogLog(precision = 12)
    val hllB = HyperLogLog(precision = 12)
    val setA = (0 until 50_000).toList()
    val setB = (30_000 until 80_000).toList() // overlap of 20_000
    for (v in setA) hllA.add(v)
    for (v in setB) hllB.add(v)

    hllA.merge(hllB)
    val mergedEstimate = hllA.estimate()
    val trueCardinality = 80_000 // union size
    val mergedError = kotlin.math.abs(mergedEstimate - trueCardinality) / trueCardinality
    check(mergedError < 0.07) { "Merged error $mergedError exceeds tolerance" }

    // Edge case: single element
    val single = HyperLogLog(precision = 8)
    single.add("only")
    val singleEst = single.estimate()
    check(singleEst >= 0.5 && singleEst <= 2.0) { "Single element estimate out of range: $singleEst" }

    // Demonstration output
    println("All HyperLogLog tests passed.")
    println("Estimated cardinality for $n distinct ints: ${"%.2f".format(estimate)} (error ${"%.2f".format(error * 100)}%)")
    println("Merged estimator union size estimate: ${"%.2f".format(mergedEstimate)} (error ${"%.2f".format(mergedError * 100)}%)")
}