import java.util.ArrayDeque

data class BipartiteGraph(val leftSize: Int, val rightSize: Int) {
    private val adjacency: Array<MutableList<Int>> = Array(leftSize) { mutableListOf<Int>() }

    fun addEdge(u: Int, v: Int) {
        require(u in 0 until leftSize) { "Left vertex out of bounds: $u" }
        require(v in 0 until rightSize) { "Right vertex out of bounds: $v" }
        adjacency[u].add(v)
    }

    fun hopcroftKarp(): Int {
        val pairU = IntArray(leftSize) { -1 }
        val pairV = IntArray(rightSize) { -1 }
        val dist = IntArray(leftSize)
        val queue = ArrayDeque<Int>()
        val INF = Int.MAX_VALUE

        fun bfs(): Boolean {
            queue.clear()
            for (u in 0 until leftSize) {
                if (pairU[u] == -1) {
                    dist[u] = 0
                    queue.add(u)
                } else {
                    dist[u] = INF
                }
            }
            var found = false
            while (queue.isNotEmpty()) {
                val u = queue.removeFirst()
                for (v in adjacency[u]) {
                    val pu = pairV[v]
                    if (pu == -1) {
                        found = true
                    } else if (dist[pu] == INF) {
                        dist[pu] = dist[u] + 1
                        queue.add(pu)
                    }
                }
            }
            return found
        }

        fun dfs(u: Int): Boolean {
            for (v in adjacency[u]) {
                val pu = pairV[v]
                if (pu == -1 || (dist[pu] == dist[u] + 1 && dfs(pu))) {
                    pairU[u] = v
                    pairV[v] = u
                    return true
                }
            }
            dist[u] = INF
            return false
        }

        var matching = 0
        while (bfs()) {
            for (u in 0 until leftSize) {
                if (pairU[u] == -1 && dfs(u)) {
                    matching++
                }
            }
        }
        return matching
    }

    fun getMatching(): List<Pair<Int, Int>> {
        val pairU = IntArray(leftSize) { -1 }
        val pairV = IntArray(rightSize) { -1 }
        val dist = IntArray(leftSize)
        val queue = ArrayDeque<Int>()
        val INF = Int.MAX_VALUE

        fun bfs(): Boolean {
            queue.clear()
            for (u in 0 until leftSize) {
                if (pairU[u] == -1) {
                    dist[u] = 0
                    queue.add(u)
                } else {
                    dist[u] = INF
                }
            }
            var found = false
            while (queue.isNotEmpty()) {
                val u = queue.removeFirst()
                for (v in adjacency[u]) {
                    val pu = pairV[v]
                    if (pu == -1) {
                        found = true
                    } else if (dist[pu] == INF) {
                        dist[pu] = dist[u] + 1
                        queue.add(pu)
                    }
                }
            }
            return found
        }

        fun dfs(u: Int): Boolean {
            for (v in adjacency[u]) {
                val pu = pairV[v]
                if (pu == -1 || (dist[pu] == dist[u] + 1 && dfs(pu))) {
                    pairU[u] = v
                    pairV[v] = u
                    return true
                }
            }
            dist[u] = INF
            return false
        }

        while (bfs()) {
            for (u in 0 until leftSize) {
                if (pairU[u] == -1 && dfs(u)) {
                    // matched
                }
            }
        }

        val result = mutableListOf<Pair<Int, Int>>()
        for (u in 0 until leftSize) {
            val v = pairU[u]
            if (v != -1) {
                result.add(Pair(u, v))
            }
        }
        return result
    }
}
