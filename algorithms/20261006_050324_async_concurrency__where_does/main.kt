import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.TimeUnit
import java.util.concurrent.RejectedExecutionException

fun main() {
    runTests()
    runBenchmark()
    println("All tests passed")
}

fun runTests() {
    testSingleThreadExecution()
    testConcurrentExecution()
    testShutdownBehavior()
}

fun testSingleThreadExecution() {
    val counter = AtomicInteger(0)
    val scheduler = Scheduler(1)
    repeat(1000) {
        scheduler.schedule { counter.incrementAndGet() }
    }
    scheduler.awaitTermination(5L, TimeUnit.SECONDS)
    check(counter.get() == 1000) { "Counter should be 1000, got ${counter.get()}" }
}

fun testConcurrentExecution() {
    val counter = AtomicInteger(0)
    val scheduler = Scheduler(4)
    repeat(4000) {
        scheduler.schedule {
            Thread.sleep(10L)
            counter.incrementAndGet()
        }
    }
    scheduler.awaitTermination(10L, TimeUnit.SECONDS)
    check(counter.get() == 4000) { "Counter should be 4000, got ${counter.get()}" }
    val start = System.nanoTime()
    val scheduler2 = Scheduler(4)
    repeat(4000) {
        scheduler2.schedule {
            Thread.sleep(10L)
        }
    }
    scheduler2.awaitTermination(10L, TimeUnit.SECONDS)
    val elapsedMs = (System.nanoTime() - start) / 1_000_000
    check(elapsedMs < 5000L) { "Tasks took too long: $elapsedMs ms" }
}

fun testShutdownBehavior() {
    val scheduler = Scheduler(2)
    scheduler.schedule { }
    scheduler.awaitTermination(1L, TimeUnit.SECONDS)
    check(scheduler.isShutdown()) { "Scheduler should be shutdown" }
    try {
        scheduler.schedule { }
        check(false) { "Scheduling after shutdown should throw" }
    } catch (e: RejectedExecutionException) {
        // expected
    }
}

fun runBenchmark() {
    val scheduler = Scheduler(4)
    val start = System.nanoTime()
    repeat(10000) {
        scheduler.schedule { Thread.sleep(1L) }
    }
    scheduler.awaitTermination(30L, TimeUnit.SECONDS)
    val elapsedMs = (System.nanoTime() - start) / 1_000_000
    println("Benchmark: 10000 tasks with 1ms sleep each took $elapsedMs ms")
}
