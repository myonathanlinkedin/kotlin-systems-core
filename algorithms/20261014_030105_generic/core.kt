package core

import kotlin.math.max

/**
 * Immutable AVL Tree implementation with generic key and value types.
 * The tree requires a Comparator for the key type to define ordering.
 */
data class AVLNode<K, V>(
    var key: K,
    val value: V,
    val height: Int = 1,
    var left: AVLNode<K, V>? = null,
    var right: AVLNode<K, V>? = null
) {
    companion object {
        fun <K, V> height(node: AVLNode<K, V>?): Int = node?.height ?: 0
    }
}

/**
 * Public AVL Tree API. All mutating operations return a new tree instance,
 * preserving immutability.
 */
data class AVLTree<K, V>(private val comparator: Comparator<K>, private val root: AVLNode<K, V>? = null) {

    /** Returns the value associated with [searchKey] or null if absent. */
    fun find(searchKey: K): V? = findNode(root, searchKey)?.value

    /** Inserts or replaces the entry (key, value). Returns a new AVLTree. */
    fun insert(key: K, value: V): AVLTree<K, V> = AVLTree(comparator, insertNode(root, key, value))

    /** Deletes the entry with [key] if present. Returns a new AVLTree. */
    fun delete(key: K): AVLTree<K, V> = AVLTree(comparator, deleteNode(root, key))

    /** Returns an in‑order list of key/value pairs. */
    fun inOrder(): List<Pair<K, V>> {
        val result = mutableListOf<Pair<K, V>>()
        fun traverse(node: AVLNode<K, V>?) {
            if (node == null) return
            traverse(node.left)
            result.add(Pair(node.key, node.value))
            traverse(node.right)
        }
        traverse(root)
        return result
    }

    /** Returns the height of the tree (0 for empty). */
    fun height(): Int = AVLNode.height(root)

    // ---------- Internal helpers ----------

    private fun findNode(node: AVLNode<K, V>?, searchKey: K): AVLNode<K, V>? {
        var current = node
        while (current != null) {
            val cmp = comparator.compare(searchKey, current.key)
            when {
                cmp < 0 -> current = current.left
                cmp > 0 -> current = current.right
                else -> return current
            }
        }
        return null
    }

    private fun insertNode(node: AVLNode<K, V>?, key: K, value: V): AVLNode<K, V> {
        if (node == null) return AVLNode(key, value)

        val cmp = comparator.compare(key, node.key)
        val newNode = when {
            cmp < 0 -> node.copy(left = insertNode(node.left, key, value))
            cmp > 0 -> node.copy(right = insertNode(node.right, key, value))
            else -> node.copy(value = value) // replace existing value
        }
        return rebalance(newNode)
    }

    private fun deleteNode(node: AVLNode<K, V>?, key: K): AVLNode<K, V>? {
        if (node == null) return null

        val cmp = comparator.compare(key, node.key)
        val newNode = when {
            cmp < 0 -> node.copy(left = deleteNode(node.left, key))
            cmp > 0 -> node.copy(right = deleteNode(node.right, key))
            else -> {
                // Node to delete found
                when {
                    node.left == null && node.right == null -> return null
                    node.left == null -> return node.right
                    node.right == null -> return node.left
                    else -> {
                        // Replace with inorder successor
                        val successor = minNode(node.right!!)!!
                        node.copy(
                            key = successor.key,
                            value = successor.value,
                            right = deleteMin(node.right)
                        )
                    }
                }
            }
        }
        return rebalance(newNode)
    }

    private fun minNode(node: AVLNode<K, V>): AVLNode<K, V>? {
        var current = node
        while (current.left != null) {
            current = current.left!!
        }
        return current
    }

    private fun deleteMin(node: AVLNode<K, V>?): AVLNode<K, V>? {
        if (node == null) return null
        if (node.left == null) return node.right
        val newNode = node.copy(left = deleteMin(node.left))
        return rebalance(newNode)
    }

    private fun rebalance(node: AVLNode<K, V>): AVLNode<K, V> {
        val balance = AVLNode.height(node.left) - AVLNode.height(node.right)

        // Left heavy
        if (balance > 1) {
            val leftChild = node.left!!
            if (AVLNode.height(leftChild.left) >= AVLNode.height(leftChild.right)) {
                return rotateRight(node)
            } else {
                return rotateRight(node.copy(left = rotateLeft(leftChild)))
            }
        }

        // Right heavy
        if (balance < -1) {
            val rightChild = node.right!!
            if (AVLNode.height(rightChild.right) >= AVLNode.height(rightChild.left)) {
                return rotateLeft(node)
            } else {
                return rotateLeft(node.copy(right = rotateRight(rightChild)))
            }
        }

        // Update height
        val newHeight = 1 + max(AVLNode.height(node.left), AVLNode.height(node.right))
        return if (node.height == newHeight) node else node.copy(height = newHeight)
    }

    private fun rotateRight(y: AVLNode<K, V>): AVLNode<K, V> {
        val x = y.left!!
        val t2 = x.right
        val newRight = y.copy(left = t2)
        val newRoot = x.copy(right = newRight)
        val updatedRight = newRight.copy(height = 1 + max(AVLNode.height(newRight.left), AVLNode.height(newRight.right)))
        return newRoot.copy(height = 1 + max(AVLNode.height(newRoot.left), updatedRight.height))
    }

    private fun rotateLeft(x: AVLNode<K, V>): AVLNode<K, V> {
        val y = x.right!!
        val t2 = y.left
        val newLeft = x.copy(right = t2)
        val newRoot = y.copy(left = newLeft)
        val updatedLeft = newLeft.copy(height = 1 + max(AVLNode.height(newLeft.left), AVLNode.height(newLeft.right)))
        return newRoot.copy(height = 1 + max(updatedLeft.height, AVLNode.height(newRoot.right)))
    }
}