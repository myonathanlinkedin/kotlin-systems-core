import java.util.PriorityQueue

data class Position(val x: Int, val y: Int)

data class Node(
    val position: Position,
    val gScore: Int,
    val fScore: Int
)

object AStar {
    private fun heuristic(a: Position, b: Position): Int {
        return kotlin.math.abs(a.x - b.x) + kotlin.math.abs(a.y - b.y)
    }

    private fun inBounds(pos: Position, width: Int, height: Int): Boolean {
        return pos.x in 0 until width && pos.y in 0 until height
    }

    private fun neighbors(pos: Position, width: Int, height: Int): List<Position> {
        val dirs = listOf(
            Position(0, -1), // up
            Position(0, 1),  // down
            Position(-1, 0), // left
            Position(1, 0)   // right
        )
        return dirs.map { Position(pos.x + it.x, pos.y + it.y) }
            .filter { inBounds(it, width, height) }
    }

    fun findPath(
        start: Position,
        goal: Position,
        costGrid: Array<IntArray>
    ): List<Position> {
        val height = costGrid.size
        if (height == 0) return emptyList()
        val width = costGrid[0].size

        val openSet = PriorityQueue<Node>(compareBy { it.fScore })
        val startNode = Node(start, 0, heuristic(start, goal))
        openSet.add(startNode)

        val cameFrom = mutableMapOf<Position, Position>()
        val gScore = mutableMapOf<Position, Int>()
        gScore[start] = 0

        while (openSet.isNotEmpty()) {
            val current = openSet.poll()
            if (current.position == goal) {
                return reconstructPath(cameFrom, current.position)
            }

            for (neighbor in neighbors(current.position, width, height)) {
                val tentativeG = gScore.getOrDefault(current.position, Int.MAX_VALUE) +
                        costGrid[neighbor.y][neighbor.x]

                if (tentativeG < gScore.getOrDefault(neighbor, Int.MAX_VALUE)) {
                    cameFrom[neighbor] = current.position
                    gScore[neighbor] = tentativeG
                    val f = tentativeG + heuristic(neighbor, goal)
                    openSet.add(Node(neighbor, tentativeG, f))
                }
            }
        }
        return emptyList()
    }

    private fun reconstructPath(
        cameFrom: Map<Position, Position>,
        current: Position
    ): List<Position> {
        val totalPath = mutableListOf<Position>()
        var cur: Position? = current
        while (cur != null) {
            totalPath.add(cur)
            cur = cameFrom[cur]
        }
        totalPath.reverse()
        return totalPath
    }
}
