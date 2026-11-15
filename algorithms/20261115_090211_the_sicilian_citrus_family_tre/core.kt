package core

import kotlin.math.*

data class TreeNode(
    val name: String,
    var parent: TreeNode? = null,
    val children: MutableList<TreeNode> = mutableListOf()
)

class FamilyTree(rootName: String) {
    // public map of all nodes for external visibility as required
    val nodes: MutableMap<String, TreeNode> = mutableMapOf()

    init {
        val root = TreeNode(rootName)
        nodes[rootName] = root
    }

    // Add a child under an existing parent
    fun addChild(parentName: String, childName: String) {
        check(!nodes.containsKey(childName)) { "Node '$childName' already exists." }
        val parentNode = nodes[parentName] ?: throw IllegalArgumentException("Parent '$parentName' not found.")
        val childNode = TreeNode(childName, parentNode)
        parentNode.children.add(childNode)
        nodes[childName] = childNode
    }

    // Retrieve a node by name; throws if absent
    fun getNode(name: String): TreeNode =
        nodes[name] ?: throw IllegalArgumentException("Node '$name' not found.")

    // List ancestors from immediate parent up to root
    fun ancestors(name: String): List<String> {
        val result = mutableListOf<String>()
        var current = getNode(name).parent
        while (current != null) {
            result.add(current.name)
            current = current.parent
        }
        return result
    }

    // Depth of a node (root has depth 0)
    fun depth(name: String): Int {
        var d = 0
        var current = getNode(name).parent
        while (current != null) {
            d++
            current = current.parent
        }
        return d
    }

    // Determine if 'possibleAncestor' is an ancestor of 'descendant'
    fun isAncestor(possibleAncestor: String, descendant: String): Boolean {
        var current = getNode(descendant).parent
        while (current != null) {
            if (current.name == possibleAncestor) return true
            current = current.parent
        }
        return false
    }

    // Find lowest common ancestor of two nodes; null if none (different trees)
    fun commonAncestor(name1: String, name2: String): String? {
        val ancestors1 = ancestors(name1).toSet()
        var current = getNode(name2).parent
        while (current != null) {
            if (ancestors1.contains(current.name)) return current.name
            current = current.parent
        }
        return null
    }

    // Return all descendants (any depth) of a node in pre‑order
    fun descendants(name: String): List<String> {
        val result = mutableListOf<String>()
        fun dfs(node: TreeNode) {
            for (child in node.children) {
                result.add(child.name)
                dfs(child)
            }
        }
        dfs(getNode(name))
        return result
    }
}