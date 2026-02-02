package com.compiler.lexer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LexerTest {

    @Test
    public void testBasicTokens() {
        String source = "class MyClass { int x; }";
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        
        assertEquals(TokenType.CLASS, tokens.get(0).getType());
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).getType());
        assertEquals("MyClass", tokens.get(1).getValue());
        assertEquals(TokenType.LBRACE, tokens.get(2).getType());
        assertEquals(TokenType.INT, tokens.get(3).getType());
        assertEquals(TokenType.IDENTIFIER, tokens.get(4).getType());
        assertEquals("x", tokens.get(4).getValue());
        assertEquals(TokenType.SEMICOLON, tokens.get(5).getType());
        assertEquals(TokenType.RBRACE, tokens.get(6).getType());
    }

    @Test
    public void testNumbers() {
        String source = "42 123 0";
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        
        assertEquals(TokenType.NUMBER, tokens.get(0).getType());
        assertEquals("42", tokens.get(0).getValue());
        assertEquals(TokenType.NUMBER, tokens.get(1).getType());
        assertEquals("123", tokens.get(1).getValue());
        assertEquals(TokenType.NUMBER, tokens.get(2).getType());
        assertEquals("0", tokens.get(2).getValue());
    }

    @Test
    public void testStrings() {
        String source = "\"Hello, World!\"";
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        
        assertEquals(TokenType.STRING, tokens.get(0).getType());
        assertEquals("Hello, World!", tokens.get(0).getValue());
    }

    @Test
    public void testOperators() {
        String source = "+ - * / == != < > <= >= && ||";
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        
        assertEquals(TokenType.PLUS, tokens.get(0).getType());
        assertEquals(TokenType.MINUS, tokens.get(1).getType());
        assertEquals(TokenType.MULTIPLY, tokens.get(2).getType());
        assertEquals(TokenType.DIVIDE, tokens.get(3).getType());
        assertEquals(TokenType.EQUALS, tokens.get(4).getType());
        assertEquals(TokenType.NOT_EQUALS, tokens.get(5).getType());
        assertEquals(TokenType.LESS_THAN, tokens.get(6).getType());
        assertEquals(TokenType.GREATER_THAN, tokens.get(7).getType());
        assertEquals(TokenType.LESS_EQUAL, tokens.get(8).getType());
        assertEquals(TokenType.GREATER_EQUAL, tokens.get(9).getType());
        assertEquals(TokenType.AND, tokens.get(10).getType());
        assertEquals(TokenType.OR, tokens.get(11).getType());
    }

    @Test
    public void testComments() {
        String source = "// This is a comment\nint x; /* block comment */";
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        
        assertEquals(TokenType.INT, tokens.get(0).getType());
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).getType());
        assertEquals(TokenType.SEMICOLON, tokens.get(2).getType());
    }

    @Test
    public void testKeywords() {
        String source = "class if else while for return true false";
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        
        assertEquals(TokenType.CLASS, tokens.get(0).getType());
        assertEquals(TokenType.IF, tokens.get(1).getType());
        assertEquals(TokenType.ELSE, tokens.get(2).getType());
        assertEquals(TokenType.WHILE, tokens.get(3).getType());
        assertEquals(TokenType.FOR, tokens.get(4).getType());
        assertEquals(TokenType.RETURN, tokens.get(5).getType());
        assertEquals(TokenType.TRUE, tokens.get(6).getType());
        assertEquals(TokenType.FALSE, tokens.get(7).getType());
    }
}
