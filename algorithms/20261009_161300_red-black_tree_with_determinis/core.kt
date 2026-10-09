package core

import kotlin.math.max

enum class Color { RED, BLACK }

data class Node<K : Comparable<K>, V>(
    var key: K,
    var value: V,
    var color: Color = Color.RED,
    var left: Node<K, V>? = null,
    var right: Node<K, V>? = null,
    var parent: Node<K, V>? = null
)

class RedBlackTree<K : Comparable<K>, V> {
    private var root: Node<K, V>? = null

    // Public API
    fun insert(key: K, value: V) {
        val newNode = Node(key, value)
        var y: Node<K, V>? = null
        var x = root
        while (x != null) {
            y = x
            x = if (key < x.key) x.left else if (key > x.key) x.right else {
                // Duplicate key: replace value and exit
                x.value = value
                return
            }
        }
        newNode.parent = y
        if (y == null) {
            root = newNode
        } else if (key < y.key) {
            y.left = newNode
        } else {
            y.right = newNode
        }
        fixInsert(newNode)
    }

    operator fun get(key: K): V? {
        var node = root
        while (node != null) {
            node = when {
                key < node.key -> node.left
                key > node.key -> node.right
                else -> return node.value
            }
        }
        return null
    }

    fun contains(key: K): Boolean = get(key) != null

    // Validation exposed for tests
    fun isValid(): Boolean = validateProperties()

    // ----------------- Internal helpers -----------------
    private fun rotateLeft(x: Node<K, V>) {
        var y = x.right ?: return
        x.right = y.left
        if (y.left != null) y.left!!.parent = x
        y.parent = x.parent
        if (x.parent == null) {
            root = y
        } else if (x === x.parent!!.left) {
            x.parent!!.left = y
        } else {
            x.parent!!.right = y
        }
        y.left = x
        x.parent = y
    }

    private fun rotateRight(y: Node<K, V>) {
        var x = y.left ?: return
        y.left = x.right
        if (x.right != null) x.right!!.parent = y
        x.parent = y.parent
        if (y.parent == null) {
            root = x
        } else if (y === y.parent!!.right) {
            y.parent!!.right = x
        } else {
            y.parent!!.left = x
        }
        x.right = y
        y.parent = x
    }

    private fun fixInsert(z: Node<K, V>) {
        var node = z
        while (node.parent?.color == Color.RED) {
            var parent = node.parent!!
            val grandParent = parent.parent!!
            if (parent === grandParent.left) {
                val uncle = grandParent.right
                if (uncle?.color == Color.RED) {
                    parent.color = Color.BLACK
                    uncle.color = Color.BLACK
                    grandParent.color = Color.RED
                    node = grandParent
                } else {
                    if (node === parent.right) {
                        node = parent
                        rotateLeft(node)
                    }
                    node.parent!!.color = Color.BLACK
                    grandParent.color = Color.RED
                    rotateRight(grandParent)
                }
            } else {
                val uncle = grandParent.left
                if (uncle?.color == Color.RED) {
                    parent.color = Color.BLACK
                    uncle.color = Color.BLACK
                    grandParent.color = Color.RED
                    node = grandParent
                } else {
                    if (node === parent.left) {
                        node = parent
                        rotateRight(node)
                    }
                    node.parent!!.color = Color.BLACK
                    grandParent.color = Color.RED
                    rotateLeft(grandParent)
                }
            }
        }
        root?.color = Color.BLACK
    }

    // Validation of Red-Black properties
    private fun validateProperties(): Boolean {
        // Property 1: root is black
        if (root?.color != Color.BLACK) return false

        // Property 2: red nodes have black children
        fun redChildrenBlack(node: Node<K, V>?): Boolean {
            if (node == null) return true
            if (node.color == Color.RED) {
                if (node.left?.color == Color.RED) return false
                if (node.right?.color == Color.RED) return false
            }
            return redChildrenBlack(node.left) && redChildrenBlack(node.right)
        }

        // Property 3: same black-height on all paths
        fun blackHeight(node: Node<K, V>?): Int? {
            if (node == null) return 0
            val leftHeight = blackHeight(node.left) ?: return null
            val rightHeight = blackHeight(node.right) ?: return null
            if (leftHeight != rightHeight) return null
            return leftHeight + if (node.color == Color.BLACK) 1 else 0
        }

        return redChildrenBlack(root) && blackHeight(root) != null
    }
}