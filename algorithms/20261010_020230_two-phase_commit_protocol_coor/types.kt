package tpcc

enum class CoordinatorState {
    INIT,
    PREPARED,
    COMMITTED,
    ABORTED
}

enum class ParticipantState {
    INIT,
    READY,
    COMMITTED,
    ABORTED
}

/**
 * Messages exchanged between coordinator and participants.
 */
sealed class Message {
    object Prepare : Message()
    object Commit : Message()
    object Abort : Message()
    data class Vote(val participantId: Int, val canCommit: Boolean) : Message()
}

/**
 * Participant representation.
 *
 * @property id Unique identifier.
 * @property canCommit Predetermined willingness to commit when asked.
 * @property state Current state of the participant.
 */
data class Participant(
    val id: Int,
    val canCommit: Boolean,
    var state: ParticipantState = ParticipantState.INIT
)

/**
 * Coordinator representation.
 *
 * @property participants List of participants involved in the transaction.
 * @property state Current state of the coordinator.
 * @property votes Collected votes keyed by participant id.
 */
data class Coordinator(
    val participants: List<Participant>,
    var state: CoordinatorState = CoordinatorState.INIT,
    val votes: MutableMap<Int, Boolean> = mutableMapOf()
)