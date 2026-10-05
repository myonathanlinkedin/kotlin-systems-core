package quantum

import kotlin.math.sqrt

data class Vector(val components: DoubleArray) {
    val size: Int get() = components.size

    operator fun plus(other: Vector): Vector =
        Vector(components.zip(other.components) { a, b -> a + b }.toDoubleArray())

    operator fun minus(other: Vector): Vector =
        Vector(components.zip(other.components) { a, b -> a - b }.toDoubleArray())

    operator fun times(scalar: Double): Vector =
        Vector(components.map { it * scalar }.toDoubleArray())

    fun dot(other: Vector): Double =
        components.zip(other.components) { a, b -> a * b }.sum()

    fun norm(): Double = sqrt(dot(this))
}

data class Matrix(val rows: Array<DoubleArray>) {
    val rowsCount: Int get() = rows.size
    val colsCount: Int get() = if (rows.isEmpty()) 0 else rows[0].size

    operator fun times(vector: Vector): Vector {
        require(colsCount == vector.size) { "Matrix columns must match vector size" }
        val result = DoubleArray(rowsCount)
        for (i in 0 until rowsCount) {
            var sum = 0.0
            for (j in 0 until colsCount) {
                sum += rows[i][j] * vector.components[j]
            }
            result[i] = sum
        }
        return Vector(result)
    }

    operator fun times(other: Matrix): Matrix {
        require(colsCount == other.rowsCount) { "Matrix dimensions incompatible for multiplication" }
        val result = Array(rowsCount) { DoubleArray(other.colsCount) }
        for (i in 0 until rowsCount) {
            for (j in 0 until other.colsCount) {
                var sum = 0.0
                for (k in 0 until colsCount) {
                    sum += rows[i][k] * other.rows[k][j]
                }
                result[i][j] = sum
            }
        }
        return Matrix(result)
    }
}

data class MetricTensor(val matrix: Matrix) {
    fun innerProduct(v1: Vector, v2: Vector): Double {
        require(v1.size == matrix.rowsCount && v2.size == matrix.rowsCount) {
            "Vector size must match metric dimension"
        }
        val temp = matrix * v1
        return temp.dot(v2)
    }
}

data class QuantumState(val vector: Vector) {
    fun normalize(metric: MetricTensor): QuantumState {
        val norm = sqrt(metric.innerProduct(vector, vector))
        return QuantumState(vector * (1.0 / norm))
    }
}

data class Hamiltonian(val matrix: Matrix)

fun evolve(state: QuantumState, hamiltonian: Hamiltonian, dt: Double): QuantumState {
    val delta = hamiltonian.matrix * state.vector
    val newVector = state.vector + delta * (-dt)
    return QuantumState(newVector)
}

fun simulate(
    initialState: QuantumState,
    hamiltonian: Hamiltonian,
    metric: MetricTensor,
    steps: Int,
    dt: Double
): List<QuantumState> {
    val states = mutableListOf<QuantumState>()
    var current = initialState.normalize(metric)
    states.add(current)
    repeat(steps) {
        current = evolve(current, hamiltonian, dt).normalize(metric)
        states.add(current)
    }
    return states
}
