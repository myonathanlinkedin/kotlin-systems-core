package tpcc

fun main() {
    // Test 1: All participants can commit -> transaction should be committed.
    val participantsAllYes = listOf(
        Participant(id = 1, canCommit = true),
        Participant(id = 2, canCommit = true),
        Participant(id = 3, canCommit = true)
    )
    val coordinatorAllYes = Coordinator(participants = participantsAllYes)
    val finalStateAllYes = runTwoPhaseCommit(coordinatorAllYes)

    check(finalStateAllYes == CoordinatorState.COMMITTED) { "Coordinator should be COMMITTED when all votes are YES." }
    for (p in participantsAllYes) {
        check(p.state == ParticipantState.COMMITTED) { "Participant ${p.id} should be COMMITTED." }
    }

    // Test 2: One participant votes NO -> transaction should be aborted.
    val participantsOneNo = listOf(
        Participant(id = 1, canCommit = true),
        Participant(id = 2, canCommit = false), // votes NO
        Participant(id = 3, canCommit = true)
    )
    val coordinatorOneNo = Coordinator(participants = participantsOneNo)
    val finalStateOneNo = runTwoPhaseCommit(coordinatorOneNo)

    check(finalStateOneNo == CoordinatorState.ABORTED) { "Coordinator should be ABORTED when any vote is NO." }
    for (p in participantsOneNo) {
        check(p.state == ParticipantState.ABORTED) { "Participant ${p.id} should be ABORTED." }
    }

    // Test 3: No participants -> coordinator should commit trivially.
    val emptyParticipants = emptyList<Participant>()
    val coordinatorEmpty = Coordinator(participants = emptyParticipants)
    val finalStateEmpty = runTwoPhaseCommit(coordinatorEmpty)

    check(finalStateEmpty == CoordinatorState.COMMITTED) { "Coordinator should be COMMITTED with zero participants." }

    // Test 4: Verify intermediate states during the protocol.
    val participantsIntermediate = listOf(
        Participant(id = 1, canCommit = true),
        Participant(id = 2, canCommit = true)
    )
    val coordinatorIntermediate = Coordinator(participants = participantsIntermediate)

    // Phase 1 only
    coordinatorPrepare(coordinatorIntermediate)
    check(coordinatorIntermediate.state == CoordinatorState.PREPARED) { "Coordinator should be in PREPARED after prepare phase." }
    for (p in participantsIntermediate) {
        check(p.state == ParticipantState.READY) { "Participant ${p.id} should be READY after receiving PREPARE." }
    }

    // Phase 2 decision
    val decisionMsg = coordinatorDecide(coordinatorIntermediate)
    check(decisionMsg is Message.Commit) { "Decision should be COMMIT when all votes are YES." }
    check(coordinatorIntermediate.state == CoordinatorState.COMMITTED) { "Coordinator should be COMMITTED after decision." }

    // Notify participants
    for (p in participantsIntermediate) {
        participantReceiveDecision(p, decisionMsg)
        check(p.state == ParticipantState.COMMITTED) { "Participant ${p.id} should be COMMITTED after decision." }
    }

    // All tests passed
    println("All Two‑Phase Commit tests passed successfully.")
}