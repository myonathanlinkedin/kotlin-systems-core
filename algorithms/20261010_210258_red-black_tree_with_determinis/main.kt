import kotlin.random.Random

fun main() {
    // Simple deterministic test set
    val tree = RedBlackTree<Int>()
    val values = listOf(10, 20, 30, 15, 5, 25, 1, 6, 14, 16)
    for (v in values) {
        tree.insert(v)
        check(tree.isValid()) { "Tree invalid after inserting $v" }
    }
    check(tree.size == values.size) { "Size mismatch" }
    check(tree.toList() == values.sorted()) { "In‑order traversal incorrect" }

    // Randomized deterministic test (fixed seed)
    val rand = Random(42)
    val randTree = RedBlackTree<Int>()
    val generated = mutableSetOf<Int>()
    repeat(1000) {
        val candidate = rand.nextInt(0, 5000)
        generated.add(candidate)
        randTree.insert(candidate)
        check(randTree.isValid()) { "Random tree invalid after inserting $candidate" }
    }
    check(randTree.size == generated.size) { "Random tree size mismatch" }
    check(randTree.toList() == generated.sorted()) { "Random tree ordering mismatch" }

    // Duplicate handling test
    val dupTree = RedBlackTree<Int>()
    dupTree.insert(42)
    dupTree.insert(42) // duplicate should be ignored
    check(dupTree.size == 1) { "Duplicate insertion affected size" }
    check(dupTree.isValid()) { "Tree invalid after duplicate insertion" }

    // Edge case: single element
    val single = RedBlackTree<Int>()
    single.insert(0)
    check(single.isValid()) { "Single node tree invalid" }
    check(single.toList() == listOf(0)) { "Single node traversal incorrect" }

    println("All tests passed")
}
