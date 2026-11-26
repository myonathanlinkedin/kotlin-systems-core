package hll

import java.security.MessageDigest
import kotlin.math.*

private val sha256Digest: MessageDigest = MessageDigest.getInstance("SHA-256")

private fun hashToLong(value: Any): Long {
    val bytes = sha256Digest.digest(value.toString().toByteArray())
    var h = 0L
    for (i in 0 until 8) {
        h = (h shl 8) or (bytes[i].toLong() and 0xffL)
    }
    return h
}

private fun rho(w: Long, maxBits: Int): Int {
    // Count leading zeros in the (64 - p) bits portion, then add 1.
    // w has already been left‑shifted by p bits, so the top p bits are zero.
    val leading = java.lang.Long.numberOfLeadingZeros(w) - maxBits
    return leading + 1
}

fun HyperLogLog.add(element: Any) {
    val hash = hashToLong(element)
    val p = precision
    val idx = (hash ushr (64 - p)).toInt()               // register index
    val w = hash shl p                                   // remaining bits
    val rank = rho(w, p)
    if (rank > registers[idx]) {
        registers[idx] = rank
    }
}

fun HyperLogLog.estimate(): Double {
    val m = registerCount
    var sum = 0.0
    for (register in registers) {
        sum += 2.0.pow(-register.toDouble())
    }
    val raw = alphaMM / sum

    // Small range correction (linear counting)
    val zeroCount = registers.count { it == 0 }
    if (raw <= 5.0 * m / 2.0 && zeroCount != 0) {
        return m * ln(m.toDouble() / zeroCount.toDouble())
    }

    // Large range correction
    val two64 = 2.0.pow(64.0)
    if (raw > (1.0 / 30.0) * two64) {
        return -two64 * ln(1.0 - raw / two64)
    }

    return raw
}

fun HyperLogLog.merge(other: HyperLogLog) {
    require(this.precision == other.precision) { "Precisions must match to merge." }
    for (i in registers.indices) {
        if (other.registers[i] > this.registers[i]) {
            this.registers[i] = other.registers[i]
        }
    }
}