package main

import core.*
import kotlin.math.*

fun testCommitAllYes() {
    val participants = listOf(
        Participant(id = 1, willVoteCommit = true),
        Participant(id = 2, willVoteCommit = true),
        Participant(id = 3, willVoteCommit = true)
    )
    val coordinator = Coordinator(participants)
    val result = coordinator.runTransaction()
    check(result.committed) { "Transaction should be committed" }
    participants.forEach {
        check(it.state == ParticipantState.COMMITTED) {
            "Participant ${it.id} expected COMMITTED, got ${it.state}"
        }
    }
}

fun testAbortOnNoVote() {
    val participants = listOf(
        Participant(id = 1, willVoteCommit = true),
        Participant(id = 2, willVoteCommit = false), // votes ABORT
        Participant(id = 3, willVoteCommit = true)
    )
    val coordinator = Coordinator(participants)
    val result = coordinator.runTransaction()
    check(!result.committed) { "Transaction should be aborted due to a NO vote" }
    participants.forEach {
        check(it.state == ParticipantState.ABORTED) {
            "Participant ${it.id} expected ABORTED, got ${it.state}"
        }
    }
}

fun testAbortOnTimeout() {
    val participants = listOf(
        Participant(id = 1, willVoteCommit = true),
        Participant(id = 2, willVoteCommit = true, willTimeout = true), // timeout
        Participant(id = 3, willVoteCommit = true)
    )
    val coordinator = Coordinator(participants)
    val result = coordinator.runTransaction()
    check(!result.committed) { "Transaction should be aborted due to timeout" }
    participants.forEach {
        check(it.state == ParticipantState.ABORTED) {
            "Participant ${it.id} expected ABORTED after timeout, got ${it.state}"
        }
    }
}

/**
 * Simple benchmark to illustrate deterministic runtime.
 * Not a performance test – just ensures the algorithm runs in O(n) time.
 */
fun benchmarkLinearScale(participantCount: Int) {
    val participants = (1..participantCount).map { Participant(it, willVoteCommit = true) }
    val coordinator = Coordinator(participants)
    val start = System.nanoTime()
    coordinator.runTransaction()
    val elapsed = System.nanoTime() - start
    // Expect linear growth; we only assert that it finishes quickly.
    check(elapsed < 10_000_000L) { "Benchmark took too long: $elapsed ns" }
}

fun main() {
    testCommitAllYes()
    testAbortOnNoVote()
    testAbortOnTimeout()
    benchmarkLinearScale(1000)
    println("All tests passed.")
}