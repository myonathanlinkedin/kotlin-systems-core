import kotlin.math.abs
import kotlin.random.Random

/**
 * Count‑Min Sketch (CMS) for frequency estimation.
 *
 * @param T type of the items to be counted; must provide a stable hashCode().
 * @param width number of columns (w). Larger width reduces the additive error.
 * @param depth number of hash functions (d). Larger depth reduces the probability of over‑estimation.
 */
class CountMinSketch<T>(private val width: Int, private val depth: Int) {
    // 2‑D array of counters, initialized to zero.
    private val table: Array<LongArray> = Array(depth) { LongArray(width) }

    // Pairwise‑independent hash seeds, one per row.
    private val hashSeeds: LongArray = LongArray(depth) { Random.nextLong() }

    /**
     * Adds [count] occurrences of [item] to the sketch.
     * @param count must be non‑negative.
     */
    fun add(item: T, count: Long = 1L) {
        require(count >= 0L) { "count must be non‑negative" }
        val baseHash = item.hashCode().toLong()
        for (i in 0 until depth) {
            val idx = indexFor(baseHash, hashSeeds[i])
            table[i][idx] += count
        }
    }

    /**
     * Returns the estimated frequency of [item].
     * The estimate is always ≥ the true frequency and ≤ true + ε·N,
     * where ε = 2 / width and N is the total number of inserted items.
     */
    fun estimate(item: T): Long {
        val baseHash = item.hashCode().toLong()
        var min = Long.MAX_VALUE
        for (i in 0 until depth) {
            val idx = indexFor(baseHash, hashSeeds[i])
            val value = table[i][idx]
            if (value < min) min = value
        }
        return min
    }

    /**
     * Computes a column index in [0, width) using a simple 64‑bit mix.
     */
    private fun indexFor(hash: Long, seed: Long): Int {
        // 64‑bit mix based on the golden ratio constant.
        val mixed = (hash xor seed) * 0x9e3779b97f4a7c15UL.toLong()
        // Use the high 32 bits, make it positive, then modulo width.
        return ((mixed ushr 32).toInt().let { if (it < 0) -it else it }) % width
    }
}

/**
 * Heavy‑hitter wrapper that keeps a lightweight dictionary of observed items
 * to enable extraction of items whose estimated frequency exceeds a threshold.
 *
 * @param T type of the items.
 */
class CountMinSketchHeavy<T>(width: Int, depth: Int) {
    private val cms = CountMinSketch<T>(width, depth)

    // Stores the exact number of insertions per distinct item.
    // This auxiliary map is optional in a pure CMS; it is kept here
    // solely to make heavy‑hitter extraction deterministic for testing.
    private val exactCounts = mutableMapOf<T, Long>()

    /** Adds [count] occurrences of [item] to the sketch and the auxiliary map. */
    fun add(item: T, count: Long = 1L) {
        require(count >= 0L) { "count must be non‑negative" }
        cms.add(item, count)
        exactCounts[item] = (exactCounts[item] ?: 0L) + count
    }

    /** Returns the CMS estimate for [item]. */
    fun estimate(item: T): Long = cms.estimate(item)

    /**
     * Returns a list of items whose estimated frequency is at least [threshold].
     * The list is sorted descending by estimated count.
     */
    fun heavyHitters(threshold: Long): List<HeavyHit<T>> {
        require(threshold >= 0L) { "threshold must be non‑negative" }
        return exactCounts.entries
            .asSequence()
            .map { Pair(it.key, cms.estimate(it.key)) }
            .filter { it.second >= threshold }
            .sortedByDescending { it.second }
            .map { HeavyHit(it.first, it.second) }
            .toList()
    }
}

/** Simple data holder for heavy‑hitter results. */
data class HeavyHit<T>(val item: T, val estimatedCount: Long)
