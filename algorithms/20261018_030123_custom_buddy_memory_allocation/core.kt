package buddy

import kotlin.math.*

/**
 * Buddy memory allocator managing a contiguous memory region.
 *
 * @param totalSize total size of the memory pool; must be a power of two and >= minBlockSize.
 * @param minBlockSize smallest allocatable block size; must be a power of two.
 */
class BuddyAllocator(
    val totalSize: Int,
    val minBlockSize: Int = 16
) {
    // Number of orders: order 0 corresponds to minBlockSize, order maxOrder to totalSize.
    var maxOrder: Int

    // Free lists indexed by order; each list holds offsets of free blocks of that order.
    val freeLists: Array<MutableList<Int>>

    init {
        require(totalSize > 0 && (totalSize and (totalSize - 1)) == 0) { "totalSize must be power of two" }
        require(minBlockSize > 0 && (minBlockSize and (minBlockSize - 1)) == 0) { "minBlockSize must be power of two" }
        require(totalSize >= minBlockSize) { "totalSize must be >= minBlockSize" }

        var order = 0
        var size = minBlockSize
        while (size < totalSize) {
            size = size shl 1
            order++
        }
        maxOrder = order
        freeLists = Array(maxOrder + 1) { mutableListOf<Int>() }
        // Initially the whole memory is one free block at maxOrder.
        freeLists[maxOrder].add(0)
    }

    /**
     * Allocate a block of at least [size] bytes.
     *
     * @return offset of allocated block within the pool, or null if allocation fails.
     */
    fun allocate(size: Int): Int? {
        if (size <= 0) return null
        val order = orderForSize(size)
        var currentOrder = order
        // Find the smallest free block that can satisfy the request.
        while (currentOrder <= maxOrder && freeLists[currentOrder].isEmpty()) {
            currentOrder++
        }
        if (currentOrder > maxOrder) {
            // No suitable block.
            return null
        }
        // Remove a block from the found order.
        var offset = freeLists[currentOrder].removeAt(freeLists[currentOrder].lastIndex)
        // Split until we reach the desired order.
        while (currentOrder > order) {
            currentOrder--
            val buddyOffset = offset + blockSize(currentOrder)
            freeLists[currentOrder].add(buddyOffset)
        }
        return offset
    }

    /**
     * Free a previously allocated block.
     *
     * @param offset offset returned by [allocate].
     * @param size original requested size used during allocation.
     */
    fun free(offset: Int, size: Int) {
        require(offset >= 0 && offset < totalSize) { "Invalid offset" }
        require(size > 0) { "Size must be positive" }
        var order = orderForSize(size)
        var currentOffset = offset
        while (order < maxOrder) {
            val buddyOffset = buddyOf(currentOffset, order)
            val buddyList = freeLists[order]
            val buddyIndex = buddyList.indexOf(buddyOffset)
            if (buddyIndex == -1) {
                // Buddy not free; stop coalescing.
                break
            }
            // Remove buddy and coalesce.
            buddyList.removeAt(buddyIndex)
            currentOffset = min(currentOffset, buddyOffset)
            order++
        }
        freeLists[order].add(currentOffset)
    }

    /** Compute block size for a given order. */
    private fun blockSize(order: Int): Int = minBlockSize shl order

    /** Determine the smallest order whose block size can accommodate [size]. */
    private fun orderForSize(size: Int): Int {
        var order = 0
        var block = minBlockSize
        while (block < size) {
            block = block shl 1
            order++
        }
        return order
    }

    /** Compute buddy offset for a block at [offset] with given [order]. */
    private fun buddyOf(offset: Int, order: Int): Int {
        var size = blockSize(order)
        return offset xor size
    }

    /** For debugging: snapshot of free blocks per order. */
    fun snapshot(): List<List<Int>> = freeLists.map { it.toList() }
}