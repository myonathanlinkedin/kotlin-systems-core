package cuckoo

import kotlin.random.Random
import kotlin.math.*

/**
 * A simple Cuckoo Filter implementation.
 * Uses 8-bit fingerprints and 4 slots per bucket.
 */
class CuckooFilter(capacity: Int) {
    private val bucketSize = 4
    private val maxKicks = 500
    private val numBuckets: Int = max(1, capacity / bucketSize)
    private val buckets: Array<IntArray> = Array(numBuckets) { IntArray(bucketSize) { 0 } }
    private val rand = Random.Default

    /**
     * Inserts a key into the filter.
     * @return true if insertion succeeded, false otherwise.
     */
    fun insert(key: Any): Boolean {
        val fp = fingerprint(key)
        val i1 = index1(key)
        val i2 = index2(i1, fp)

        if (place(fp, i1) || place(fp, i2)) return true

        var curIndex = if (rand.nextBoolean()) i1 else i2
        var curFp = fp
        for (k in 0 until maxKicks) {
            val slot = rand.nextInt(bucketSize)
            val evicted = buckets[curIndex][slot]
            buckets[curIndex][slot] = curFp
            curFp = evicted
            curIndex = altIndex(curIndex, curFp)
            if (place(curFp, curIndex)) return true
        }
        return false
    }

    /**
     * Checks if a key is possibly in the filter.
     */
    fun contains(key: Any): Boolean {
        val fp = fingerprint(key)
        val i1 = index1(key)
        val i2 = index2(i1, fp)
        return find(fp, i1) || find(fp, i2)
    }

    /**
     * Deletes a key from the filter.
     * @return true if deletion succeeded, false if key was not present.
     */
    fun delete(key: Any): Boolean {
        val fp = fingerprint(key)
        val i1 = index1(key)
        val i2 = index2(i1, fp)
        return remove(fp, i1) || remove(fp, i2)
    }

    private fun fingerprint(key: Any): Int {
        var h = key.hashCode()
        h = h xor (h ushr 16)
        var fp = (h and 0xFF)
        if (fp == 0) fp = 1
        return fp
    }

    private fun index1(key: Any): Int {
        val h = key.hashCode()
        return (h and 0x7FFFFFFF) % numBuckets
    }

    private fun index2(i1: Int, fp: Int): Int {
        val h = fp.hashCode()
        return (i1 xor (h and 0x7FFFFFFF)) % numBuckets
    }

    private fun altIndex(index: Int, fp: Int): Int {
        val h = fp.hashCode()
        return (index xor (h and 0x7FFFFFFF)) % numBuckets
    }

    private fun place(fp: Int, bucketIndex: Int): Boolean {
        val bucket = buckets[bucketIndex]
        for (i in 0 until bucketSize) {
            if (bucket[i] == 0) {
                bucket[i] = fp
                return true
            }
        }
        return false
    }

    private fun find(fp: Int, bucketIndex: Int): Boolean {
        val bucket = buckets[bucketIndex]
        for (i in 0 until bucketSize) {
            if (bucket[i] == fp) return true
        }
        return false
    }

    private fun remove(fp: Int, bucketIndex: Int): Boolean {
        val bucket = buckets[bucketIndex]
        for (i in 0 until bucketSize) {
            if (bucket[i] == fp) {
                bucket[i] = 0
                return true
            }
        }
        return false
    }
}