package vm

fun main() {
    // Test 1: Simple arithmetic 2 3 + 4 *
    val prog1 = listOf(
        Instruction(OpCode.PUSH, 2),
        Instruction(OpCode.PUSH, 3),
        Instruction(OpCode.ADD),
        Instruction(OpCode.PUSH, 4),
        Instruction(OpCode.MUL),
        Instruction(OpCode.HALT)
    )
    val vm1 = BytecodeVM(prog1)
    val result1 = vm1.run()
    check(result1.size == 1 && result1[0] == 20) { "Arithmetic test failed" }

    // Test 2: Negation and subtraction
    val prog2 = listOf(
        Instruction(OpCode.PUSH, 10),
        Instruction(OpCode.NEG),
        Instruction(OpCode.PUSH, 3),
        Instruction(OpCode.SUB), // (-10) - 3 = -13
        Instruction(OpCode.HALT)
    )
    val vm2 = BytecodeVM(prog2)
    val result2 = vm2.run()
    check(result2.size == 1 && result2[0] == -13) { "Negation/Sub test failed" }

    // Test 3: Stack manipulation DUP and SWAP
    val prog3 = listOf(
        Instruction(OpCode.PUSH, 7),
        Instruction(OpCode.DUP),   // [7,7]
        Instruction(OpCode.PUSH, 2),
        Instruction(OpCode.SWAP), // [7,2,7]
        Instruction(OpCode.ADD),   // pop 7+2=9 -> [7,9]
        Instruction(OpCode.HALT)
    )
    val vm3 = BytecodeVM(prog3)
    val result3 = vm3.run()
    check(result3 == listOf(7, 9)) { "Dup/Swap test failed" }

    // Test 4: Conditional jump (compute factorial of 4)
    // Pseudocode:
    // result = 1
    // n = 4
    // loop:
    //   if n == 0 goto end
    //   result = result * n
    //   n = n - 1
    //   goto loop
    // end:
    // halt
    val prog4 = listOf(
        Instruction(OpCode.PUSH, 1),   // result
        Instruction(OpCode.PUSH, 4),   // n
        // loop start at index 2
        Instruction(OpCode.DUP),       // duplicate n
        Instruction(OpCode.JZ, 12),    // if n==0 jump to halt (index 12)
        Instruction(OpCode.SWAP),      // bring result under n
        Instruction(OpCode.MUL),       // result *= n
        Instruction(OpCode.PUSH, 1),
        Instruction(OpCode.SWAP),
        Instruction(OpCode.SUB),       // n -= 1
        Instruction(OpCode.JUMP, 2),   // repeat loop
        // halt
        Instruction(OpCode.HALT)
    )
    val vm4 = BytecodeVM(prog4)
    val result4 = vm4.run()
    // After loop, stack holds [result, n==0]
    check(result4.size == 2 && result4[0] == 24 && result4[1] == 0) { "Factorial test failed" }

    // Test 5: Division and modulo edge cases
    val prog5 = listOf(
        Instruction(OpCode.PUSH, 20),
        Instruction(OpCode.PUSH, 6),
        Instruction(OpCode.DIV),   // 20 / 6 = 3
        Instruction(OpCode.PUSH, 20),
        Instruction(OpCode.PUSH, 6),
        Instruction(OpCode.MOD),   // 20 % 6 = 2
        Instruction(OpCode.HALT)
    )
    val vm5 = BytecodeVM(prog5)
    val result5 = vm5.run()
    check(result5 == listOf(3, 2)) { "Div/Mod test failed" }

    // All tests passed
    println("All VM tests passed.")
}