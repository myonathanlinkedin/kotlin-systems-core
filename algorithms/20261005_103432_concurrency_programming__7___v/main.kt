package concurrency

fun main() {
    // Test 1: flag becomes true after setter thread runs
    val flag = VolatileFlag()
    val setter = FlagSetter(flag, delayMs = 100)
    setter.start()
    val observed = waitForFlag(flag, timeoutMs = 2000)
    assert(observed) { "Flag should have been set by setter thread" }
    setter.join()

    // Test 2: multiple setters race, final flag true
    val flag2 = VolatileFlag()
    val setters = List(10) { FlagSetter(flag2, delayMs = (it * 10).toLong()) }
    setters.forEach { it.start() }
    val observed2 = waitForFlag(flag2, timeoutMs = 3000)
    assert(observed2) { "Flag should be true after multiple setters" }
    setters.forEach { it.join() }

    // Test 3: Counter increment with volatile visibility
    val counter = Counter()
    val increments = 100_000
    val threadCount = 4
    val threads = List(threadCount) {
        Thread {
            repeat(increments) {
                // Visibility is guaranteed by @Volatile, but operation is not atomic.
                counter.value = counter.value + 1
            }
        }
    }
    threads.forEach { it.start() }
    threads.forEach { it.join() }
    // Since increments are not atomic, final value may be less than expected.
    // We assert that at least a single thread's work is visible.
    assert(counter.value >= increments) {
        "Counter should be at least $increments, got ${counter.value}"
    }

    println("All concurrency volatile tests passed.")
}
