import kotlin.collections.*

// Helper to build a simple CNF grammar for a^n b^n
fun buildAnBnGrammar(): Grammar {
    // NonTerminals
    val S = Symbol.NonTerminal("S")
    val A = Symbol.NonTerminal("A")
    val B = Symbol.NonTerminal("B")
    val Ta = Symbol.NonTerminal("Ta") // generates 'a'
    val Tb = Symbol.NonTerminal("Tb") // generates 'b'

    // Productions
    val prods = listOf(
        // S -> A B
        Production(S, listOf(A, B)),
        // A -> Ta | Ta A
        Production(A, listOf(Ta)),
        Production(A, listOf(Ta, A)),
        // B -> Tb | Tb B
        Production(B, listOf(Tb)),
        Production(B, listOf(Tb, B)),
        // Ta -> 'a'
        Production(Ta, listOf(Symbol.Terminal('a'))),
        // Tb -> 'b'
        Production(Tb, listOf(Symbol.Terminal('b')))
    )
    return Grammar(startSymbol = S, productions = prods)
}

// Helper to build a grammar that generates exactly the single character 'a'
fun buildSingleATerminalGrammar(): Grammar {
    val S = Symbol.NonTerminal("S")
    val prods = listOf(
        Production(S, listOf(Symbol.Terminal('a')))
    )
    return Grammar(startSymbol = S, productions = prods)
}

// Unit tests using Kotlin's check function
fun runTests() {
    // Test 1: a^n b^n grammar
    val grammarAnBn = buildAnBnGrammar()
    check(CYKEngine.parse(grammarAnBn, "ab")) { "Failed: 'ab' should be accepted" }
    check(CYKEngine.parse(grammarAnBn, "aabb")) { "Failed: 'aabb' should be accepted" }
    check(CYKEngine.parse(grammarAnBn, "aaabbb")) { "Failed: 'aaabbb' should be accepted" }
    check(!CYKEngine.parse(grammarAnBn, "aab")) { "Failed: 'aab' should be rejected" }
    check(!CYKEngine.parse(grammarAnBn, "")) { "Failed: empty string should be rejected" }
    check(!CYKEngine.parse(grammarAnBn, "aba")) { "Failed: 'aba' should be rejected" }

    // Test 2: single terminal grammar
    val grammarSingleA = buildSingleATerminalGrammar()
    check(CYKEngine.parse(grammarSingleA, "a")) { "Failed: 'a' should be accepted by single terminal grammar" }
    check(!CYKEngine.parse(grammarSingleA, "b")) { "Failed: 'b' should be rejected by single terminal grammar" }
    check(!CYKEngine.parse(grammarSingleA, "")) { "Failed: empty string should be rejected by single terminal grammar" }

    // Additional edge case: longer non-derivable string
    check(!CYKEngine.parse(grammarAnBn, "aaaaabbbbbc")) { "Failed: string with extra character should be rejected" }

    // All tests passed
    println("All CYK parsing tests passed.")
}

// Demo driver
fun main() {
    runTests()
    // Demonstration with user-provided input (hardcoded for safety)
    val demoGrammar = buildAnBnGrammar()
    val demoString = "aaabbb"
    val result = CYKEngine.parse(demoGrammar, demoString)
    println("String \"$demoString\" is ${if (result) "in" else "not in"} the language of the grammar.")
}
