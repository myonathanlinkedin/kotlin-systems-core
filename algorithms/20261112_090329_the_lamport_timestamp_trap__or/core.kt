import kotlin.math.max

/**
 * Lamport logical timestamp consisting of a counter and a process identifier.
 * The pair (counter, pid) provides a total order.
 */
data class LamportTimestamp(var counter: Long, val pid: Int)

/**
 * LamportClock implements the classic Lamport logical clock for a single process.
 *
 * @property pid Unique identifier of the process owning this clock.
 */
class LamportClock(val pid: Int) {
    var counter: Long = 0L

    /**
     * Record a local event. Increments the internal counter and returns the new timestamp.
     */
    fun tick(): LamportTimestamp {
        counter = counter + 1L
        return LamportTimestamp(counter, pid)
    }

    /**
     * Incorporate a timestamp received from another process.
     * The internal counter becomes max(local, remote) + 1.
     *
     * @param remote Timestamp received from another process.
     * @return New timestamp after processing the receive event.
     */
    fun receive(remote: LamportTimestamp): LamportTimestamp {
        counter = max(counter, remote.counter) + 1L
        return LamportTimestamp(counter, pid)
    }
}

/**
 * Determines whether timestamp `a` *happens-before* timestamp `b` according to Lamport's definition.
 *
 * @return true if a < b in the total order, false otherwise.
 */
fun happensBefore(a: LamportTimestamp, b: LamportTimestamp): Boolean {
    return if (a.counter < b.counter) {
        true
    } else if (a.counter > b.counter) {
        false
    } else {
        a.pid < b.pid
    }
}

/**
 * Determines whether two timestamps are concurrent (i.e., neither happens-before the other).
 *
 * @return true if a and b are concurrent, false otherwise.
 */
fun isConcurrent(a: LamportTimestamp, b: LamportTimestamp): Boolean {
    return !(happensBefore(a, b) || happensBefore(b, a))
}
