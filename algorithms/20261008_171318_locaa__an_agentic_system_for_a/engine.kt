import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class DummyCompressor : Compressor {
    override fun compress(inputSize: Long, params: CompressionParams): CompressionResult {
        val quality = max(0, min(100, params.quality))
        val baseSize = inputSize * (100L - quality.toLong()) / 100L
        val noise = (quality % 10).toLong()
        val compressedSize = baseSize + noise
        val qualityScore = quality.toDouble() - (noise.toDouble() / 10.0)
        return CompressionResult(compressedSize, qualityScore)
    }
}

class AgenticTuner(
    val compressor: Compressor,
    val inputSize: Long,
    val targetSize: Long,
    val tolerance: Double = 0.01
) {
    fun tune(initialParams: CompressionParams): CompressionParams {
        if (targetSize <= 0L) return CompressionParams(0)
        if (targetSize >= inputSize) return CompressionParams(0)
        var low = 0
        var high = 100
        var bestParams = CompressionParams(0)
        var bestDiff = Long.MAX_VALUE
        while (low <= high) {
            val mid = (low + high) / 2
            val params = CompressionParams(mid)
            val result = compressor.compress(inputSize, params)
            val diff = abs(result.compressedSize - targetSize)
            if (diff < bestDiff) {
                bestDiff = diff
                bestParams = params
            }
            if (result.compressedSize > targetSize) {
                low = mid + 1
            } else {
                high = mid - 1
            }
        }
        return bestParams
    }
}
