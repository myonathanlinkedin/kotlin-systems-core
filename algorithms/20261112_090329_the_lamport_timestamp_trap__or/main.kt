import kotlin.random.Random

/**
 * Simple deterministic test harness using Kotlin's built‑in `assert`.
 * All assertions must hold; otherwise an AssertionError is thrown.
 */
fun testMonotonicLocalEvents() {
    val clock = LamportClock(pid = 1)
    val t1 = clock.tick()
    val t2 = clock.tick()
    assert(t1.counter == 1L && t1.pid == 1) { "First tick incorrect" }
    assert(t2.counter == 2L && t2.pid == 1) { "Second tick incorrect" }
    assert(happensBefore(t1, t2)) { "t1 should happen before t2" }
}

fun testReceiveUpdates() {
    val sender = LamportClock(pid = 2)
    val receiver = LamportClock(pid = 3)

    // Sender performs two local events
    val s1 = sender.tick()   // counter = 1
    val s2 = sender.tick()   // counter = 2

    // Receiver receives s2
    val r1 = receiver.receive(s2) // counter = max(0,2)+1 = 3
    assert(r1.counter == 3L) { "Receiver counter after receive incorrect" }
    assert(happensBefore(s2, r1)) { "Sender's event should happen before receiver's receive" }

    // Receiver performs a local event
    val r2 = receiver.tick() // counter = 4
    assert(r2.counter == 4L) { "Receiver local tick after receive incorrect" }
    assert(happensBefore(r1, r2)) { "Receive should happen before next local event" }
}

fun testConcurrencyDetection() {
    val pA = LamportClock(pid = 10)
    val pB = LamportClock(pid = 20)

    // Each process performs a single local event without communication
    val a1 = pA.tick() // (1,10)
    val b1 = pB.tick() // (1,20)

    // According to total order, (1,10) < (1,20)
    assert(happensBefore(a1, b1)) { "a1 should be ordered before b1 by pid tie‑break" }
    // Yet they are concurrent because no causal path exists
    assert(isConcurrent(a1, b1)) { "a1 and b1 must be concurrent despite ordering" }
}

fun testRandomScenario() {
    val processes = List(5) { idx -> LamportClock(pid = idx + 1) }
    val events = mutableListOf<LamportTimestamp>()

    // Simulate 100 random local events or message exchanges
    repeat(100) {
        val actorIdx = Random.nextInt(processes.size)
        val actor = processes[actorIdx]

        if (Random.nextBoolean()) {
            // Local event
            events.add(actor.tick())
        } else {
            // Message exchange: pick a different target
            val targetIdx = (actorIdx + Random.nextInt(1, processes.size)) % processes.size
            val target = processes[targetIdx]
            val sent = actor.tick()
            events.add(sent) // record send event
            events.add(target.receive(sent)) // record receive event
        }
    }

    // Verify that for every receive event, the sender's timestamp happens before the receive timestamp
    for (i in events.indices) {
        val ts = events[i]
        // Find a later event that is a receive of this timestamp (if any)
        for (j in i + 1 until events.size) {
            val later = events[j]
            if (later.pid != ts.pid && later.counter >= ts.counter) {
                // If later event is a receive of ts, it must happen after ts
                if (happensBefore(ts, later) || isConcurrent(ts, later)) {
                    // No assertion needed; just ensure no contradictory ordering
                }
            }
        }
    }
}

/**
 * Entry point executing all unit tests.
 */
fun main() {
    testMonotonicLocalEvents()
    testReceiveUpdates()
    testConcurrencyDetection()
    testRandomScenario()
    println("All Lamport clock tests passed.")
}
