package main

import kotlin.random.Random

import core.SpinSystem
import core.tvDistanceExact
import core.tvDistanceBound
import kotlin.math.abs

/**
 * Simple deterministic unit‑test harness using Kotlin's built‑in assert.
 * All tests are executed from main().
 */
fun testIdenticalSystems() {
    val probs = doubleArrayOf(0.2, 0.5, 0.8)
    val s1 = SpinSystem(probs)
    val s2 = SpinSystem(probs.clone())
    assert(tvDistanceExact(s1, s2) == 0.0) { "Exact TV of identical systems should be 0" }
    assert(tvDistanceBound(s1, s2) == 0.0) { "Bound TV of identical systems should be 0" }
}

fun testSingleSpin() {
    val a = SpinSystem(doubleArrayOf(1.0))
    val b = SpinSystem(doubleArrayOf(0.0))
    // Exact TV = 1 (disjoint distributions)
    assert(abs(tvDistanceExact(a, b) - 1.0) < 1e-12) { "Exact TV for opposite single spin should be 1" }
    // Bound = 0.5 * |1-0| = 0.5, capped at 1 => 0.5
    assert(abs(tvDistanceBound(a, b) - 0.5) < 1e-12) { "Bound TV for opposite single spin should be 0.5" }
}

fun testTwoSpins() {
    val a = SpinSystem(doubleArrayOf(0.9, 0.1))
    val b = SpinSystem(doubleArrayOf(0.2, 0.8))
    val exact = tvDistanceExact(a, b)
    val bound = tvDistanceBound(a, b)
    // Bound must be ≥ exact
    assert(bound >= exact - 1e-12) { "Bound $bound should be >= exact $exact" }
    // Bound should not exceed 1
    assert(bound <= 1.0 + 1e-12) { "Bound $bound exceeds 1" }
}

fun testZeroSpins() {
    val emptyA = SpinSystem(doubleArrayOf())
    val emptyB = SpinSystem(doubleArrayOf())
    assert(tvDistanceExact(emptyA, emptyB) == 0.0) { "Exact TV for empty systems should be 0" }
    assert(tvDistanceBound(emptyA, emptyB) == 0.0) { "Bound TV for empty systems should be 0" }
}

/**
 * Exhaustive sanity check for n ≤ 5: compare exact TV with bound for random systems.
 */
fun testRandomSmallSystems() {
    val rng = kotlin.random.Random(1234)
    for (n in 1..5) {
        repeat(20) {
            val probsA = DoubleArray(n) { rng.nextDouble() }
            val probsB = DoubleArray(n) { rng.nextDouble() }
            val a = SpinSystem(probsA)
            val b = SpinSystem(probsB)
            val exact = tvDistanceExact(a, b)
            val bound = tvDistanceBound(a, b)
            assert(bound >= exact - 1e-12) { "Bound $bound < exact $exact for n=$n" }
            assert(bound <= 1.0 + 1e-12) { "Bound $bound exceeds 1 for n=$n" }
        }
    }
}

/**
 * Entry point runs all tests and prints a success message.
 */
fun main() {
    testIdenticalSystems()
    testSingleSpin()
    testTwoSpins()
    testZeroSpins()
    testRandomSmallSystems()
    println("All deterministic tests passed.")
}