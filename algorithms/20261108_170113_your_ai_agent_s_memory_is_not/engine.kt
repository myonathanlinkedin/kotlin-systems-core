package main

class LRUCache<K, V>(capacity: Int) {
    val maxSize: Int = if (capacity < 0) 0 else capacity
    val cacheMap = mutableMapOf<K, DoublyNode<K, V>>()
    var head: DoublyNode<K, V>? = null
    var tail: DoublyNode<K, V>? = null

    operator fun get(key: K): V? {
        val node = cacheMap[key] ?: return null
        moveToHead(node)
        return node.value
    }

    fun put(key: K, value: V) {
        if (maxSize == 0) return
        val existing = cacheMap[key]
        if (existing != null) {
            existing.value = value
            moveToHead(existing)
        } else {
            val newNode = DoublyNode(key, value)
            cacheMap[key] = newNode
            addNode(newNode)
            if (cacheMap.size > maxSize) {
                val tailNode = popTail()
                if (tailNode != null) {
                    cacheMap.remove(tailNode.key)
                }
            }
        }
    }

    fun addNode(node: DoublyNode<K, V>) {
        node.prev = null
        node.next = head
        if (head != null) {
            head?.prev = node
        }
        head = node
        if (tail == null) {
            tail = node
        }
    }

    fun removeNode(node: DoublyNode<K, V>) {
        val prevNode = node.prev
        val nextNode = node.next
        if (prevNode != null) {
            prevNode.next = nextNode
        } else {
            head = nextNode
        }
        if (nextNode != null) {
            nextNode.prev = prevNode
        } else {
            tail = prevNode
        }
        node.prev = null
        node.next = null
    }

    fun moveToHead(node: DoublyNode<K, V>) {
        removeNode(node)
        addNode(node)
    }

    fun popTail(): DoublyNode<K, V>? {
        val res = tail
        if (res != null) {
            removeNode(res)
        }
        return res
    }
}