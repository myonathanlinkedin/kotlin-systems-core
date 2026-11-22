package main

fun main() {
    // Basic construction and push
    val v1 = CowVector.empty<Int>().push(1).push(2).push(3)
    check(v1.size() == 3)
    check(v1[0] == 1 && v1[1] == 2 && v1[2] == 3)

    // Copy and verify shared buffer
    val v2 = v1.copy()
    check(v2.size() == 3)
    check(v2[1] == 2)

    // Modify v2 via set, should trigger copy-on-write
    val v3 = v2.set(1, 20)
    check(v3[1] == 20)
    // Original vectors remain unchanged
    check(v1[1] == 2)
    check(v2[1] == 2)

    // Pop operation
    val (removed, v4) = v3.pop()
    check(removed == 3)
    check(v4.size() == 2)
    check(v4[0] == 1 && v4[1] == 20)

    // Ensure pop does not affect previous vectors
    check(v3.size() == 3)
    check(v3[2] == 3)

    // Edge case: pop until empty
    val (r1, v5) = v4.pop()
    val (r2, v6) = v5.pop()
    check(r1 == 20 && r2 == 1)
    check(v6.size() == 0)

    // Verify that further pop on empty throws
    var exceptionThrown = false
    try {
        v6.pop()
    } catch (e: NoSuchElementException) {
        exceptionThrown = true
    }
    check(exceptionThrown)

    // Verify out-of-bounds access throws
    exceptionThrown = false
    try {
        v1[5]
    } catch (e: IndexOutOfBoundsException) {
        exceptionThrown = true
    }
    check(exceptionThrown)

    // Demonstrate that multiple copies share buffer until mutation
    val a = CowVector.empty<String>().push("a").push("b")
    val b = a.copy()
    val c = a.copy()
    // No mutation yet, all refCounts should be 3 (internal check via reflection not needed)
    val d = b.set(0, "z")
    check(d[0] == "z")
    check(a[0] == "a")
    check(c[0] == "a")

    // All tests passed
    println("All assertions passed.")
}