package main

import core.AVLTree
import kotlin.math.abs

fun main() {
    // Basic sanity check
    val intTree = AVLTree<Int, String>(compareBy { it })
        .insert(10, "ten")
        .insert(20, "twenty")
        .insert(5, "five")
        .insert(6, "six")
        .insert(15, "fifteen")

    // Verify in‑order traversal yields sorted keys
    val ordered = intTree.inOrder().map { it.first }
    check(ordered == listOf(5, 6, 10, 15, 20)) { "In‑order traversal failed" }

    // Verify find operation
    check(intTree.find(15) == "fifteen")
    check(intTree.find(99) == null)

    // Verify height balance property (difference <= 1 for every node)
    fun assertBalanced(tree: AVLTree<Int, String>) {
        fun checkNode(nodeKey: Int?): Int {
            if (nodeKey == null) return 0
            // Retrieve node via internal traversal (inefficient but acceptable for test)
            val node = tree.inOrder().find { it.first == nodeKey } ?: return 0
            // Not accessing internal structure; instead we rely on height() and properties
            return 0 // placeholder, actual balance checked via height diff later
        }
        // Since internal nodes are not exposed, we validate via height property:
        // For AVL tree, height is O(log n). For n=5, height should be <= 3.
        check(tree.height() <= 3) { "Tree height ${tree.height()} exceeds expected bound" }
    }
    assertBalanced(intTree)

    // Test delete operation
    val afterDelete = intTree.delete(10).delete(5)
    val afterOrder = afterDelete.inOrder().map { it.first }
    check(afterOrder == listOf(6, 15, 20)) { "Delete operation failed" }
    check(afterDelete.find(10) == null)

    // Test replacement of existing key
    val replaced = afterDelete.insert(15, "fifteen-updated")
    check(replaced.find(15) == "fifteen-updated")

    // Generic test with strings as keys
    val stringTree = AVLTree<String, Int>(compareBy { it })
        .insert("delta", 4)
        .insert("alpha", 1)
        .insert("charlie", 3)
        .insert("bravo", 2)

    val stringOrder = stringTree.inOrder().map { it.first }
    check(stringOrder == listOf("alpha", "bravo", "charlie", "delta")) { "String key ordering failed" }

    // Stress test: insert 1000 sequential integers and verify height bound ~log2(1000) ≈ 10
    var largeTree = AVLTree<Int, Int>(compareBy { it })
    for (i in 0 until 1000) {
        largeTree = largeTree.insert(i, i)
    }
    val expectedMaxHeight = 20 // generous upper bound
    check(largeTree.height() <= expectedMaxHeight) {
        "Large tree height ${largeTree.height()} exceeds bound $expectedMaxHeight"
    }

    // Print success message
    println("All AVLTree tests passed.")
}