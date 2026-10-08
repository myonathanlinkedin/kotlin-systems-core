package rk4

import kotlin.math.*

fun rk4Step(f: ODEFunction, t: Double, y: Double, h: Double): Double {
    val k1 = f(t, y)
    val k2 = f(t + h / 2.0, y + h * k1 / 2.0)
    val k3 = f(t + h / 2.0, y + h * k2 / 2.0)
    val k4 = f(t + h, y + h * k3)
    return y + h * (k1 + 2.0 * k2 + 2.0 * k3 + k4) / 6.0
}

fun integrate(
    f: ODEFunction,
    t0: Double,
    y0: Double,
    t1: Double,
    steps: Int
): List<RK4Result> {
    require(steps > 0) { "Number of steps must be positive" }
    val result = mutableListOf<RK4Result>()
    val h = (t1 - t0) / steps.toDouble()
    var t = t0
    var y = y0
    result.add(RK4Result(t, y))
    repeat(steps) {
        y = rk4Step(f, t, y, h)
        t += h
        result.add(RK4Result(t, y))
    }
    return result
}