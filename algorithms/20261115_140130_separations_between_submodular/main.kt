package main

import core.Graph
import core.stoerWagnerMinCut

fun testTriangleGraph() {
    // Triangle with unit weight edges. Minimum cut = 2.
    val g = Graph.empty(3)
    g.addUndirectedEdge(0, 1, 1L)
    g.addUndirectedEdge(1, 2, 1L)
    g.addUndirectedEdge(2, 0, 1L)
    var cut = stoerWagnerMinCut(g)
    assert(cut == 2L) { "Triangle graph expected cut 2, got $cut" }
}

fun testTwoVertexGraph() {
    // Two vertices with weight 5 edge. Minimum cut = 5.
    val g = Graph.empty(2)
    g.addUndirectedEdge(0, 1, 5L)
    var cut = stoerWagnerMinCut(g)
    assert(cut == 5L) { "Two‑vertex graph expected cut 5, got $cut" }
}

fun testSquareWithDiagonal() {
    // Square (0‑1‑2‑3‑0) with unit edges, diagonal 0‑2 weight 10.
    // Minimum cut = 2 (cut two opposite edges of the square).
    val g = Graph.empty(4)
    g.addUndirectedEdge(0, 1, 1L)
    g.addUndirectedEdge(1, 2, 1L)
    g.addUndirectedEdge(2, 3, 1L)
    g.addUndirectedEdge(3, 0, 1L)
    g.addUndirectedEdge(0, 2, 10L)
    var cut = stoerWagnerMinCut(g)
    assert(cut == 2L) { "Square with diagonal expected cut 2, got $cut" }
}

fun testDisconnectedGraph() {
    // Graph with two components: {0,1} edge weight 3, {2,3} edge weight 4.
    // Global min cut = 0 because there is already a separation.
    val g = Graph.empty(4)
    g.addUndirectedEdge(0, 1, 3L)
    g.addUndirectedEdge(2, 3, 4L)
    var cut = stoerWagnerMinCut(g)
    assert(cut == 0L) { "Disconnected graph expected cut 0, got $cut" }
}

fun runAllTests() {
    testTriangleGraph()
    testTwoVertexGraph()
    testSquareWithDiagonal()
    testDisconnectedGraph()
    println("All tests passed.")
}

fun main() {
    runAllTests()
}