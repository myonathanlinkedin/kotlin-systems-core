package lsm

fun main() {
    // Test 1: Basic put/get without flush
    val engine1 = LSMTreeEngine(memTableThreshold = 10)
    engine1.put("a", "1")
    engine1.put("b", "2")
    check(engine1.get("a") == "1")
    check(engine1.get("b") == "2")
    check(engine1.get("c") == null)
    check(engine1.memTableSize() == 2)
    check(engine1.sstableCount() == 0)

    // Test 2: Automatic flush on threshold
    val engine2 = LSMTreeEngine(memTableThreshold = 3)
    engine2.put("k1", "v1")
    engine2.put("k2", "v2")
    engine2.put("k3", "v3") // should trigger flush
    check(engine2.memTableSize() == 0)
    check(engine2.sstableCount() == 1)
    check(engine2.get("k1") == "v1")
    check(engine2.get("k3") == "v3")

    // Test 3: Multiple flushes and reads
    engine2.put("k4", "v4")
    engine2.put("k5", "v5")
    engine2.put("k6", "v6") // second flush
    check(engine2.sstableCount() == 2)
    check(engine2.get("k5") == "v5")
    check(engine2.get("k6") == "v6")
    check(engine2.get("k2") == "v2")

    // Test 4: Overwrite keys across flushes, then compact
    engine2.put("k2", "v2_new") // goes to memtable
    engine2.put("k7", "v7")
    engine2.flush() // create third SSTable
    check(engine2.sstableCount() == 3)
    // Before compaction, latest value for k2 is in newest SSTable
    check(engine2.get("k2") == "v2_new")
    // Compact all SSTables
    engine2.compact()
    check(engine2.sstableCount() == 1)
    // After compaction, only latest versions remain
    check(engine2.get("k2") == "v2_new")
    check(engine2.get("k1") == "v1")
    check(engine2.get("k5") == "v5")
    // Ensure overwritten older version is gone
    val compactedSSTable = engine2.sstables[0]
    val oldK2 = compactedSSTable.entries.find { it.key == "k2" }!!
    check(oldK2.value == "v2_new")
    // Ensure no duplicate keys
    val keys = compactedSSTable.entries.map { it.key }
    check(keys.distinct().size == keys.size)

    // Test 5: Edge case - compact with single SSTable
    val engine3 = LSMTreeEngine(memTableThreshold = 2)
    engine3.put("x", "1")
    engine3.put("y", "2") // flush
    engine3.compact() // should leave single SSTable unchanged
    check(engine3.sstableCount() == 1)
    check(engine3.get("x") == "1")
    check(engine3.get("y") == "2")

    // Test 6: No data scenario
    val engine4 = LSMTreeEngine()
    check(engine4.get("nothing") == null)
    engine4.flush()
    engine4.compact()
    check(engine4.sstableCount() == 0)

    println("All LSM-Tree tests passed.")
}