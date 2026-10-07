package core

import kotlin.math.sqrt
import kotlin.math.abs

data class Vector(val components: DoubleArray) {
    val size: Int get() = components.size

    operator fun plus(other: Vector): Vector {
        require(size == other.size) { "Vector sizes must match for addition." }
        return Vector(DoubleArray(size) { components[it] + other.components[it] })
    }

    operator fun minus(other: Vector): Vector {
        require(size == other.size) { "Vector sizes must match for subtraction." }
        return Vector(DoubleArray(size) { components[it] - other.components[it] })
    }

    operator fun times(scalar: Double): Vector {
        return Vector(DoubleArray(size) { components[it] * scalar })
    }

    fun dot(other: Vector): Double {
        require(size == other.size) { "Vector sizes must match for dot product." }
        var sum = 0.0
        for (i in components.indices) {
            sum += components[i] * other.components[i]
        }
        return sum
    }

    fun norm(): Double = sqrt(this.dot(this))

    fun projectionOnto(basis: Vector): Vector {
        val denom = basis.dot(basis)
        if (abs(denom) < 1e-12) return Vector(DoubleArray(size) { 0.0 })
        val coeff = this.dot(basis) / denom
        return basis * coeff
    }

    fun copy(): Vector = Vector(components.clone())
}

/**
 * Performs classical Gram–Schmidt orthogonalization on the given list of vectors.
 * Returns a list of orthogonal (not necessarily normalized) vectors.
 */
fun gramSchmidtOrthogonalize(vectors: List<Vector>): List<Vector> {
    val orthogonal = mutableListOf<Vector>()
    for (v in vectors) {
        var proj = Vector(DoubleArray(v.size) { 0.0 })
        for (u in orthogonal) {
            proj = proj + v.projectionOnto(u)
        }
        val orth = v - proj
        orthogonal.add(orth)
    }
    return orthogonal
}

/**
 * Executes a finite‑precision Gram‑Schmidt walk.
 *
 * Given a list of input vectors `aList` and a target vector `target`,
 * the algorithm iteratively chooses a sign (+1 or -1) for each vector,
 * subtracts the signed vector from the current residual, and records the step.
 *
 * The walk proceeds in reverse order (from last vector to first) using the
 * orthogonal basis derived from `aList`. The returned list contains the step
 * vectors in the order they were taken (first step corresponds to the first
 * vector in `aList`).
 */
fun gramSchmidtWalk(aList: List<Vector>, target: Vector): List<Vector> {
    require(aList.isNotEmpty()) { "Input vector list must not be empty." }
    require(aList.all { it.size == target.size }) { "All vectors must share the same dimension." }

    // Orthogonal basis of the input vectors (same order)
    val orthogonalBasis = gramSchmidtOrthogonalize(aList)

    // Copy of target that will be updated
    var residual = target.copy()
    val steps = MutableList(aList.size) { Vector(DoubleArray(target.size) { 0.0 }) }

    // Process vectors in reverse order
    for (i in aList.indices.reversed()) {
        val u = orthogonalBasis[i]
        val denom = u.dot(u)
        val coeff = if (abs(denom) < 1e-12) 0.0 else residual.dot(u) / denom
        val sign = if (coeff >= 0) 1.0 else -1.0
        val step = aList[i] * sign
        steps[i] = step
        residual = residual - step
    }
    return steps
}