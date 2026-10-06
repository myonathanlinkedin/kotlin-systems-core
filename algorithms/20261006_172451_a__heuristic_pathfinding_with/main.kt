import kotlin.random.Random

fun testSimplePath() {
    val grid = Array(5) { IntArray(5) { 1 } }
    val start = Position(0, 0)
    val goal = Position(4, 4)
    val path = AStar.findPath(start, goal, grid)
    check(path.isNotEmpty()) { "Path should not be empty" }
    check(path.first() == start) { "Path should start at start position" }
    check(path.last() == goal) { "Path should end at goal position" }
    // Expected length is Manhattan distance + 1 (including start)
    val expectedLength = kotlin.math.abs(start.x - goal.x) + kotlin.math.abs(start.y - goal.y) + 1
    check(path.size == expectedLength) { "Path length $${path.size} != expected $expectedLength" }
}

fun testAvoidHighCost() {
    val grid = Array(5) { IntArray(5) { 1 } }
    // Place a high-cost barrier across row 2
    for (x in 0 until 5) {
        grid[2][x] = 10
    }
    val start = Position(0, 0)
    val goal = Position(4, 4)
    val path = AStar.findPath(start, goal, grid)
    check(path.isNotEmpty()) { "Path should not be empty" }
    // Path should not cross row 2 because of high cost
    val crossesBarrier = path.any { it.y == 2 }
    check(!crossesBarrier) { "Path should avoid high-cost barrier" }
}

fun testDynamicCostUpdate() {
    val grid = Array(5) { IntArray(5) { 1 } }
    val start = Position(0, 0)
    val goal = Position(4, 0)
    // Initial path straight across top row
    var path = AStar.findPath(start, goal, grid)
    check(path.size == 5) { "Initial path length should be 5" }
    // Increase cost of middle cell (2,0)
    grid[0][2] = 20
    path = AStar.findPath(start, goal, grid)
    check(path.isNotEmpty()) { "Path after cost change should not be empty" }
    // New path should detour downwards
    val usesHighCost = path.any { it == Position(2, 0) }
    check(!usesHighCost) { "Path should avoid newly expensive cell" }
    // Ensure path length increased
    check(path.size > 5) { "Detour path should be longer than original" }
}

fun runAllTests() {
    testSimplePath()
    testAvoidHighCost()
    testDynamicCostUpdate()
    println("All A* tests passed.")
}

fun main() {
    runAllTests()
}
