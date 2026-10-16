package rk4

typealias ODEFunction = (Double, Double) -> Double

data class RK4Result(val t: Double, val y: Double)