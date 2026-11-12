package core

import java.util.concurrent.locks.ReentrantLock

data class Node(
    val key: Int,
    @Volatile var next: Node?,
    @Volatile var marked: Boolean = false,
    val lock: ReentrantLock = ReentrantLock()
)

class OptimisticList {
    private val tail = Node(Int.MAX_VALUE, null)
    private val head = Node(Int.MIN_VALUE, tail)

    /**
     * Find the predecessor and current node for a given key.
     * Both returned nodes are locked and validated.
     */
    fun find(key: Int): Pair<Node, Node> {
        while (true) {
            var pred = head
            var curr = pred.next!!

            // Traverse without locks
            while (curr.key < key) {
                pred = curr
                curr = curr.next!!
            }

            // Acquire locks in key order to avoid deadlock
            pred.lock.lock()
            curr.lock.lock()

            // Validate that nodes are still adjacent and not logically removed
            if (!pred.marked && !curr.marked && pred.next === curr) {
                return Pair(pred, curr)
            } else {
                // Validation failed – release and retry
                pred.lock.unlock()
                curr.lock.unlock()
            }
        }
    }

    /**
     * Insert a key into the list if absent.
     * Returns true if insertion succeeded.
     */
    fun add(key: Int): Boolean {
        while (true) {
            val (pred, curr) = find(key)
            return if (curr.key == key) {
                // Key already present
                pred.lock.unlock()
                curr.lock.unlock()
                false
            } else {
                // Insert new node between pred and curr
                var node = Node(key, curr)
                pred.next = node
                pred.lock.unlock()
                curr.lock.unlock()
                true
            }
        }
    }

    /**
     * Remove a key from the list if present.
     * Returns true if removal succeeded.
     */
    fun remove(key: Int): Boolean {
        while (true) {
            val (pred, curr) = find(key)
            return if (curr.key != key) {
                // Key not found
                pred.lock.unlock()
                curr.lock.unlock()
                false
            } else {
                // Logical deletion then physical unlink
                curr.marked = true
                pred.next = curr.next
                curr.lock.unlock()
                pred.lock.unlock()
                true
            }
        }
    }

    /**
     * Check whether a key is present and not logically removed.
     */
    fun contains(key: Int): Boolean {
        var pred = head
        var curr = pred.next!!

        while (curr.key < key) {
            pred = curr
            curr = curr.next!!
        }

        // Lock the candidate node to read its state safely
        curr.lock.lock()
        val result = (curr.key == key && !curr.marked)
        curr.lock.unlock()
        return result
    }

    /**
     * Produce a snapshot of the current keys in ascending order.
     */
    fun toList(): List<Int> {
        val result = mutableListOf<Int>()
        var node = head.next
        while (node != null && node !== tail) {
            if (!node.marked) {
                result.add(node.key)
            }
            node = node.next
        }
        return result
    }
}