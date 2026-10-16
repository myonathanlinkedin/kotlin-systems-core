package suffixautomaton

import kotlin.random.Random

fun runTests() {
    // Basic deterministic tests
    val sam1 = SuffixAutomaton("ababa")
    check(sam1.contains("aba"))
    check(sam1.contains("bab"))
    check(sam1.contains("ababa"))
    check(sam1.contains("ba"))
    check(!sam1.contains("c"))
    check(sam1.contains("")) // empty substring always present

    val samEmpty = SuffixAutomaton("")
    check(samEmpty.contains(""))
    check(!samEmpty.contains("a"))

    val samSingle = SuffixAutomaton("x")
    check(samSingle.contains("x"))
    check(!samSingle.contains("xx"))
    check(samSingle.contains(""))

    // Randomized tests against naive contains
    repeat(20) {
        val length = Random.nextInt(1, 200)
        val text = buildString {
            repeat(length) { append(('a'..'z').random()) }
        }
        val sam = SuffixAutomaton(text)
        repeat(100) {
            val start = Random.nextInt(0, text.length)
            val end = Random.nextInt(start, text.length)
            val sub = text.substring(start, end)
            check(sam.contains(sub))
        }
        repeat(100) {
            val subLen = Random.nextInt(0, 20)
            val sub = buildString {
                repeat(subLen) { append(('a'..'z').random()) }
            }
            check(sam.contains(sub) == text.contains(sub))
        }
    }
}

fun main() {
    runTests()
    println("All tests passed")
}