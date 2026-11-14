package chronos

import kotlin.math.*

class BloomFilter<T : Any>(private val params: BloomFilterParams) {
    private val m: Int
    private val k: Int
    private val bitArray: LongArray

    init {
        require(params.capacity > 0) { "Capacity must be positive" }
        require(params.falsePositiveRate in 0.0..1.0) { "False positive rate must be between 0 and 1" }

        // Compute optimal number of bits (m) and hash functions (k)
        val n = params.capacity.toDouble()
        val p = params.falsePositiveRate
        m = ceil(-n * ln(p) / (ln(2.0).pow(2.0))).toInt()
        k = ceil((m.toDouble() / n) * ln(2.0)).toInt()

        val arraySize = (m + 63) / 64
        bitArray = LongArray(arraySize)
    }

    fun add(item: T) {
        val hashes = generateHashes(item)
        for (hash in hashes) {
            setBit(hash)
        }
    }

    fun mightContain(item: T): Boolean {
        val hashes = generateHashes(item)
        for (hash in hashes) {
            if (!getBit(hash)) return false
        }
        return true
    }

    private fun generateHashes(item: T): List<Int> {
        val hash1 = (item.hashCode().toLong() and 0xFFFFFFFFL)
        val hash2 = ((hash1 xor 0x9E3779B9L) and 0xFFFFFFFFL)
        val hashes = mutableListOf<Int>()
        for (i in 0 until k) {
            val combined = (hash1 + i * hash2) and 0xFFFFFFFFL
            val index = (combined % m.toLong()).toInt()
            hashes.add(index)
        }
        return hashes
    }

    private fun setBit(index: Int) {
        val arrayIndex = index ushr 6
        val bitIndex = index and 63
        bitArray[arrayIndex] = bitArray[arrayIndex] or (1L shl bitIndex)
    }

    private fun getBit(index: Int): Boolean {
        val arrayIndex = index ushr 6
        val bitIndex = index and 63
        return (bitArray[arrayIndex] and (1L shl bitIndex)) != 0L
    }
}