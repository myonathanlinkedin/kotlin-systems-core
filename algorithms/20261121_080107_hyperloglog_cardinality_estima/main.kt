package hll

import kotlin.math.abs

fun testEmpty() {
    val hll = HyperLogLog()
    check(hll.estimate() == 0L)
}

fun testSingle() {
    val hll = HyperLogLog()
    hll.add("a")
    val est = hll.estimate()
    check(est == 1L || est == 2L)
}

fun testSmallSet() {
    val n = 1000
    val hll = HyperLogLog(p = 10)
    for (i in 0 until n) {
        hll.add(i)
    }
    val est = hll.estimate()
    val err = abs(est - n).toDouble() / n
    check(err < 0.1)
}

fun testDuplicate() {
    val hll = HyperLogLog(p = 10)
    for (i in 0 until 500) {
        hll.add(i)
        hll.add(i)
    }
    val est = hll.estimate()
    val err = abs(est - 500).toDouble() / 500
    check(err < 0.1)
}

fun testLargeSet() {
    val n = 100_000
    val hll = HyperLogLog(p = 14)
    for (i in 0 until n) {
        hll.add(i)
    }
    val est = hll.estimate()
    val err = abs(est - n).toDouble() / n
    check(err < 0.07)
}

fun main() {
    testEmpty()
    testSingle()
    testSmallSet()
    testDuplicate()
    testLargeSet()
    println("All HyperLogLog tests passed.")
}