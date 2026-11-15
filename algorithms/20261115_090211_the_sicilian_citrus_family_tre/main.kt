package main

import core.FamilyTree
import kotlin.math.*

fun runTests() {
    // Build a sample Sicilian Citrus family tree
    // Root: "Lemon"
    val tree = FamilyTree("Lemon")
    tree.addChild("Lemon", "Orange")
    tree.addChild("Lemon", "Mandarin")
    tree.addChild("Orange", "BloodOrange")
    tree.addChild("Orange", "Seville")
    tree.addChild("Mandarin", "Clementine")
    tree.addChild("Mandarin", "Tangerine")
    tree.addChild("Tangerine", "Minneola")

    // Test depth
    check(tree.depth("Lemon") == 0)
    check(tree.depth("Orange") == 1)
    check(tree.depth("BloodOrange") == 2)
    check(tree.depth("Minneola") == 3)

    // Test ancestors
    check(tree.ancestors("Minneola") == listOf("Tangerine", "Mandarin", "Lemon"))
    check(tree.ancestors("Lemon").isEmpty())

    // Test descendants
    check(tree.descendants("Orange").sorted() == listOf("BloodOrange", "Seville"))
    check(tree.descendants("Mandarin").sorted() == listOf("Clementine", "Tangerine", "Minneola"))
    check(tree.descendants("Lemon").sorted() == listOf(
        "Orange", "Mandarin", "BloodOrange", "Seville",
        "Clementine", "Tangerine", "Minneola"
    ))

    // Test isAncestor
    check(tree.isAncestor("Lemon", "Minneola"))
    check(tree.isAncestor("Mandarin", "Minneola"))
    check(!tree.isAncestor("Orange", "Minneola"))
    check(!tree.isAncestor("Minneola", "Lemon"))

    // Test commonAncestor
    check(tree.commonAncestor("BloodOrange", "Seville") == "Orange")
    check(tree.commonAncestor("Clementine", "Minneola") == "Mandarin")
    check(tree.commonAncestor("BloodOrange", "Minneola") == "Lemon")
    check(tree.commonAncestor("Lemon", "Minneola") == null) // root has no ancestor

    // Test duplicate insertion handling
    var duplicateCaught = false
    try {
        tree.addChild("Lemon", "Orange") // already exists
    } catch (e: IllegalArgumentException) {
        duplicateCaught = true
    }
    check(duplicateCaught)

    // Test adding child to non‑existent parent
    var missingParentCaught = false
    try {
        tree.addChild("NonExistent", "NewCitrus")
    } catch (e: IllegalArgumentException) {
        missingParentCaught = true
    }
    check(missingParentCaught)

    // Test retrieval of unknown node
    var unknownNodeCaught = false
    try {
        tree.getNode("Ghost")
    } catch (e: IllegalArgumentException) {
        unknownNodeCaught = true
    }
    check(unknownNodeCaught)

    // All assertions passed
    println("All tests passed.")
}

fun main() {
    runTests()
}