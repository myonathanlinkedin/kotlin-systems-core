import kotlin.random.Random

fun main() {
    // Basic functionality tests
    val tree = RedBlackTree<Int, String>()
    check(tree.get(1) == null)

    val keys = listOf(10, 20, 30, 15, 25, 5, 1, 8)
    for (k in keys) {
        tree.insert(k, "v$k")
        tree.validate()
        check(tree.get(k) == "v$k")
    }
    check(tree.size == keys.size)

    val inorderKeys = tree.inorder().map { it.first }
    check(inorderKeys == keys.sorted())

    // Update existing key
    tree.insert(15, "new15")
    check(tree.get(15) == "new15")
    tree.validate()

    // Randomized stress test
    val randTree = RedBlackTree<Int, Int>()
    val rand = Random(0)
    val inserted = mutableSetOf<Int>()
    repeat(1000) {
        val v = rand.nextInt(0, 2000)
        inserted.add(v)
        randTree.insert(v, v)
        randTree.validate()
    }
    check(randTree.size == inserted.size)
    for (v in inserted) {
        check(randTree.get(v) == v)
    }

    println("All tests passed.")
}
