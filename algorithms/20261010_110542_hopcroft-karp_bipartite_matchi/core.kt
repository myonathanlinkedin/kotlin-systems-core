import kotlin.math.*

class HopcroftKarp(private val nLeft: Int, private val nRight: Int) {
    private val adj: Array<MutableList<Int>> = Array(nLeft) { mutableListOf<Int>() }
    private val pairU: IntArray = IntArray(nLeft) { -1 }   // left -> right
    private val pairV: IntArray = IntArray(nRight) { -1 }  // right -> left
    private val dist: IntArray = IntArray(nLeft)

    fun addEdge(u: Int, v: Int) {
        require(u in 0 until nLeft) { "Left vertex out of range" }
        require(v in 0 until nRight) { "Right vertex out of range" }
        adj[u].add(v)
    }

    fun maxMatching(): Int {
        var matching = 0
        while (bfs()) {
            for (u in 0 until nLeft) {
                if (pairU[u] == -1 && dfs(u)) {
                    matching++
                }
            }
        }
        return matching
    }

    private fun bfs(): Boolean {
        val queue = IntArray(nLeft)
        var head = 0
        var tail = 0
        for (u in 0 until nLeft) {
            if (pairU[u] == -1) {
                dist[u] = 0
                queue[tail++] = u
            } else {
                dist[u] = -1
            }
        }
        var found = false
        while (head < tail) {
            val u = queue[head++]
            for (v in adj[u]) {
                val pu = pairV[v]
                if (pu != -1 && dist[pu] == -1) {
                    dist[pu] = dist[u] + 1
                    queue[tail++] = pu
                } else if (pu == -1) {
                    found = true
                }
            }
        }
        return found
    }

    private fun dfs(u: Int): Boolean {
        for (v in adj[u]) {
            val pu = pairV[v]
            if (pu == -1 || (dist[pu] == dist[u] + 1 && dfs(pu))) {
                pairU[u] = v
                pairV[v] = u
                return true
            }
        }
        dist[u] = -1
        return false
    }

    // Expose matching for testing if needed
    fun getMatchingPairs(): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        for (u in 0 until nLeft) {
            val v = pairU[u]
            if (v != -1) {
                result.add(Pair(u, v))
            }
        }
        return result
    }
}
