package vm

import kotlin.math.*

sealed class Instruction {
    data class PushInt(val value: Int) : Instruction()
    object Add : Instruction()
    object Sub : Instruction()
    object Mul : Instruction()
    object Div : Instruction()
    object Dup : Instruction()
    object Swap : Instruction()
    object Print : Instruction()
    object Halt : Instruction()
}

class VirtualMachine(val program: List<Instruction>) {
    var pc: Int = 0
    val stack: MutableList<Int> = mutableListOf()
    val output: MutableList<Int> = mutableListOf()
    var halted: Boolean = false

    private fun pop(): Int {
        if (stack.isEmpty()) {
            throw IllegalStateException("Stack underflow at pc=$pc")
        }
        return stack.removeAt(stack.lastIndex)
    }

    private fun push(value: Int) {
        stack.add(value)
    }

    fun step(): Boolean {
        if (halted || pc >= program.size) return false
        when (val instr = program[pc]) {
            is Instruction.PushInt -> push(instr.value)
            Instruction.Add -> {
                val b = pop()
                val a = pop()
                push(a + b)
            }
            Instruction.Sub -> {
                val b = pop()
                val a = pop()
                push(a - b)
            }
            Instruction.Mul -> {
                val b = pop()
                val a = pop()
                push(a * b)
            }
            Instruction.Div -> {
                val b = pop()
                if (b == 0) throw IllegalArgumentException("Division by zero at pc=$pc")
                val a = pop()
                push(a / b)
            }
            Instruction.Dup -> {
                val a = pop()
                push(a)
                push(a)
            }
            Instruction.Swap -> {
                val b = pop()
                val a = pop()
                push(b)
                push(a)
            }
            Instruction.Print -> {
                val a = pop()
                output.add(a)
            }
            Instruction.Halt -> halted = true
        }
        pc += 1
        return !halted
    }

    fun run(): List<Int> {
        while (step()) {
            // loop until halted
        }
        return output.toList()
    }
}