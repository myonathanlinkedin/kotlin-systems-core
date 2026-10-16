package rqlite

import kotlin.random.Random

fun main() {
    // Deterministic random source
    val random = Random(42)

    // Create a 3‑node cluster
    val nodes = listOf(
        RaftNode(id = 0),
        RaftNode(id = 1),
        RaftNode(id = 2)
    )

    // Helper to find node by id
    fun nodeById(id: Int): RaftNode = nodes.first { it.id == id }

    // ---------- Test 1: Leader election ----------
    // Force node 0 to start an election
    startElection(nodeById(0), nodes, random)

    // Exactly one leader should exist
    val leaders = nodes.filter { it.state == NodeState.Leader }
    check(leaders.size == 1) { "Expected exactly one leader, found ${leaders.size}" }
    val leader = leaders[0]

    // All other nodes must be followers and have term equal to leader's term
    for (node in nodes) {
        if (node.id != leader.id) {
            check(node.state == NodeState.Follower) { "Node ${node.id} should be follower" }
            check(node.currentTerm == leader.currentTerm) { "Node ${node.id} term mismatch" }
        }
    }

    // ---------- Test 2: Log replication ----------
    var command = "SET x=1"
    val replicated = replicateCommand(leader, nodes, command)
    check(replicated) { "Leader failed to replicate command" }

    // After replication, all nodes should have identical logs
    val referenceLog = leader.log
    for (node in nodes) {
        check(node.log == referenceLog) { "Node ${node.id} log diverged from leader" }
        check(node.commitIndex == leader.commitIndex) { "Node ${node.id} commit index mismatch" }
    }

    // Apply logs on each node and ensure lastApplied reaches commitIndex
    for (node in nodes) {
        applyLog(node)
        check(node.lastApplied == node.commitIndex) { "Node ${node.id} did not apply all committed entries" }
    }

    // ---------- Test 3: Term safety ----------
    // Simulate a follower receiving AppendEntries with lower term
    val staleAppend = AppendEntries(
        term = leader.currentTerm - 1,
        leaderId = leader.id,
        prevLogIndex = leader.lastLogIndex(),
        prevLogTerm = leader.lastLogTerm(),
        entries = listOf(LogEntry(term = leader.currentTerm - 1, command = "STALE")),
        leaderCommit = leader.commitIndex
    )
    val follower = nodeById((leader.id + 1) % nodes.size)
    val staleResponse = handleAppendEntries(follower, staleAppend)
    check(!staleResponse.success) { "Follower incorrectly accepted stale AppendEntries" }
    check(follower.currentTerm == leader.currentTerm) { "Follower term changed incorrectly" }

    // ---------- Test 4: Vote denial after granting ----------
    // Node 1 has already voted for leader 0 in current term.
    // Create a new candidate with same term but different id.
    val candidate = nodeById(2)
    candidate.state = NodeState.Candidate
    candidate.currentTerm = leader.currentTerm
    candidate.votedFor = null
    val voteRequest = RequestVote(
        term = candidate.currentTerm,
        candidateId = candidate.id,
        lastLogIndex = candidate.lastLogIndex(),
        lastLogTerm = candidate.lastLogTerm()
    )
    val voteResponse = handleRequestVote(nodeById(1), voteRequest)
    check(!voteResponse.voteGranted) { "Node 1 incorrectly granted second vote in same term" }

    // ---------- Test 5: Higher term discovery ----------
    // Simulate node 2 receiving a RequestVote with higher term
    val higherTermRequest = RequestVote(
        term = leader.currentTerm + 5,
        candidateId = 2,
        lastLogIndex = 0,
        lastLogTerm = 0
    )
    val higherTermResponse = handleRequestVote(nodeById(0), higherTermRequest)
    check(higherTermResponse.term == leader.currentTerm + 5) { "Higher term not reflected in response" }
    check(nodeById(0).currentTerm == leader.currentTerm + 5) { "Node 0 did not update term" }
    check(nodeById(0).state == NodeState.Follower) { "Node 0 should revert to follower on higher term" }

    // All assertions passed
    println("All RQLite‑style Raft tests passed.")
}