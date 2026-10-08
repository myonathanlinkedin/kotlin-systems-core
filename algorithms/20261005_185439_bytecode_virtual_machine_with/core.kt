package vm

enum class OpCode(val operandCount: Int) {
    NOP(0),
    PUSH(1),      // operand: immediate integer
    POP(0),
    ADD(0),
    SUB(0),
    MUL(0),
    DIV(0),
    LOAD(1),      // operand: register index
    STORE(1),     // operand: register index
    JMP(1),       // operand: target address
    JZ(1),        // operand: target address if top of stack == 0
    HALT(0)
}

data class Instruction(val opcode: OpCode, val operand: Int? = null)

class VirtualMachine(
    private val program: List<Instruction>,
    private val registerCount: Int = 8,
    private val maxStackSize: Int = 1024
) {
    private val registers = IntArray(registerCount) { 0 }
    private val stack = IntArray(maxStackSize)
    private var sp = -1                     // stack pointer, -1 means empty
    private var ip = 0                      // instruction pointer
    var halted = false
        private set

    private fun push(value: Int) {
        if (sp + 1 >= maxStackSize) throw IllegalStateException("Stack overflow")
        sp++
        stack[sp] = value
    }

    private fun pop(): Int {
        if (sp < 0) throw IllegalStateException("Stack underflow")
        val value = stack[sp]
        sp--
        return value
    }

    private fun peek(): Int = if (sp >= 0) stack[sp] else throw IllegalStateException("Stack empty")

    fun reset() {
        sp = -1
        ip = 0
        halted = false
        registers.fill(0)
    }

    fun run(): Int? {
        while (!halted && ip < program.size) {
            step()
        }
        return if (sp >= 0) stack[sp] else null
    }

    private fun step() {
        val instr = program[ip]
        when (instr.opcode) {
            OpCode.NOP -> ip++
            OpCode.PUSH -> {
                val value = instr.operand ?: throw IllegalArgumentException("PUSH requires operand")
                push(value)
                ip++
            }
            OpCode.POP -> {
                pop()
                ip++
            }
            OpCode.ADD -> {
                val b = pop()
                val a = pop()
                push(a + b)
                ip++
            }
            OpCode.SUB -> {
                val b = pop()
                val a = pop()
                push(a - b)
                ip++
            }
            OpCode.MUL -> {
                val b = pop()
                val a = pop()
                push(a * b)
                ip++
            }
            OpCode.DIV -> {
                val b = pop()
                val a = pop()
                if (b == 0) throw ArithmeticException("Division by zero")
                push(a / b)
                ip++
            }
            OpCode.LOAD -> {
                val idx = instr.operand ?: throw IllegalArgumentException("LOAD requires operand")
                check(idx in registers.indices) { "Register index out of bounds" }
                push(registers[idx])
                ip++
            }
            OpCode.STORE -> {
                val idx = instr.operand ?: throw IllegalArgumentException("STORE requires operand")
                check(idx in registers.indices) { "Register index out of bounds" }
                registers[idx] = pop()
                ip++
            }
            OpCode.JMP -> {
                val target = instr.operand ?: throw IllegalArgumentException("JMP requires operand")
                check(target in program.indices) { "Jump target out of bounds" }
                ip = target
            }
            OpCode.JZ -> {
                val target = instr.operand ?: throw IllegalArgumentException("JZ requires operand")
                check(target in program.indices) { "Jump target out of bounds" }
                val value = pop()
                ip = if (value == 0) target else ip + 1
            }
            OpCode.HALT -> {
                halted = true
            }
        }
    }
}
