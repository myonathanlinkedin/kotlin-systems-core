package main

import core.Graph
import kotlin.random.Random

fun testSimplePath() {
    val g = Graph(4)
    g.addEdge(0, 1, 5L)
    g.addEdge(1, 2, 3L)
    g.addEdge(2, 3, 2L)
    g.setExit(3)
    val cost = g.minCostToExit(0)
    assert(cost == 10L) { "Expected cost 10, got $cost" }
}

fun testMultipleExits() {
    val g = Graph(5)
    g.addUndirectedEdge(0, 1, 1L)
    g.addUndirectedEdge(1, 2, 2L)
    g.addUndirectedEdge(2, 3, 3L)
    g.addUndirectedEdge(3, 4, 4L)
    g.setExit(0)
    g.setExit(4)
    val costFrom2 = g.minCostToExit(2)
    // Shortest to exit 0: 2+1 =3, to exit 4: 3+4=7 => choose 3
    assert(costFrom2 == 3L) { "Expected cost 3, got $costFrom2" }
}

fun testUnreachable() {
    val g = Graph(3)
    g.addEdge(0, 1, 1L)
    // Node 2 isolated
    g.setExit(2)
    val cost = g.minCostToExit(0)
    assert(cost == null) { "Expected null for unreachable exit, got $cost" }
}

fun testAllMinCosts() {
    val g = Graph(6)
    g.addUndirectedEdge(0, 1, 2L)
    g.addUndirectedEdge(1, 2, 2L)
    g.addUndirectedEdge(2, 3, 2L)
    g.addUndirectedEdge(3, 4, 2L)
    g.addUndirectedEdge(4, 5, 2L)
    g.setExit(0)
    g.setExit(5)
    var all = g.allMinCostsToExit()
    // Expected distances: 0->0,1->2,2->4,3->4,4->2,5->0
    val expected = mapOf(
        0 to 0L,
        1 to 2L,
        2 to 4L,
        3 to 4L,
        4 to 2L,
        5 to 0L
    )
    for ((node, exp) in expected) {
        assert(all[node] == exp) { "Node $node expected $exp, got ${all[node]}" }
    }
}

fun testRandomGraphConsistency() {
    val n = 10
    val g = Graph(n)
    val rand = Random(42)
    repeat(30) {
        val a = rand.nextInt(n)
        val b = rand.nextInt(n)
        val w = rand.nextLong(1L, 10L)
        g.addUndirectedEdge(a, b, w)
    }
    // Choose random exits
    val exits = listOf(0, n - 1)
    for (e in exits) g.setExit(e)

    // Verify that allMinCostsToExit gives same result as repeated single-source queries
    var all = g.allMinCostsToExit()
    for (src in 0 until n) {
        var single = g.minCostToExit(src)
        assert(all[src] == single) { "Mismatch at node $src: all=${all[src]}, single=$single" }
    }
}

fun main() {
    testSimplePath()
    testMultipleExits()
    testUnreachable()
    testAllMinCosts()
    testRandomGraphConsistency()
    println("All Easegress tests passed.")
}