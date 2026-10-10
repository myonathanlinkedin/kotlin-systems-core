import kotlin.random.Random

fun bruteForceMaxMatching(nLeft: Int, nRight: Int, edges: List<Pair<Int, Int>>): Int {
    // Represent adjacency as boolean matrix
    val adj = Array(nLeft) { BooleanArray(nRight) }
    for ((u, v) in edges) {
        adj[u][v] = true
    }
    var best = 0
    // Enumerate all subsets of left vertices assignments via recursion
    fun backtrack(u: Int, usedRight: BooleanArray, count: Int) {
        if (u == nLeft) {
            if (count > best) best = count
            return
        }
        // Option: leave u unmatched
        backtrack(u + 1, usedRight, count)
        // Try to match u with any free right neighbor
        for (v in 0 until nRight) {
            if (adj[u][v] && !usedRight[v]) {
                usedRight[v] = true
                backtrack(u + 1, usedRight, count + 1)
                usedRight[v] = false
            }
        }
    }
    backtrack(0, BooleanArray(nRight), 0)
    return best
}

fun testSimpleCase() {
    var hk = HopcroftKarp(3, 3)
    hk.addEdge(0, 0)
    hk.addEdge(0, 1)
    hk.addEdge(1, 1)
    hk.addEdge(2, 2)
    val result = hk.maxMatching()
    check(result == 3) { "Simple case expected 3, got $result" }
}

fun testEmptyGraph() {
    var hk = HopcroftKarp(4, 5)
    val result = hk.maxMatching()
    check(result == 0) { "Empty graph expected 0, got $result" }
}

fun testCompleteBipartite() {
    val n = 4
    var hk = HopcroftKarp(n, n)
    for (u in 0 until n) {
        for (v in 0 until n) {
            hk.addEdge(u, v)
        }
    }
    val result = hk.maxMatching()
    check(result == n) { "Complete bipartite expected $n, got $result" }
}

fun testUnequalSides() {
    var hk = HopcroftKarp(2, 5)
    hk.addEdge(0, 0)
    hk.addEdge(0, 1)
    hk.addEdge(1, 2)
    hk.addEdge(1, 3)
    val result = hk.maxMatching()
    check(result == 2) { "Unequal sides expected 2, got $result" }
}

fun testRandomSmallGraphs(iterations: Int = 200) {
    repeat(iterations) {
        val nLeft = Random.nextInt(1, 6)
        val nRight = Random.nextInt(1, 6)
        var hk = HopcroftKarp(nLeft, nRight)
        val edgeList = mutableListOf<Pair<Int, Int>>()
        for (u in 0 until nLeft) {
            for (v in 0 until nRight) {
                if (Random.nextBoolean()) {
                    hk.addEdge(u, v)
                    edgeList.add(Pair(u, v))
                }
            }
        }
        val hkResult = hk.maxMatching()
        val bruteResult = bruteForceMaxMatching(nLeft, nRight, edgeList)
        check(hkResult == bruteResult) {
            "Random graph mismatch: hk=$hkResult brute=$bruteResult left=$nLeft right=$nRight edges=$edgeList"
        }
    }
}

fun main() {
    testSimpleCase()
    testEmptyGraph()
    testCompleteBipartite()
    testUnequalSides()
    testRandomSmallGraphs()
    println("All tests passed.")
}
