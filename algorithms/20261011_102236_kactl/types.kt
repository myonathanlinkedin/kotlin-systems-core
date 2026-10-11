package main

data class DisjointSetUnion(
    val parent: IntArray,
    val size: IntArray
) {
    companion object {
        fun create(n: Int): DisjointSetUnion {
            require(n >= 0) { "Size must be non‑negative" }
            val parent = IntArray(n) { it }
            val size = IntArray(n) { 1 }
            return DisjointSetUnion(parent, size)
        }
    }

    fun find(x: Int): Int {
        require(x in parent.indices) { "Index out of bounds" }
        var v = x
        while (parent[v] != v) {
            v = parent[v]
        }
        // Path compression
        var cur = x
        while (parent[cur] != cur) {
            val next = parent[cur]
            parent[cur] = v
            cur = next
        }
        return v
    }

    /**
     * Unites the sets containing a and b.
     * @return true if a merge happened, false if they were already in the same set.
     */
    fun union(a: Int, b: Int): Boolean {
        var rootA = find(a)
        var rootB = find(b)
        if (rootA == rootB) return false
        // Union by size: attach smaller tree under larger one
        if (size[rootA] < size[rootB]) {
            val tmp = rootA
            rootA = rootB
            rootB = tmp
        }
        parent[rootB] = rootA
        size[rootA] += size[rootB]
        return true
    }

    fun same(a: Int, b: Int): Boolean = find(a) == find(b)

    /**
     * Returns the size of the set containing element x.
     */
    fun componentSize(x: Int): Int = size[find(x)]
}