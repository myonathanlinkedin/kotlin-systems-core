package core

import kotlin.math.min

data class Graph(val vertexCount: Int, val adjacency: Array<LongArray>) {
    companion object {
        fun empty(n: Int): Graph {
            val adj = Array(n) { LongArray(n) }
            return Graph(n, adj)
        }
    }

    fun addUndirectedEdge(u: Int, v: Int, weight: Long) {
        require(u in 0 until vertexCount && v in 0 until vertexCount) { "Vertex index out of bounds" }
        require(weight >= 0L) { "Weight must be non‑negative" }
        adjacency[u][v] = adjacency[u][v] + weight
        adjacency[v][u] = adjacency[v][u] + weight
    }

    fun copy(): Graph {
        val newAdj = Array(vertexCount) { i -> adjacency[i].clone() }
        return Graph(vertexCount, newAdj)
    }
}

/**
 * Computes the global minimum cut of an undirected weighted graph using the
 * Stoer‑Wagner algorithm. Returns the weight of the minimum cut.
 *
 * Time complexity: O(V³) with V = number of vertices.
 * Space complexity: O(V²) for the adjacency matrix.
 */
fun stoerWagnerMinCut(original: Graph): Long {
    // Work on a mutable copy so the original graph stays unchanged.
    val graph = original.copy()
    val n = graph.vertexCount
    // Active vertices that have not been merged away.
    val vertices = mutableListOf<Int>()
    for (i in 0 until n) vertices.add(i)

    var bestCut = Long.MAX_VALUE

    // Helper array for the "maximum adjacency search" phase.
    val added = BooleanArray(n)
    val weight = LongArray(n)

    while (vertices.size > 1) {
        added.fill(false)
        weight.fill(0L)

        // Start from an arbitrary vertex (first in the list).
        var prev = vertices[0]
        added[prev] = true

        // Repeatedly add the most tightly connected vertex.
        for (i in 1 until vertices.size) {
            // Update weights of vertices not yet in A.
            for (v in vertices) {
                if (!added[v]) {
                    weight[v] = weight[v] + graph.adjacency[prev][v]
                }
            }

            // Select the vertex with maximum weight.
            var maxWeight = -1L
            var sel = -1
            for (v in vertices) {
                if (!added[v] && weight[v] > maxWeight) {
                    maxWeight = weight[v]
                    sel = v
                }
            }

            // Add selected vertex to A.
            added[sel] = true

            // If this is the last vertex added in this phase, we have a cut.
            if (i == vertices.size - 1) {
                // sel is t, prev is s.
                val cutWeight = weight[sel]
                bestCut = min(bestCut, cutWeight)

                // Merge s (prev) and t (sel) into a single vertex.
                // All edges incident to t are added to s.
                for (v in vertices) {
                    if (v != prev) {
                        graph.adjacency[prev][v] = graph.adjacency[prev][v] + graph.adjacency[sel][v]
                        graph.adjacency[v][prev] = graph.adjacency[prev][v]
                    }
                }
                // Remove t from the active vertex list.
                vertices.remove(sel)
                // No need to zero out rows/columns of sel; they will never be accessed again.
            } else {
                // Continue the phase; sel becomes the previous vertex for the next iteration.
                prev = sel
            }
        }
    }

    return bestCut
}