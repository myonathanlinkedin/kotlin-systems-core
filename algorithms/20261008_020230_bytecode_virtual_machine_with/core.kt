package vm

import kotlin.math.*

enum class OpCode { NOP, PUSH, ADD, SUB, MUL, DIV, HALT }

data class Instruction(val opcode: OpCode, val operand: Long = 0L)

data class Program(val instructions: List<Instruction>)

class VirtualMachine(val program: Program) {
    val stack = mutableListOf<Long>()
    var pc = 0
    var running = true

    fun step() {
        if (!running || pc >= program.instructions.size) {
            running = false
            return
        }
        val instr = program.instructions[pc]
        when (instr.opcode) {
            OpCode.NOP -> {}
            OpCode.PUSH -> stack.add(instr.operand)
            OpCode.ADD -> {
                val b = stack.removeAt(stack.lastIndex)
                val a = stack.removeAt(stack.lastIndex)
                stack.add(a + b)
            }
            OpCode.SUB -> {
                val b = stack.removeAt(stack.lastIndex)
                val a = stack.removeAt(stack.lastIndex)
                stack.add(a - b)
            }
            OpCode.MUL -> {
                val b = stack.removeAt(stack.lastIndex)
                val a = stack.removeAt(stack.lastIndex)
                stack.add(a * b)
            }
            OpCode.DIV -> {
                val b = stack.removeAt(stack.lastIndex)
                val a = stack.removeAt(stack.lastIndex)
                stack.add(a / b)
            }
            OpCode.HALT -> running = false
        }
        pc++
    }

    fun run() {
        while (running) step()
    }
}