package rk4

import kotlin.math.*

fun main() {
    // Test 1: dy/dt = y, solution y = y0 * exp(t - t0)
    val f1: ODEFunction = { t, y -> y }
    val t0_1 = 0.0
    val y0_1 = 1.0
    val t1_1 = 1.0
    val steps1 = 1000
    val result1 = integrate(f1, t0_1, y0_1, t1_1, steps1)
    val approx1 = result1.last().y
    val exact1 = y0_1 * exp(t1_1 - t0_1)
    check(kotlin.math.abs(approx1 - exact1) < 1e-6) { "Test 1 failed: approx=$approx1, exact=$exact1" }

    // Test 2: dy/dt = -2*y, solution y = y0 * exp(-2*(t - t0))
    val f2: ODEFunction = { t, y -> -2.0 * y }
    val t0_2 = 0.0
    val y0_2 = 3.0
    val t1_2 = 0.5
    val steps2 = 500
    val result2 = integrate(f2, t0_2, y0_2, t1_2, steps2)
    val approx2 = result2.last().y
    val exact2 = y0_2 * exp(-2.0 * (t1_2 - t0_2))
    check(kotlin.math.abs(approx2 - exact2) < 1e-6) { "Test 2 failed: approx=$approx2, exact=$exact2" }

    // Test 3: dy/dt = sin(t), y(0)=0, solution y = -cos(t)+1
    val f3: ODEFunction = { t, y -> sin(t) }
    val t0_3 = 0.0
    val y0_3 = 0.0
    val t1_3 = PI
    val steps3 = 2000
    val result3 = integrate(f3, t0_3, y0_3, t1_3, steps3)
    val approx3 = result3.last().y
    val exact3 = -cos(t1_3) + 1.0
    check(kotlin.math.abs(approx3 - exact3) < 1e-6) { "Test 3 failed: approx=$approx3, exact=$exact3" }

    // Edge case: single step
    val f4: ODEFunction = { t, y -> 2.0 * y }
    val t0_4 = 0.0
    val y0_4 = 1.0
    val t1_4 = 0.1
    val steps4 = 1
    val result4 = integrate(f4, t0_4, y0_4, t1_4, steps4)
    val approx4 = result4.last().y
    val exact4 = y0_4 * exp(2.0 * (t1_4 - t0_4))
    check(kotlin.math.abs(approx4 - exact4) < 1e-4) { "Test 4 failed: approx=$approx4, exact=$exact4" }

    // Edge case: negative time interval
    val f5: ODEFunction = { t, y -> y }
    val t0_5 = 1.0
    val y0_5 = 2.0
    val t1_5 = 0.0
    val steps5 = 100
    val result5 = integrate(f5, t0_5, y0_5, t1_5, steps5)
    val approx5 = result5.last().y
    val exact5 = y0_5 * exp(t1_5 - t0_5)
    check(kotlin.math.abs(approx5 - exact5) < 1e-6) { "Test 5 failed: approx=$approx5, exact=$exact5" }

    // Demo: print a few points for dy/dt = cos(t)
    val fDemo: ODEFunction = { t, y -> cos(t) }
    val demo = integrate(fDemo, 0.0, 0.0, 2 * PI, 20)
    println("Demo: dy/dt = cos(t)")
    demo.forEach { point ->
        println("t = ${"%.3f".format(point.t)}, y = ${"%.5f".format(point.y)}")
    }
    println("All tests passed.")
}