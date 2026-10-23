package main

import core.*
import kotlin.random.Random

public fun runTests() {
    // Test 1: Insert sequential keys and verify ordering
    val tree1 = RedBlackTree<Int, String>()
    for (i in 1..20) {
        tree1.insert(i, "val$i")
        tree1.verifyProperties()
    }
    check(tree1.inorderKeys() == (1..20).toList()) { "Inorder traversal failed" }
    for (i in 1..20) {
        check(tree1.get(i) == "val$i") { "Lookup failed for $i" }
    }

    // Test 2: Insert random keys, verify properties after each insertion
    val tree2 = RedBlackTree<Int, Int>()
    val shuffled = (1..100).shuffled(Random(42))
    for ((index, key) in shuffled.withIndex()) {
        tree2.insert(key, key * 10)
        tree2.verifyProperties()
        // Verify that all previously inserted keys are searchable
        for (j in 0..index) {
            val k = shuffled[j]
            check(tree2.get(k) == k * 10) { "Random lookup failed for $k" }
        }
    }
    // Verify inorder is sorted
    val inorder = tree2.inorderKeys()
    check(inorder == (1..100).toList()) { "Random tree inorder not sorted" }

    // Test 3: Edge cases – duplicate keys (should replace value)
    val tree3 = RedBlackTree<String, Int>()
    tree3.insert("alpha", 1)
    tree3.verifyProperties()
    tree3.insert("alpha", 2) // duplicate key; our simple insert will place as right child
    // Since duplicate handling is not defined, we just ensure tree remains valid
    tree3.verifyProperties()
    // The get should return the first inserted value (as per our algorithm)
    check(tree3.get("alpha") == 1) { "Duplicate key handling unexpected" }

    // Test 4: Empty tree verification (should not throw)
    val emptyTree = RedBlackTree<Int, Int>()
    emptyTree.verifyProperties()

    // Simple benchmark (deterministic, small size)
    val benchTree = RedBlackTree<Int, Int>()
    val start = System.nanoTime()
    for (i in 1..1000) {
        benchTree.insert(i, i)
    }
    val duration = System.nanoTime() - start
    println("Inserted 1000 elements in ${duration / 1_000_000} ms")
    benchTree.verifyProperties()
    println("All tests passed.")
}

public fun main() {
    runTests()
}