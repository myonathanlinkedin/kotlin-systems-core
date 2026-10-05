package com.dag.runtime

import kotlin.math.max

/**
 * Represents a unique identifier for a node in the Directed Acyclic Graph (DAG).
 * In a production environment, this could be a UUID or a composite key.
 */
data class NodeId(val id: String) {
    override fun toString(): String = id
}

/**
 * Represents a single task or node in the DAG.
 * @param id Unique identifier for the node.
 * @param name Human-readable name for the node.
 * @param weight Computational cost or duration associated with the node.
 */
data class DagNode(
    val id: NodeId,
    val name: String,
    val weight: Int = 1
)

/**
 * Represents a directed edge from a source node to a target node.
 * @param source The predecessor node.
 * @param target The successor node.
 */
data class DagEdge(
    val source: NodeId,
    val target: NodeId
)

/**
 * Represents the complete Directed Acyclic Graph structure.
 * @param nodes List of all nodes in the graph.
 * @param edges List of all directed edges in the graph.
 */
data class Dag(
    val nodes: List<DagNode>,
    val edges: List<DagEdge>
)

/**
 * Represents the result of a topological sort operation.
 * @param success Indicates if a valid topological order was found.
 * @param order The list of NodeIds in topological order (if successful).
 * @param cycleNodes The list of NodeIds involved in a cycle (if failed).
 */
sealed class TopologicalSortResult {
    data class Success(val order: List<NodeId>) : TopologicalSortResult()
    data class CycleDetected(val cycleNodes: List<NodeId>) : TopologicalSortResult()
}

/**
 * Represents the result of a critical path calculation.
 * @param path The list of NodeIds forming the critical path.
 * @param totalWeight The sum of weights along the critical path.
 */
data class CriticalPathResult(
    val path: List<NodeId>,
    val totalWeight: Int
)

/**
 * Represents the result of a parallel execution simulation.
 * @param executionOrder The list of batches of NodeIds that can be executed in parallel.
 * @param totalSteps The number of parallel steps required to complete the DAG.
 */
data class ParallelExecutionPlan(
    val executionOrder: List<List<NodeId>>,
    val totalSteps: Int
)
