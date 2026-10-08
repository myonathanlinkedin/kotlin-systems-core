package core

import kotlin.random.Random

data class SKModel(val n: Int, val interactions: Array<DoubleArray>) {
    init {
        require(interactions.size == n) { "Interaction matrix size mismatch" }
        for (row in interactions) {
            require(row.size == n) { "Interaction matrix rows must have length n" }
        }
    }

    fun energy(spins: IntArray): Double {
        require(spins.size == n) { "Spin vector size mismatch" }
        var sum = 0.0
        for (i in 0 until n) {
            for (j in i + 1 until n) {
                sum += interactions[i][j] * spins[i] * spins[j]
            }
        }
        return -sum
    }

    fun randomSpins(random: Random = Random.Default): IntArray =
        IntArray(n) { if (random.nextBoolean()) 1 else -1 }

    fun bruteForceMinEnergy(): Pair<IntArray, Double> {
        var bestEnergy = Double.POSITIVE_INFINITY
        var bestSpins = IntArray(n)
        val total = 1 shl n
        for (mask in 0 until total) {
            val spins = IntArray(n) { idx -> if ((mask shr idx) and 1 == 1) 1 else -1 }
            val e = energy(spins)
            if (e < bestEnergy) {
                bestEnergy = e
                bestSpins = spins.copyOf()
            }
        }
        return Pair(bestSpins, bestEnergy)
    }

    fun colorCoding(samples: Int, random: Random = Random.Default): Pair<IntArray, Double> {
        require(samples > 0) { "Number of samples must be positive" }
        var bestEnergy = Double.POSITIVE_INFINITY
        var bestSpins = IntArray(n)
        repeat(samples) {
            val spins = randomSpins(random)
            val e = energy(spins)
            if (e < bestEnergy) {
                bestEnergy = e
                bestSpins = spins.copyOf()
            }
        }
        return Pair(bestSpins, bestEnergy)
    }
}