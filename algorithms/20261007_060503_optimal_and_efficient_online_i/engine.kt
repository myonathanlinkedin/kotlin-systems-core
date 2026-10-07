import kotlin.math.abs

class InverseOptimizer(
    private val feasibleSet: List<Decision>,
    private var learningRate: Double = 0.1
) {
    init {
        require(feasibleSet.isNotEmpty()) { "Feasible set cannot be empty." }
        val dim = feasibleSet.first().dimension()
        require(feasibleSet.all { it.dimension() == dim }) { "All decisions must share the same dimension." }
        require(learningRate >= 0.0) { "Learning rate must be non‑negative." }
    }

    private val dimension = feasibleSet.first().dimension()
    val model = Model(DoubleArray(dimension) { 0.0 })

    private fun dot(weights: DoubleArray, decision: Decision): Double {
        var sum = 0.0
        for (i in weights.indices) {
            sum += weights[i] * decision[i]
        }
        return sum
    }

    fun predict(): Decision {
        var bestDecision: Decision? = null
        var bestValue = Double.MAX_VALUE
        for (d in feasibleSet) {
            val value = dot(model.weights, d)
            if (value < bestValue) {
                bestValue = value
                bestDecision = d
            }
        }
        return bestDecision!!
    }

    fun update(observed: Decision) {
        require(observed.dimension() == dimension) { "Observed decision dimension mismatch." }
        val predicted = predict()
        for (i in model.weights.indices) {
            val gradient = (observed[i] - predicted[i]).toDouble()
            model[i] = model[i] + learningRate * gradient
        }
    }

    fun loss(observed: Decision): Double = observed.squaredDistance(predict())
}
