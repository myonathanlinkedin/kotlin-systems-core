package vm

enum class OpCode {
    PUSH,    // operand: immediate integer
    POP,     // operand ignored
    ADD,     // pop two, push sum
    SUB,     // pop two, push (second - top)
    MUL,     // pop two, push product
    DIV,     // pop two, push (second / top), integer division
    MOD,     // pop two, push (second % top)
    NEG,     // pop one, push its negation
    DUP,     // duplicate top value
    SWAP,    // swap top two values
    JUMP,    // operand: absolute instruction index
    JZ,      // pop, if zero jump to operand
    HALT     // stop execution
}

data class Instruction(val opcode: OpCode, val operand: Int = 0)

class BytecodeVM(private val program: List<Instruction>) {
    private val stack = mutableListOf<Int>()
    private var ip: Int = 0
    private var halted = false

    private fun push(value: Int) {
        stack.add(value)
    }

    private fun pop(): Int {
        if (stack.isEmpty()) throw IllegalStateException("Stack underflow")
        return stack.removeAt(stack.size - 1)
    }

    private fun peek(): Int {
        if (stack.isEmpty()) throw IllegalStateException("Stack underflow")
        return stack[stack.size - 1]
    }

    private fun step() {
        if (ip !in program.indices) {
            halted = true
            return
        }
        val instr = program[ip]
        when (instr.opcode) {
            OpCode.PUSH -> {
                push(instr.operand)
                ip += 1
            }
            OpCode.POP -> {
                pop()
                ip += 1
            }
            OpCode.ADD -> {
                val b = pop()
                val a = pop()
                push(a + b)
                ip += 1
            }
            OpCode.SUB -> {
                val b = pop()
                val a = pop()
                push(a - b)
                ip += 1
            }
            OpCode.MUL -> {
                val b = pop()
                val a = pop()
                push(a * b)
                ip += 1
            }
            OpCode.DIV -> {
                val b = pop()
                if (b == 0) throw IllegalArgumentException("Division by zero")
                val a = pop()
                push(a / b)
                ip += 1
            }
            OpCode.MOD -> {
                val b = pop()
                if (b == 0) throw IllegalArgumentException("Modulo by zero")
                val a = pop()
                push(a % b)
                ip += 1
            }
            OpCode.NEG -> {
                val a = pop()
                push(-a)
                ip += 1
            }
            OpCode.DUP -> {
                val a = peek()
                push(a)
                ip += 1
            }
            OpCode.SWAP -> {
                val a = pop()
                val b = pop()
                push(a)
                push(b)
                ip += 1
            }
            OpCode.JUMP -> {
                ip = instr.operand
            }
            OpCode.JZ -> {
                val a = pop()
                ip = if (a == 0) instr.operand else ip + 1
            }
            OpCode.HALT -> {
                halted = true
            }
        }
    }

    fun run(): List<Int> {
        while (!halted) {
            step()
        }
        return stack.toList()
    }

    // Expose stack for testing (read‑only)
    fun snapshotStack(): List<Int> = stack.toList()
}