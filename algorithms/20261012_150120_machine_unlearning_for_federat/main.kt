package federatedkmeans

import kotlin.math.*
import kotlin.random.Random

fun main() {
    val rand = Random(1234)

    // Synthetic data: 3 clients, 2 clusters in 2‑D space
    val clientData = mutableMapOf<Int, MutableList<Point>>()
    var k = 2
    for (clientId in 0..2) {
        val points = mutableListOf<Point>()
        repeat(5) {
            val x = rand.nextDouble(-1.0, 1.0)
            val y = rand.nextDouble(-1.0, 1.0)
            points.add(Point(doubleArrayOf(x, y)))
        }
        repeat(5) {
            val x = rand.nextDouble(9.0, 11.0)
            val y = rand.nextDouble(9.0, 11.0)
            points.add(Point(doubleArrayOf(x, y)))
        }
        clientData[clientId] = points
    }

    val config = Config(maxIterations = 50, tolerance = 1e-6, randomSeed = 42)
    val state = KMeansEngine.runKMeans(clientData, config, k)

    // ---- Fairness verification ----
    for ((clientId, points) in clientData) {
        val limit = (points.size + k - 1) / k
        val counts = IntArray(k)
        for (a in state.assignments) {
            if (a.clientId == clientId) {
                counts[a.clusterId]++
            }
        }
        for (c in 0 until k) {
            assert(counts[c] <= limit) {
                "Fairness violated for client $clientId cluster $c: ${counts[c]} > $limit"
            }
        }
    }

    // ---- Unlearning test ----
    val beforeCentroids = state.centroids.map { it.copy() }
    val toRemove = state.assignments.firstOrNull { it.clientId == 0 && it.clusterId == 0 }?.point
    if (toRemove != null) {
        KMeansEngine.unlearnPoint(0, toRemove, clientData, state)
        val afterCentroids = state.centroids
        val shift = KMeansEngine.totalCentroidShift(beforeCentroids, afterCentroids)
        assert(shift > 0.0) { "Centroid did not shift after unlearning a point" }
    }

    // ---- Edge case: single point, single cluster ----
    val singleClient = mutableMapOf<Int, MutableList<Point>>()
    singleClient[0] = mutableListOf(Point(doubleArrayOf(5.0, -3.0)))
    val singleState = KMeansEngine.runKMeans(singleClient, Config(maxIterations = 10, tolerance = 1e-9, randomSeed = 1), k = 1)
    assert(singleState.centroids.size == 1) { "Expected exactly one centroid" }
    val centroid = singleState.centroids[0]
    assert(abs(centroid.coordinates[0] - 5.0) < 1e-9 && abs(centroid.coordinates[1] + 3.0) < 1e-9) {
        "Centroid does not match the sole data point"
    }

    // ---- Edge case: empty dataset ----
    val emptyClients = mutableMapOf<Int, MutableList<Point>>()
    try {
        val emptyState = KMeansEngine.runKMeans(emptyClients, Config(maxIterations = 5, tolerance = 1e-6, randomSeed = 0), k = 1)
        assert(emptyState.assignments.isEmpty()) { "Assignments should be empty when no data is provided" }
    } catch (e: IllegalArgumentException) {
        // Expected because centroids cannot be initialized without points
    }

    println("All assertions passed.")
}