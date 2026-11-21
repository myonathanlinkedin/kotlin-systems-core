fun testInsertAndFind() {
    val tree = AVLTree()
    tree.insert(10, "ten")
    tree.insert(20, "twenty")
    tree.insert(5, "five")
    assert(tree.find(10) == "ten")
    assert(tree.find(20) == "twenty")
    assert(tree.find(5) == "five")
    assert(tree.find(99) == null)
}

fun testDuplicateKeyUpdatesValue() {
    val tree = AVLTree()
    tree.insert(15, "first")
    assert(tree.find(15) == "first")
    tree.insert(15, "second")
    assert(tree.find(15) == "second")
}

fun testAscendingInsertBalancesTree() {
    val tree = AVLTree()
    for (i in 1..7) {
        tree.insert(i, "v$i")
    }
    // Height of AVL tree with 7 nodes should be <= 3
    val rootHeight = tree.height(tree.root)
    assert(rootHeight <= 3) { "Height $rootHeight exceeds expected bound" }
    // Inorder should be sorted
    val inorder = tree.inorder().map { it.first }
    assert(inorder == (1..7).toList())
}

fun testDeleteLeafNode() {
    val tree = AVLTree()
    tree.insert(30, "a")
    tree.insert(20, "b")
    tree.insert(40, "c")
    tree.delete(20) // leaf
    assert(tree.find(20) == null)
    val inorder = tree.inorder().map { it.first }
    assert(inorder == listOf(30, 40))
}

fun testDeleteNodeWithOneChild() {
    val tree = AVLTree()
    tree.insert(30, "a")
    tree.insert(20, "b")
    tree.insert(10, "c") // left child of 20
    tree.delete(20) // node with one child
    assert(tree.find(20) == null)
    val inorder = tree.inorder().map { it.first }
    assert(inorder == listOf(10, 30))
}

fun testDeleteNodeWithTwoChildren() {
    val tree = AVLTree()
    val keys = listOf(50, 30, 70, 20, 40, 60, 80)
    for (k in keys) {
        tree.insert(k, "v$k")
    }
    tree.delete(30) // node with two children (20 and 40)
    assert(tree.find(30) == null)
    val inorder = tree.inorder().map { it.first }
    assert(inorder == listOf(20, 40, 50, 60, 70, 80))
}

fun testInorderTraversalSorted() {
    val tree = AVLTree()
    val shuffled = listOf(8, 3, 10, 1, 6, 14, 4, 7, 13)
    for (k in shuffled) {
        tree.insert(k, "v$k")
    }
    val inorderKeys = tree.inorder().map { it.first }
    assert(inorderKeys == shuffled.sorted())
}

fun main() {
    testInsertAndFind()
    testDuplicateKeyUpdatesValue()
    testAscendingInsertBalancesTree()
    testDeleteLeafNode()
    testDeleteNodeWithOneChild()
    testDeleteNodeWithTwoChildren()
    testInorderTraversalSorted()
    println("All AVL Tree tests passed.")
}
