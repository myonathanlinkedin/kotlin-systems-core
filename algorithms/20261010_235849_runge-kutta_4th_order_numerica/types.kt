package rk4

data class ODEProblem(
    val derivative: (t: Double, y: DoubleArray) -> DoubleArray,
    val initialT: Double,
    val initialY: DoubleArray
)

data class RK4Result(
    val tValues: DoubleArray,
    val yValues: Array<DoubleArray>
)