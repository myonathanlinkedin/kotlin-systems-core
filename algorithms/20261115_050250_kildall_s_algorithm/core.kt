package core

import java.util.*

data class BasicBlock(
    val id: Int,
    val gen: Set<String>,
    val kill: Set<String>,
    val successors: List<Int>
)

class CFG(val blocks: List<BasicBlock>) {
    private val blockMap: Map<Int, BasicBlock> = blocks.associateBy { it.id }

    fun getBlock(id: Int): BasicBlock = blockMap[id] ?: throw IllegalArgumentException("Block $id not found")
}

class DataFlowAnalysis(private val cfg: CFG) {
    fun analyze(): Map<Int, Set<String>> {
        val inSets: MutableMap<Int, MutableSet<String>> = mutableMapOf()
        val outSets: MutableMap<Int, MutableSet<String>> = mutableMapOf()

        for (block in cfg.blocks) {
            inSets[block.id] = mutableSetOf()
            outSets[block.id] = mutableSetOf()
        }

        var changed: Boolean
        do {
            changed = false
            for (block in cfg.blocks) {
                val newOut = mutableSetOf<String>()
                for (succId in block.successors) {
                    newOut.addAll(inSets[succId]!!)
                }

                val newIn = mutableSetOf<String>()
                newIn.addAll(block.gen)
                val temp = newOut.toMutableSet()
                temp.removeAll(block.kill)
                newIn.addAll(temp)

                if (newIn != inSets[block.id] || newOut != outSets[block.id]) {
                    inSets[block.id] = newIn
                    outSets[block.id] = newOut
                    changed = true
                }
            }
        } while (changed)

        return inSets.mapValues { it.value.toSet() }
    }
}