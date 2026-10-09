package main

import kotlin.random.Random

import core.RedBlackTree
import kotlin.math.abs

fun main() {
    testInsertionAndSearch()
    testDuplicateKeyReplacement()
    testBalanceProperties()
    testRandomInsertions()
    println("All tests passed.")
}

// Simple assertion helper
fun assertCondition(condition: Boolean, message: String) {
    if (!condition) throw AssertionError(message)
}

// Test 1: basic insertion and retrieval
fun testInsertionAndSearch() {
    val tree = RedBlackTree<Int, String>()
    tree.insert(10, "ten")
    tree.insert(5, "five")
    tree.insert(15, "fifteen")
    assertCondition(tree.get(10) == "ten", "Value for key 10 should be 'ten'")
    assertCondition(tree.get(5) == "five", "Value for key 5 should be 'five'")
    assertCondition(tree.get(15) == "fifteen", "Value for key 15 should be 'fifteen'")
    assertCondition(tree.get(20) == null, "Value for missing key should be null")
    assertCondition(tree.isValid(), "Tree should be valid after basic insertions")
}

// Test 2: duplicate key handling (replace value)
fun testDuplicateKeyReplacement() {
    val tree = RedBlackTree<String, Int>()
    tree.insert("alpha", 1)
    tree.insert("beta", 2)
    tree.insert("alpha", 42) // replace
    assertCondition(tree.get("alpha") == 42, "Duplicate key should replace existing value")
    assertCondition(tree.isValid(), "Tree should remain valid after duplicate insertion")
}

// Test 3: verify red-black properties after many insertions
fun testBalanceProperties() {
    val tree = RedBlackTree<Int, Int>()
    for (i in 1..1000) {
        tree.insert(i, i * i)
        assertCondition(tree.isValid(), "Tree invalid after inserting $i")
    }
    // Verify black-height consistency by checking that isValid returned true
}

// Test 4: random insertions and structural checks
fun testRandomInsertions() {
    val tree = RedBlackTree<Int, Int>()
    val numbers = mutableListOf<Int>()
    val rand = kotlin.random.Random(12345)
    repeat(500) {
        val value = rand.nextInt(0, 10000)
        numbers.add(value)
        tree.insert(value, value)
        assertCondition(tree.isValid(), "Tree invalid after random insertion $value")
    }
    // Verify that all inserted keys are searchable
    for (key in numbers) {
        assertCondition(tree.get(key) == key, "Search failed for key $key")
    }
    // Verify that non-inserted keys are not found (sample)
    for (i in 0 until 100) {
        val probe = 100000 + i
        assertCondition(tree.get(probe) == null, "Unexpected value for non-existent key $probe")
    }
}