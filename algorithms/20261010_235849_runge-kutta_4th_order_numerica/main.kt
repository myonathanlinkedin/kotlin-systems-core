package rk4

import kotlin.math.*

fun assertClose(actual: Double, expected: Double, tolerance: Double = 1e-6) {
    check(abs(actual - expected) <= tolerance) {
        "Assertion failed: expected $expected, got $actual, tolerance $tolerance"
    }
}

fun testExponentialGrowth() {
    // dy/dt = y, y(0) = 1 => y(t) = exp(t)
    val derivative = { _: Double, y: DoubleArray -> doubleArrayOf(y[0]) }
    val problem = ODEProblem(derivative, 0.0, doubleArrayOf(1.0))
    val h = 0.1
    val tEnd = 1.0
    val result = integrateRK4(problem, tEnd, h)
    val yFinal = result.yValues.last()[0]
    val expected = exp(tEnd)
    assertClose(yFinal, expected, tolerance = 1e-5)
}

fun testHarmonicOscillator() {
    // System: dy/dt = v, dv/dt = -y
    // Initial conditions: y(0)=0, v(0)=1 => solution y = sin(t), v = cos(t)
    val derivative = { _: Double, y: DoubleArray ->
        doubleArrayOf(y[1], -y[0])
    }
    val problem = ODEProblem(derivative, 0.0, doubleArrayOf(0.0, 1.0))
    val h = 0.05
    val tEnd = Math.PI / 2.0 // quarter period, y should be 1, v should be 0
    val result = integrateRK4(problem, tEnd, h)
    val yFinal = result.yValues.last()
    assertClose(yFinal[0], 1.0, tolerance = 1e-4) // sin(pi/2) = 1
    assertClose(yFinal[1], 0.0, tolerance = 1e-4) // cos(pi/2) = 0
}

fun testZeroStepSize() {
    val derivative = { _: Double, y: DoubleArray -> doubleArrayOf(y[0] * 2.0) }
    val problem = ODEProblem(derivative, 0.0, doubleArrayOf(1.0))
    val h = 0.0
    try {
        integrateRK4(problem, 1.0, h)
        check(false) { "Expected exception for zero step size." }
    } catch (e: IllegalArgumentException) {
        // Expected path
    }
}

fun main() {
    testExponentialGrowth()
    testHarmonicOscillator()
    testZeroStepSize()
    println("All RK4 tests passed successfully.")
}