import kotlin.system.exitProcess

fun main() {
    val tree = BinarySearchTree()

    // Test insert and inorder
    val values = listOf(50, 30, 70, 20, 40, 60, 80)
    values.forEach { tree.insert(it) }
    val inorder = tree.inorder()
    check(inorder == listOf(20, 30, 40, 50, 60, 70, 80)) { "Inorder traversal failed" }

    // Test search existing
    check(tree.search(30)) { "Search failed for existing key 30" }
    // Test search non-existing
    check(!tree.search(99)) { "Search incorrectly found non-existing key 99" }

    // Test size
    check(tree.size() == 7) { "Size incorrect after insertions" }

    // Test delete leaf node
    tree.delete(20)
    check(!tree.search(20)) { "Leaf node 20 not deleted" }
    check(tree.size() == 6) { "Size incorrect after deleting leaf" }

    // Test delete node with one child
    tree.delete(30)
    check(!tree.search(30)) { "Node 30 not deleted" }
    check(tree.size() == 5) { "Size incorrect after deleting node with one child" }

    // Test delete node with two children
    tree.delete(70)
    check(!tree.search(70)) { "Node 70 not deleted" }
    check(tree.size() == 4) { "Size incorrect after deleting node with two children" }

    // Test delete root
    tree.delete(50)
    check(!tree.search(50)) { "Root 50 not deleted" }
    check(tree.size() == 3) { "Size incorrect after deleting root" }

    // Test inorder after deletions
    val inorderAfter = tree.inorder()
    check(inorderAfter == listOf(40, 60, 80)) { "Inorder incorrect after deletions" }

    // Test delete non-existent key
    val sizeBefore = tree.size()
    tree.delete(999)
    check(tree.size() == sizeBefore) { "Size changed after deleting non-existent key" }

    // Test inserting duplicate (should be ignored)
    tree.insert(40)
    check(tree.size() == 3) { "Duplicate insertion altered size" }

    // Test inserting new keys
    tree.insert(10)
    tree.insert(90)
    check(tree.size() == 5) { "Size incorrect after inserting new keys" }
    check(tree.search(10) && tree.search(90)) { "Search failed for newly inserted keys" }

    // Final inorder check
    val finalInorder = tree.inorder()
    check(finalInorder == listOf(10, 40, 60, 80, 90)) { "Final inorder incorrect" }

    println("All tests passed successfully.")
    exitProcess(0)
}
