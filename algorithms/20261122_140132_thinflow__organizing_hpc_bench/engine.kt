package thinflow

object ThinflowEngine {

    // Find a node by absolute path components starting from the given root.
    // Returns null if any component is missing.
    fun findNode(root: ThinflowNode, pathComponents: List<String>): ThinflowNode? {
        if (pathComponents.isEmpty()) return root
        var current: ThinflowNode = root
        for (component in pathComponents) {
            val next = current.children.find { it.name == component } ?: return null
            current = next
        }
        return current
    }

    // Add a new node under the directory specified by parentPath.
    // Returns true on success, false if parentPath does not exist or a child with the same name already exists.
    fun addNode(root: ThinflowNode, parentPath: List<String>, newNode: ThinflowNode): Boolean {
        val parent = findNode(root, parentPath) ?: return false
        if (parent.children.any { it.name == newNode.name }) return false
        parent.children.add(newNode)
        return true
    }

    // Rerun a node: deep‑clone the subtree at targetPath, append a suffix to each node's name,
    // and attach the cloned subtree as a sibling of the original node.
    // Returns the cloned root node, or null if targetPath does not exist.
    fun rerunNode(root: ThinflowNode, targetPath: List<String>, suffix: String): ThinflowNode? {
        val target = findNode(root, targetPath) ?: return null
        val parentPath = if (targetPath.isEmpty()) emptyList() else targetPath.dropLast(1)
        val parent = if (parentPath.isEmpty()) root else findNode(root, parentPath) ?: return null

        // Transform function appends suffix to each name
        val transformer: (String) -> String = { it + suffix }
        val cloned = target.deepCopy(transformer)

        // Ensure no name clash among siblings
        if (parent.children.any { it.name == cloned.name }) return null
        parent.children.add(cloned)
        return cloned
    }

    // Produce a deterministic pre‑order traversal list of all nodes.
    fun preOrder(root: ThinflowNode): List<ThinflowNode> {
        val result = mutableListOf<ThinflowNode>()
        fun walk(node: ThinflowNode) {
            result.add(node)
            for (child in node.children) {
                walk(child)
            }
        }
        walk(root)
        return result
    }

    // Compute a topological order assuming each node depends on its parent (i.e., parent must run before child).
    // For a pure tree this is identical to pre‑order.
    fun topologicalOrder(root: ThinflowNode): List<ThinflowNode> = preOrder(root)
}