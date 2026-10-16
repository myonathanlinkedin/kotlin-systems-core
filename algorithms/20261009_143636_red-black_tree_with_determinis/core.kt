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
    var root: Node<K, V>? = null
    var size: Int = 0

    operator fun get(searchKey: K): V? {
        var current = root
        while (current != null) {
            val cmp = searchKey.compareTo(current.key)
            when {
                cmp == 0 -> return current.value
                cmp < 0 -> current = current.left
                else -> current = current.right
            }
        }
        return null
    }

    fun insert(newKey: K, newValue: V) {
        var y: Node<K, V>? = null
        var x = root
        while (x != null) {
            y = x
            val cmp = newKey.compareTo(x.key)
            when {
                cmp == 0 -> {
                    x.value = newValue
                    return
                }
                cmp < 0 -> x = x.left
                else -> x = x.right
            }
        }
        var z = Node(newKey, newValue, Color.RED, null, null, y)
        if (y == null) {
            root = z
        } else if (newKey.compareTo(y.key) < 0) {
            y.left = z
        } else {
            y.right = z
        }
        size++
        fixInsert(z)
    }

    private fun rotateLeft(x: Node<K, V>) {
        var y = x.right ?: return
        x.right = y.left
        if (y.left != null) y.left!!.parent = x
        y.parent = x.parent
        when {
            x.parent == null -> root = y
            x === x.parent!!.left -> x.parent!!.left = y
            else -> x.parent!!.right = y
        }
        y.left = x
        x.parent = y
    }

    private fun rotateRight(y: Node<K, V>) {
        var x = y.left ?: return
        y.left = x.right
        if (x.right != null) x.right!!.parent = y
        x.parent = y.parent
        when {
            y.parent == null -> root = x
            y === y.parent!!.right -> y.parent!!.right = x
            else -> y.parent!!.left = x
        }
        x.right = y
        y.parent = x
    }

    private fun fixInsert(z0: Node<K, V>) {
        var z = z0
        while (z.parent?.color == Color.RED) {
            val p = z.parent!!
            val g = p.parent!!
            if (p === g.left) {
                var y = g.right
                if (y?.color == Color.RED) {
                    p.color = Color.BLACK
                    y.color = Color.BLACK
                    g.color = Color.RED
                    z = g
                } else {
                    if (z === p.right) {
                        z = p
                        rotateLeft(z)
                    }
                    p.color = Color.BLACK
                    g.color = Color.RED
                    rotateRight(g)
                }
            } else {
                var y = g.left
                if (y?.color == Color.RED) {
                    p.color = Color.BLACK
                    y.color = Color.BLACK
                    g.color = Color.RED
                    z = g
                } else {
                    if (z === p.left) {
                        z = p
                        rotateRight(z)
                    }
                    p.color = Color.BLACK
                    g.color = Color.RED
                    rotateLeft(g)
                }
            }
        }
        root?.color = Color.BLACK
    }

    fun validate() {
        // Property 1: root is black
        check(root?.color == Color.BLACK || root == null) { "Root must be black" }

        // Property 2 & 3: no red node has red child, and all paths have same black height
        fun dfs(node: Node<K, V>?): Int {
            if (node == null) return 1 // null leaves count as black
            if (node.color == Color.RED) {
                check(node.left?.color != Color.RED) { "Red node with red left child at key ${node.key}" }
                check(node.right?.color != Color.RED) { "Red node with red right child at key ${node.key}" }
            }
            val leftBlack = dfs(node.left)
            val rightBlack = dfs(node.right)
            check(leftBlack == rightBlack) { "Black height mismatch at key ${node.key}" }
            return leftBlack + if (node.color == Color.BLACK) 1 else 0
        }
        dfs(root)
    }

    fun inorder(): List<Pair<K, V>> {
        val result = mutableListOf<Pair<K, V>>()
        fun traverse(node: Node<K, V>?) {
            if (node == null) return
            traverse(node.left)
            result.add(Pair(node.key, node.value))
            traverse(node.right)
        }
        traverse(root)
        return result
    }
}
