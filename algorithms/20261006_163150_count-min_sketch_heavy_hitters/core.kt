package sketch

import kotlin.math.ceil
import kotlin.math.ln
import kotlin.random.Random

class CountMinSketch<T>(
    epsilon: Double,
    delta: Double
) {
    private val width: Int = ceil(Math.E / epsilon).toInt()
    private val depth: Int = ceil(ln(1.0 / delta)).toInt()
    private val table: Array<LongArray> = Array(depth) { LongArray(width) }
    private val hashSeeds: IntArray = IntArray(depth) { Random.nextInt() }
    private val observed: MutableSet<T> = mutableSetOf()

    fun add(item: T, increment: Long = 1L) {
        require(increment >= 0L) { "increment must be non‑negative" }
        observed.add(item)
        val hash = item.hashCode()
        for (i in 0 until depth) {
            val idx = ((hash xor hashSeeds[i]).toLong() and 0xffffffffL % width).toInt()
            table[i][idx] = table[i][idx] + increment
        }
    }

    fun estimate(item: T): Long {
        val hash = item.hashCode()
        var min = Long.MAX_VALUE
        for (i in 0 until depth) {
            val idx = ((hash xor hashSeeds[i]).toLong() and 0xffffffffL % width).toInt()
            val value = table[i][idx]
            if (value < min) min = value
        }
        return min
    }

    fun heavyHitters(threshold: Long): List<Pair<T, Long>> {
        require(threshold >= 0L) { "threshold must be non‑negative" }
        return observed.map { it to estimate(it) }
            .filter { it.second >= threshold }
            .sortedByDescending { it.second }
    }
}