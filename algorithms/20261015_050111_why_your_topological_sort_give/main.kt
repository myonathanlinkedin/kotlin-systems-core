package topological

fun main() {
    // Helper to build a graph from edge list
    fun buildGraph(edges: List<Pair<Int, Int>>): Graph {
        var nodes = mutableSetOf<Int>()
        var adjacency = mutableMapOf<Int, MutableList<Int>>()
        for ((src, dst) in edges) {
            nodes.add(src)
            nodes.add(dst)
            adjacency.computeIfAbsent(src) { mutableListOf() }.add(dst)
        }
        // Ensure all nodes appear in adjacency map (even with empty list)
        for (n in nodes) {
            adjacency.putIfAbsent(n, mutableListOf())
        }
        // Sort adjacency lists to keep deterministic iteration order
        val sortedAdjacency = adjacency.mapValues { (_, list) -> list.sorted() }
        return Graph(nodes = nodes, adjacency = sortedAdjacency)
    }

    // Test 1: Empty graph
    run {
        val g = Graph(nodes = emptySet(), adjacency = emptyMap())
        val order = TopologicalSorter.sort(g)
        assert(order.isEmpty()) { "Empty graph should yield empty order." }
    }

    // Test 2: Single node, no edges
    run {
        val g = Graph(nodes = setOf(42), adjacency = mapOf(42 to emptyList()))
        val order = TopologicalSorter.sort(g)
        assert(order == listOf(42)) { "Single node graph should return that node." }
    }

    // Test 3: Simple DAG
    run {
        // 5 -> 2, 5 -> 0, 4 -> 0, 4 -> 1, 2 -> 3, 3 -> 1
        val edges = listOf(
            5 to 2,
            5 to 0,
            4 to 0,
            4 to 1,
            2 to 3,
            3 to 1
        )
        val g = buildGraph(edges)
        val order = TopologicalSorter.sort(g)
        // Expected deterministic order: 4,5,0,2,3,1
        val expected = listOf(4, 5, 0, 2, 3, 1)
        assert(order == expected) { "Simple DAG order mismatch. Got $order, expected $expected." }
    }

    // Test 4: Multiple valid orders – ensure deterministic smallest-first order
    run {
        // Graph: 1 -> 3, 2 -> 3, 1 -> 2
        // Nodes with indegree 0: 1
        // After removing 1, indegree 0 nodes: 2
        // After removing 2, indegree 0 nodes: 3
        val edges = listOf(
            1 to 3,
            2 to 3,
            1 to 2
        )
        val g = buildGraph(edges)
        val order = TopologicalSorter.sort(g)
        val expected = listOf(1, 2, 3)
        assert(order == expected) { "Deterministic order failed. Got $order, expected $expected." }
    }

    // Test 5: Graph where two independent nodes exist – should be sorted ascending
    run {
        // Nodes: 10, 20, 30 with no edges
        var nodes = setOf(10, 20, 30)
        var adjacency = mapOf<Int, List<Int>>(
            10 to emptyList(),
            20 to emptyList(),
            30 to emptyList()
        )
        val g = Graph(nodes = nodes, adjacency = adjacency)
        val order = TopologicalSorter.sort(g)
        val expected = listOf(10, 20, 30)
        assert(order == expected) { "Independent nodes should be ordered ascending. Got $order." }
    }

    // Test 6: Cycle detection
    run {
        // Cycle: 1 -> 2, 2 -> 3, 3 -> 1
        val edges = listOf(
            1 to 2,
            2 to 3,
            3 to 1
        )
        val g = buildGraph(edges)
        var caught = false
        try {
            TopologicalSorter.sort(g)
        } catch (e: IllegalArgumentException) {
            caught = true
        }
        assert(caught) { "Cycle should cause IllegalArgumentException." }
    }

    // All tests passed
    println("All topological sort tests passed.")
}