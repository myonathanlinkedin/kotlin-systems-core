package core

import kotlin.math.*

public const val RED = true
public const val BLACK = false

public data class RBNode<K : Comparable<K>, V>(
    var key: K,
    var value: V,
    var color: Boolean = RED,
    var left: RBNode<K, V>? = null,
    var right: RBNode<K, V>? = null,
    var parent: RBNode<K, V>? = null
)

public class RedBlackTree<K : Comparable<K>, V> {
    public var root: RBNode<K, V>? = null

    // Public insert API
    public fun insert(key: K, value: V) {
        var z = RBNode(key, value, RED, null, null, null)
        var y: RBNode<K, V>? = null
        var x = root
        while (x != null) {
            y = x
            if (z.key < x.key) {
                x = x.left
            } else {
                x = x.right
            }
        }
        z.parent = y
        if (y == null) {
            root = z
        } else if (z.key < y.key) {
            y.left = z
        } else {
            y.right = z
        }
        z.left = null
        z.right = null
        z.color = RED
        insertFixup(z)
    }

    // Public search API
    public operator fun get(key: K): V? {
        var node = root
        while (node != null) {
            when {
                key < node.key -> node = node.left
                key > node.key -> node = node.right
                else -> return node.value
            }
        }
        return null
    }

    // Inorder traversal returning list of keys
    public fun inorderKeys(): List<K> {
        val result = mutableListOf<K>()
        fun inorder(node: RBNode<K, V>?) {
            if (node == null) return
            inorder(node.left)
            result.add(node.key)
            inorder(node.right)
        }
        inorder(root)
        return result
    }

    // Verify Red-Black properties; throws AssertionError if violated
    public fun verifyProperties() {
        // Property 1: root is black
        check(root == null || root?.color == BLACK) { "Root must be black" }

        // Property 2: all leaves (null) are black – implicit

        // Property 3: red node's children are black
        // Property 4: same black-height on all paths
        fun checkNode(node: RBNode<K, V>?): Int {
            if (node == null) return 0 // black-height of leaf
            if (node.color == RED) {
                check(node.left?.color == BLACK) { "Red node has red left child" }
                check(node.right?.color == BLACK) { "Red node has red right child" }
            }
            val leftBH = checkNode(node.left)
            val rightBH = checkNode(node.right)
            check(leftBH == rightBH) { "Black-height mismatch at key ${node.key}" }
            return leftBH + if (node.color == BLACK) 1 else 0
        }
        checkNode(root)
    }

    // --- Internal helpers -------------------------------------------------

    private fun leftRotate(x: RBNode<K, V>) {
        var y = x.right ?: return
        x.right = y.left
        if (y.left != null) {
            y.left?.parent = x
        }
        y.parent = x.parent
        if (x.parent == null) {
            root = y
        } else if (x === x.parent?.left) {
            x.parent?.left = y
        } else {
            x.parent?.right = y
        }
        y.left = x
        x.parent = y
    }

    private fun rightRotate(y: RBNode<K, V>) {
        var x = y.left ?: return
        y.left = x.right
        if (x.right != null) {
            x.right?.parent = y
        }
        x.parent = y.parent
        if (y.parent == null) {
            root = x
        } else if (y === y.parent?.right) {
            y.parent?.right = x
        } else {
            y.parent?.left = x
        }
        x.right = y
        y.parent = x
    }

    private fun insertFixup(zInput: RBNode<K, V>) {
        var z = zInput
        while (z.parent?.color == RED) {
            var parent = z.parent!!
            val grandparent = parent.parent!!
            if (parent === grandparent.left) {
                var y = grandparent.right // uncle
                if (y?.color == RED) {
                    // Case 1
                    parent.color = BLACK
                    y.color = BLACK
                    grandparent.color = RED
                    z = grandparent
                } else {
                    if (z === parent.right) {
                        // Case 2
                        z = parent
                        leftRotate(z)
                    }
                    // Case 3
                    parent.color = BLACK
                    grandparent.color = RED
                    rightRotate(grandparent)
                }
            } else {
                // Mirror of above
                var y = grandparent.left // uncle
                if (y?.color == RED) {
                    parent.color = BLACK
                    y.color = BLACK
                    grandparent.color = RED
                    z = grandparent
                } else {
                    if (z === parent.left) {
                        z = parent
                        rightRotate(z)
                    }
                    parent.color = BLACK
                    grandparent.color = RED
                    leftRotate(grandparent)
                }
            }
        }
        root?.color = BLACK
    }
}