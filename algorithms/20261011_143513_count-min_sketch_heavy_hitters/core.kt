package core

import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.max
import kotlin.random.Random

/**
 * Count‑Min Sketch (CMS) implementation for frequency estimation.
 *
 * The sketch provides:
 *  - add(item, count): increment the count of an item.
 *  - estimate(item): return an upper‑bound estimate of the item's frequency.
 *  - heavyHitters(threshold): return all items whose estimated frequency
 *    meets or exceeds the given threshold.
 *
 * The implementation uses pairwise‑independent hash functions of the form
 *      h_i(x) = ((a_i * x + b_i) mod PRIME) mod width
 * with fixed seeds generated from a deterministic Random source.
 *
 * @param epsilon Desired error factor (relative to total count). Guarantees
 *                over‑estimation error ≤ ε·N with probability 1‑δ.
 * @param delta   Desired failure probability.
 */
class CountMinSketch(epsilon: Double = 0.01, delta: Double = 0.001) {
    // Width w = ceil(e / ε)
    val width: Int = ceil(Math.E / epsilon).toInt()
    // Depth d = ceil(ln(1/δ))
    val depth: Int = ceil(-ln(delta)).toInt()

    // 2‑D table of counters
    private val table: Array<LongArray> = Array(depth) { LongArray(width) }

    // Hash function parameters (a_i, b_i)
    private val hashA: LongArray = LongArray(depth)
    private val hashB: LongArray = LongArray(depth)

    // Large prime > width for universal hashing
    private val PRIME: Long = 0xFFFFFFFFFFFFFUL.toLong() // 2^52‑1, fits in Long

    // Set of distinct items observed – required for heavy‑hitter extraction
    private val observedItems: MutableSet<String> = mutableSetOf()

    init {
        // Deterministic generation of hash parameters
        val rng = Random(0xC0FFEE) // fixed seed for reproducibility
        for (i in 0 until depth) {
            // a must be non‑zero; b can be any value in [0, PRIME)
            var a = rng.nextLong(1L, PRIME)
            var b = rng.nextLong(0L, PRIME)
            // Ensure positivity
            if (a < 0L) a = -a
            if (b < 0L) b = -b
            hashA[i] = a
            hashB[i] = b
        }
    }

    /**
     * Increment the count of [item] by [count] (default 1).
     *
     * @throws IllegalArgumentException if count ≤ 0.
     */
    fun add(item: String, count: Long = 1L) {
        require(count > 0L) { "count must be positive" }
        observedItems.add(item)
        val x = item.hashCode().toLong() and 0xffffffffL
        for (i in 0 until depth) {
            val idx = ((hashA[i] * x + hashB[i]) % PRIME % width).toInt()
            table[i][idx] = table[i][idx] + count
        }
    }

    /**
     * Return the CMS estimate for the frequency of [item].
     * The value is guaranteed to be ≥ the true frequency.
     */
    fun estimate(item: String): Long {
        val x = item.hashCode().toLong() and 0xffffffffL
        var min = Long.MAX_VALUE
        for (i in 0 until depth) {
            val idx = ((hashA[i] * x + hashB[i]) % PRIME % width).toInt()
            val v = table[i][idx]
            if (v < min) min = v
        }
        return min
    }

    /**
     * Retrieve all items whose estimated frequency is at least [threshold].
     *
     * The method scans the internal set of observed items; therefore it
     * requires O(number of distinct items) time.
     *
     * @return List of pairs (item, estimatedCount) sorted descending by count.
     */
    fun heavyHitters(threshold: Long): List<Pair<String, Long>> {
        require(threshold >= 0L) { "threshold must be non‑negative" }
        val result = mutableListOf<Pair<String, Long>>()
        for (item in observedItems) {
            val est = estimate(item)
            if (est >= threshold) {
                result.add(Pair(item, est))
            }
        }
        // Sort for deterministic test results
        result.sortWith(compareByDescending<Pair<String, Long>> { it.second }.thenBy { it.first })
        return result
    }

    /**
     * Expose the set of distinct items seen so far (read‑only).
     */
    fun distinctItems(): Set<String> = observedItems.toSet()
}