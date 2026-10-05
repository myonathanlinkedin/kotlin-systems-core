package vm

fun buildProgram(vararg ops: Instruction): List<Instruction> = ops.toList()

fun testArithmetic() {
    val prog = buildProgram(
        Instruction(OpCode.PUSH, 10),
        Instruction(OpCode.PUSH, 20),
        Instruction(OpCode.ADD),
        Instruction(OpCode.PUSH, 5),
        Instruction(OpCode.MUL),
        Instruction(OpCode.HALT)
    )
    val vm = VirtualMachine(prog)
    val result = vm.run()
    check(result == 150) { "Arithmetic test failed, expected 150 got $result" }
}

fun testDivisionByZero() {
    val prog = buildProgram(
        Instruction(OpCode.PUSH, 10),
        Instruction(OpCode.PUSH, 0),
        Instruction(OpCode.DIV),
        Instruction(OpCode.HALT)
    )
    val vm = VirtualMachine(prog)
    var caught = false
    try {
        vm.run()
    } catch (e: ArithmeticException) {
        caught = true
    }
    check(caught) { "Division by zero should throw ArithmeticException" }
}

fun testJumpAndConditional() {
    // Compute factorial of 4 using loop
    // Registers: 0 = n, 1 = result, 2 = loop flag
    val prog = buildProgram(
        // init n = 4
        Instruction(OpCode.PUSH, 4),
        Instruction(OpCode.STORE, 0),
        // init result = 1
        Instruction(OpCode.PUSH, 1),
        Instruction(OpCode.STORE, 1),
        // loop start address 8
        Instruction(OpCode.JMP, 8),

        // loop body:
        // load n
        Instruction(OpCode.LOAD, 0),
        // if n == 0 jump to end (addr 20)
        Instruction(OpCode.JZ, 20),
        // load result
        Instruction(OpCode.LOAD, 1),
        // load n
        Instruction(OpCode.LOAD, 0),
        // multiply result * n
        Instruction(OpCode.MUL),
        // store back to result
        Instruction(OpCode.STORE, 1),
        // decrement n
        Instruction(OpCode.LOAD, 0),
        Instruction(OpCode.PUSH, 1),
        Instruction(OpCode.SUB),
        Instruction(OpCode.STORE, 0),
        // jump to loop start
        Instruction(OpCode.JMP, 8),

        // end:
        Instruction(OpCode.LOAD, 1),
        Instruction(OpCode.HALT)
    )
    val vm = VirtualMachine(prog)
    val result = vm.run()
    check(result == 24) { "Factorial test failed, expected 24 got $result" }
}

fun benchmarkFactorial(iterations: Int = 100_000) {
    val prog = buildProgram(
        Instruction(OpCode.PUSH, 10),
        Instruction(OpCode.STORE, 0),
        Instruction(OpCode.PUSH, 1),
        Instruction(OpCode.STORE, 1),
        Instruction(OpCode.JMP, 8),
        Instruction(OpCode.LOAD, 0),
        Instruction(OpCode.JZ, 20),
        Instruction(OpCode.LOAD, 1),
        Instruction(OpCode.LOAD, 0),
        Instruction(OpCode.MUL),
        Instruction(OpCode.STORE, 1),
        Instruction(OpCode.LOAD, 0),
        Instruction(OpCode.PUSH, 1),
        Instruction(OpCode.SUB),
        Instruction(OpCode.STORE, 0),
        Instruction(OpCode.JMP, 8),
        Instruction(OpCode.LOAD, 1),
        Instruction(OpCode.HALT)
    )
    val vm = VirtualMachine(prog)
    var sum = 0L
    repeat(iterations) {
        vm.reset()
        val res = vm.run() ?: 0
        sum += res.toLong()
    }
    println("Benchmark completed: $iterations runs, cumulative result = $sum")
}

fun main() {
    testArithmetic()
    testDivisionByZero()
    testJumpAndConditional()
    println("All unit tests passed.")
    benchmarkFactorial(10_000)
}
