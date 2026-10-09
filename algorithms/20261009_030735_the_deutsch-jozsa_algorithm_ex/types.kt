package dj

import kotlin.math.sqrt
import kotlin.math.abs

data class Complex(val real: Double, val imag: Double = 0.0) {
    operator fun plus(other: Complex) = Complex(real + other.real, imag + other.imag)
    operator fun minus(other: Complex) = Complex(real - other.real, imag - other.imag)
    operator fun times(other: Complex) =
        Complex(real * other.real - imag * other.imag, real * other.imag + imag * other.real)

    operator fun times(scalar: Double) = Complex(real * scalar, imag * scalar)
    operator fun div(scalar: Double) = Complex(real / scalar, imag / scalar)

    fun magnitudeSquared() = real * real + imag * imag
}

// Helper to create a zero-filled quantum state
fun zeroState(size: Int): Array<Complex> = Array(size) { Complex(0.0, 0.0) }