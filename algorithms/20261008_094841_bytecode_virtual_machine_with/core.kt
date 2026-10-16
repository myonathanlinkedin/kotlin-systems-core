package vm

import kotlin.math.*

enum class OpCode {
    PUSH, POP, ADD, SUB, MUL, DIV, PRINT, HALT
}

data class Instruction(val opcode: OpCode, val operand: Int? = null)

class StackVM(private val program: List<Instruction>) {
    private var pc: Int = 0
    private val stack = mutableListOf<Int>()
    private val output = mutableListOf<Int>()
    var halted: Boolean = false
        private set

    fun run(): List<Int> {
        while (!halted && pc < program.size) {
            val instr = program[pc]
            execute(instr)
            pc++
        }
        return output.toList()
    }

    private fun execute(instr: Instruction) {
        when (instr.opcode) {
            OpCode.PUSH -> {
                val value = instr.operand ?: error("PUSH requires operand")
                stack.add(value)
            }
            OpCode.POP -> {
                ensureStackSize(1, "POP")
                stack.removeAt(stack.lastIndex)
            }
            OpCode.ADD -> binaryOp(Int::plus, "ADD")
            OpCode.SUB -> binaryOp(Int::minus, "SUB")
            OpCode.MUL -> binaryOp(Int::times, "MUL")
            OpCode.DIV -> {
                ensureStackSize(2, "DIV")
                val b = stack.removeAt(stack.lastIndex)
                check(b != 0) { "Division by zero" }
                val a = stack.removeAt(stack.lastIndex)
                stack.add(a / b)
            }
            OpCode.PRINT -> {
                ensureStackSize(1, "PRINT")
                val value = stack.last()
                output.add(value)
            }
            OpCode.HALT -> halted = true
        }
    }

    private fun binaryOp(op: (Int, Int) -> Int, name: String) {
        ensureStackSize(2, name)
        val b = stack.removeAt(stack.lastIndex)
        val a = stack.removeAt(stack.lastIndex)
        stack.add(op(a, b))
    }

    private fun ensureStackSize(required: Int, opName: String) {
        check(stack.size >= required) { "$opName requires at least $required values on stack, found ${stack.size}" }
    }
}