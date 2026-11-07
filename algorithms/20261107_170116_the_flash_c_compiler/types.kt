package main

enum class TokenType {
    // Single-character tokens
    LEFT_PAREN, RIGHT_PAREN, PLUS, MINUS, STAR, SLASH, SEMICOLON, EQUAL,

    // Literals
    IDENTIFIER, NUMBER,

    // Keywords
    INT, RETURN,

    EOF
}

data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any?,
    val position: Int
)

sealed class Expr {
    data class Literal(val value: Int) : Expr()
    data class Variable(val name: String) : Expr()
    data class Unary(val operator: Token, val right: Expr) : Expr()
    data class Binary(val left: Expr, val operator: Token, val right: Expr) : Expr()
}

sealed class Stmt {
    data class VariableDecl(val name: String, val initializer: Expr?) : Stmt()
    data class Assignment(val name: String, val value: Expr) : Stmt()
    data class Return(val value: Expr) : Stmt()
}