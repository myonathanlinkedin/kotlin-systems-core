package core

import java.security.MessageDigest
import kotlin.math.*

data class HyperLogLog(val p: Int = 14) {
    val m: Int = 1 shl p
    val registers: IntArray = IntArray(m)

    // Alpha constant based on number of registers
    private val alphaMM: Double = run {
        when (m) {
            16 -> 0.673 * m * m
            32 -> 0.697 * m * m
            64 -> 0.709 * m * m
            else -> (0.7213 / (1.0 + 1.079 / m)) * m * m
        }
    }

    // SHA-256 based 64‑bit hash, deterministic for any Kotlin object
    private fun hash(item: Any): Long {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(item.toString().toByteArray())
        var h = 0L
        for (i in 0 until 8) {
            h = (h shl 8) or (bytes[i].toLong() and 0xffL)
        }
        return h
    }

    // Add a single element to the sketch
    fun add(item: Any) {
        val x = hash(item)
        val idx = (x ushr (64 - p)).toInt()
        val w = x shl p
        // rank = position of first 1-bit in w, starting at 1
        var rank = java.lang.Long.numberOfLeadingZeros(w) + 1
        if (rank > registers[idx]) {
            registers[idx] = rank
        }
    }

    // Merge another sketch into this one (union)
    fun merge(other: HyperLogLog) {
        require(other.p == this.p) { "Precision must match for merge" }
        for (i in 0 until m) {
            if (other.registers[i] > this.registers[i]) {
                this.registers[i] = other.registers[i]
            }
        }
    }

    // Raw estimate before small/large range corrections
    private fun rawEstimate(): Double {
        var sum = 0.0
        for (register in registers) {
            sum += 2.0.pow(-register.toDouble())
        }
        return alphaMM / sum
    }

    // Cardinality estimate with bias corrections
    fun estimate(): Double {
        val raw = rawEstimate()
        // Small range correction (linear counting)
        val zeros = registers.count { it == 0 }
        if (raw <= 5.0 / 2.0 * m && zeros != 0) {
            return m * ln(m.toDouble() / zeros)
        }
        // Large range correction (using 2^32 as the hash space)
        val twoPow32 = 4294967296.0 // 2^32
        return if (raw > (1.0 / 30.0) * twoPow32) {
            -twoPow32 * ln(1.0 - raw / twoPow32)
        } else {
            raw
        }
    }
}