fun testAllCommit() {
    val participants = listOf(
        Participant(id = 1),
        Participant(id = 2),
        Participant(id = 3)
    )
    val coordinator = Coordinator(participants)
    val result = coordinator.execute()
    check(result) { "Transaction should commit when all participants vote commit." }
    check(coordinator.state == CoordinatorState.COMMITTED) { "Coordinator state must be COMMITTED." }
    for (p in participants) {
        check(p.state == ParticipantState.COMMITTED) { "Participant ${p.id} must be COMMITTED." }
    }
}

fun testOneAbort() {
    val participants = listOf(
        Participant(id = 1),
        Participant(id = 2, willCommit = false), // This participant will vote abort
        Participant(id = 3)
    )
    val coordinator = Coordinator(participants)
    val result = coordinator.execute()
    check(!result) { "Transaction should abort when any participant votes abort." }
    check(coordinator.state == CoordinatorState.ABORTED) { "Coordinator state must be ABORTED." }
    // All participants should end in ABORTED state
    for (p in participants) {
        check(p.state == ParticipantState.ABORTED) { "Participant ${p.id} must be ABORTED." }
    }
}

fun testNoResponse() {
    val participants = listOf(
        Participant(id = 1),
        Participant(id = 2, willRespond = false), // Silent participant
        Participant(id = 3)
    )
    val coordinator = Coordinator(participants)
    val result = coordinator.execute()
    check(!result) { "Transaction should abort when a participant does not respond." }
    check(coordinator.state == CoordinatorState.ABORTED) { "Coordinator state must be ABORTED." }
    // Participants that responded should be ABORTED, silent one stays in INIT
    check(participants[0].state == ParticipantState.ABORTED) { "Participant 1 must be ABORTED." }
    check(participants[2].state == ParticipantState.ABORTED) { "Participant 3 must be ABORTED." }
    check(participants[1].state == ParticipantState.INIT) { "Silent participant should remain in INIT." }
}

fun runAllTests() {
    testAllCommit()
    testOneAbort()
    testNoResponse()
    println("All tests passed.")
}

fun main() {
    runAllTests()
}
