import kotlin.math.*

fun expectException(block: () -> Unit): Boolean {
    return try {
        block()
        false
    } catch (e: IllegalStateException) {
        true
    }
}

fun main() {
    val manager = MemoryManager()

    // Test 1: Normal allocation, write, read
    val handleA = manager.allocate(5)
    manager.write(handleA, 0, 10)
    manager.write(handleA, 4, 50)
    assert(manager.read(handleA, 0) == 10)
    assert(manager.read(handleA, 4) == 50)

    // Test 2: Out-of-bounds access
    assert(expectException { manager.read(handleA, 5) })
    assert(expectException { manager.write(handleA, -1, 0) })

    // Test 3: Double free detection
    manager.free(handleA)
    assert(expectException { manager.free(handleA) })
    assert(expectException { manager.read(handleA, 0) })
    assert(expectException { manager.write(handleA, 0, 1) })

    // Test 4: Invalid handle usage
    val bogusHandle = AllocationHandle(9999)
    assert(expectException { manager.read(bogusHandle, 0) })
    assert(expectException { manager.write(bogusHandle, 0, 1) })
    assert(expectException { manager.free(bogusHandle) })

    // Test 5: Zero-size allocation should be rejected
    assert(expectException { manager.allocate(0) })

    // Test 6: Multiple allocations and independence
    val h1 = manager.allocate(3)
    val h2 = manager.allocate(3)
    manager.write(h1, 0, 1)
    manager.write(h2, 0, 2)
    assert(manager.read(h1, 0) == 1)
    assert(manager.read(h2, 0) == 2)
    manager.free(h1)
    assert(expectException { manager.read(h1, 0) })
    assert(manager.read(h2, 0) == 2)

    // All assertions passed
    println("All memory safety tests passed.")
}
