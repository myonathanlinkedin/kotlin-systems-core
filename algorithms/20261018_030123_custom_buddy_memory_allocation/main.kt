package buddy

import kotlin.random.Random

import kotlin.math.*

fun runTests() {
    // Test 1: Simple allocation and free.
    val allocator1 = BuddyAllocator(1024, 32)
    val a1 = allocator1.allocate(100)
    check(a1 != null) { "Allocation failed when it should succeed" }
    allocator1.free(a1!!, 100)
    // After freeing, the whole pool should be back to a single block.
    val snap1 = allocator1.snapshot()
    check(snap1[allocator1.maxOrder].size == 1 && snap1[allocator1.maxOrder][0] == 0) {
        "Pool not fully restored after free"
    }

    // Test 2: Allocate multiple blocks and ensure no overlap.
    val allocator2 = BuddyAllocator(2048, 64)
    val offsets = mutableListOf<Int>()
    repeat(5) { i ->
        val sz = 100 + i * 20
        val off = allocator2.allocate(sz)
        check(off != null) { "Failed to allocate block $i of size $sz" }
        // Verify no overlap with previous allocations.
        for (prev in offsets) {
            check(prev != off) { "Overlap detected between allocations" }
        }
        offsets.add(off!!)
    }
    // Free in reverse order.
    for (i in offsets.indices.reversed()) {
        val sz = 100 + i * 20
        allocator2.free(offsets[i], sz)
    }
    // After all frees, pool should be restored.
    val snap2 = allocator2.snapshot()
    check(snap2[allocator2.maxOrder].size == 1 && snap2[allocator2.maxOrder][0] == 0) {
        "Pool not fully restored after multiple frees"
    }

    // Test 3: Allocation larger than pool.
    val allocator3 = BuddyAllocator(512, 32)
    val tooBig = allocator3.allocate(1024)
    check(tooBig == null) { "Allocation should fail for oversized request" }

    // Test 4: Allocate exact block size (power of two).
    val allocator4 = BuddyAllocator(256, 16)
    val exact = allocator4.allocate(64)
    check(exact != null) { "Exact size allocation failed" }
    allocator4.free(exact!!, 64)
    val snap4 = allocator4.snapshot()
    check(snap4[allocator4.maxOrder].size == 1 && snap4[allocator4.maxOrder][0] == 0) {
        "Exact size free failed"
    }

    // Test 5: Stress test random allocations and frees.
    val allocator5 = BuddyAllocator(4096, 32)
    val random = kotlin.random.Random(0)
    data class Allocation(val offset: Int, val size: Int)
    val active = mutableListOf<Allocation>()
    repeat(1000) {
        if (random.nextBoolean() && active.isNotEmpty()) {
            // Free a random active allocation.
            val idx = random.nextInt(active.size)
            val alloc = active.removeAt(idx)
            allocator5.free(alloc.offset, alloc.size)
        } else {
            // Attempt a new allocation.
            val reqSize = random.nextInt(1, 200)
            val off = allocator5.allocate(reqSize)
            if (off != null) {
                active.add(Allocation(off, reqSize))
            }
        }
    }
    // Free remaining allocations.
    for (alloc in active) {
        allocator5.free(alloc.offset, alloc.size)
    }
    val snap5 = allocator5.snapshot()
    check(snap5[allocator5.maxOrder].size == 1 && snap5[allocator5.maxOrder][0] == 0) {
        "Pool not fully restored after stress test"
    }
}

fun main() {
    runTests()
    println("All BuddyAllocator tests passed.")
}