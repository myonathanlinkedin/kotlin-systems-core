package hll

import java.security.MessageDigest
import kotlin.math.*

data class HyperLogLog(val p: Int = 14) {
    val m: Int = 1 shl p
    val registers: IntArray = IntArray(m)
    val alphaMM: Double = when (m) {
        16 -> 0.673 * m * m
        32 -> 0.697 * m * m
        64 -> 0.709 * m * m
        else -> (0.7213 / (1.0 + 1.079 / m)) * m * m
    }

    private fun hash(item: Any): Long {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(item.toString().toByteArray())
        var h = 0L
        for (i in 0 until 8) {
            h = (h shl 8) or (bytes[i].toLong() and 0xffL)
        }
        return h
    }

    fun add(item: Any) {
        val x = hash(item)
        val idx = (x ushr (64 - p)).toInt()
        var w = x shl p
        var rank = 1
        while (rank <= 64 - p && (w and (1L shl 63)) == 0L) {
            rank++
            w = w shl 1
        }
        if (rank > registers[idx]) {
            registers[idx] = rank
        }
    }

    fun estimate(): Long {
        var sum = 0.0
        var zeros = 0
        for (reg in registers) {
            sum += 2.0.pow(-reg)
            if (reg == 0) zeros++
        }
        var raw = alphaMM / sum
        if (raw <= 5.0 / 2.0 * m && zeros != 0) {
            raw = m * ln(m.toDouble() / zeros)
        }
        return raw.roundToLong()
    }
}