import kotlin.collections.*

// Representation of a production rule in Chomsky Normal Form.
// For terminal productions, right side contains a single terminal string.
// For non‑terminal productions, right side contains exactly two non‑terminal strings.
data class Production(
    val left: String,
    val right: List<String>
)

// Grammar consisting of a set of productions.
// The start symbol is stored separately for convenience.
data class Grammar(
    val startSymbol: String,
    val productions: List<Production>
)
