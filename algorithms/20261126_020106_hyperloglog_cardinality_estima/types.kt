package hll

import kotlin.math.*

data class HyperLogLog(
    val precision: Int,                     // number of bits for register index (p)
    val registers: IntArray = IntArray(1 shl precision) { 0 } // m = 2^p registers
) {
    init {
        require(precision in 4..16) { "Precision must be between 4 and 16 inclusive." }
    }

    // Number of registers m = 2^p
    val registerCount: Int
        get() = 1 shl precision

    // Alpha constant based on m
    val alphaMM: Double
        get() {
            val m = registerCount.toDouble()
            return when (registerCount) {
                16 -> 0.673 * m * m
                32 -> 0.697 * m * m
                64 -> 0.709 * m * m
                else -> (0.7213 / (1.0 + 1.079 / m)) * m * m
            }
        }
}