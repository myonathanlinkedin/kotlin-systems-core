import kotlin.collections.*

// Simple unit‑test driver using standard `check` assertions.
fun main() {
    // Grammar for language { a^n b^n | n >= 1 } in CNF:
    // S -> A B
    // S -> S S
    // A -> a
    // B -> b
    var productions = listOf(
        Production("S", listOf("A", "B")),
        Production("S", listOf("S", "S")),
        Production("A", listOf("a")),
        Production("B", listOf("b"))
    )
    val grammar = Grammar(startSymbol = "S", productions = productions)
    val parser = CYKParser(grammar)

    // Positive test cases.
    check(parser.parse("ab"))          // n = 1
    check(parser.parse("aabb"))        // n = 2
    check(parser.parse("aaabbb"))      // n = 3
    check(parser.parse("aaaabbbb"))    // n = 4

    // Negative test cases.
    check(!parser.parse("aab"))        // mismatched counts
    check(!parser.parse("abb"))        // mismatched counts
    check(!parser.parse("ba"))         // wrong order
    check(!parser.parse(""))           // empty string not in language

    // Additional demonstration with a different grammar:
    // Grammar for strings consisting of a single 'c' or concatenations thereof:
    // T -> C C
    // C -> c
    val prod2 = listOf(
        Production("T", listOf("C", "C")),
        Production("C", listOf("c"))
    )
    val grammar2 = Grammar(startSymbol = "T", productions = prod2)
    val parser2 = CYKParser(grammar2)

    check(parser2.parse("cc"))         // T -> C C -> c c
    check(!parser2.parse("c"))         // single 'c' not derivable (needs two C's)
    check(!parser2.parse("ccc"))       // odd length not derivable

    // Print confirmation if all checks pass.
    println("All CYK parser unit tests passed.")
}
