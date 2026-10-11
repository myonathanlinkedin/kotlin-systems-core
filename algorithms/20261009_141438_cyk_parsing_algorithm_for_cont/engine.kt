package cyk

fun cykParse(grammar: Grammar, input: String): Boolean {
    val n = input.length
    if (n == 0) {
        // CNF does not generate ε unless explicitly added; we treat as false
        return false
    }

    // table[length‑1][start] = set of non‑terminals deriving the substring
    val table = Array(n) { Array(n) { mutableSetOf<String>() } }

    // Length 1 substrings (terminals)
    for (s in 0 until n) {
        val ch = input[s].toString()
        val nts = grammar.terminalMap[ch] ?: emptySet()
        table[0][s].addAll(nts)
    }

    // Substrings of length >= 2
    for (len in 2..n) {               // current substring length
        val row = len - 1               // row index in table
        for (start in 0..(n - len)) {   // start position of substring
            for (partition in 1 until len) { // split point
                val leftSet = table[partition - 1][start]
                val rightSet = table[row - partition][start + partition]
                for (b in leftSet) {
                    for (c in rightSet) {
                        val producers = grammar.pairMap[Pair(b, c)] ?: continue
                        table[row][start].addAll(producers)
                    }
                }
            }
        }
    }

    // Accept if start symbol derives the whole string
    return grammar.startSymbol in table[n - 1][0]
}