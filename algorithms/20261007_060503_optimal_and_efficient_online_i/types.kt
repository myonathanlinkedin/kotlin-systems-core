import kotlin.math.abs

data class Decision(val components: IntArray) {
    init {
        require(components.isNotEmpty()) { "Decision must have at least one component." }
    }

    fun dimension(): Int = components.size

    operator fun get(index: Int): Int = components[index]

    fun squaredDistance(other: Decision): Double {
        require(this.dimension() == other.dimension()) { "Dimension mismatch." }
        var sum = 0.0
        for (i in components.indices) {
            val diff = (components[i] - other.components[i]).toDouble()
            sum += diff * diff
        }
        return sum
    }
}

data class Model(var weights: DoubleArray) {
    init {
        require(weights.isNotEmpty()) { "Weight vector must not be empty." }
    }

    fun dimension(): Int = weights.size

    operator fun get(index: Int): Double = weights[index]

    operator fun set(index: Int, value: Double) {
        weights[index] = value
    }

    fun copy(): Model = Model(weights.clone())
}
