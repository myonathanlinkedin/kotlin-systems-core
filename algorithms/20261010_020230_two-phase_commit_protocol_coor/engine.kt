package tpcc

/**
 * Coordinator sends a Prepare message to each participant and records their votes.
 */
fun coordinatorPrepare(coord: Coordinator) {
    coord.state = CoordinatorState.PREPARED
    for (participant in coord.participants) {
        val vote = participantReceivePrepare(participant)
        coord.votes[participant.id] = vote.canCommit
    }
}

/**
 * Participant processes a Prepare request and replies with a Vote.
 */
fun participantReceivePrepare(participant: Participant): Message.Vote {
    participant.state = ParticipantState.READY
    return Message.Vote(participant.id, participant.canCommit)
}

/**
 * Coordinator decides based on collected votes and returns the final decision message.
 */
fun coordinatorDecide(coord: Coordinator): Message {
    val allYes = coord.votes.values.all { it }
    return if (allYes) {
        coord.state = CoordinatorState.COMMITTED
        Message.Commit
    } else {
        coord.state = CoordinatorState.ABORTED
        Message.Abort
    }
}

/**
 * Participant processes the final decision from the coordinator.
 */
fun participantReceiveDecision(participant: Participant, decision: Message) {
    when (decision) {
        is Message.Commit -> participant.state = ParticipantState.COMMITTED
        is Message.Abort -> participant.state = ParticipantState.ABORTED
        else -> {
            // No other messages are expected at this stage.
        }
    }
}

/**
 * Executes the full two‑phase commit protocol.
 *
 * @return Final state of the coordinator after the protocol completes.
 */
fun runTwoPhaseCommit(coord: Coordinator): CoordinatorState {
    // Phase 1: Prepare
    coordinatorPrepare(coord)

    // Phase 2: Decision
    val decision = coordinatorDecide(coord)

    // Notify participants
    for (participant in coord.participants) {
        participantReceiveDecision(participant, decision)
    }

    return coord.state
}