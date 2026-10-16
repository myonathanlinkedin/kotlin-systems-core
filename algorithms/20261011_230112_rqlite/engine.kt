package rqlite

import kotlin.random.Random

/**
 * Starts an election for the given node.
 * The node becomes Candidate, increments its term, votes for itself,
 * and sends RequestVote RPCs to all peers.
 */
fun startElection(node: RaftNode, peers: List<RaftNode>, random: Random) {
    node.state = NodeState.Candidate
    node.currentTerm = node.currentTerm + 1
    node.votedFor = node.id
    node.votesReceived = 1 // vote for self

    val request = RequestVote(
        term = node.currentTerm,
        candidateId = node.id,
        lastLogIndex = node.lastLogIndex(),
        lastLogTerm = node.lastLogTerm()
    )

    for (peer in peers) {
        if (peer.id == node.id) continue
        val response = handleRequestVote(peer, request)
        if (response.term > node.currentTerm) {
            // discovered higher term, step down
            node.currentTerm = response.term
            node.state = NodeState.Follower
            node.votedFor = null
            node.votesReceived = 0
            return
        }
        if (response.voteGranted) {
            node.votesReceived += 1
        }
    }

    // Majority check (simple majority of total nodes)
    val majority = (peers.size / 2) + 1
    if (node.votesReceived >= majority) {
        node.state = NodeState.Leader
        // Upon election, leader initializes nextIndex & matchIndex (omitted for brevity)
    } else {
        // election failed, revert to follower for next round
        node.state = NodeState.Follower
        node.votedFor = null
        node.votesReceived = 0
    }
}

/**
 * Handles a RequestVote RPC on a follower (or candidate).
 */
fun handleRequestVote(node: RaftNode, request: RequestVote): RequestVoteResponse {
    if (request.term < node.currentTerm) {
        return RequestVoteResponse(term = node.currentTerm, voteGranted = false)
    }

    // If term is higher, update term and convert to follower
    if (request.term > node.currentTerm) {
        node.currentTerm = request.term
        node.state = NodeState.Follower
        node.votedFor = null
    }

    val notVotedYet = node.votedFor == null || node.votedFor == request.candidateId
    val candidateUpToDate = (request.lastLogTerm > node.lastLogTerm()) ||
            (request.lastLogTerm == node.lastLogTerm() && request.lastLogIndex >= node.lastLogIndex())

    val grant = notVotedYet && candidateUpToDate
    if (grant) {
        node.votedFor = request.candidateId
    }

    return RequestVoteResponse(term = node.currentTerm, voteGranted = grant)
}

/**
 * Handles an AppendEntries RPC.
 */
fun handleAppendEntries(node: RaftNode, request: AppendEntries): AppendEntriesResponse {
    if (request.term < node.currentTerm) {
        return AppendEntriesResponse(term = node.currentTerm, success = false, matchIndex = node.lastLogIndex())
    }

    // If term is higher, adopt it and become follower
    if (request.term > node.currentTerm) {
        node.currentTerm = request.term
        node.state = NodeState.Follower
        node.votedFor = null
    }

    // Reset election timer (omitted; caller may handle)

    // Consistency check
    if (request.prevLogIndex >= 0) {
        if (request.prevLogIndex >= node.log.size) {
            return AppendEntriesResponse(term = node.currentTerm, success = false, matchIndex = node.lastLogIndex())
        }
        val prevTerm = node.log[request.prevLogIndex].term
        if (prevTerm != request.prevLogTerm) {
            // Conflict: delete entry and all that follow it
            node.log.subList(request.prevLogIndex, node.log.size).clear()
            return AppendEntriesResponse(term = node.currentTerm, success = false, matchIndex = node.lastLogIndex())
        }
    }

    // Append any new entries not already in the log
    var index = request.prevLogIndex + 1
    for (entry in request.entries) {
        if (index < node.log.size) {
            if (node.log[index].term != entry.term) {
                // Delete conflicting entry and all that follow
                node.log.subList(index, node.log.size).clear()
                node.log.add(entry)
            }
        } else {
            node.log.add(entry)
        }
        index += 1
    }

    // Update commit index
    if (request.leaderCommit > node.commitIndex) {
        node.commitIndex = kotlin.math.min(request.leaderCommit, node.lastLogIndex())
    }

    return AppendEntriesResponse(term = node.currentTerm, success = true, matchIndex = node.lastLogIndex())
}

/**
 * Leader appends a new command to its log and replicates it to followers.
 * Returns true if the entry is committed on a majority of nodes.
 */
fun replicateCommand(leader: RaftNode, peers: List<RaftNode>, command: String): Boolean {
    // Append to leader's own log
    val entry = LogEntry(term = leader.currentTerm, command = command)
    leader.log.add(entry)

    // For each follower, send AppendEntries
    for (peer in peers) {
        if (peer.id == leader.id) continue
        val prevIndex = leader.log.size - 2 // index of entry preceding the new one
        val prevTerm = if (prevIndex >= 0) leader.log[prevIndex].term else 0
        val request = AppendEntries(
            term = leader.currentTerm,
            leaderId = leader.id,
            prevLogIndex = prevIndex,
            prevLogTerm = prevTerm,
            entries = listOf(entry),
            leaderCommit = leader.commitIndex
        )
        val response = handleAppendEntries(peer, request)
        if (!response.success && response.term > leader.currentTerm) {
            // Step down if a higher term is discovered
            leader.currentTerm = response.term
            leader.state = NodeState.Follower
            leader.votedFor = null
            return false
        }
    }

    // If we reach here, all followers accepted (simplified majority check)
    leader.commitIndex = leader.lastLogIndex()
    // Propagate commit to followers
    for (peer in peers) {
        if (peer.id == leader.id) continue
        val commitRequest = AppendEntries(
            term = leader.currentTerm,
            leaderId = leader.id,
            prevLogIndex = leader.lastLogIndex(),
            prevLogTerm = leader.lastLogTerm(),
            entries = emptyList(),
            leaderCommit = leader.commitIndex
        )
        handleAppendEntries(peer, commitRequest)
    }
    return true
}

/**
 * Applies committed log entries to the state machine.
 * In this pure implementation we simply advance lastApplied.
 */
fun applyLog(node: RaftNode) {
    while (node.lastApplied < node.commitIndex) {
        node.lastApplied += 1
        // In a real system we would execute node.log[node.lastApplied].command
    }
}