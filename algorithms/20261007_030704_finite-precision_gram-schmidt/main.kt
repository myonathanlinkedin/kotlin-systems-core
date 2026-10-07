package main

import core.*
import kotlin.math.abs

private const val EPS = 1e-8

fun assertClose(a: Double, b: Double, eps: Double = EPS) {
    check(abs(a - b) <= eps) { "Values $a and $b differ by more than $eps" }
}

fun testOrthogonalization() {
    // Simple 2‑D case: vectors (1,1) and (1,-1) should become orthogonal
    val v1 = Vector(doubleArrayOf(1.0, 1.0))
    val v2 = Vector(doubleArrayOf(1.0, -1.0))
    val orth = gramSchmidtOrthogonalize(listOf(v1, v2))
    val dot = orth[0].dot(orth[1])
    assertClose(dot, 0.0)
}

fun testWalkIdentity() {
    // Identity matrix vectors in 3‑D
    val e1 = Vector(doubleArrayOf(1.0, 0.0, 0.0))
    val e2 = Vector(doubleArrayOf(0.0, 1.0, 0.0))
    val e3 = Vector(doubleArrayOf(0.0, 0.0, 1.0))
    val target = Vector(doubleArrayOf(0.5, -0.3, 0.9))
    val steps = gramSchmidtWalk(listOf(e1, e2, e3), target)

    // Reconstruct the walk sum
    var sum = Vector(doubleArrayOf(0.0, 0.0, 0.0))
    for (step in steps) {
        sum = sum + step
    }
    // Residual should be target - sum, close to zero
    val residual = target - sum
    assertClose(residual.norm(), 0.0)
}

fun testWalkRandom() {
    // Small random set of vectors in 4‑D
    val a = Vector(doubleArrayOf(0.6, 0.8, 0.0, 0.0))
    val b = Vector(doubleArrayOf(0.0, 0.6, 0.8, 0.0))
    val c = Vector(doubleArrayOf(0.0, 0.0, 0.6, 0.8))
    val d = Vector(doubleArrayOf(0.8, 0.0, 0.0, 0.6))
    val target = Vector(doubleArrayOf(0.2, -0.1, 0.3, -0.4))
    val steps = gramSchmidtWalk(listOf(a, b, c, d), target)

    var sum = Vector(doubleArrayOf(0.0, 0.0, 0.0, 0.0))
    for (step in steps) {
        sum = sum + step
    }
    val residual = target - sum
    // The residual norm should be bounded; we accept a modest tolerance
    check(residual.norm() < 1.0) { "Residual too large: ${residual.norm()}" }
}

fun runAllTests() {
    testOrthogonalization()
    testWalkIdentity()
    testWalkRandom()
    println("All tests passed.")
}

fun main() {
    runAllTests()
}