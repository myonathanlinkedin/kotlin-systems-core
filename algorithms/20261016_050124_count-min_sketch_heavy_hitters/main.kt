import kotlin.random.Random

fun testBasicFunctionality() {
    val cms = CountMinSketch<String>(width = 1000, depth = 5)
    cms.add("alpha", 5L)
    cms.add("beta", 3L)
    cms.add("alpha", 2L) // total for "alpha" = 7

    assert(cms.estimate("alpha") == 7L) { "Alpha count mismatch" }
    assert(cms.estimate("beta") == 3L) { "Beta count mismatch" }
    assert(cms.estimate("gamma") == 0L) { "Gamma should be zero" }
}

fun testErrorBound() {
    var width = 200
    var depth = 5
    val cms = CountMinSketch<String>(width, depth)

    val total = 10_000L
    repeat(total.toInt()) { cms.add("x") }

    val est = cms.estimate("x")
    // Theoretical additive error ≤ total / width
    val epsilon = total / width
    assert(est >= total) { "Estimate must not be below true count" }
    assert(est <= total + epsilon + 1) { "Estimate exceeds error bound" }
}

fun testHeavyHittersExtraction() {
    val cmsHeavy = CountMinSketchHeavy<String>(width = 500, depth = 5)

    repeat(1_000) { cmsHeavy.add("apple") }
    repeat(200) { cmsHeavy.add("banana") }
    repeat(50) { cmsHeavy.add("cherry") }

    val hitters = cmsHeavy.heavyHitters(threshold = 300L)
    val hitterSet = hitters.map { it.item }.toSet()

    assert("apple" in hitterSet) { "Apple should be reported as heavy hitter" }
    assert("banana" !in hitterSet) { "Banana should NOT be reported (below threshold)" }
    assert("cherry" !in hitterSet) { "Cherry should NOT be reported (below threshold)" }

    // Verify that the reported counts are at least the threshold.
    for (hit in hitters) {
        assert(hit.estimatedCount >= 300L) { "Reported count below threshold" }
    }
}

/**
 * Simple benchmark to demonstrate deterministic runtime.
 * Not part of the verification suite but useful for manual inspection.
 */
fun benchmark() {
    var width = 1024
    var depth = 7
    val cms = CountMinSketch<Int>(width, depth)
    val items = IntArray(1_000_000) { Random.nextInt(0, 10_000) }

    val start = System.nanoTime()
    for (v in items) {
        cms.add(v)
    }
    val durationMs = (System.nanoTime() - start) / 1_000_000
    println("Inserted ${items.size} items in $durationMs ms")
}

/** Entry point – runs all unit tests. */
fun main() {
    testBasicFunctionality()
    testErrorBound()
    testHeavyHittersExtraction()
    println("All Count‑Min Sketch tests passed.")
    // Uncomment to run a quick benchmark:
    // benchmark()
}
