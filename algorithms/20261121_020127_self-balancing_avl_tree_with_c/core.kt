import kotlin.math.max

data class AVLNode(
    var key: Int,
    var value: String,
    var height: Int = 1,
    var left: AVLNode? = null,
    var right: AVLNode? = null
)

class AVLTree {
    var root: AVLNode? = null

    fun insert(key: Int, value: String) {
        root = insertNode(root, key, value)
    }

    fun delete(key: Int) {
        root = deleteNode(root, key)
    }

    fun find(key: Int): String? {
        var current = root
        while (current != null) {
            when {
                key < current.key -> current = current.left
                key > current.key -> current = current.right
                else -> return current.value
            }
        }
        return null
    }

    fun inorder(): List<Pair<Int, String>> {
        val result = mutableListOf<Pair<Int, String>>()
        inorderTraverse(root, result)
        return result
    }

    // ---------- internal helpers (public per specification) ----------
    fun height(node: AVLNode?): Int = node?.height ?: 0

    fun getBalance(node: AVLNode?): Int = height(node?.left) - height(node?.right)

    fun rightRotate(y: AVLNode): AVLNode {
        val x = y.left!!
        val T2 = x.right

        // Perform rotation
        x.right = y
        y.left = T2

        // Update heights
        y.height = max(height(y.left), height(y.right)) + 1
        x.height = max(height(x.left), height(x.right)) + 1

        // Return new root
        return x
    }

    fun leftRotate(x: AVLNode): AVLNode {
        val y = x.right!!
        val T2 = y.left

        // Perform rotation
        y.left = x
        x.right = T2

        // Update heights
        x.height = max(height(x.left), height(x.right)) + 1
        y.height = max(height(y.left), height(y.right)) + 1

        // Return new root
        return y
    }

    fun insertNode(node: AVLNode?, key: Int, value: String): AVLNode {
        if (node == null) {
            return AVLNode(key, value)
        }

        when {
            key < node.key -> node.left = insertNode(node.left, key, value)
            key > node.key -> node.right = insertNode(node.right, key, value)
            else -> {
                // Duplicate key: update value
                node.value = value
                return node
            }
        }

        // Update height of this ancestor node
        node.height = max(height(node.left), height(node.right)) + 1

        // Get balance factor
        val balance = getBalance(node)

        // Left Left Case
        if (balance > 1 && key < node.left!!.key) {
            return rightRotate(node)
        }

        // Right Right Case
        if (balance < -1 && key > node.right!!.key) {
            return leftRotate(node)
        }

        // Left Right Case
        if (balance > 1 && key > node.left!!.key) {
            node.left = leftRotate(node.left!!)
            return rightRotate(node)
        }

        // Right Left Case
        if (balance < -1 && key < node.right!!.key) {
            node.right = rightRotate(node.right!!)
            return leftRotate(node)
        }

        // Return unchanged node pointer
        return node
    }

    fun minValueNode(node: AVLNode): AVLNode {
        var current = node
        while (current.left != null) {
            current = current.left!!
        }
        return current
    }

    fun deleteNode(rootNode: AVLNode?, key: Int): AVLNode? {
        if (rootNode == null) {
            return null
        }

        when {
            key < rootNode.key -> rootNode.left = deleteNode(rootNode.left, key)
            key > rootNode.key -> rootNode.right = deleteNode(rootNode.right, key)
            else -> {
                // Node with only one child or no child
                if (rootNode.left == null) {
                    return rootNode.right
                } else if (rootNode.right == null) {
                    return rootNode.left
                }

                // Node with two children: Get inorder successor (smallest in the right subtree)
                val temp = minValueNode(rootNode.right!!)
                // Copy the inorder successor's data to this node
                rootNode.key = temp.key
                rootNode.value = temp.value
                // Delete the inorder successor
                rootNode.right = deleteNode(rootNode.right, temp.key)
            }
        }

        // If the tree had only one node then return
        if (rootNode == null) {
            return null
        }

        // Update height
        rootNode.height = max(height(rootNode.left), height(rootNode.right)) + 1

        // Get balance factor
        val balance = getBalance(rootNode)

        // Left Left Case
        if (balance > 1 && getBalance(rootNode.left) >= 0) {
            return rightRotate(rootNode)
        }

        // Left Right Case
        if (balance > 1 && getBalance(rootNode.left) < 0) {
            rootNode.left = leftRotate(rootNode.left!!)
            return rightRotate(rootNode)
        }

        // Right Right Case
        if (balance < -1 && getBalance(rootNode.right) <= 0) {
            return leftRotate(rootNode)
        }

        // Right Left Case
        if (balance < -1 && getBalance(rootNode.right) > 0) {
            rootNode.right = rightRotate(rootNode.right!!)
            return leftRotate(rootNode)
        }

        return rootNode
    }

    fun inorderTraverse(node: AVLNode?, acc: MutableList<Pair<Int, String>>) {
        if (node == null) return
        inorderTraverse(node.left, acc)
        acc.add(Pair(node.key, node.value))
        inorderTraverse(node.right, acc)
    }
}
