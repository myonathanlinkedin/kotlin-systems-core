package lsm

data class Record(
    val value: String?, // null represents a tombstone (deletion)
    val seq: Long       // monotonically increasing sequence number
)

data class SSTable(
    val entries: List<Pair<Int, Record>> // sorted by key ascending
)