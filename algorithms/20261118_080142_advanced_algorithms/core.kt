package core

import kotlin.math.max
import kotlin.math.abs

data class AVLNode(
    var key: Int,
    var height: Int = 1,
    var left: AVLNode? = null,
    var right: AVLNode? = null
)

class AVLTree {
    var root: AVLNode? = null

    fun contains(searchKey: Int): Boolean = containsRecursive(root, searchKey)

    private fun containsRecursive(node: AVLNode?, searchKey: Int): Boolean {
        if (node == null) return false
        return when {
            searchKey < node.key -> containsRecursive(node.left, searchKey)
            searchKey > node.key -> containsRecursive(node.right, searchKey)
            else -> true
        }
    }

    fun insert(key: Int) {
        root = insertRecursive(root, key)
    }

    private fun insertRecursive(node: AVLNode?, key: Int): AVLNode {
        if (node == null) return AVLNode(key)

        when {
            key < node.key -> node.left = insertRecursive(node.left, key)
            key > node.key -> node.right = insertRecursive(node.right, key)
            else -> return node // duplicate keys are ignored
        }

        updateHeight(node)
        return rebalance(node)
    }

    fun delete(key: Int) {
        root = deleteRecursive(root, key)
    }

    private fun deleteRecursive(node: AVLNode?, key: Int): AVLNode? {
        if (node == null) return null

        when {
            key < node.key -> node.left = deleteRecursive(node.left, key)
            key > node.key -> node.right = deleteRecursive(node.right, key)
            else -> {
                // node with only one child or no child
                if (node.left == null) return node.right
                if (node.right == null) return node.left

                // node with two children: get inorder successor (smallest in right subtree)
                val successor = minValueNode(node.right!!)
                node.key = successor.key
                node.right = deleteRecursive(node.right, successor.key)
            }
        }

        updateHeight(node)
        return rebalance(node)
    }

    private fun minValueNode(node: AVLNode): AVLNode {
        var current = node
        while (current.left != null) {
            current = current.left!!
        }
        return current
    }

    private fun height(node: AVLNode?): Int = node?.height ?: 0

    private fun updateHeight(node: AVLNode) {
        node.height = 1 + max(height(node.left), height(node.right))
    }

    private fun getBalance(node: AVLNode): Int = height(node.left) - height(node.right)

    private fun rebalance(node: AVLNode): AVLNode {
        val balance = getBalance(node)

        // Left heavy
        if (balance > 1) {
            if (getBalance(node.left!!) < 0) {
                node.left = rotateLeft(node.left!!)
            }
            return rotateRight(node)
        }

        // Right heavy
        if (balance < -1) {
            if (getBalance(node.right!!) > 0) {
                node.right = rotateRight(node.right!!)
            }
            return rotateLeft(node)
        }

        return node
    }

    private fun rotateRight(y: AVLNode): AVLNode {
        val x = y.left!!
        val T2 = x.right

        // Perform rotation
        x.right = y
        y.left = T2

        // Update heights
        updateHeight(y)
        updateHeight(x)

        return x
    }

    private fun rotateLeft(x: AVLNode): AVLNode {
        val y = x.right!!
        val T2 = y.left

        // Perform rotation
        y.left = x
        x.right = T2

        // Update heights
        updateHeight(x)
        updateHeight(y)

        return y
    }

    fun inorder(): List<Int> {
        val result = mutableListOf<Int>()
        inorderRecursive(root, result)
        return result
    }

    private fun inorderRecursive(node: AVLNode?, acc: MutableList<Int>) {
        if (node == null) return
        inorderRecursive(node.left, acc)
        acc.add(node.key)
        inorderRecursive(node.right, acc)
    }

    fun isBalanced(): Boolean = isBalancedRecursive(root)

    private fun isBalancedRecursive(node: AVLNode?): Boolean {
        if (node == null) return true
        val balance = abs(getBalance(node))
        if (balance > 1) return false
        return isBalancedRecursive(node.left) && isBalancedRecursive(node.right)
    }
}