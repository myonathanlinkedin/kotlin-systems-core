import kotlin.collections.*

// Core CYK parsing engine
object CYKEngine {

    // Parse input string using given CNF grammar; returns true if string is generated
    fun parse(grammar: Grammar, input: String): Boolean {
        val n = input.length
        if (n == 0) {
            // CNF without epsilon productions cannot generate empty string
            return false
        }

        // Table[i][j] holds set of NonTerminals that derive substring input[i..j] (inclusive)
        val table: Array<Array<MutableSet<Symbol.NonTerminal>>> = Array(n) { i ->
            Array(n) { mutableSetOf<Symbol.NonTerminal>() }
        }

        // Preprocess productions for fast lookup
        val terminalMap = mutableMapOf<Char, MutableSet<Symbol.NonTerminal>>()
        val binaryMap = mutableMapOf<Pair<Symbol.NonTerminal, Symbol.NonTerminal>, MutableSet<Symbol.NonTerminal>>()

        for (prod in grammar.productions) {
            when (prod.right.size) {
                1 -> {
                    val sym = prod.right[0]
                    if (sym is Symbol.Terminal) {
                        terminalMap.computeIfAbsent(sym.value) { mutableSetOf() }.add(prod.left)
                    }
                }
                2 -> {
                    val leftSym = prod.right[0]
                    val rightSym = prod.right[1]
                    if (leftSym is Symbol.NonTerminal && rightSym is Symbol.NonTerminal) {
                        val key = Pair(leftSym, rightSym)
                        binaryMap.computeIfAbsent(key) { mutableSetOf() }.add(prod.left)
                    }
                }
                else -> {
                    // CNF guarantees size 1 or 2; ignore otherwise
                }
            }
        }

        // Fill base case: substrings of length 1
        for (i in 0 until n) {
            val ch = input[i]
            val nts = terminalMap[ch] ?: emptySet()
            table[i][i].addAll(nts)
        }

        // Fill DP table for lengths >= 2
        for (length in 2..n) { // length of the span
            for (i in 0..(n - length)) {
                val j = i + length - 1
                for (split in i until j) {
                    val leftSet = table[i][split]
                    val rightSet = table[split + 1][j]
                    for (b in leftSet) {
                        for (c in rightSet) {
                            val possibleParents = binaryMap[Pair(b, c)] ?: continue
                            table[i][j].addAll(possibleParents)
                        }
                    }
                }
            }
        }

        // Acceptance condition: start symbol derives whole string
        return table[0][n - 1].contains(grammar.startSymbol)
    }
}
