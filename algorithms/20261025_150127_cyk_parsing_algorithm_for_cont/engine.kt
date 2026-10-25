import kotlin.collections.*

// CYK parsing engine for grammars in Chomsky Normal Form.
class CYKParser(
    private val grammar: Grammar
) {
    // Pre‑computed lookup tables for fast parsing.
    private val terminalMap: Map<String, Set<String>>
    private val pairMap: Map<Pair<String, String>, Set<String>>

    init {
        // Build map: terminal -> set of non‑terminals that produce it.
        val termMap = mutableMapOf<String, MutableSet<String>>()
        // Build map: (B, C) -> set of non‑terminals A such that A -> B C.
        val pairMapMutable = mutableMapOf<Pair<String, String>, MutableSet<String>>()

        for (prod in grammar.productions) {
            when (prod.right.size) {
                1 -> {
                    // Terminal production.
                    val terminal = prod.right[0]
                    termMap.computeIfAbsent(terminal) { mutableSetOf() }.add(prod.left)
                }
                2 -> {
                    // Non‑terminal production.
                    val b = prod.right[0]
                    val c = prod.right[1]
                    val key = Pair(b, c)
                    pairMapMutable.computeIfAbsent(key) { mutableSetOf() }.add(prod.left)
                }
                else -> {
                    // CNF requires exactly 1 or 2 symbols on the right side.
                    // Ignore malformed productions.
                }
            }
        }

        terminalMap = termMap.mapValues { it.value.toSet() }
        pairMap = pairMapMutable.mapValues { it.value.toSet() }
    }

    // Returns true iff the start symbol derives the given input string.
    fun parse(input: String): Boolean {
        if (input.isEmpty()) {
            // CNF without epsilon productions cannot generate empty string.
            return false
        }

        val n = input.length
        // table[length][start] = set of non‑terminals deriving substring.
        val table = Array(n) { Array(n) { mutableSetOf<String>() } }

        // Fill length = 1 (terminals).
        for (i in 0 until n) {
            val ch = input[i].toString()
            val nts = terminalMap[ch] ?: emptySet()
            table[0][i].addAll(nts)
        }

        // Fill for lengths >= 2.
        for (len in 2..n) {               // current substring length
            val row = len - 1               // row index in table
            for (start in 0..(n - len)) {   // start position of substring
                val cell = table[row][start]
                // Partition the substring into two parts.
                for (split in 1 until len) {
                    val leftSet = table[split - 1][start]
                    val rightSet = table[len - split - 1][start + split]
                    for (b in leftSet) {
                        for (c in rightSet) {
                            val producers = pairMap[Pair(b, c)] ?: continue
                            cell.addAll(producers)
                        }
                    }
                }
            }
        }

        // The whole string corresponds to length n, start 0.
        return table[n - 1][0].contains(grammar.startSymbol)
    }
}
