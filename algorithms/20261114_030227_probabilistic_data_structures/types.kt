package chronos

import kotlin.math.*

data class BloomFilterParams(
    val capacity: Int,
    val falsePositiveRate: Double
)