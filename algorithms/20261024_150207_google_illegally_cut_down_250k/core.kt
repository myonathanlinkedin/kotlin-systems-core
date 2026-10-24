data class BSTNode(var key: Int, var left: BSTNode? = null, var right: BSTNode? = null)

class BinarySearchTree {
    var root: BSTNode? = null

    fun insert(key: Int) {
        root = insertRec(root, key)
    }

    private fun insertRec(node: BSTNode?, key: Int): BSTNode {
        if (node == null) return BSTNode(key)
        if (key < node.key) {
            node.left = insertRec(node.left, key)
        } else if (key > node.key) {
            node.right = insertRec(node.right, key)
        }
        return node
    }

    fun search(key: Int): Boolean {
        return searchRec(root, key)
    }

    private fun searchRec(node: BSTNode?, key: Int): Boolean {
        if (node == null) return false
        return when {
            key == node.key -> true
            key < node.key -> searchRec(node.left, key)
            else -> searchRec(node.right, key)
        }
    }

    fun delete(key: Int) {
        root = deleteRec(root, key)
    }

    private fun deleteRec(node: BSTNode?, key: Int): BSTNode? {
        if (node == null) return null
        when {
            key < node.key -> node.left = deleteRec(node.left, key)
            key > node.key -> node.right = deleteRec(node.right, key)
            else -> {
                if (node.left == null) return node.right
                if (node.right == null) return node.left
                val successor = minValueNode(node.right!!)
                node.key = successor.key
                node.right = deleteRec(node.right, successor.key)
            }
        }
        return node
    }

    private fun minValueNode(node: BSTNode): BSTNode {
        var current = node
        while (current.left != null) {
            current = current.left!!
        }
        return current
    }

    fun inorder(): List<Int> {
        val result = mutableListOf<Int>()
        inorderRec(root, result)
        return result
    }

    private fun inorderRec(node: BSTNode?, result: MutableList<Int>) {
        if (node == null) return
        inorderRec(node.left, result)
        result.add(node.key)
        inorderRec(node.right, result)
    }

    fun size(): Int {
        return sizeRec(root)
    }

    private fun sizeRec(node: BSTNode?): Int {
        if (node == null) return 0
        return 1 + sizeRec(node.left) + sizeRec(node.right)
    }
}
