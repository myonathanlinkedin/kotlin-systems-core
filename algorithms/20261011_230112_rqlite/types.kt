package rqlite

import kotlin.math.max

enum class NodeState {
    Follower,
    Candidate,
    Leader
}

data class LogEntry(val term: Int, val command: String)

data class RequestVote(
    val term: Int,
    val candidateId: Int,
    val lastLogIndex: Int,
    val lastLogTerm: Int
)

data class RequestVoteResponse(
    val term: Int,
    val voteGranted: Boolean
)

data class AppendEntries(
    val term: Int,
    val leaderId: Int,
    val prevLogIndex: Int,
    val prevLogTerm: Int,
    val entries: List<LogEntry>,
    val leaderCommit: Int
)

data class AppendEntriesResponse(
    val term: Int,
    val success: Boolean,
    val matchIndex: Int
)

data class RaftNode(
    val id: Int,
    var currentTerm: Int = 0,
    var votedFor: Int? = null,
    val log: MutableList<LogEntry> = mutableListOf(),
    var commitIndex: Int = -1,
    var lastApplied: Int = -1,
    var state: NodeState = NodeState.Follower,
    var votesReceived: Int = 0
) {
    // Helper to get last log index (0‑based) or -1 if empty
    fun lastLogIndex(): Int = log.size - 1

    // Helper to get last log term or 0 if empty
    fun lastLogTerm(): Int = if (log.isEmpty()) 0 else log.last().term
}