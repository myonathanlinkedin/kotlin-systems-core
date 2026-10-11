package main

import core.ZiWeiCalculator
import core.StemBranch
import kotlin.system.measureTimeMillis

fun testSingleCase(
    year: Int, month: Int, day: Int, hour: Int,
    expected: Map<String, StemBranch>
) {
    val result = ZiWeiCalculator.computeFourPillars(year, month, day, hour)
    check(result == expected) {
        "Mismatch for $year-$month-$day $hour: expected $expected, got $result"
    }
}

fun runUnitTests() {
    // Test case 1 – simple reference values
    val expected1 = mapOf(
        "Year" to StemBranch("Gui", "Hai"),
        "Month" to StemBranch("Ji", "Mao"),
        "Day" to StemBranch("Ren", "Wei"),
        "Hour" to StemBranch("Jia", "Zi")
    )
    testSingleCase(2023, 1, 1, 0, expected1)

    // Test case 2 – end of year edge
    val expected2 = mapOf(
        "Year" to StemBranch("Jia", "Zi"),
        "Month" to StemBranch("Bing", "Hai"),
        "Day" to StemBranch("Ding", "Mao"),
        "Hour" to StemBranch("Wu", "Wei")
    )
    testSingleCase(2024, 12, 31, 23, expected2)

    // Test case 3 – random mid‑month hour
    val expected3 = mapOf(
        "Year" to StemBranch("Ding", "Mao"),
        "Month" to StemBranch("Ji", "Shen"),
        "Day" to StemBranch("Gui", "Chou"),
        "Hour" to StemBranch("Yi", "Wei")
    )
    testSingleCase(1995, 5, 15, 14, expected3)

    // Verify that illegal arguments throw
    var caught = false
    try {
        ZiWeiCalculator.computeFourPillars(2020, 0, 10, 5)
    } catch (e: IllegalArgumentException) {
        caught = true
    }
    check(caught) { "Expected exception for invalid month" }

    caught = false
    try {
        ZiWeiCalculator.computeFourPillars(2020, 5, 32, 5)
    } catch (e: IllegalArgumentException) {
        caught = true
    }
    check(caught) { "Expected exception for invalid day" }

    caught = false
    try {
        ZiWeiCalculator.computeFourPillars(2020, 5, 10, 24)
    } catch (e: IllegalArgumentException) {
        caught = true
    }
    check(caught) { "Expected exception for invalid hour" }
}

fun benchmark(iterations: Int = 100_000) {
    val time = measureTimeMillis {
        repeat(iterations) {
            ZiWeiCalculator.computeFourPillars(1988, 8, 8, 8)
        }
    }
    println("Benchmark: $iterations iterations took $time ms")
}

fun main() {
    runUnitTests()
    println("All unit tests passed.")
    benchmark()
}