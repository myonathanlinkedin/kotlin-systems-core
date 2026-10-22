package vm

fun main() {
    // Test 1: simple addition
    val prog1 = listOf(
        Instruction.PushInt(2),
        Instruction.PushInt(3),
        Instruction.Add,
        Instruction.Print,
        Instruction.Halt
    )
    val out1 = VirtualMachine(prog1).run()
    check(out1 == listOf(5))

    // Test 2: mixed arithmetic ((4 * 5) - (6 / 2)) = 17
    val prog2 = listOf(
        Instruction.PushInt(4),
        Instruction.PushInt(5),
        Instruction.Mul,
        Instruction.PushInt(6),
        Instruction.PushInt(2),
        Instruction.Div,
        Instruction.Sub,
        Instruction.Print,
        Instruction.Halt
    )
    val out2 = VirtualMachine(prog2).run()
    check(out2 == listOf(17))

    // Test 3: stack manipulation (dup, swap, sub)
    val prog3 = listOf(
        Instruction.PushInt(9),
        Instruction.Dup,
        Instruction.PushInt(1),
        Instruction.Swap,
        Instruction.Sub,
        Instruction.Print,
        Instruction.Halt
    )
    val out3 = VirtualMachine(prog3).run()
    check(out3 == listOf(8))

    // Test 4: division by zero throws IllegalArgumentException
    val prog4 = listOf(
        Instruction.PushInt(10),
        Instruction.PushInt(0),
        Instruction.Div,
        Instruction.Halt
    )
    var caughtDivZero = false
    try {
        VirtualMachine(prog4).run()
    } catch (e: IllegalArgumentException) {
        caughtDivZero = true
    }
    check(caughtDivZero)

    // Test 5: stack underflow throws IllegalStateException
    val prog5 = listOf(
        Instruction.Add,
        Instruction.Halt
    )
    var caughtUnderflow = false
    try {
        VirtualMachine(prog5).run()
    } catch (e: IllegalStateException) {
        caughtUnderflow = true
    }
    check(caughtUnderflow)

    println("All tests passed.")
}