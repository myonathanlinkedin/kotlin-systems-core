package main

import core.FlashMemory
import core.UART
import core.WiFiSecretManager
import kotlin.math.min

fun testFlashMemory() {
    val pageSize = 16
    val pageCount = 4
    val flash = FlashMemory(pageSize, pageCount, maxWear = 3)

    // Write within a single page
    val data1 = ByteArray(8) { it.toByte() }
    flash.write(0, data1)
    assert(flash.read(0, 8).contentEquals(data1))

    // Write spanning two pages
    val data2 = ByteArray(20) { (it + 10).toByte() }
    flash.write(pageSize - 4, data2) // starts near end of page 0
    val readBack = flash.read(pageSize - 4, 20)
    assert(readBack.contentEquals(data2))

    // Verify wear counts
    assert(flash.getWear(0) == 2) // first write + spanning write
    assert(flash.getWear(1) == 1) // part of spanning write

    // Exceed wear limit
    flash.write(pageSize * 2, ByteArray(1) { 0 })
    flash.write(pageSize * 2, ByteArray(1) { 1 })
    try {
        flash.write(pageSize * 2, ByteArray(1) { 2 })
        assert(false) // should not reach
    } catch (e: IllegalStateException) {
        assert(e.message!!.contains("Wear limit exceeded"))
    }
}

fun testUART() {
    val uart = UART()
    // Locked state should reject send/receive
    try {
        uart.send(byteArrayOf(1, 2, 3))
        assert(false)
    } catch (e: IllegalStateException) {
        assert(e.message!!.contains("locked"))
    }

    uart.unlock()
    val payload = byteArrayOf(10, 20, 30)
    uart.send(payload)
    val received = uart.receive()
    assert(received.contentEquals(payload))

    // After receive buffer should be empty
    val empty = uart.receive()
    assert(empty.isEmpty())

    uart.lock()
    try {
        uart.receive()
        assert(false)
    } catch (e: IllegalStateException) {
        assert(e.message!!.contains("locked"))
    }
}

fun testWiFiSecretManager() {
    val key: Byte = 0x5A
    val manager = WiFiSecretManager(key)
    val secret = "TopSecret123"
    manager.store(secret)
    val retrieved = manager.retrieve()
    assert(retrieved == secret)

    // Changing key should break retrieval
    val wrongManager = WiFiSecretManager(0x00)
    wrongManager.store(secret)
    assert(wrongManager.retrieve() != secret)
}

fun main() {
    testFlashMemory()
    testUART()
    testWiFiSecretManager()
    println("All tests passed")
}