package core

import java.util.PriorityQueue
import kotlin.math.*

data class Edge(val to: Int, val weight: Long)

class Graph(val nodeCount: Int) {
    private val adjacency: Array<MutableList<Edge>> = Array(nodeCount) { mutableListOf() }
    private val exitNodes: MutableSet<Int> = mutableSetOf()

    fun addEdge(from: Int, to: Int, weight: Long) {
        require(from in 0 until nodeCount && to in 0 until nodeCount) { "Node index out of bounds" }
        require(weight >= 0L) { "Negative weight not allowed" }
        adjacency[from].add(Edge(to, weight))
    }

    fun addUndirectedEdge(a: Int, b: Int, weight: Long) {
        addEdge(a, b, weight)
        addEdge(b, a, weight)
    }

    fun setExit(node: Int) {
        require(node in 0 until nodeCount) { "Node index out of bounds" }
        exitNodes.add(node)
    }

    fun clearExits() {
        exitNodes.clear()
    }

    /**
     * Returns the minimum total weight required to reach any exit node from [source].
     * If no exit is reachable, returns null.
     */
    fun minCostToExit(source: Int): Long? {
        require(source in 0 until nodeCount) { "Source node out of bounds" }
        if (exitNodes.isEmpty()) return null
        val dist = LongArray(nodeCount) { Long.MAX_VALUE }
        val visited = BooleanArray(nodeCount)
        val pq = PriorityQueue<Pair<Long, Int>>(compareBy { it.first })
        dist[source] = 0L
        pq.add(0L to source)

        while (pq.isNotEmpty()) {
            val (d, u) = pq.poll()
            if (visited[u]) continue
            visited[u] = true
            if (u in exitNodes) return d
            for (edge in adjacency[u]) {
                val v = edge.to
                val nd = d + edge.weight
                if (nd < dist[v]) {
                    dist[v] = nd
                    pq.add(nd to v)
                }
            }
        }
        return null
    }

    /**
     * Returns a map from each node to its minimum cost to the nearest exit.
     * Unreachable nodes map to null.
     */
    fun allMinCostsToExit(): Map<Int, Long?> {
        if (exitNodes.isEmpty()) return (0 until nodeCount).associateWith { null }
        // Multi-source Dijkstra: start from all exits with distance 0.
        val dist = LongArray(nodeCount) { Long.MAX_VALUE }
        val visited = BooleanArray(nodeCount)
        val pq = PriorityQueue<Pair<Long, Int>>(compareBy { it.first })
        for (exit in exitNodes) {
            dist[exit] = 0L
            pq.add(0L to exit)
        }

        while (pq.isNotEmpty()) {
            val (d, u) = pq.poll()
            if (visited[u]) continue
            visited[u] = true
            for (edge in adjacency[u]) {
                val v = edge.to
                val nd = d + edge.weight
                if (nd < dist[v]) {
                    dist[v] = nd
                    pq.add(nd to v)
                }
            }
        }

        return (0 until nodeCount).associateWith { if (dist[it] == Long.MAX_VALUE) null else dist[it] }
    }
}