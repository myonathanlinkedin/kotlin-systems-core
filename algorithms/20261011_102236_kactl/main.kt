package main

import kotlin.random.Random

fun main() {
    // Basic deterministic test
    val dsu1 = DsuEngine.makeSet(5)
    check(!DsuEngine.areConnected(dsu1, 0, 1))
    DsuEngine.merge(dsu1, 0, 1)
    check(DsuEngine.areConnected(dsu1, 0, 1))
    check(DsuEngine.componentSize(dsu1, 0) == 2)
    check(DsuEngine.componentSize(dsu1, 1) == 2)

    // Merging already connected components should return false
    val mergedAgain = DsuEngine.merge(dsu1, 1, 0)
    check(!mergedAgain)

    // Chain merging
    DsuEngine.merge(dsu1, 1, 2)
    DsuEngine.merge(dsu1, 2, 3)
    DsuEngine.merge(dsu1, 3, 4)
    for (i in 0 until 5) {
        check(DsuEngine.areConnected(dsu1, 0, i))
        check(DsuEngine.componentSize(dsu1, i) == 5)
    }

    // Edge case: self‑union
    val selfMerged = DsuEngine.merge(dsu1, 2, 2)
    check(!selfMerged)
    check(DsuEngine.componentSize(dsu1, 2) == 5)

    // Randomized stress test with deterministic seed
    val seed = 123456789L
    val rand = Random(seed)
    val n = 1000
    val dsu2 = DsuEngine.makeSet(n)
    var expectedComponents = n

    repeat(5000) {
        val a = rand.nextInt(n)
        val b = rand.nextInt(n)
        if (DsuEngine.merge(dsu2, a, b)) {
            expectedComponents--
        }
        // Verify that component count never goes below 1
        check(expectedComponents >= 1)
    }

    // Verify that the number of distinct roots equals expectedComponents
    val roots = mutableSetOf<Int>()
    for (i in 0 until n) {
        roots.add(dsu2.find(i))
    }
    check(roots.size == expectedComponents)

    // Verify transitivity: if a~b and b~c then a~c
    repeat(1000) {
        val a = rand.nextInt(n)
        val b = rand.nextInt(n)
        val c = rand.nextInt(n)
        if (DsuEngine.areConnected(dsu2, a, b) && DsuEngine.areConnected(dsu2, b, c)) {
            check(DsuEngine.areConnected(dsu2, a, c))
        }
    }

    // All tests passed
    println("All DSU unit tests passed.")
}