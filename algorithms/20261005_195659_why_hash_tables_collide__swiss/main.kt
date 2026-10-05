package hash

import kotlin.random.Random

fun basicTests() {
    val map = RobinHoodHashMap<String, Int>()
    assert(map.size() == 0)
    assert(map.put("a", 1) == null)
    assert(map.get("a") == 1)
    assert(map.put("a", 2) == 1)
    assert(map.get("a") == 2)
    assert(map.put("b", 3) == null)
    assert(map.get("b") == 3)
    assert(map.remove("a") == 2)
    assert(map.get("a") == null)
    assert(map.size() == 1)
    map.clear()
    assert(map.size() == 0)
}

fun collisionTests() {
    // Force collisions by using keys with same hash modulo small capacity
    val map = RobinHoodHashMap<Int, String>(8)
    for (i in 0 until 20) {
        map.put(i, "v$i")
    }
    for (i in 0 until 20) {
        assert(map.get(i) == "v$i")
    }
    // Remove a few keys
    for (i in 0 until 20 step 3) {
        assert(map.remove(i) == "v$i")
    }
    for (i in 0 until 20) {
        if (i % 3 == 0) {
            assert(map.get(i) == null)
        } else {
            assert(map.get(i) == "v$i")
        }
    }
}

fun randomStressTest() {
    val map = RobinHoodHashMap<Int, Int>()
    val reference = mutableMapOf<Int, Int>()
    repeat(10_000) {
        val key = Random.nextInt(0, 5000)
        val value = Random.nextInt()
        when (Random.nextInt(3)) {
            0 -> {
                // put
                val old = map.put(key, value)
                val refOld = reference.put(key, value)
                assert(old == refOld)
            }
            1 -> {
                // get
                assert(map.get(key) == reference[key])
            }
            else -> {
                // remove
                val old = map.remove(key)
                val refOld = reference.remove(key)
                assert(old == refOld)
            }
        }
    }
    // final consistency check
    for ((k, v) in reference) {
        assert(map.get(k) == v)
    }
    assert(map.size() == reference.size)
}

fun benchmark() {
    val map = RobinHoodHashMap<Int, Int>()
    val n = 1_000_000
    val start = System.nanoTime()
    for (i in 0 until n) {
        map.put(i, i)
    }
    val mid = System.nanoTime()
    for (i in 0 until n) {
        assert(map.get(i) == i)
    }
    val end = System.nanoTime()
    val putTime = (mid - start) / 1_000_000.0
    val getTime = (end - mid) / 1_000_000.0
    println("Put $n entries in %.2f ms".format(putTime))
    println("Get $n entries in %.2f ms".format(getTime))
}

fun main() {
    basicTests()
    collisionTests()
    randomStressTest()
    benchmark()
    println("All tests passed.")
}
