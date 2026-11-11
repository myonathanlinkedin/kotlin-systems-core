import kotlin.math.*

class MemoryBlock(val size: Int) {
    val data: IntArray = IntArray(size)
    var isFreed: Boolean = false
}

class MemoryManager {
    var nextId: Int = 1
    var allocations: MutableMap<Int, MemoryBlock> = mutableMapOf()

    fun allocate(size: Int): AllocationHandle {
        check(size > 0) { MemoryError.ZERO_ALLOCATION.name }
        val handle = AllocationHandle(nextId++)
        val block = MemoryBlock(size)
        allocations[handle.id] = block
        return handle
    }

    fun free(handle: AllocationHandle) {
        val block = allocations[handle.id] ?: throw IllegalStateException(MemoryError.INVALID_HANDLE.name)
        check(!block.isFreed) { MemoryError.DOUBLE_FREE.name }
        block.isFreed = true
        // Optionally remove from map to simulate deallocation
        allocations.remove(handle.id)
    }

    fun write(handle: AllocationHandle, offset: Int, value: Int) {
        val block = allocations[handle.id] ?: throw IllegalStateException(MemoryError.INVALID_HANDLE.name)
        check(!block.isFreed) { MemoryError.DOUBLE_FREE.name }
        check(offset in 0 until block.size) { MemoryError.OUT_OF_BOUNDS.name }
        block.data[offset] = value
    }

    fun read(handle: AllocationHandle, offset: Int): Int {
        val block = allocations[handle.id] ?: throw IllegalStateException(MemoryError.INVALID_HANDLE.name)
        check(!block.isFreed) { MemoryError.DOUBLE_FREE.name }
        check(offset in 0 until block.size) { MemoryError.OUT_OF_BOUNDS.name }
        return block.data[offset]
    }
}
