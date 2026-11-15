package core

import kotlin.math.*

class BloomFilter<T>(private val size: Int, private val hashCount: Int) {
    private val bitArray: LongArray = LongArray((size + 63) / 64)
    private val mask: Long = 0xFFFFFFFFFFFFFFFFUL.toLong()

    private fun hash1(value: T): Int = value.hashCode()
    private fun hash2(value: T): Int = (hash1(value) shl 5) or (hash1(value) ushr 27)

    private fun setBit(index: Int) {
        val wordIndex = index ushr 6
        val bitIndex = index and 63
        bitArray[wordIndex] = bitArray[wordIndex] or (1L shl bitIndex)
    }

    private fun getBit(index: Int): Boolean {
        val wordIndex = index ushr 6
        val bitIndex = index and 63
        return (bitArray[wordIndex] and (1L shl bitIndex)) != 0L
    }

    fun add(item: T) {
        val h1 = hash1(item)
        val h2 = hash2(item)
        for (i in 0 until hashCount) {
            val combined = (h1 + i * h2).toLong()
            val index = (combined and mask).toInt() % size
            setBit(index)
        }
    }

    fun mightContain(item: T): Boolean {
        val h1 = hash1(item)
        val h2 = hash2(item)
        for (i in 0 until hashCount) {
            val combined = (h1 + i * h2).toLong()
            val index = (combined and mask).toInt() % size
            if (!getBit(index)) return false
        }
        return true
    }
}