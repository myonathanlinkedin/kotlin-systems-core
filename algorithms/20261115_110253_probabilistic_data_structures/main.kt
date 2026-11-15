package main

import core.BloomFilter

fun testEmptyFilter() {
    val bf = BloomFilter<Int>(1000, 3)
    check(!bf.mightContain(42))
}

fun testSingleInsert() {
    val bf = BloomFilter<Int>(1000, 3)
    bf.add(42)
    check(bf.mightContain(42))
}

fun testFalsePositiveRate() {
    val bf = BloomFilter<Int>(10000, 7)
    val inserted = 1000
    for (i in 0 until inserted) bf.add(i)
    var falsePositives = 0
    val trials = 10000
    for (i in inserted until inserted + trials) {
        if (bf.mightContain(i)) falsePositives++
    }
    val rate = falsePositives.toDouble() / trials
    println("False positive rate: $rate")
}

fun testLargeElements() {
    val bf = BloomFilter<String>(20000, 5)
    val words = listOf("apple", "banana", "cherry", "date", "elderberry")
    words.forEach { bf.add(it) }
    words.forEach { check(bf.mightContain(it)) }
}

fun testDuplicateInsert() {
    val bf = BloomFilter<Int>(500, 4)
    bf.add(7)
    bf.add(7)
    check(bf.mightContain(7))
}

fun testAllBitsSet() {
    val bf = BloomFilter<Int>(10, 3)
    for (i in 0 until 100) bf.add(i)
    check(bf.mightContain(999999))
}

fun main() {
    testEmptyFilter()
    testSingleInsert()
    testDuplicateInsert()
    testLargeElements()
    testAllBitsSet()
    testFalsePositiveRate()
    println("All tests passed.")
}