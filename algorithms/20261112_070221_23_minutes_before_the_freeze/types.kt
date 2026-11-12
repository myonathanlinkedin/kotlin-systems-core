import kotlin.math.*

// Domain models for graph representation and algorithm result
data class Edge(val to: Int, val weight: Long)

data class Graph(val nodeCount: Int, val adjacency: List<List<Edge>>) {
    companion object {
        // Helper to build a graph from a list of directed edges (from, to, weight)
        fun fromEdges(nodeCount: Int, rawEdges: List<Triple<Int, Int, Long>>): Graph {
            val adj = MutableList(nodeCount) { mutableListOf<Edge>() }
            for ((from, to, w) in rawEdges) {
                require(from in 0 until nodeCount) { "Source node $from out of bounds" }
                require(to in 0 until nodeCount) { "Target node $to out of bounds" }
                require(w >= 0L) { "Negative weight $w not allowed for Dijkstra" }
                adj[from].add(Edge(to, w))
            }
            // Convert inner mutable lists to immutable lists for safety
            val immutableAdj = adj.map { it.toList() }
            return Graph(nodeCount, immutableAdj)
        }
    }
}

// Result container for shortest‑path computation
data class ShortestPathResult(
    val distances: List<Long>,          // distance from source to each node (Long.MAX_VALUE = unreachable)
    val predecessors: List<Int?>       // previous node on the shortest path, null for source or unreachable
)
