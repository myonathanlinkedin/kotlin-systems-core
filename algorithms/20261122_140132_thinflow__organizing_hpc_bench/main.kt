package thinflow

import kotlin.random.Random

fun main() {
    // ---------- Setup ----------
    val root = ThinflowNode(name = "campaign", command = null)

    // Add benchmark directories and runs
    val addedA = ThinflowEngine.addNode(root, emptyList(), ThinflowNode("benchmarkA", null))
    assert(addedA) { "Failed to add benchmarkA" }

    val addedB = ThinflowEngine.addNode(root, emptyList(), ThinflowNode("benchmarkB", null))
    assert(addedB) { "Failed to add benchmarkB" }

    // Add runs under benchmarkA
    val runA1 = ThinflowNode("run1", "execA --size=small")
    val addedRunA1 = ThinflowEngine.addNode(root, listOf("benchmarkA"), runA1)
    assert(addedRunA1) { "Failed to add run1 under benchmarkA" }

    val runA2 = ThinflowNode("run2", "execA --size=large")
    val addedRunA2 = ThinflowEngine.addNode(root, listOf("benchmarkA"), runA2)
    assert(addedRunA2) { "Failed to add run2 under benchmarkA" }

    // Add a nested directory under benchmarkB
    val subDir = ThinflowNode("subdir", null)
    val addedSubDir = ThinflowEngine.addNode(root, listOf("benchmarkB"), subDir)
    assert(addedSubDir) { "Failed to add subdir under benchmarkB" }

    val runB1 = ThinflowNode("run1", "execB --mode=fast")
    val addedRunB1 = ThinflowEngine.addNode(root, listOf("benchmarkB", "subdir"), runB1)
    assert(addedRunB1) { "Failed to add run1 under benchmarkB/subdir" }

    // ---------- FindNode Tests ----------
    val findA = ThinflowEngine.findNode(root, listOf("benchmarkA"))
    assert(findA != null && findA?.name == "benchmarkA") { "findNode failed for benchmarkA" }

    val findRunA2 = ThinflowEngine.findNode(root, listOf("benchmarkA", "run2"))
    assert(findRunA2 != null && findRunA2?.command == "execA --size=large") { "findNode failed for benchmarkA/run2" }

    val findMissing = ThinflowEngine.findNode(root, listOf("benchmarkA", "nonexistent"))
    assert(findMissing == null) { "findNode should return null for nonexistent path" }

    // ---------- RerunNode Tests ----------
    // Rerun benchmarkA with suffix "_v2"
    val clonedBenchmarkA = ThinflowEngine.rerunNode(root, listOf("benchmarkA"), "_v2")
    assert(clonedBenchmarkA != null) { "Rerun of benchmarkA returned null" }
    assert(clonedBenchmarkA?.name == "benchmarkA_v2") { "Cloned name mismatch" }
    // Verify cloned children have suffix
    val clonedRunNames = clonedBenchmarkA?.children?.map { it.name }?.toSet()
    assert(clonedRunNames == setOf("run1_v2", "run2_v2")) { "Cloned children names incorrect" }

    // Attempt to rerun a non‑existent node
    val rerunMissing = ThinflowEngine.rerunNode(root, listOf("nonexistent"), "_copy")
    assert(rerunMissing == null) { "Rerun of nonexistent node should be null" }

    // Attempt to rerun with a suffix that would clash with existing sibling
    val clashAttempt = ThinflowEngine.rerunNode(root, listOf("benchmarkB"), "_v2") // No clash expected
    assert(clashAttempt != null) { "Rerun of benchmarkB should succeed" }

    // Force a clash by rerunning benchmarkA again with same suffix
    val clash = ThinflowEngine.rerunNode(root, listOf("benchmarkA"), "_v2")
    assert(clash == null) { "Clash detection failed; should not allow duplicate sibling names" }

    // ---------- Topological Order Tests ----------
    val order = ThinflowEngine.topologicalOrder(root).map { it.name }
    // Expected order: root, benchmarkA, run1, run2, benchmarkA_v2, run1_v2, run2_v2, benchmarkB, subdir, run1, benchmarkB_v2, subdir_v2, run1_v2
    // Build expected list dynamically for robustness
    val expected = mutableListOf<String>()
    expected.add("campaign")
    expected.addAll(listOf("benchmarkA", "run1", "run2", "benchmarkA_v2", "run1_v2", "run2_v2",
        "benchmarkB", "subdir", "run1", "benchmarkB_v2", "subdir_v2", "run1_v2"))
    assert(order == expected) { "Topological order mismatch.\nExpected: $expected\nActual:   $order" }

    // ---------- Edge Cases ----------
    // Adding a node with duplicate name under same parent should fail
    val duplicateAdd = ThinflowEngine.addNode(root, listOf("benchmarkA"), ThinflowNode("run1", "duplicate"))
    assert(!duplicateAdd) { "Duplicate child addition should be rejected" }

    // Adding under a non‑existent parent should fail
    val addUnderMissing = ThinflowEngine.addNode(root, listOf("benchmarkA", "missing"), ThinflowNode("leaf", null))
    assert(!addUnderMissing) { "Adding under missing parent should be rejected" }

    // Verify that the tree remains a valid DAG (no cycles) by checking that each node appears exactly once in pre‑order
    val preOrderList = ThinflowEngine.preOrder(root)
    val nameSet = mutableSetOf<String>()
    for (node in preOrderList) {
        assert(nameSet.add(node.name)) { "Cycle detected or duplicate node name in pre‑order traversal: ${node.name}" }
    }

    // ---------- Demo Output ----------
    println("Thinflow tree constructed successfully. Node count: ${preOrderList.size}")
    println("Pre‑order traversal:")
    for (node in preOrderList) {
        println("- ${node.name}${if (node.command != null) " [${node.command}]" else ""}")
    }
}