package vm

fun main() {
    runTests()
    println("All tests passed.")
}

fun runTests() {
    testPushAndPop()
    testAdd()
    testSub()
    testMul()
    testDiv()
    testHalt()
    testProgramExecution()
}

fun testPushAndPop() {
    val program = Program(
        listOf(
            Instruction(OpCode.PUSH, 10L),
            Instruction(OpCode.PUSH, 20L),
            Instruction(OpCode.HALT)
        )
    )
    val vm = VirtualMachine(program)
    vm.run()
    check(vm.stack.size == 2)
    check(vm.stack[0] == 10L)
    check(vm.stack[1] == 20L)
}

fun testAdd() {
    val program = Program(
        listOf(
            Instruction(OpCode.PUSH, 5L),
            Instruction(OpCode.PUSH, 7L),
            Instruction(OpCode.ADD),
            Instruction(OpCode.HALT)
        )
    )
    val vm = VirtualMachine(program)
    vm.run()
    check(vm.stack.size == 1)
    check(vm.stack[0] == 12L)
}

fun testSub() {
    val program = Program(
        listOf(
            Instruction(OpCode.PUSH, 15L),
            Instruction(OpCode.PUSH, 4L),
            Instruction(OpCode.SUB),
            Instruction(OpCode.HALT)
        )
    )
    val vm = VirtualMachine(program)
    vm.run()
    check(vm.stack.size == 1)
    check(vm.stack[0] == 11L)
}

fun testMul() {
    val program = Program(
        listOf(
            Instruction(OpCode.PUSH, 3L),
            Instruction(OpCode.PUSH, 6L),
            Instruction(OpCode.MUL),
            Instruction(OpCode.HALT)
        )
    )
    val vm = VirtualMachine(program)
    vm.run()
    check(vm.stack.size == 1)
    check(vm.stack[0] == 18L)
}

fun testDiv() {
    val program = Program(
        listOf(
            Instruction(OpCode.PUSH, 20L),
            Instruction(OpCode.PUSH, 4L),
            Instruction(OpCode.DIV),
            Instruction(OpCode.HALT)
        )
    )
    val vm = VirtualMachine(program)
    vm.run()
    check(vm.stack.size == 1)
    check(vm.stack[0] == 5L)
}

fun testHalt() {
    val program = Program(
        listOf(
            Instruction(OpCode.PUSH, 1L),
            Instruction(OpCode.HALT),
            Instruction(OpCode.PUSH, 2L) // should not execute
        )
    )
    val vm = VirtualMachine(program)
    vm.run()
    check(vm.stack.size == 1)
    check(vm.stack[0] == 1L)
}

fun testProgramExecution() {
    val program = Program(
        listOf(
            Instruction(OpCode.PUSH, 2L),
            Instruction(OpCode.PUSH, 3L),
            Instruction(OpCode.MUL),
            Instruction(OpCode.PUSH, 4L),
            Instruction(OpCode.ADD),
            Instruction(OpCode.HALT)
        )
    )
    val vm = VirtualMachine(program)
    vm.run()
    check(vm.stack.size == 1)
    check(vm.stack[0] == 10L)
}