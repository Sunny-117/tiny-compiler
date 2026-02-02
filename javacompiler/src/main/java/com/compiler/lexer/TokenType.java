package com.compiler.lexer;

public enum TokenType {
    // Keywords
    CLASS, PUBLIC, PRIVATE, STATIC, VOID, INT, BOOLEAN, IF, ELSE, WHILE, FOR, RETURN, NEW, THIS,
    
    // Literals
    NUMBER, STRING, TRUE, FALSE, NULL,
    
    // Identifiers
    IDENTIFIER,
    
    // Operators
    PLUS, MINUS, MULTIPLY, DIVIDE, MODULO,
    ASSIGN, EQUALS, NOT_EQUALS, LESS_THAN, GREATER_THAN, LESS_EQUAL, GREATER_EQUAL,
    AND, OR, NOT,
    INCREMENT, DECREMENT,
    
    // Delimiters
    LPAREN, RPAREN, LBRACE, RBRACE, LBRACKET, RBRACKET,
    SEMICOLON, COMMA, DOT,
    
    // Special
    EOF, UNKNOWN
}
