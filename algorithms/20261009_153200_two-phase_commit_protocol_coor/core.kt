import kotlin.math.*

enum class ParticipantState {
    INIT,
    PREPARED,
    COMMITTED,
    ABORTED
}

enum class CoordinatorState {
    INIT,
    WAITING,
    COMMITTED,
    ABORTED
}

sealed class Message {
    abstract val transactionId: Int
}

data class Prepare(override val transactionId: Int) : Message()
data class VoteCommit(override val transactionId: Int, val participantId: Int) : Message()
data class VoteAbort(override val transactionId: Int, val participantId: Int) : Message()
data class Commit(override val transactionId: Int) : Message()
data class Abort(override val transactionId: Int) : Message()

data class Participant(
    val id: Int,
    var state: ParticipantState = ParticipantState.INIT,
    val willCommit: Boolean = true,
    val willRespond: Boolean = true
) {
    fun onMessage(msg: Message): Message? {
        if (!willRespond) {
            // Simulate a silent participant
            return null
        }
        return when (msg) {
            is Prepare -> {
                if (state != ParticipantState.INIT) {
                    // Unexpected state, ignore
                    null
                } else {
                    if (willCommit) {
                        state = ParticipantState.PREPARED
                        VoteCommit(msg.transactionId, id)
                    } else {
                        state = ParticipantState.ABORTED
                        VoteAbort(msg.transactionId, id)
                    }
                }
            }
            is Commit -> {
                if (state == ParticipantState.PREPARED) {
                    state = ParticipantState.COMMITTED
                }
                null
            }
            is Abort -> {
                // Abort can be received in any non-final state
                if (state != ParticipantState.COMMITTED) {
                    state = ParticipantState.ABORTED
                }
                null
            }
            else -> null
        }
    }
}

data class Coordinator(
    val participants: List<Participant>,
    val transactionId: Int = 1
) {
    var state: CoordinatorState = CoordinatorState.INIT

    fun execute(): Boolean {
        state = CoordinatorState.WAITING
        val votes = mutableListOf<Message>()
        // Phase 1: Prepare
        for (p in participants) {
            val response = p.onMessage(Prepare(transactionId))
            if (response == null) {
                // No response treated as abort
                state = CoordinatorState.ABORTED
                broadcast(Abort(transactionId))
                return false
            }
            votes.add(response)
        }
        // Evaluate votes
        val anyAbort = votes.any { it is VoteAbort }
        if (anyAbort) {
            state = CoordinatorState.ABORTED
            broadcast(Abort(transactionId))
            return false
        }
        // All commit votes
        state = CoordinatorState.COMMITTED
        broadcast(Commit(transactionId))
        return true
    }

    private fun broadcast(msg: Message) {
        for (p in participants) {
            p.onMessage(msg)
        }
    }
}
