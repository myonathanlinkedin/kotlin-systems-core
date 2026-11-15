package main

import core.BasicBlock
import core.CFG
import core.DataFlowAnalysis

fun main() {
    // Define a simple CFG for live variable analysis
    // Block 0: x = 1; y = 2; goto 1
    // Block 1: if (x > 0) goto 2 else goto 3
    // Block 2: z = x + y; goto 4
    // Block 3: z = y; goto 4
    // Block 4: print(z)
    val blocks = listOf(
        BasicBlock(
            id = 0,
            gen = setOf("x", "y"),
            kill = emptySet(),
            successors = listOf(1)
        ),
        BasicBlock(
            id = 1,
            gen = emptySet(),
            kill = emptySet(),
            successors = listOf(2, 3)
        ),
        BasicBlock(
            id = 2,
            gen = setOf("z"),
            kill = emptySet(),
            successors = listOf(4)
        ),
        BasicBlock(
            id = 3,
            gen = setOf("z"),
            kill = emptySet(),
            successors = listOf(4)
        ),
        BasicBlock(
            id = 4,
            gen = emptySet(),
            kill = emptySet(),
            successors = emptyList()
        )
    )

    val cfg = CFG(blocks)
    val analysis = DataFlowAnalysis(cfg)
    val inSets = analysis.analyze()

    // Expected IN sets for live variable analysis (backwards)
    // For this simple example, we compute manually:
    // Block 4: IN = {z}
    // Block 2: IN = {z}
    // Block 3: IN = {z}
    // Block 1: IN = {z}
    // Block 0: IN = {x, y, z}
    val expectedIn = mapOf(
        4 to setOf("z"),
        2 to setOf("z"),
        3 to setOf("z"),
        1 to setOf("z"),
        0 to setOf("x", "y", "z")
    )

    for ((id, expected) in expectedIn) {
        val actual = inSets[id] ?: emptySet()
        check(actual == expected) { "Block $id: expected $expected, got $actual" }
    }

    println("All tests passed.")
}