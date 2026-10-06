import kotlin.random.Random
import kotlin.system.measureNanoTime

fun testEmptyGraph() {
    val graph = BipartiteGraph(0, 0)
    val size = graph.hopcroftKarp()
    check(size == 0) { "Empty graph should have matching size 0" }
}

fun testSingleEdge() {
    val graph = BipartiteGraph(1, 1)
    graph.addEdge(0, 0)
    val size = graph.hopcroftKarp()
    check(size == 1) { "Single edge graph should have matching size 1" }
}

fun testSimpleMatching() {
    val graph = BipartiteGraph(3, 3)
    graph.addEdge(0, 0)
    graph.addEdge(0, 1)
    graph.addEdge(1, 1)
    graph.addEdge(2, 2)
    val size = graph.hopcroftKarp()
    check(size == 3) { "Expected matching size 3" }
}

fun testComplexMatching() {
    val graph = BipartiteGraph(4, 4)
    graph.addEdge(0, 1)
    graph.addEdge(0, 2)
    graph.addEdge(1, 0)
    graph.addEdge(1, 3)
    graph.addEdge(2, 0)
    graph.addEdge(3, 2)
    val size = graph.hopcroftKarp()
    check(size == 4) { "Expected matching size 4" }
}

fun testPartialMatching() {
    val graph = BipartiteGraph(2, 2)
    graph.addEdge(0, 0)
    val size = graph.hopcroftKarp()
    check(size == 1) { "Expected matching size 1" }
}

fun bruteForceMaxMatching(leftSize: Int, rightSize: Int, edges: List<Pair<Int, Int>>): Int {
    val adj = Array(leftSize) { mutableListOf<Int>() }
    for ((u, v) in edges) {
        adj[u].add(v)
    }
    var best = 0
    fun dfs(u: Int, usedRight: BooleanArray, count: Int) {
        if (u == leftSize) {
            if (count > best) best = count
            return
        }
        for (v in adj[u]) {
            if (!usedRight[v]) {
                usedRight[v] = true
                dfs(u + 1, usedRight, count + 1)
                usedRight[v] = false
            }
        }
        dfs(u + 1, usedRight, count)
    }
    dfs(0, BooleanArray(rightSize), 0)
    return best
}

fun testBruteForceComparison() {
    val leftSize = 4
    val rightSize = 4
    val edges = listOf(
        Pair(0, 0), Pair(0, 1), Pair(1, 1), Pair(1, 2),
        Pair(2, 2), Pair(3, 3), Pair(3, 0)
    )
    val graph = BipartiteGraph(leftSize, rightSize)
    for ((u, v) in edges) graph.addEdge(u, v)
    val algoSize = graph.hopcroftKarp()
    val bruteSize = bruteForceMaxMatching(leftSize, rightSize, edges)
    check(algoSize == bruteSize) { "Algorithm size $algoSize differs from brute force $bruteSize" }
}

fun testRandomGraphs() {
    val rand = Random(42)
    repeat(10) {
        val left = rand.nextInt(1, 6)
        val right = rand.nextInt(1, 6)
        val graph = BipartiteGraph(left, right)
        val edges = mutableSetOf<Pair<Int, Int>>()
        repeat(left * right) {
            val u = rand.nextInt(0, left)
            val v = rand.nextInt(0, right)
            edges.add(Pair(u, v))
        }
        for ((u, v) in edges) graph.addEdge(u, v)
        val size = graph.hopcroftKarp()
        check(size <= kotlin.math.min(left, right)) { "Matching size exceeds min side" }
        val matching = graph.getMatching()
        val usedRight = BooleanArray(right)
        for ((u, v) in matching) {
            check(u in 0 until left && v in 0 until right) { "Invalid match ($u,$v)" }
            check(!usedRight[v]) { "Right vertex $v matched twice" }
            usedRight[v] = true
        }
    }
}

fun benchmarkLargeGraph() {
    val left = 5000
    val right = 5000
    val graph = BipartiteGraph(left, right)
    val rand = Random(123)
    repeat(left) { u ->
        repeat(10) {
            val v = rand.nextInt(0, right)
            graph.addEdge(u, v)
        }
    }
    val timeNs = measureNanoTime {
        val size = graph.hopcroftKarp()
        check(size >= 0) { "Benchmark produced negative size" }
    }
    println("Benchmark: $timeNs ns for graph with $left left, $right right")
}

fun main() {
    testEmptyGraph()
    testSingleEdge()
    testSimpleMatching()
    testComplexMatching()
    testPartialMatching()
    testBruteForceComparison()
    testRandomGraphs()
    benchmarkLargeGraph()
    println("All tests passed")
}
