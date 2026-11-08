package main

fun main() {
    // Basic functionality test
    val cache = LRUCache<Int, String>(2)
    cache.put(1, "one")
    cache.put(2, "two")
    assert(cache.get(1) == "one") { "Expected value 'one' for key 1" }
    // This put should evict key 2 because key 1 was recently accessed
    cache.put(3, "three")
    assert(cache.get(2) == null) { "Key 2 should have been evicted" }
    assert(cache.get(3) == "three") { "Expected value 'three' for key 3" }
    assert(cache.get(1) == "one") { "Expected value 'one' for key 1 after eviction" }

    // Update existing key and ensure order is correct
    cache.put(1, "ONE")
    assert(cache.get(1) == "ONE") { "Expected updated value 'ONE' for key 1" }
    cache.put(4, "four")
    // At this point, cache holds keys 1 and 4; key 3 should be evicted
    assert(cache.get(3) == null) { "Key 3 should have been evicted after inserting key 4" }
    assert(cache.get(4) == "four") { "Expected value 'four' for key 4" }

    // Edge case: capacity zero
    val zeroCache = LRUCache<String, Int>(0)
    zeroCache.put("a", 1)
    assert(zeroCache.get("a") == null) { "Zero-capacity cache should never store items" }

    // Stress test: many operations
    val bigCache = LRUCache<Int, Int>(5)
    for (i in 0 until 10) {
        bigCache.put(i, i * i)
    }
    // Only last 5 keys (5..9) should remain
    for (i in 0 until 5) {
        assert(bigCache.get(i) == null) { "Key $i should have been evicted in bigCache" }
    }
    for (i in 5 until 10) {
        assert(bigCache.get(i) == i * i) { "Key $i should be present with correct value" }
    }

    // Verify that accessing a key updates its recency
    bigCache.get(5) // now 5 is most recent
    bigCache.put(10, 100)
    // Key 6 should be evicted (oldest among 6..9)
    assert(bigCache.get(6) == null) { "Key 6 should have been evicted after accessing 5 and inserting 10" }
    assert(bigCache.get(5) == 25) { "Key 5 should still be present" }
    assert(bigCache.get(10) == 100) { "Key 10 should be present" }

    // All assertions passed
    println("All LRUCache tests passed.")
}