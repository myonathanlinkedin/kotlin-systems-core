import kotlin.collections.*

// Symbol hierarchy for grammar symbols
sealed class Symbol {
    data class NonTerminal(val name: String) : Symbol()
    data class Terminal(val value: Char) : Symbol()
}

// Production rule: left -> right (right size 1 or 2)
data class Production(
    val left: Symbol.NonTerminal,
    val right: List<Symbol>
)

// Context-Free Grammar in Chomsky Normal Form
data class Grammar(
    val startSymbol: Symbol.NonTerminal,
    val productions: List<Production>
)
