package main

import core.CountMinSketch
import kotlin.math.abs
import kotlin.math.max

/**
 * Simple deterministic unit‑test harness using Kotlin's standard `check` function.
 * No external testing libraries are used.
 */
fun main() {
    // Test parameters
    val epsilon = 0.02   // 2 % error bound
    val delta = 0.001    // 99.9 % confidence
    val cms = CountMinSketch(epsilon, delta)

    // Ground‑truth frequencies
    val trueCounts = mutableMapOf<String, Long>()
    fun record(item: String, inc: Long = 1L) {
        trueCounts[item] = (trueCounts[item] ?: 0L) + inc
        cms.add(item, inc)
    }

    // Populate sketch with a known distribution
    repeat(10_000) { record("apple") }          // heavy hitter
    repeat(5_000) { record("banana") }          // medium
    repeat(1_000) { record("cherry") }          // low
    repeat(500) { record("date") }              // lower
    repeat(200) { record("elderberry") }        // very low
    // Add some noise items
    repeat(3_000) { record("noise${it % 30}") }

    // Total number of insertions
    val total = trueCounts.values.sum()

    // 1️⃣ Verify that estimates are never below true counts
    for ((item, trueCount) in trueCounts) {
        val est = cms.estimate(item)
        check(est >= trueCount) {
            "Estimate for $item is $est, which is below true count $trueCount"
        }
    }

    // 2️⃣ Verify the error bound: est ≤ true + ε·N
    val allowedError = (epsilon * total).toLong()
    for ((item, trueCount) in trueCounts) {
        val est = cms.estimate(item)
        var diff = est - trueCount
        check(diff <= allowedError) {
            "Estimate for $item exceeds allowed error: diff=$diff, allowed=$allowedError"
        }
    }

    // 3️⃣ Heavy‑hitter extraction test
    val threshold = (0.04 * total).toLong() // 4 % of total ≈ 6000
    val heavy = cms.heavyHitters(threshold).map { it.first }.toSet()
    // Expected heavy hitters: only "apple" (10 k) and possibly "banana" (5 k) if threshold low enough
    val expectedHeavy = mutableSetOf<String>()
    if (trueCounts["apple"]!! >= threshold) expectedHeavy.add("apple")
    if (trueCounts["banana"]!! >= threshold) expectedHeavy.add("banana")
    check(heavy == expectedHeavy) {
        "Heavy hitters mismatch. Expected $expectedHeavy, got $heavy"
    }

    // 4️⃣ Edge‑case: query unseen item
    val unseen = "fig"
    val estUnseen = cms.estimate(unseen)
    check(estUnseen == 0L) {
        "Estimate for unseen item should be 0, got $estUnseen"
    }

    // 5️⃣ Stress test: add a large count in a single call
    val hugeItem = "grapefruit"
    val hugeCount = 1_000_000L
    cms.add(hugeItem, hugeCount)
    val estHuge = cms.estimate(hugeItem)
    check(estHuge >= hugeCount) {
        "Huge count estimate too low: $estHuge < $hugeCount"
    }
    check(estHuge - hugeCount <= allowedError) {
        "Huge count error exceeds bound: diff=${estHuge - hugeCount}, allowed=$allowedError"
    }

    // 6️⃣ Verify distinctItems reflects all inserted keys
    val distinct = cms.distinctItems()
    check(distinct.containsAll(trueCounts.keys)) {
        "distinctItems missing some keys"
    }
    check(distinct.contains(hugeItem) && distinct.contains(unseen).not()) {
        "distinctItems incorrect for added/absent items"
    }

    // If we reach this point, all checks passed.
    println("All Count‑Min Sketch tests passed successfully.")
}