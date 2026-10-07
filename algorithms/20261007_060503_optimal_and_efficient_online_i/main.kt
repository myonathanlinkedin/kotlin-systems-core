import kotlin.math.abs

fun dot(weights: DoubleArray, decision: Decision): Double {
    var sum = 0.0
    for (i in weights.indices) {
        sum += weights[i] * decision[i]
    }
    return sum
}

fun optimalDecision(weights: DoubleArray, feasibleSet: List<Decision>): Decision {
    var best: Decision? = null
    var bestVal = Double.MAX_VALUE
    for (d in feasibleSet) {
        val v = dot(weights, d)
        if (v < bestVal) {
            bestVal = v
            best = d
        }
    }
    return best!!
}

fun main() {
    // Define a small feasible set in 2‑D.
    val feasible = listOf(
        Decision(intArrayOf(0, 0)),
        Decision(intArrayOf(1, 0)),
        Decision(intArrayOf(0, 1)),
        Decision(intArrayOf(1, 1)),
        Decision(intArrayOf(2, 0)),
        Decision(intArrayOf(0, 2))
    )

    // True underlying weights (unknown to the optimizer).
    val trueWeights = doubleArrayOf(2.0, 1.0)

    // Create optimizer with a moderate learning rate.
    val optimizer = InverseOptimizer(feasible, learningRate = 0.2)

    // Online learning loop.
    repeat(500) {
        val observed = optimalDecision(trueWeights, feasible)
        optimizer.update(observed)
    }

    // Verify convergence: estimated weights should be close to true weights.
    val tolerance = 0.15
    for (i in trueWeights.indices) {
        val diff = abs(optimizer.model[i] - trueWeights[i])
        check(diff < tolerance) { "Weight $i diverged: diff=$diff, tolerance=$tolerance" }
    }

    // Edge case: learning rate zero should keep weights unchanged.
    val zeroRateOptimizer = InverseOptimizer(feasible, learningRate = 0.0)
    val initialCopy = zeroRateOptimizer.model.copy()
    repeat(10) {
        zeroRateOptimizer.update(optimalDecision(trueWeights, feasible))
    }
    for (i in initialCopy.weights.indices) {
        check(abs(zeroRateOptimizer.model[i] - initialCopy.weights[i]) < 1e-12) {
            "Weights changed despite zero learning rate."
        }
    }

    // Edge case: empty feasible set must throw.
    var exceptionThrown = false
    try {
        InverseOptimizer(emptyList())
    } catch (e: IllegalArgumentException) {
        exceptionThrown = true
    }
    check(exceptionThrown) { "Expected exception for empty feasible set." }

    // Demo output.
    println("Estimated weights: ${optimizer.model.weights.joinToString(prefix = "[", postfix = "]")}")
    println("All assertions passed.")
}
