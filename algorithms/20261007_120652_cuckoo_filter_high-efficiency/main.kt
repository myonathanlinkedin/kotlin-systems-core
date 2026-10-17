package cuckoo

import kotlin.math.*

fun main() {
    val filter = CuckooFilter(1000)

    // Test insertion and lookup
    val keys = List(500) { "key$it" }
    for (k in keys) {
        val inserted = filter.insert(k)
        check(inserted) { "Failed to insert $k" }
    }

    for (k in keys) {
        val contains = filter.contains(k)
        check(contains) { "Filter should contain $k" }
    }

    // Test deletion
    val toDelete = keys.take(100)
    for (k in toDelete) {
        val deleted = filter.delete(k)
        check(deleted) { "Failed to delete $k" }
    }

    for (k in toDelete) {
        val contains = filter.contains(k)
        check(!contains) { "Filter should not contain $k after deletion" }
    }

    // Test that deleted keys can be reinserted
    for (k in toDelete) {
        val inserted = filter.insert(k)
        check(inserted) { "Failed to reinsert $k after deletion" }
    }

    // Test non-existent keys
    val nonExistent = listOf("foo", "bar", "baz")
    for (k in nonExistent) {
        val contains = filter.contains(k)
        check(!contains) { "Filter should not contain $k" }
        val deleted = filter.delete(k)
        check(!deleted) { "Deletion should fail for non-existent $k" }
    }

    // Test capacity handling (should not exceed maxKicks)
    val extraKeys = List(200) { "extra$it" }
    var successCount = 0
    for (k in extraKeys) {
        if (filter.insert(k)) successCount++
    }
    println("Inserted $successCount out of ${extraKeys.size} extra keys")

    println("All tests passed.")
}