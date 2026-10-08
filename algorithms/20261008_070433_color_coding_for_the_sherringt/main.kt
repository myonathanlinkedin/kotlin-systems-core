package main

import core.SKModel
import kotlin.random.Random
import kotlin.math.abs

fun testEnergyComputation() {
    var n = 3
    val j = arrayOf(
        doubleArrayOf(0.0, 1.0, -2.0),
        doubleArrayOf(1.0, 0.0, 0.5),
        doubleArrayOf(-2.0, 0.5, 0.0)
    )
    val model = SKModel(n, j)
    val spins = intArrayOf(1, 1, 1)
    val expected = -(1.0 * 1 * 1 + -2.0 * 1 * 1 + 0.5 * 1 * 1) // -(-0.5) = 0.5? compute:
    // sum = 1 + (-2) + 0.5 = -0.5, energy = -(-0.5) = 0.5
    val computed = model.energy(spins)
    check(abs(computed - 0.5) < 1e-9) { "Energy computation failed: $computed vs 0.5" }
}

fun testRandomSpinsRange() {
    val model = SKModel(5, Array(5) { DoubleArray(5) })
    repeat(100) {
        val spins = model.randomSpins()
        spins.forEach { s ->
            check(s == 1 || s == -1) { "Random spin out of range: $s" }
        }
    }
}

fun testBruteForceVsColorCodingSmall() {
    var n = 4
    var random = Random(42)
    val j = Array(n) { DoubleArray(n) }
    for (i in 0 until n) {
        for (jIdx in i + 1 until n) {
            val value = random.nextDouble(-1.0, 1.0)
            j[i][jIdx] = value
            j[jIdx][i] = value
        }
    }
    val model = SKModel(n, j)
    val (bruteSpins, bruteEnergy) = model.bruteForceMinEnergy()
    val (colorSpins, colorEnergy) = model.colorCoding(samples = 5000, random = random)
    // For n=4 exhaustive search guarantees optimal energy; color coding should match it.
    check(abs(bruteEnergy - colorEnergy) < 1e-9) {
        "Color coding did not find optimal energy: $colorEnergy vs $bruteEnergy"
    }
    // Verify that spins are valid
    colorSpins.forEach { s -> check(s == 1 || s == -1) }
}

fun demoRandomSKModel() {
    var n = 6
    var random = Random(123)
    val j = Array(n) { DoubleArray(n) }
    for (i in 0 until n) {
        for (jIdx in i + 1 until n) {
            val value = random.nextDouble(-1.0, 1.0)
            j[i][jIdx] = value
            j[jIdx][i] = value
        }
    }
    val model = SKModel(n, j)
    val (bestSpins, bestEnergy) = model.colorCoding(samples = 20000, random = random)
    println("Best energy found (approx): $bestEnergy")
    println("Spins: ${bestSpins.joinToString(separator = " ")}")
}

fun main() {
    testEnergyComputation()
    testRandomSpinsRange()
    testBruteForceVsColorCodingSmall()
    println("All unit tests passed.")
    demoRandomSKModel()
}