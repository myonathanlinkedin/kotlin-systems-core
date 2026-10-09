package sketch

import kotlin.random.Random

fun main() {
    // Basic correctness test
    val cms = CountMinSketch<String>(epsilon = 0.01, delta = 0.01)
    val data = listOf(
        "apple" to 50L,
        "banana" to 30L,
        "cherry" to 20L,
        "date" to 5L,
        "elderberry" to 1L
    )
    data.forEach { (item, count) ->
        repeat(count.toInt()) { cms.add(item) }
    }

    // Verify estimates are never less than true counts
    data.forEach { (item, trueCount) ->
        val est = cms.estimate(item)
        check(est >= trueCount) { "Estimate $est for $item is less than true count $trueCount" }
    }

    // Heavy hitters with threshold 20
    val hitters = cms.heavyHitters(threshold = 20L).map { it.first }
    check("apple" in hitters) { "apple should be a heavy hitter" }
    check("banana" in hitters) { "banana should be a heavy hitter" }
    check("cherry" in hitters) { "cherry should be a heavy hitter" }
    check("date" !in hitters) { "date should not be a heavy hitter" }

    // Stress test with random data
    val randomCms = CountMinSketch<Int>(epsilon = 0.005, delta = 0.001)
    val trueCounts = mutableMapOf<Int, Long>()
    repeat(100_000) {
        val key = (it % 1000)
        trueCounts[key] = (trueCounts[key] ?: 0L) + 1L
        randomCms.add(key)
    }
    // Verify a sample of keys
    for (k in listOf(0, 123, 456, 789, 999)) {
        val est = randomCms.estimate(k)
        val trueCount = trueCounts[k] ?: 0L
        check(est >= trueCount) { "Random test failed for $k: est $est < true $trueCount" }
    }

    // Simple benchmark (non‑precise timing)
    val benchCms = CountMinSketch<String>(epsilon = 0.001, delta = 0.0001)
    val start = System.nanoTime()
    repeat(1_000_000) {
        benchCms.add("item${it % 100}")
    }
    val durationMs = (System.nanoTime() - start) / 1_000_000
    println("Benchmark: 1_000_000 adds took ${durationMs}ms")
    // End of program
}