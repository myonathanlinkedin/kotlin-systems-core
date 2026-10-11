package main

/**
 * Utility object exposing high‑level operations on DisjointSetUnion.
 * All functions are pure wrappers around the DSU methods.
 */
object DsuEngine {
    fun makeSet(n: Int): DisjointSetUnion = DisjointSetUnion.create(n)

    fun areConnected(dsu: DisjointSetUnion, a: Int, b: Int): Boolean = dsu.same(a, b)

    fun merge(dsu: DisjointSetUnion, a: Int, b: Int): Boolean = dsu.union(a, b)

    fun componentSize(dsu: DisjointSetUnion, x: Int): Int = dsu.componentSize(x)
}