import kotlin.math.abs

data class CompressionParams(val quality: Int)

data class CompressionResult(val compressedSize: Long, val qualityScore: Double)

interface Compressor {
    fun compress(inputSize: Long, params: CompressionParams): CompressionResult
}
