package lsm

fun main() {
    // Instantiate engine with a low threshold to test flushing
    val engine = LSMEngine(memTableThreshold = 3)

    // ---- Test 1: Basic put/get ----
    engine.put(1, "one")
    engine.put(2, "two")
    check(engine.get(1) == "one")
    check(engine.get(2) == "two")
    check(engine.get(3) == null)

    // ---- Test 2: Flush trigger ----
    // At this point memTable has 2 entries; next put should cause flush
    engine.put(3, "three") // should flush memTable (size becomes 3)
    // After flush, memTable should be empty, SSTable count = 1
    check(engine.memTable.isEmpty())
    check(engine.sstables.size == 1)
    // All values must still be readable
    check(engine.get(1) == "one")
    check(engine.get(2) == "two")
    check(engine.get(3) == "three")

    // ---- Test 3: Delete semantics ----
    engine.delete(2) // writes tombstone in memTable
    check(engine.get(2) == null) // should see deletion immediately
    // Flush to persist tombstone
    engine.flush()
    check(engine.sstables.size == 2) // another SSTable added
    check(engine.get(2) == null) // still deleted

    // ---- Test 4: Overwrite after delete ----
    engine.put(2, "two-again")
    check(engine.get(2) == "two-again")
    // Flush again
    engine.flush()
    check(engine.sstables.size == 3)

    // ---- Test 5: Compaction ----
    // Before compaction we have three SSTables, some with stale entries
    engine.compact()
    // After compaction there must be exactly one SSTable
    check(engine.sstables.size == 1)
    // All live keys must be present with latest values
    check(engine.get(1) == "one")
    check(engine.get(2) == "two-again")
    check(engine.get(3) == "three")
    // Deleted key (if any) should not appear; we re‑added key 2, so it's live

    // ---- Test 6: Edge cases ----
    // Delete a key that does not exist
    engine.delete(99)
    check(engine.get(99) == null)
    engine.flush()
    // Compaction should not create spurious entries
    engine.compact()
    check(engine.get(99) == null)

    // ---- Test 7: Large number of keys ----
    for (i in 100..199) {
        engine.put(i, "v$i")
    }
    // Force flush and compaction
    engine.flush()
    engine.compact()
    for (i in 100..199) {
        check(engine.get(i) == "v$i")
    }

    // All assertions passed
    println("All LSM‑Tree tests passed.")
}