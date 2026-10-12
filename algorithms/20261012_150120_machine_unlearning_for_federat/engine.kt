package federatedkmeans

import kotlin.math.*
import kotlin.random.Random

object KMeansEngine {
    fun initializeCentroids(points: List<Point>, k: Int, seed: Int): MutableList<Point> {
        require(k > 0) { "k must be positive" }
        require(points.size >= k) { "Not enough points to initialize $k centroids" }
        val random = Random(seed)
        val shuffled = points.shuffled(random)
        return shuffled.take(k).map { it.copy() }.toMutableList()
    }

    fun assignPoints(
        pointsByClient: Map<Int, List<Point>>,
        state: FederatedState
    ) {
        state.assignments.clear()
        for ((clientId, points) in pointsByClient) {
            for (pt in points) {
                val nearest = nearestCentroid(pt, state.centroids)
                state.assignments.add(Assignment(clientId, pt, nearest))
            }
        }
        enforceFairness(pointsByClient, state)
    }

    private fun nearestCentroid(point: Point, centroids: List<Point>): Int {
        var bestIdx = 0
        var bestDist = point.distanceSquared(centroids[0])
        for (i in 1 until centroids.size) {
            val d = point.distanceSquared(centroids[i])
            if (d < bestDist) {
                bestDist = d
                bestIdx = i
            }
        }
        return bestIdx
    }

    // Simple fairness: each client may have at most ceil(numPointsClient / k) points per cluster
    private fun enforceFairness(
        pointsByClient: Map<Int, List<Point>>,
        state: FederatedState
    ) {
        val k = state.k
        for ((clientId, points) in pointsByClient) {
            val limit = (points.size + k - 1) / k // ceil division
            val count = IntArray(k)
            val clientAssignments = state.assignments.filter { it.clientId == clientId }
            for (a in clientAssignments) {
                count[a.clusterId]++
            }
            for (clusterIdx in 0 until k) {
                while (count[clusterIdx] > limit) {
                    var bestPenalty = Double.MAX_VALUE
                    var bestAssignment: Assignment? = null
                    var bestNewCluster = -1
                    for (a in clientAssignments) {
                        if (a.clusterId != clusterIdx) continue
                        for (c in 0 until k) {
                            if (c == clusterIdx) continue
                            val penalty = pointPenalty(a.point, state.centroids[clusterIdx], state.centroids[c])
                            if (penalty < bestPenalty) {
                                bestPenalty = penalty
                                bestAssignment = a
                                bestNewCluster = c
                            }
                        }
                    }
                    if (bestAssignment == null) break
                    count[bestAssignment.clusterId]--
                    bestAssignment.clusterId = bestNewCluster
                    count[bestAssignment.clusterId]++
                }
            }
        }
    }

    private fun pointPenalty(point: Point, from: Point, to: Point): Double {
        return point.distanceSquared(to) - point.distanceSquared(from)
    }

    fun recomputeCentroids(state: FederatedState) {
        val k = state.k
        val dim = state.centroids[0].dimension
        val sums = Array(k) { DoubleArray(dim) }
        val counts = IntArray(k)
        for (a in state.assignments) {
            val idx = a.clusterId
            val coords = a.point.coordinates
            for (i in 0 until dim) {
                sums[idx][i] += coords[i]
            }
            counts[idx]++
        }
        for (i in 0 until k) {
            if (counts[i] == 0) continue // keep old centroid if cluster empty
            val newCoords = DoubleArray(dim) { j -> sums[i][j] / counts[i] }
            state.centroids[i] = Point(newCoords)
        }
    }

    fun totalCentroidShift(old: List<Point>, new: List<Point>): Double {
        var sum = 0.0
        for (i in old.indices) {
            sum += old[i].distanceSquared(new[i])
        }
        return sqrt(sum)
    }

    fun runKMeans(
        pointsByClient: Map<Int, List<Point>>,
        config: Config,
        k: Int
    ): FederatedState {
        val allPoints = pointsByClient.values.flatten()
        require(allPoints.isNotEmpty()) { "No data points provided" }
        val centroids = initializeCentroids(allPoints, k, config.randomSeed)
        val state = FederatedState(k, centroids, mutableListOf())
        assignPoints(pointsByClient, state)
        recomputeCentroids(state)
        var iteration = 0
        while (iteration < config.maxIterations) {
            val oldCentroids = state.centroids.map { it.copy() }
            assignPoints(pointsByClient, state)
            recomputeCentroids(state)
            val shift = totalCentroidShift(oldCentroids, state.centroids)
            if (shift < config.tolerance) break
            iteration++
        }
        return state
    }

    fun unlearnPoint(
        clientId: Int,
        point: Point,
        pointsByClient: MutableMap<Int, MutableList<Point>>,
        state: FederatedState
    ) {
        val clientPoints = pointsByClient[clientId] ?: return
        val removed = clientPoints.remove(point)
        if (!removed) return
        state.assignments.removeIf { it.clientId == clientId && it.point === point }
        recomputeCentroids(state)
    }
}