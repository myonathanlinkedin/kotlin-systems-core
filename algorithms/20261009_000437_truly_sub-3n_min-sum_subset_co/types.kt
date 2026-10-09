package main

import kotlin.math.max
import kotlin.math.min

/**
 * Immutable, hashable representation of a subset of a universe of size [n].
 * The subset is encoded as a bitmask where bit i (0-indexed) is set iff element i is present.
 *
 * Invariants:
 *  - [mask] is in the range [0, 2^n - 1].
 *  - [n] is the universe size (number of bits considered).
 */
data class Subset(
    val mask: Int,
    var n: Int
) {
    init {
        require(n in 0..31) { "Universe size n must be in [0, 31], got $n" }
        require(mask in 0 until (1 shl n)) { "Mask $mask out of range for n=$n" }
    }

    /** Number of elements in this subset (popcount). */
    val cardinality: Int
        get() = Integer.bitCount(mask)

    /** True if this subset is empty. */
    val isEmpty: Boolean
        get() = mask == 0

    /** True if this subset contains all n elements. */
    val isFull: Boolean
        get() = mask == (1 shl n) - 1

    /** True if [other] is a subset of this one (other ⊆ this). */
    fun contains(other: Subset): Boolean {
        require(other.n == n) { "Universe size mismatch" }
        return (mask and other.mask) == other.mask
    }

    /** Union: this ∪ other. */
    fun union(other: Subset): Subset {
        require(other.n == n) { "Universe size mismatch" }
        return Subset(mask or other.mask, n)
    }

    /** Intersection: this ∩ other. */
    fun intersection(other: Subset): Subset {
        require(other.n == n) { "Universe size mismatch" }
        return Subset(mask and other.mask, n)
    }

    /** Difference: this \ other. */
    fun difference(other: Subset): Subset {
        require(other.n == n) { "Universe size mismatch" }
        return Subset(mask and other.mask.inv(), n)
    }

    /** True if this and [other] are disjoint (this ∩ other = ∅). */
    fun isDisjoint(other: Subset): Boolean {
        require(other.n == n) { "Universe size mismatch" }
        return (mask and other.mask) == 0
    }

    override fun toString(): String {
        val bits = (0 until n).filter { (mask shr it) and 1 == 1 }.joinToString(",")
        return "S{$bits}"
    }
}

/**
 * A weighted directed graph on [n] vertices, used for the join-ordering problem.
 *
 * [weight][i][j] is the cost of joining vertex i with vertex j (i ≠ j).
 * Diagonal entries are ignored (set to 0).
 *
 * The join-ordering problem: find a binary tree whose leaves are the n vertices
 * such that the total cost (sum of edge costs in the tree) is minimized.
 * This is equivalent to finding the minimum-cost binary tree over all 2^(n-1) - 1
 * possible full binary tree topologies, weighted by the pairwise join costs.
 *
 * The classic DP over subsets runs in O(3^n · n) time.
 * The "sub-3^n" improvement uses the fact that for min-sum subset convolution,
 * we can exploit the structure of the cost function.
 */
data class WeightedGraph(
    var n: Int,
    val weight: Array<IntArray>
) {
    init {
        require(n in 0..15) { "n must be in [0, 15] for bitmask DP, got $n" }
        require(weight.size == n) { "weight array size must equal n" }
        for (i in 0 until n) {
            require(weight[i].size == n) { "weight[$i] size must equal n" }
        }
    }

    /** Cost of joining vertex i with vertex j. */
    fun joinCost(i: Int, j: Int): Int {
        if (i == j) return 0
        return weight[i][j]
    }

    /**
     * Total cost of a complete binary join tree over the vertex set [subset].
     * The tree is defined recursively: pick a split (A, B) of subset where A ∪ B = subset,
     * A ∩ B = ∅, A ≠ ∅, B ≠ ∅, and cost = cost(A) + cost(B) + joinCost(A, B).
     *
     * This is the value we want to minimize.
     */
    fun totalJoinCost(subset: Subset): Int {
        if (subset.cardinality <= 1) return 0
        // The actual cost is computed by the DP in engine.kt
        throw UnsupportedOperationException("Use MinSumSubsetConvolution.computeMinCost")
    }
}

/**
 * Result of the min-sum subset convolution / join ordering computation.
 */
data class JoinOrderResult(
    val minCost: Int,
    val optimalSplit: Map<Int, Pair<Int, Int>>,
    val dpTable: Array<IntArray>
) {
    /**
     * Reconstruct the optimal binary join tree as a list of (leftMask, rightMask) splits
     * for each non-trivial subset.
     */
    fun getSplits(): Map<Int, Pair<Int, Int>> = optimalSplit
}

/**
 * A single entry in the DP table: the minimum cost to join the subset [mask],
 * and the optimal split (leftMask, rightMask) that achieves it.
 */
data class DpEntry(
    val cost: Int,
    val leftMask: Int,
    val rightMask: Int
)