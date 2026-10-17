package cyk

data class Production(val left: String, val right: List<String>)

class Grammar(val startSymbol: String, val productions: List<Production>) {
    // Map from terminal string to set of non‑terminals that produce it
    val terminalMap: Map<String, Set<String>>
    // Map from pair (B, C) to set of non‑terminals A such that A → B C
    val pairMap: Map<Pair<String, String>, Set<String>>

    init {
        val termTmp = mutableMapOf<String, MutableSet<String>>()
        val pairTmp = mutableMapOf<Pair<String, String>, MutableSet<String>>()

        for (prod in productions) {
            when (prod.right.size) {
                1 -> {
                    // Terminal production
                    val term = prod.right[0]
                    termTmp.computeIfAbsent(term) { mutableSetOf() }.add(prod.left)
                }
                2 -> {
                    // Binary non‑terminal production
                    val pair = Pair(prod.right[0], prod.right[1])
                    pairTmp.computeIfAbsent(pair) { mutableSetOf() }.add(prod.left)
                }
                else -> {
                    // Not CNF – ignored for CYK
                }
            }
        }
        terminalMap = termTmp.mapValues { it.value.toSet() }
        pairMap = pairTmp.mapValues { it.value.toSet() }
    }
}