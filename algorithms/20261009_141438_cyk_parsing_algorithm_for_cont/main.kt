package cyk

fun main() {
    // Grammar G1: generates exactly "ab"
    // S → A B
    // A → a
    // B → b
    val g1 = Grammar(
        startSymbol = "S",
        productions = listOf(
            Production("S", listOf("A", "B")),
            Production("A", listOf("a")),
            Production("B", listOf("b"))
        )
    )

    // Grammar G2: generates { a^n b^n | n ≥ 1 }
    // CNF version:
    // S → A B | S S
    // A → a
    // B → b
    val g2 = Grammar(
        startSymbol = "S",
        productions = listOf(
            Production("S", listOf("A", "B")),
            Production("S", listOf("S", "S")),
            Production("A", listOf("a")),
            Production("B", listOf("b"))
        )
    )

    // Helper for assertions
    fun assertResult(expected: Boolean, actual: Boolean, description: String) {
        check(actual == expected) { "FAIL: $description – expected $expected, got $actual" }
    }

    // Tests for G1
    assertResult(true, cykParse(g1, "ab"), "G1 parses \"ab\"")
    assertResult(false, cykParse(g1, "a"), "G1 rejects \"a\"")
    assertResult(false, cykParse(g1, "b"), "G1 rejects \"b\"")
    assertResult(false, cykParse(g1, "ba"), "G1 rejects \"ba\"")
    assertResult(false, cykParse(g1, ""), "G1 rejects empty string")

    // Tests for G2
    assertResult(true, cykParse(g2, "ab"), "G2 parses \"ab\"")
    assertResult(true, cykParse(g2, "aabb"), "G2 parses \"aabb\"")
    assertResult(true, cykParse(g2, "aaabbb"), "G2 parses \"aaabbb\"")
    assertResult(true, cykParse(g2, "aaaabbbb"), "G2 parses \"aaaabbbb\"")
    assertResult(false, cykParse(g2, "aab"), "G2 rejects \"aab\"")
    assertResult(false, cykParse(g2, "abb"), "G2 rejects \"abb\"")
    assertResult(false, cykParse(g2, "aba"), "G2 rejects \"aba\"")
    assertResult(false, cykParse(g2, ""), "G2 rejects empty string")

    // Demonstration output
    val demoString = "aaabbb"
    val result = cykParse(g2, demoString)
    println("CYK parsing of \"$demoString\" with G2 yields: $result")
}