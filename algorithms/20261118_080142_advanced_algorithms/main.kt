package main

import core.AVLTree
import kotlin.math.abs

fun testInsertionAndBalance() {
    val tree = AVLTree()
    val keys = listOf(10, 20, 30, 40, 50, 25)
    for (k in keys) {
        tree.insert(k)
        assert(tree.isBalanced()) { "Tree became unbalanced after inserting $k" }
    }
    val inorder = tree.inorder()
    assert(inorder == listOf(10, 20, 25, 30, 40, 50)) { "Inorder traversal incorrect: $inorder" }
    for (k in keys) {
        assert(tree.contains(k)) { "Tree should contain $k" }
    }
}

fun testDeletionLeafNode() {
    val tree = AVLTree()
    val keys = listOf(15, 10, 20, 8, 12, 17, 25)
    for (k in keys) tree.insert(k)
    tree.delete(8) // leaf
    assert(!tree.contains(8)) { "Deleted leaf 8 still present" }
    assert(tree.isBalanced()) { "Tree unbalanced after deleting leaf" }
    val expected = listOf(10, 12, 15, 17, 20, 25)
    assert(tree.inorder() == expected) { "Inorder after leaf deletion wrong: ${tree.inorder()}" }
}

fun testDeletionNodeWithOneChild() {
    val tree = AVLTree()
    val keys = listOf(30, 20, 40, 10, 25, 35, 50, 22)
    for (k in keys) tree.insert(k)
    tree.delete(25) // node with one child (22)
    assert(!tree.contains(25)) { "Deleted node 25 still present" }
    assert(tree.contains(22)) { "Child 22 missing after deletion" }
    assert(tree.isBalanced()) { "Tree unbalanced after deleting node with one child" }
    val expected = listOf(10, 20, 22, 30, 35, 40, 50)
    assert(tree.inorder() == expected) { "Inorder after one-child deletion wrong: ${tree.inorder()}" }
}

fun testDeletionNodeWithTwoChildren() {
    val tree = AVLTree()
    val keys = listOf(50, 30, 70, 20, 40, 60, 80, 35, 45)
    for (k in keys) tree.insert(k)
    tree.delete(30) // node with two children
    assert(!tree.contains(30)) { "Deleted node 30 still present" }
    assert(tree.isBalanced()) { "Tree unbalanced after deleting node with two children" }
    val expected = listOf(20, 35, 40, 45, 50, 60, 70, 80)
    assert(tree.inorder() == expected) { "Inorder after two-children deletion wrong: ${tree.inorder()}" }
}

fun testDuplicateInsertionIgnored() {
    val tree = AVLTree()
    tree.insert(5)
    tree.insert(5) // duplicate
    assert(tree.inorder().size == 1) { "Duplicate key inserted" }
    assert(tree.contains(5)) { "Key 5 missing after insertion" }
}

fun testLargeRandomSequence() {
    val tree = AVLTree()
    val randomKeys = (1..1000).shuffled()
    for (k in randomKeys) tree.insert(k)
    assert(tree.isBalanced()) { "Tree unbalanced after bulk random insertions" }
    for (k in randomKeys) assert(tree.contains(k)) { "Missing key $k after bulk insert" }
    // delete half of them
    val toDelete = randomKeys.take(500)
    for (k in toDelete) tree.delete(k)
    assert(tree.isBalanced()) { "Tree unbalanced after bulk deletions" }
    for (k in toDelete) assert(!tree.contains(k)) { "Key $k still present after deletion" }
    for (k in randomKeys.drop(500)) assert(tree.contains(k)) { "Key $k missing after deletions" }
}

fun main() {
    testInsertionAndBalance()
    testDeletionLeafNode()
    testDeletionNodeWithOneChild()
    testDeletionNodeWithTwoChildren()
    testDuplicateInsertionIgnored()
    testLargeRandomSequence()
    println("All AVLTree tests passed.")
}