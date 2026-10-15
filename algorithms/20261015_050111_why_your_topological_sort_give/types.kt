package topological

data class Graph(
    val nodes: Set<Int>,
    val adjacency: Map<Int, List<Int>>
)