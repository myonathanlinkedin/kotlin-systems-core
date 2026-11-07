package main

import kotlin.math.*

class Lexer(private val source: String) {
    private val tokens = mutableListOf<Token>()
    private var start = 0
    private var current = 0

    private val keywords = mapOf(
        "int" to TokenType.INT,
        "return" to TokenType.RETURN
    )

    fun tokenize(): List<Token> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }
        tokens.add(Token(TokenType.EOF, "", null, current))
        return tokens
    }

    private fun isAtEnd(): Boolean = current >= source.length

    private fun advance(): Char = source[current++]

    private fun addToken(type: TokenType, literal: Any? = null) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, start))
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false
        current++
        return true
    }

    private fun peek(): Char = if (isAtEnd()) '\u0000' else source[current]

    private fun scanToken() {
        val c = advance()
        when (c) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '+' -> addToken(TokenType.PLUS)
            '-' -> addToken(TokenType.MINUS)
            '*' -> addToken(TokenType.STAR)
            '/' -> addToken(TokenType.SLASH)
            ';' -> addToken(TokenType.SEMICOLON)
            '=' -> addToken(TokenType.EQUAL)
            ' ', '\r', '\t', '\n' -> { /* ignore whitespace */ }
            else -> {
                when {
                    c.isDigit() -> number()
                    c.isLetter() || c == '_' -> identifier()
                    else -> throw IllegalArgumentException("Unexpected character '$c' at position $start")
                }
            }
        }
    }

    private fun number() {
        while (peek().isDigit()) advance()
        val text = source.substring(start, current)
        val value = text.toInt()
        addToken(TokenType.NUMBER, value)
    }

    private fun identifier() {
        while (peek().isLetterOrDigit() || peek() == '_') advance()
        val text = source.substring(start, current)
        val type = keywords[text] ?: TokenType.IDENTIFIER
        addToken(type)
    }
}

class Parser(private val tokens: List<Token>) {
    private var current = 0

    fun parse(): List<Stmt> {
        val statements = mutableListOf<Stmt>()
        while (!isAtEnd()) {
            statements.add(statement())
        }
        return statements
    }

    private fun statement(): Stmt {
        return when {
            match(TokenType.INT) -> varDeclaration()
            match(TokenType.RETURN) -> returnStatement()
            check(TokenType.IDENTIFIER) && checkNext(TokenType.EQUAL) -> assignment()
            else -> throw IllegalArgumentException("Unexpected token ${peek().type} at position ${peek().position}")
        }
    }

    private fun varDeclaration(): Stmt.VariableDecl {
        val nameToken = consume(TokenType.IDENTIFIER, "Expected variable name after 'int'")
        val initializer = if (match(TokenType.EQUAL)) expression() else null
        consume(TokenType.SEMICOLON, "Expected ';' after variable declaration")
        return Stmt.VariableDecl(nameToken.lexeme, initializer)
    }

    private fun assignment(): Stmt.Assignment {
        val nameToken = consume(TokenType.IDENTIFIER, "Expected variable name")
        consume(TokenType.EQUAL, "Expected '=' after variable name")
        val value = expression()
        consume(TokenType.SEMICOLON, "Expected ';' after assignment")
        return Stmt.Assignment(nameToken.lexeme, value)
    }

    private fun returnStatement(): Stmt.Return {
        val value = expression()
        consume(TokenType.SEMICOLON, "Expected ';' after return expression")
        return Stmt.Return(value)
    }

    private fun expression(): Expr = addition()

    private fun addition(): Expr {
        var expr = multiplication()
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            val operator = previous()
            val right = multiplication()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun multiplication(): Expr {
        var expr = unary()
        while (match(TokenType.STAR, TokenType.SLASH)) {
            val operator = previous()
            val right = unary()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun unary(): Expr {
        if (match(TokenType.MINUS)) {
            val operator = previous()
            val right = unary()
            return Expr.Unary(operator, right)
        }
        return primary()
    }

    private fun primary(): Expr {
        when {
            match(TokenType.NUMBER) -> {
                val value = previous().literal as Int
                return Expr.Literal(value)
            }
            match(TokenType.IDENTIFIER) -> {
                return Expr.Variable(previous().lexeme)
            }
            match(TokenType.LEFT_PAREN) -> {
                var expr = expression()
                consume(TokenType.RIGHT_PAREN, "Expected ')' after expression")
                return expr
            }
            else -> throw IllegalArgumentException("Unexpected token ${peek().type} at position ${peek().position}")
        }
    }

    // Utility parsing methods
    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun check(type: TokenType): Boolean {
        if (isAtEnd()) return false
        return peek().type == type
    }

    private fun checkNext(type: TokenType): Boolean {
        if (current + 1 >= tokens.size) return false
        return tokens[current + 1].type == type
    }

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        throw IllegalArgumentException("$message at position ${peek().position}")
    }

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun isAtEnd(): Boolean = peek().type == TokenType.EOF

    private fun peek(): Token = tokens[current]

    private fun previous(): Token = tokens[current - 1]
}

class Interpreter {
    private val environment = mutableMapOf<String, Int>()
    private var returnValue: Int? = null

    fun interpret(statements: List<Stmt>): Int {
        for (stmt in statements) {
            execute(stmt)
            if (returnValue != null) {
                return returnValue!!
            }
        }
        throw IllegalStateException("Program terminated without a return statement")
    }

    private fun execute(stmt: Stmt) {
        when (stmt) {
            is Stmt.VariableDecl -> {
                val value = stmt.initializer?.let { evaluate(it) } ?: 0
                environment[stmt.name] = value
            }
            is Stmt.Assignment -> {
                if (!environment.containsKey(stmt.name)) {
                    throw IllegalArgumentException("Variable '${stmt.name}' not declared")
                }
                val value = evaluate(stmt.value)
                environment[stmt.name] = value
            }
            is Stmt.Return -> {
                returnValue = evaluate(stmt.value)
            }
        }
    }

    private fun evaluate(expr: Expr): Int {
        return when (expr) {
            is Expr.Literal -> expr.value
            is Expr.Variable -> environment[expr.name]
                ?: throw IllegalArgumentException("Variable '${expr.name}' not declared")
            is Expr.Unary -> {
                val right = evaluate(expr.right)
                when (expr.operator.type) {
                    TokenType.MINUS -> -right
                    else -> throw IllegalArgumentException("Unsupported unary operator ${expr.operator.type}")
                }
            }
            is Expr.Binary -> {
                val left = evaluate(expr.left)
                val right = evaluate(expr.right)
                when (expr.operator.type) {
                    TokenType.PLUS -> left + right
                    TokenType.MINUS -> left - right
                    TokenType.STAR -> left * right
                    TokenType.SLASH -> {
                        if (right == 0) throw ArithmeticException("Division by zero")
                        left / right
                    }
                    else -> throw IllegalArgumentException("Unsupported binary operator ${expr.operator.type}")
                }
            }
        }
    }
}