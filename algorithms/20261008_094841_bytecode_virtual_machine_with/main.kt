package vm

fun main() {
    // Test 1: Simple arithmetic 2 + 3 * 4 = 14
    val prog1 = listOf(
        Instruction(OpCode.PUSH, 2),
        Instruction(OpCode.PUSH, 3),
        Instruction(OpCode.PUSH, 4),
        Instruction(OpCode.MUL),
        Instruction(OpCode.ADD),
        Instruction(OpCode.PRINT),
        Instruction(OpCode.HALT)
    )
    val vm1 = StackVM(prog1)
    val out1 = vm1.run()
    check(out1.size == 1 && out1[0] == 14) { "Test 1 failed: expected 14, got $out1" }

    // Test 2: Subtraction and division (10 - 2) / 4 = 2
    val prog2 = listOf(
        Instruction(OpCode.PUSH, 10),
        Instruction(OpCode.PUSH, 2),
        Instruction(OpCode.SUB),
        Instruction(OpCode.PUSH, 4),
        Instruction(OpCode.DIV),
        Instruction(OpCode.PRINT),
        Instruction(OpCode.HALT)
    )
    val vm2 = StackVM(prog2)
    val out2 = vm2.run()
    check(out2.size == 1 && out2[0] == 2) { "Test 2 failed: expected 2, got $out2" }

    // Test 3: Stack underflow detection
    val prog3 = listOf(
        Instruction(OpCode.POP),
        Instruction(OpCode.HALT)
    )
    val vm3 = StackVM(prog3)
    try {
        vm3.run()
        check(false) { "Test 3 failed: expected exception for POP on empty stack" }
    } catch (e: IllegalStateException) {
        // Expected
    }

    // Test 4: Division by zero detection
    val prog4 = listOf(
        Instruction(OpCode.PUSH, 5),
        Instruction(OpCode.PUSH, 0),
        Instruction(OpCode.DIV),
        Instruction(OpCode.HALT)
    )
    val vm4 = StackVM(prog4)
    try {
        vm4.run()
        check(false) { "Test 4 failed: expected exception for division by zero" }
    } catch (e: IllegalStateException) {
        // Expected
    }

    // Test 5: Multiple PRINTs
    val prog5 = listOf(
        Instruction(OpCode.PUSH, 7),
        Instruction(OpCode.PRINT),
        Instruction(OpCode.PUSH, 3),
        Instruction(OpCode.PRINT),
        Instruction(OpCode.HALT)
    )
    val vm5 = StackVM(prog5)
    val out5 = vm5.run()
    check(out5 == listOf(7, 3)) { "Test 5 failed: expected [7,3], got $out5" }

    // All tests passed
    println("All tests passed")
}