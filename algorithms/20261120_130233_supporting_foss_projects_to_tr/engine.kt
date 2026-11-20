import java.util.ArrayDeque

fun computeMigrationOrder(projects: List<Project>, dependencies: List<Dependency>): List<Project> {
    // Map project name to Project instance for quick lookup
    val nameToProject = projects.associateBy { it.name }

    // Initialize adjacency list and indegree count for each project
    val adjacency = mutableMapOf<String, MutableList<String>>()
    val indegree = mutableMapOf<String, Int>()
    for (proj in projects) {
        adjacency[proj.name] = mutableListOf()
        indegree[proj.name] = 0
    }

    // Build graph: edge from dependency.to -> dependency.from
    for (dep in dependencies) {
        adjacency[dep.to]?.add(dep.from)
        indegree[dep.from] = (indegree[dep.from] ?: 0) + 1
    }

    // Kahn's algorithm: start with nodes of indegree 0
    val queue = ArrayDeque<String>()
    for ((name, deg) in indegree) {
        if (deg == 0) {
            queue.add(name)
        }
    }

    val ordered = mutableListOf<Project>()
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        ordered.add(nameToProject[current]!!)

        for (neighbor in adjacency[current]!!) {
            indegree[neighbor] = indegree[neighbor]!! - 1
            if (indegree[neighbor] == 0) {
                queue.add(neighbor)
            }
        }
    }

    // If we couldn't process all projects, a cycle exists
    return if (ordered.size == projects.size) ordered else emptyList()
}
