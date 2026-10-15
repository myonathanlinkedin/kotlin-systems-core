package topological

import java.util.PriorityQueue

object TopologicalSorter {
    /**
     * Returns a deterministic topological ordering of the given DAG.
     * Nodes are processed in ascending numeric order when multiple choices exist.
     *
     * @throws IllegalArgumentException if the graph contains a cycle.
     */
    fun sort(graph: Graph): List<Int> {
        // Compute indegree for each node
        val indegree = mutableMapOf<Int, Int>()
        for (node in graph.nodes) {
            indegree[node] = 0
        }
        for ((_, targets) in graph.adjacency) {
            for (v in targets) {
                indegree[v] = indegree.getOrDefault(v, 0) + 1
            }
        }

        // Priority queue to obtain the smallest available node each step
        val zeroIndegree = PriorityQueue<Int>()
        for ((node, deg) in indegree) {
            if (deg == 0) zeroIndegree.add(node)
        }

        val result = mutableListOf<Int>()
        while (zeroIndegree.isNotEmpty()) {
            val u = zeroIndegree.poll()
            result.add(u)

            val neighbors = graph.adjacency[u] ?: emptyList()
            for (v in neighbors) {
                val newDeg = indegree[v]!! - 1
                indegree[v] = newDeg
                if (newDeg == 0) zeroIndegree.add(v)
            }
        }

        // If we couldn't process all nodes, a cycle exists
        if (result.size != graph.nodes.size) {
            throw IllegalArgumentException("Graph contains a cycle; topological order not possible.")
        }

        return result
    }
}