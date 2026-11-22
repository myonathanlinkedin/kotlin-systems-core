package thinflow

data class ThinflowNode(
    val name: String,
    val command: String?,                     // Command to execute the benchmark, null for pure directory nodes
    val children: MutableList<ThinflowNode> = mutableListOf()
) {
    // Deep copy with optional name transformation
    fun deepCopy(nameTransformer: (String) -> String = { it }): ThinflowNode {
        val copiedChildren = children.map { it.deepCopy(nameTransformer) }.toMutableList()
        return ThinflowNode(nameTransformer(name), command, copiedChildren)
    }

    // Returns the full path from the root as a list of component names
    fun pathFrom(root: ThinflowNode, accumulator: MutableList<String> = mutableListOf()): List<String>? {
        if (this === root) {
            accumulator.add(name)
            return accumulator
        }
        // Search recursively in children
        for (child in root.children) {
            val result = child.pathFrom(root, accumulator.apply { add(root.name) })
            if (result != null) return result
            accumulator.removeAt(accumulator.size - 1) // backtrack
        }
        return null
    }
}

// Utility object for path handling
object ThinflowPath {
    fun parse(path: String): List<String> = path.trim('/').split('/').filter { it.isNotEmpty() }
}