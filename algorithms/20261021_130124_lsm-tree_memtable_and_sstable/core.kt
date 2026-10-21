package lsm

import java.util.TreeMap
import kotlin.collections.binarySearch

data class Entry(val key: String, val value: String, val seq: Long)

data class SSTable(val entries: List<Entry>)

class MemTable {
    val map: TreeMap<String, Entry> = TreeMap()
}

class LSMTreeEngine(
    val memTableThreshold: Int = 4
) {
    val memTable = MemTable()
    val sstables: MutableList<SSTable> = mutableListOf()
    var nextSeq: Long = 0L

    fun put(key: String, value: String) {
        val entry = Entry(key, value, nextSeq++)
        memTable.map[key] = entry
        if (memTable.map.size >= memTableThreshold) {
            flush()
        }
    }

    operator fun get(key: String): String? {
        memTable.map[key]?.let { return it.value }
        for (i in sstables.size - 1 downTo 0) {
            val sstable = sstables[i]
            val idx = sstable.entries.binarySearchBy(key) { it.key }
            if (idx >= 0) {
                return sstable.entries[idx].value
            }
        }
        return null
    }

    fun flush() {
        if (memTable.map.isEmpty()) return
        val entries = memTable.map.values.toList()
        val newSSTable = SSTable(entries)
        sstables.add(newSSTable)
        memTable.map.clear()
    }

    fun compact() {
        if (sstables.isEmpty()) return
        val merged = TreeMap<String, Entry>()
        for (sstable in sstables) {
            for (entry in sstable.entries) {
                val existing = merged[entry.key]
                if (existing == null || entry.seq > existing.seq) {
                    merged[entry.key] = entry
                }
            }
        }
        val compacted = SSTable(merged.values.toList())
        sstables.clear()
        sstables.add(compacted)
    }

    // For testing purposes: expose internal state sizes
    fun memTableSize(): Int = memTable.map.size
    fun sstableCount(): Int = sstables.size
}