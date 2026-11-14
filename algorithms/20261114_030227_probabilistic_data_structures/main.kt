package chronos

import kotlin.random.Random
import kotlin.math.*

fun main() {
    // Test parameters
    val capacity = 1000
    val falsePositiveRate = 0.01
    val params = BloomFilterParams(capacity, falsePositiveRate)
    val filter = BloomFilter<String>(params)

    // Generate test data
    val addedItems = mutableSetOf<String>()
    val random = Random(42)

    // Add items
    repeat(capacity) {
        val item = "item-${random.nextInt(1_000_000)}"
        addedItems.add(item)
        filter.add(item)
    }

    // Verify all added items are reported as present
    for (item in addedItems) {
        check(filter.mightContain(item)) { "Item $item should be present" }
    }

    // Test false positive rate
    val testSize = 10_000
    var falsePositives = 0
    repeat(testSize) {
        val item = "not-added-${random.nextInt(1_000_000)}"
        if (addedItems.contains(item)) return@repeat
        if (filter.mightContain(item)) falsePositives++
    }
    val observedRate = falsePositives.toDouble() / testSize
    println("Observed false positive rate: $observedRate")
    check(observedRate <= falsePositiveRate * 1.5) { "False positive rate too high" }

    // Edge case: empty filter
    val emptyFilter = BloomFilter<String>(BloomFilterParams(10, 0.1))
    check(!emptyFilter.mightContain("anything")) { "Empty filter should not contain any item" }

    // Edge case: single element
    val singleFilter = BloomFilter<String>(BloomFilterParams(1, 0.5))
    singleFilter.add("single")
    check(singleFilter.mightContain("single")) { "Single element should be present" }
    check(!singleFilter.mightContain("other")) { "Other element should not be present" }

    println("All tests passed.")
}