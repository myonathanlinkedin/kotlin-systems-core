import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    val compressor = DummyCompressor()

    // Test 1: Standard tuning
    val tuner1 = AgenticTuner(compressor, inputSize = 1000L, targetSize = 500L)
    val tunedParams1 = tuner1.tune(CompressionParams(50))
    val result1 = compressor.compress(1000L, tunedParams1)
    val relDiff1 = abs(result1.compressedSize - 500L).toDouble() / 500.0
    check(relDiff1 <= 0.01) { "Test 1 failed: relative diff $relDiff1" }

    // Test 2: Zero input and target
    val tuner2 = AgenticTuner(compressor, inputSize = 0L, targetSize = 0L)
    val tunedParams2 = tuner2.tune(CompressionParams(50))
    check(tunedParams2.quality == 0) { "Test 2 failed: expected quality 0" }

    // Test 3: Target larger than input
    val tuner3 = AgenticTuner(compressor, inputSize = 1000L, targetSize = 1200L)
    val tunedParams3 = tuner3.tune(CompressionParams(50))
    check(tunedParams3.quality == 0) { "Test 3 failed: expected quality 0" }

    // Test 4: Tight target
    val tuner4 = AgenticTuner(compressor, inputSize = 1000L, targetSize = 100L)
    val tunedParams4 = tuner4.tune(CompressionParams(50))
    val result4 = compressor.compress(1000L, tunedParams4)
    val relDiff4 = abs(result4.compressedSize - 100L).toDouble() / 100.0
    check(relDiff4 <= 0.01) { "Test 4 failed: relative diff $relDiff4" }
    check(tunedParams4.quality == 90) { "Test 4 failed: expected quality 90, got ${tunedParams4.quality}" }

    // Test 5: Quality bounds
    val tuner5 = AgenticTuner(compressor, inputSize = 1000L, targetSize = 500L)
    val tunedParams5 = tuner5.tune(CompressionParams(-10))
    check(tunedParams5.quality in 0..100) { "Test 5 failed: quality out of bounds" }

    println("All tests passed.")
}
