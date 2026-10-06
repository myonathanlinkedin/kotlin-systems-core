package main

import kotlin.math.ln
import kotlin.math.sqrt

/**
 * Represents a node in the graph.
 */
data class Node(val id: Int)

/**
 * Represents an undirected edge in the graph.
 */
data class Edge(val u: Int, val v: Int)

/**
 * Represents the graph structure.
 */
data class Graph(
    val n: Int,
    val edges: List<Edge>
) {
    val adjacency: List<List<Int>> by lazy {
        val adj = List(n) { mutableListOf<Int>() }
        for (edge in edges) {
            adj[edge.u].add(edge.v)
            adj[edge.v].add(edge.u)
        }
        adj.map { it.toList() }
    }

    val degrees: List<Int> by lazy {
        adjacency.map { it.size }
    }

    val totalDegree: Int by lazy {
        degrees.sum()
    }
}

/**
 * Configuration for the random walk approximation.
 */
data class WalkConfig(
    val n: Int,
    val epsilon: Double,
    val delta: Double,
    val seed: Long
)

/**
 * Result of the random walk approximation.
 */
data class WalkResult(
    val stationaryDistribution: List<Double>,
    val estimatedMixingTime: Int,
    val stepsExecuted: Int,
    val success: Boolean
)