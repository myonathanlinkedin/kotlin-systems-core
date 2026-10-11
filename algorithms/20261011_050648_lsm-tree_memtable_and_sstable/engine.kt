package lsm

import kotlin.math.max

class LSMEngine(
    private val memTableThreshold: Int = 4 // flush when memtable reaches this size
) {
    // sequence counter for ordering operations
    var nextSeq: Long = 1L
        private set

    // mutable in‑memory table
    val memTable: MutableMap<Int, Record> = mutableMapOf()

    // immutable list of SSTables; newest first
    val sstables: MutableList<SSTable> = mutableListOf()

    /** Insert or update a key with a non‑null value. */
    fun put(key: Int, value: String) {
        require(value.isNotEmpty()) { "value must be non‑empty" }
        val rec = Record(value, nextSeq++)
        memTable[key] = rec
        maybeFlush()
    }

    /** Delete a key by writing a tombstone. */
    fun delete(key: Int) {
        val rec = Record(null, nextSeq++)
        memTable[key] = rec
        maybeFlush()
    }

    /** Retrieve the latest visible value for a key, or null if absent or deleted. */
    operator fun get(key: Int): String? {
        // Check memtable first (most recent)
        memTable[key]?.let { rec ->
            return rec.value // may be null (tombstone)
        }
        // Scan SSTables from newest to oldest
        for (sst in sstables) {
            // binary search because entries are sorted
            var lo = 0
            var hi = sst.entries.size - 1
            while (lo <= hi) {
                val mid = (lo + hi) ushr 1
                val midKey = sst.entries[mid].first
                when {
                    key < midKey -> hi = mid - 1
                    key > midKey -> lo = mid + 1
                    else -> {
                        return sst.entries[mid].second.value // may be null
                    }
                }
            }
        }
        return null // not found
    }

    /** Force a memtable flush regardless of size. */
    fun flush() {
        if (memTable.isEmpty()) return
        val sorted = memTable.entries
            .sortedBy { it.key }
            .map { Pair(it.key, it.value) }
        sstables.add(0, SSTable(sorted)) // prepend as newest
        memTable.clear()
    }

    /** Merge all existing SSTables into a single compacted SSTable. */
    fun compact() {
        if (sstables.isEmpty()) return
        // Map to keep the newest record per key
        val latest: MutableMap<Int, Record> = mutableMapOf()
        // Iterate from newest to oldest to respect seq ordering
        for (sst in sstables) {
            for ((k, rec) in sst.entries) {
                // If we already have a newer record, skip
                val existing = latest[k]
                if (existing == null || rec.seq > existing.seq) {
                    latest[k] = rec
                }
            }
        }
        // Build sorted list, discarding tombstones
        val compactedEntries = latest.entries
            .filter { it.value.value != null } // keep only live records
            .sortedBy { it.key }
            .map { Pair(it.key, it.value) }
        sstables.clear()
        sstables.add(SSTable(compactedEntries))
    }

    /** Internal helper: flush when threshold reached. */
    private fun maybeFlush() {
        if (memTable.size >= memTableThreshold) {
            flush()
        }
    }
}