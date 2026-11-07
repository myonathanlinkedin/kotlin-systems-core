package main

fun runTest(name: String, block: () -> Unit) {
    try {
        block()
        println("Test '$name' passed.")
    } catch (e: Throwable) {
        println("Test '$name' failed: ${e.message}")
        throw e
    }
}

fun main() {
    // Test 1: Simple arithmetic with precedence
    runTest("Arithmetic precedence") {
        val source = "return 2 + 3 * 4;"
        val tokens = Lexer(source).tokenize()
        val stmts = Parser(tokens).parse()
        val result = Interpreter().interpret(stmts)
        check(result == 14) { "Expected 14, got $result" }
    }

    // Test 2: Parentheses override precedence
    runTest("Parentheses") {
        val source = "return (2 + 3) * 4;"
        val tokens = Lexer(source).tokenize()
        val stmts = Parser(tokens).parse()
        val result = Interpreter().interpret(stmts)
        check(result == 20) { "Expected 20, got $result" }
    }

    // Test 3: Unary minus
    runTest("Unary minus") {
        val source = "return -5 + 2;"
        val tokens = Lexer(source).tokenize()
        val stmts = Parser(tokens).parse()
        val result = Interpreter().interpret(stmts)
        check(result == -3) { "Expected -3, got $result" }
    }

    // Test 4: Variable declaration, assignment, and usage
    runTest("Variables") {
        val source = """
            int a = 5;
            int b = a * 2;
            return b + 3;
        """.trimIndent()
        val tokens = Lexer(source).tokenize()
        val stmts = Parser(tokens).parse()
        val result = Interpreter().interpret(stmts)
        check(result == 13) { "Expected 13, got $result" }
    }

    // Test 5: Variable default initialization (no initializer)
    runTest("Default init") {
        val source = """
            int x;
            x = 7;
            return x;
        """.trimIndent()
        val tokens = Lexer(source).tokenize()
        val stmts = Parser(tokens).parse()
        val result = Interpreter().interpret(stmts)
        check(result == 7) { "Expected 7, got $result" }
    }

    // Test 6: Division and error handling (division by zero)
    runTest("Division by zero") {
        val source = "return 10 / 0;"
        val tokens = Lexer(source).tokenize()
        val stmts = Parser(tokens).parse()
        try {
            Interpreter().interpret(stmts)
            check(false) { "Expected ArithmeticException for division by zero" }
        } catch (e: ArithmeticException) {
            // Expected
        }
    }

    println("All tests passed.")
}