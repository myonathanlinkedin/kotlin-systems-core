package federatedkmeans

import kotlin.math.*

data class Point(val coordinates: DoubleArray) {
    val dimension: Int get() = coordinates.size
    fun distanceSquared(other: Point): Double {
        var sum = 0.0
        for (i in coordinates.indices) {
            val diff = coordinates[i] - other.coordinates[i]
            sum += diff * diff
        }
        return sum
    }
    fun copy(): Point = Point(coordinates.clone())
}

data class Assignment(val clientId: Int, val point: Point, var clusterId: Int)

data class FederatedState(
    val k: Int,
    var centroids: MutableList<Point>,
    val assignments: MutableList<Assignment>
)

data class Config(
    val maxIterations: Int = 20,
    val tolerance: Double = 1e-4,
    val randomSeed: Int = 42
)