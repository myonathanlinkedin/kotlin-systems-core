import java.util.PriorityQueue

// Core Dijkstra implementation returning distances and predecessor information
fun dijkstra(graph: Graph, source: Int): ShortestPathResult {
    require(source in 0 until graph.nodeCount) { "Source $source out of bounds" }

    val n = graph.nodeCount
    val dist = MutableList(n) { Long.MAX_VALUE }
    val prev = MutableList<Int?>(n) { null }
    dist[source] = 0L

    // Priority queue ordered by current known distance
    data class Node(val id: Int, val distance: Long)
    val pq = PriorityQueue<Node>(compareBy { it.distance })
    pq.add(Node(source, 0L))

    while (pq.isNotEmpty()) {
        val (u, d) = pq.poll()
        // Skip stale entries
        if (d != dist[u]) continue

        for (edge in graph.adjacency[u]) {
            val v = edge.to
            val weight = edge.weight
            // Guard against overflow
            if (dist[u] != Long.MAX_VALUE && dist[u] + weight < dist[v]) {
                dist[v] = dist[u] + weight
                prev[v] = u
                pq.add(Node(v, dist[v]))
            }
        }
    }

    return ShortestPathResult(dist.toList(), prev.toList())
}

// Utility to reconstruct the path from source to target using predecessor list
fun reconstructPath(result: ShortestPathResult, source: Int, target: Int): List<Int> {
    require(source in result.distances.indices) { "Source out of range" }
    require(target in result.distances.indices) { "Target out of range" }

    if (result.distances[target] == Long.MAX_VALUE) return emptyList()
    val path = mutableListOf<Int>()
    var cur: Int? = target
    while (cur != null) {
        path.add(cur)
        if (cur == source) break
        cur = result.predecessors[cur]
    }
    path.reverse()
    return if (path.firstOrNull() == source) path else emptyList()
}
