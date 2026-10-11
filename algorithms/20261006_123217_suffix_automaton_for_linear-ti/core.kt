package suffixautomaton

data class Node(
    var len: Int = 0,
    var link: Int = -1,
    val next: MutableMap<Char, Int> = mutableMapOf()
)

class SuffixAutomaton(input: String) {
    val nodes = mutableListOf<Node>()
    private var last = 0

    init {
        nodes.add(Node()) // root
        for (ch in input) {
            extend(ch)
        }
    }

    private fun extend(c: Char) {
        val cur = nodes.size
        nodes.add(Node(len = nodes[last].len + 1))
        var p = last
        while (p != -1 && !nodes[p].next.containsKey(c)) {
            nodes[p].next[c] = cur
            p = nodes[p].link
        }
        if (p == -1) {
            nodes[cur].link = 0
        } else {
            val q = nodes[p].next[c]!!
            if (nodes[p].len + 1 == nodes[q].len) {
                nodes[cur].link = q
            } else {
                val clone = nodes.size
                nodes.add(
                    Node(
                        len = nodes[p].len + 1,
                        link = nodes[q].link,
                        next = nodes[q].next.toMutableMap()
                    )
                )
                while (p != -1 && nodes[p].next[c] == q) {
                    nodes[p].next[c] = clone
                    p = nodes[p].link
                }
                nodes[q].link = clone
                nodes[cur].link = clone
            }
        }
        last = cur
    }

    fun contains(sub: String): Boolean {
        var v = 0
        for (ch in sub) {
            val nxt = nodes[v].next[ch] ?: return false
            v = nxt
        }
        return true
    }
}