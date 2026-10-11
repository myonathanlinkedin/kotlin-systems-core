package core

import kotlin.math.abs
import kotlin.math.min
import kotlin.math.max

/**
 * Represents a product spin system of n binary spins.
 * Each spin takes value +1 with probability probs[i] and -1 with probability 1 - probs[i].
 */
data class SpinSystem(val probs: DoubleArray) {
    init {
        // Validate probabilities are within [0,1]
        for (p in probs) {
            assert(p >= 0.0 && p <= 1.0) { "Probability $p out of range [0,1]" }
        }
    }

    /** Number of spins */
    val size: Int = probs.size
}

/**
 * Exact total variation distance between two product spin systems.
 * Enumerates all 2^n configurations; feasible only for small n (≤20).
 *
 * TV(P,Q) = 0.5 * Σ_x |P(x) - Q(x)|
 *          = 1 - Σ_x min(P(x), Q(x))
 */
fun tvDistanceExact(a: SpinSystem, b: SpinSystem): Double {
    require(a.size == b.size) { "Spin systems must have the same size" }
    val n = a.size
    if (n == 0) return 0.0

    // Ensure n is not too large for exhaustive enumeration
    assert(n <= 20) { "Exact TV distance only supported for n ≤ 20 (got $n)" }

    var overlap = 0.0
    val totalConfigs = 1 shl n
    for (mask in 0 until totalConfigs) {
        var probA = 1.0
        var probB = 1.0
        for (i in 0 until n) {
            val spinIsPlus = (mask shr i) and 1 == 1
            val pA = if (spinIsPlus) a.probs[i] else 1.0 - a.probs[i]
            val pB = if (spinIsPlus) b.probs[i] else 1.0 - b.probs[i]
            probA *= pA
            probB *= pB
        }
        overlap += min(probA, probB)
    }
    val tv = 1.0 - overlap
    // Clamp due to floating‑point rounding
    return max(0.0, min(1.0, tv))
}

/**
 * Deterministic approximation (upper bound) of the total variation distance.
 * Uses the inequality TV(P,Q) ≤ 0.5 * Σ_i |p_i - q_i| and caps the result at 1.
 */
fun tvDistanceBound(a: SpinSystem, b: SpinSystem): Double {
    require(a.size == b.size) { "Spin systems must have the same size" }
    var sumDiff = 0.0
    for (i in a.probs.indices) {
        sumDiff += abs(a.probs[i] - b.probs[i])
    }
    val bound = 0.5 * sumDiff
    return min(1.0, bound)
}