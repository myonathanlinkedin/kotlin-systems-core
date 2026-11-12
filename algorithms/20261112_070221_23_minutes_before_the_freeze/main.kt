import kotlin.random.Random

fun main() {
    // ---------- Test 1: Basic graph ----------
    val edges1 = listOf(
        Triple(0, 1, 2L),
        Triple(0, 2, 5L),
        Triple(1, 2, 1L),
        Triple(1, 3, 2L),
        Triple(2, 3, 3L),
        Triple(3, 4, 1L)
    )
    val graph1 = Graph.fromEdges(5, edges1)
    val result1 = dijkstra(graph1, 0)

    // Expected distances
    val expectedDist1 = listOf(0L, 2L, 3L, 4L, 5L)
    for (i in expectedDist1.indices) {
        check(result1.distances[i] == expectedDist1[i]) {
            "Test1 distance mismatch at node $i: got ${result1.distances[i]}, expected ${expectedDist1[i]}"
        }
    }

    // Expected predecessor chain for node 4: 0 -> 1 -> 3 -> 4
    val expectedPath4 = listOf(0, 1, 3, 4)
    val actualPath4 = reconstructPath(result1, 0, 4)
    check(actualPath4 == expectedPath4) {
        "Test1 path reconstruction failed for node 4: got $actualPath4, expected $expectedPath4"
    }

    // ---------- Test 2: Disconnected node ----------
    val edges2 = listOf(
        Triple(0, 1, 3L),
        Triple(1, 2, 4L)
        // Node 3 is isolated
    )
    val graph2 = Graph.fromEdges(4, edges2)
    val result2 = dijkstra(graph2, 0)

    val expectedDist2 = listOf(0L, 3L, 7L, Long.MAX_VALUE)
    for (i in expectedDist2.indices) {
        check(result2.distances[i] == expectedDist2[i]) {
            "Test2 distance mismatch at node $i: got ${result2.distances[i]}, expected ${expectedDist2[i]}"
        }
    }
    // Path to isolated node should be empty
    check(reconstructPath(result2, 0, 3).isEmpty()) {
        "Test2 expected empty path to isolated node 3"
    }

    // ---------- Test 3: Single node graph ----------
    val graph3 = Graph.fromEdges(1, emptyList())
    val result3 = dijkstra(graph3, 0)
    check(result3.distances.size == 1 && result3.distances[0] == 0L) {
        "Test3 single node distance incorrect"
    }
    check(result3.predecessors[0] == null) {
        "Test3 single node predecessor should be null"
    }

    // ---------- Test 4: Zero‑weight edges ----------
    val edges4 = listOf(
        Triple(0, 1, 0L),
        Triple(1, 2, 0L),
        Triple(0, 2, 5L)
    )
    val graph4 = Graph.fromEdges(3, edges4)
    val result4 = dijkstra(graph4, 0)

    val expectedDist4 = listOf(0L, 0L, 0L)
    for (i in expectedDist4.indices) {
        check(result4.distances[i] == expectedDist4[i]) {
            "Test4 distance mismatch at node $i"
        }
    }
    // Path 0 -> 2 should prefer zero‑weight chain 0-1-2
    val expectedPath2 = listOf(0, 1, 2)
    val actualPath2 = reconstructPath(result4, 0, 2)
    check(actualPath2 == expectedPath2) {
        "Test4 path reconstruction failed for node 2: got $actualPath2, expected $expectedPath2"
    }

    // ---------- Test 5: Random dense graph sanity check ----------
    val nodeCount5 = 6
    val randomEdges = mutableListOf<Triple<Int, Int, Long>>()
    val rand = Random(42)
    for (i in 0 until nodeCount5) {
        for (j in 0 until nodeCount5) {
            if (i != j && rand.nextDouble() < 0.4) {
                val w = rand.nextLong(1L, 20L)
                randomEdges.add(Triple(i, j, w))
            }
        }
    }
    val graph5 = Graph.fromEdges(nodeCount5, randomEdges)
    val source5 = 0
    val result5 = dijkstra(graph5, source5)

    // Verify that all reported distances are non‑negative and reachable nodes have finite distance
    for (i in 0 until nodeCount5) {
        val d = result5.distances[i]
        check(d >= 0L) { "Test5 negative distance for node $i" }
        if (result5.predecessors[i] != null) {
            check(d != Long.MAX_VALUE) { "Test5 predecessor exists but distance is infinite for node $i" }
        }
    }

    // If we reach here, all assertions passed
    println("All tests passed successfully.")
}
