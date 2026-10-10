package core

import kotlin.math.*

enum class ParticipantState {
    INIT,
    PREPARED,
    COMMITTED,
    ABORTED
}

enum class Vote {
    COMMIT,
    ABORT
}

/**
 * Simulated participant in a Two‑Phase Commit protocol.
 *
 * @property id Unique identifier.
 * @property willVoteCommit Determines the vote returned on prepare.
 * @property willTimeout If true, the participant simulates a timeout by returning null.
 */
class Participant(
    val id: Int,
    private val willVoteCommit: Boolean = true,
    private val willTimeout: Boolean = false
) {
    var state: ParticipantState = ParticipantState.INIT
        private set

    /**
     * Called by the coordinator during the prepare phase.
     *
     * @return Vote.COMMIT, Vote.ABORT or null (timeout).
     */
    fun onPrepare(): Vote? {
        if (state != ParticipantState.INIT) return null // already processed
        if (willTimeout) {
            // Simulate a timeout – coordinator treats as failure.
            return null
        }
        state = ParticipantState.PREPARED
        return if (willVoteCommit) Vote.COMMIT else Vote.ABORT
    }

    /** Called by the coordinator when the global decision is COMMIT. */
    fun onCommit() {
        if (state == ParticipantState.PREPARED) {
            state = ParticipantState.COMMITTED
        }
    }

    /** Called by the coordinator when the global decision is ABORT. */
    fun onAbort() {
        if (state == ParticipantState.PREPARED || state == ParticipantState.INIT) {
            state = ParticipantState.ABORTED
        }
    }
}

/**
 * Result of a transaction execution.
 *
 * @property committed True if the transaction was committed globally.
 * @property participantStates Snapshot of each participant's final state.
 */
data class TransactionResult(
    var committed: Boolean,
    val participantStates: Map<Int, ParticipantState>
)

/**
 * Coordinator for the Two‑Phase Commit protocol.
 *
 * @property participants List of participants involved in the transaction.
 */
class Coordinator(private val participants: List<Participant>) {

    /**
     * Executes a single transaction using the 2PC algorithm.
     *
     * @return TransactionResult containing the global decision and final states.
     */
    fun runTransaction(): TransactionResult {
        // Phase 1 – Prepare
        var allYes = true
        for (p in participants) {
            val vote = p.onPrepare()
            if (vote != Vote.COMMIT) {
                // vote is ABORT or null (timeout)
                allYes = false
                break
            }
        }

        // Phase 2 – Commit or Abort
        if (allYes) {
            participants.forEach { it.onCommit() }
        } else {
            participants.forEach { it.onAbort() }
        }

        val finalStates = participants.associate { it.id to it.state }
        return TransactionResult(committed = allYes, participantStates = finalStates)
    }
}