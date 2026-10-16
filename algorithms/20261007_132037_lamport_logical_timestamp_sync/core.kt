import kotlin.math.max

data class Message(val senderId: Int, val timestamp: Long)

data class Process(val id: Int, var timestamp: Long = 0L)

class LamportEngine(processCount: Int) {
    val processes: List<Process> = List(processCount) { Process(it) }

    fun internalEvent(pid: Int) {
        val proc = processes[pid]
        proc.timestamp = proc.timestamp + 1L
    }

    fun sendEvent(senderPid: Int, receiverPid: Int): Message {
        val sender = processes[senderPid]
        sender.timestamp = sender.timestamp + 1L
        return Message(senderId = sender.id, timestamp = sender.timestamp)
    }

    fun receiveEvent(receiverPid: Int, msg: Message) {
        val receiver = processes[receiverPid]
        receiver.timestamp = max(receiver.timestamp, msg.timestamp) + 1L
    }

    fun compare(p1: Process, p2: Process): Int {
        return when {
            p1.timestamp < p2.timestamp -> -1
            p1.timestamp > p2.timestamp -> 1
            else -> p1.id.compareTo(p2.id)
        }
    }
}
