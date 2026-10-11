package rk4

import kotlin.math.*

private fun addVectors(a: DoubleArray, b: DoubleArray): DoubleArray {
    require(a.size == b.size) { "Vector sizes must match for addition." }
    return DoubleArray(a.size) { i -> a[i] + b[i] }
}

private fun scaleVector(v: DoubleArray, scalar: Double): DoubleArray {
    return DoubleArray(v.size) { i -> v[i] * scalar }
}

/**
 * Performs a single RK4 step.
 *
 * @param f derivative function f(t, y) = dy/dt
 * @param t current independent variable
 * @param y current state vector
 * @param h step size
 * @return pair of (t + h, yNext)
 */
fun rk4Step(
    f: (t: Double, y: DoubleArray) -> DoubleArray,
    t: Double,
    y: DoubleArray,
    h: Double
): Pair<Double, DoubleArray> {
    require(h > 0.0) { "Step size h must be positive." }
    val k1 = f(t, y)
    val yMid1 = addVectors(y, scaleVector(k1, h / 2.0))
    val k2 = f(t + h / 2.0, yMid1)
    val yMid2 = addVectors(y, scaleVector(k2, h / 2.0))
    val k3 = f(t + h / 2.0, yMid2)
    val yEnd = addVectors(y, scaleVector(k3, h))
    val k4 = f(t + h, yEnd)

    val increment = DoubleArray(y.size) { i ->
        (k1[i] + 2.0 * k2[i] + 2.0 * k3[i] + k4[i]) * (h / 6.0)
    }
    val yNext = addVectors(y, increment)
    return Pair(t + h, yNext)
}

/**
 * Integrates the ODE from t0 to tEnd using fixed step size h.
 *
 * @param problem ODE problem definition
 * @param tEnd final independent variable value
 * @param h step size (must divide (tEnd - t0) evenly)
 * @return RK4Result containing all intermediate points
 */
fun integrateRK4(problem: ODEProblem, tEnd: Double, h: Double): RK4Result {
    require(h > 0.0) { "Step size h must be positive." }
    val totalSpan = tEnd - problem.initialT
    require(totalSpan >= 0.0) { "tEnd must be greater than or equal to initialT." }
    val steps = (totalSpan / h).roundToInt()
    require(abs(steps * h - totalSpan) < 1e-12) {
        "Step size h must evenly divide the interval [${problem.initialT}, $tEnd]."
    }

    val tValues = DoubleArray(steps + 1)
    val yValues = Array(steps + 1) { DoubleArray(problem.initialY.size) }

    var t = problem.initialT
    var y = problem.initialY.copyOf()
    tValues[0] = t
    yValues[0] = y.copyOf()

    for (i in 1..steps) {
        val (tNext, yNext) = rk4Step(problem.derivative, t, y, h)
        t = tNext
        y = yNext
        tValues[i] = t
        yValues[i] = y.copyOf()
    }

    return RK4Result(tValues, yValues)
}