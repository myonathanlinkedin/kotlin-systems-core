import kotlin.math.max

enum class Color { RED, BLACK }

data class Node<T : Comparable<T>>(
    var value: T,
    var color: Color = Color.RED,
    var left: Node<T>? = null,
    var right: Node<T>? = null,
    var parent: Node<T>? = null
)

class RedBlackTree<T : Comparable<T>> {
    var root: Node<T>? = null
        private set
    var size: Int = 0
        private set

    // Public insert API – ignores duplicates
    fun insert(key: T) {
        if (root == null) {
            root = Node(key, Color.BLACK)
            size = 1
            return
        }
        var current = root
        var parent: Node<T>? = null
        while (current != null) {
            parent = current
            when {
                key < current.value -> current = current.left
                key > current.value -> current = current.right
                else -> return // duplicate, ignore
            }
        }
        val newNode = Node(key, Color.RED, parent = parent)
        if (key < parent!!.value) parent.left = newNode else parent.right = newNode
        size++
        fixInsert(newNode)
    }

    // Search API
    fun contains(key: T): Boolean {
        var cur = root
        while (cur != null) {
            cur = when {
                key < cur.value -> cur.left
                key > cur.value -> cur.right
                else -> return true
            }
        }
        return false
    }

    // In‑order traversal returning a list
    fun toList(): List<T> {
        val result = mutableListOf<T>()
        fun inorder(node: Node<T>?) {
            if (node == null) return
            inorder(node.left)
            result.add(node.value)
            inorder(node.right)
        }
        inorder(root)
        return result
    }

    // ---------- Red‑Black fix‑up ----------
    private fun rotateLeft(x: Node<T>) {
        val y = x.right ?: return
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

    private fun rotateRight(y: Node<T>) {
        val x = y.left ?: return
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

    private fun fixInsert(z0: Node<T>) {
        var z = z0
        while (z.parent?.color == Color.RED) {
            val p = z.parent!!
            val gp = p.parent!!
            if (p === gp.left) {
                val y = gp.right
                if (y?.color == Color.RED) {
                    p.color = Color.BLACK
                    y.color = Color.BLACK
                    gp.color = Color.RED
                    z = gp
                } else {
                    if (z === p.right) {
                        z = p
                        rotateLeft(z)
                    }
                    p.color = Color.BLACK
                    gp.color = Color.RED
                    rotateRight(gp)
                }
            } else {
                val y = gp.left
                if (y?.color == Color.RED) {
                    p.color = Color.BLACK
                    y.color = Color.BLACK
                    gp.color = Color.RED
                    z = gp
                } else {
                    if (z === p.left) {
                        z = p
                        rotateRight(z)
                    }
                    p.color = Color.BLACK
                    gp.color = Color.RED
                    rotateLeft(gp)
                }
            }
        }
        root?.color = Color.BLACK
    }

    // ---------- Validation ----------
    fun isValid(): Boolean {
        // Property 1: root is black
        if (root?.color != Color.BLACK) return false
        // Property 2 & 3: no red node has red child, and all leaves are black (nulls are black by definition)
        // Property 4: same black‑height on all paths
        fun check(node: Node<T>?): Pair<Boolean, Int> {
            if (node == null) return Pair(true, 1) // null leaf contributes black count 1
            // No consecutive reds
            if (node.color == Color.RED) {
                if (node.left?.color == Color.RED || node.right?.color == Color.RED) return Pair(false, 0)
            }
            val leftRes = check(node.left)
            val rightRes = check(node.right)
            if (!leftRes.first || !rightRes.first) return Pair(false, 0)
            if (leftRes.second != rightRes.second) return Pair(false, 0)
            val blackAdd = if (node.color == Color.BLACK) 1 else 0
            return Pair(true, leftRes.second + blackAdd)
        }
        return check(root).first
    }
}
