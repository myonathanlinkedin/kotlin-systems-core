package core

import kotlin.math.min

data class FlashMemory(val pageSize: Int, val pageCount: Int, val maxWear: Int = 1000) {
    private val storage = ByteArray(pageSize * pageCount) { 0 }
    private val wearCounts = IntArray(pageCount) { 0 }

    fun read(address: Int, length: Int): ByteArray {
        check(address >= 0 && length >= 0 && address + length <= storage.size) { "Read out of bounds" }
        return storage.copyOfRange(address, address + length)
    }

    fun write(address: Int, data: ByteArray) {
        check(address >= 0 && address + data.size <= storage.size) { "Write out of bounds" }
        var offset = address
        var remaining = data.size
        var dataIdx = 0
        while (remaining > 0) {
            val pageIdx = offset / pageSize
            val pageOffset = offset % pageSize
            val toWrite = min(remaining, pageSize - pageOffset)
            check(wearCounts[pageIdx] < maxWear) { "Wear limit exceeded on page $pageIdx" }
            for (i in 0 until toWrite) {
                storage[offset + i] = data[dataIdx + i]
            }
            wearCounts[pageIdx] += 1
            offset += toWrite
            dataIdx += toWrite
            remaining -= toWrite
        }
    }

    fun getWear(pageIdx: Int): Int = wearCounts[pageIdx]

    fun totalWear(): Int = wearCounts.sum()
}

class UART {
    var locked: Boolean = true
    private val buffer = mutableListOf<Byte>()

    fun unlock() {
        locked = false
    }

    fun lock() {
        locked = true
    }

    fun send(data: ByteArray) {
        check(!locked) { "UART is locked" }
        buffer.addAll(data.toList())
    }

    fun receive(): ByteArray {
        check(!locked) { "UART is locked" }
        val out = buffer.toByteArray()
        buffer.clear()
        return out
    }
}

class WiFiSecretManager(private val key: Byte) {
    private var encryptedSecret: ByteArray? = null

    fun store(secret: String) {
        val bytes = secret.toByteArray()
        encryptedSecret = bytes.map { (it.toInt() xor key.toInt()).toByte() }.toByteArray()
    }

    fun retrieve(): String {
        val enc = encryptedSecret ?: return ""
        val dec = enc.map { (it.toInt() xor key.toInt()).toByte() }.toByteArray()
        return String(dec)
    }
}