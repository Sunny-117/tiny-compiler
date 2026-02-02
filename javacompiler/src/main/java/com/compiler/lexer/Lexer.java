package com.compiler.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    private final String source;
    private int position;
    private int line;
    private int column;
    private char currentChar;
    
    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
    
    static {
        KEYWORDS.put("class", TokenType.CLASS);
        KEYWORDS.put("public", TokenType.PUBLIC);
        KEYWORDS.put("private", TokenType.PRIVATE);
        KEYWORDS.put("static", TokenType.STATIC);
        KEYWORDS.put("void", TokenType.VOID);
        KEYWORDS.put("int", TokenType.INT);
        KEYWORDS.put("boolean", TokenType.BOOLEAN);
        KEYWORDS.put("if", TokenType.IF);
        KEYWORDS.put("else", TokenType.ELSE);
        KEYWORDS.put("while", TokenType.WHILE);
        KEYWORDS.put("for", TokenType.FOR);
        KEYWORDS.put("return", TokenType.RETURN);
        KEYWORDS.put("new", TokenType.NEW);
        KEYWORDS.put("this", TokenType.THIS);
        KEYWORDS.put("true", TokenType.TRUE);
        KEYWORDS.put("false", TokenType.FALSE);
        KEYWORDS.put("null", TokenType.NULL);
    }

    public Lexer(String source) {
        this.source = source;
        this.position = 0;
        this.line = 1;
        this.column = 1;
        this.currentChar = source.isEmpty() ? '\0' : source.charAt(0);
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token token;
        
        while ((token = nextToken()).getType() != TokenType.EOF) {
            tokens.add(token);
        }
        tokens.add(token); // Add EOF token
        
        return tokens;
    }

    public Token nextToken() {
        while (currentChar != '\0') {
            if (Character.isWhitespace(currentChar)) {
                skipWhitespace();
                continue;
            }
            
            if (currentChar == '/' && peek() == '/') {
                skipLineComment();
                continue;
            }
            
            if (currentChar == '/' && peek() == '*') {
                skipBlockComment();
                continue;
            }
            
            if (Character.isLetter(currentChar) || currentChar == '_') {
                return identifier();
            }
            
            if (Character.isDigit(currentChar)) {
                return number();
            }
            
            if (currentChar == '"') {
                return string();
            }
            
            return operator();
        }
        
        return new Token(TokenType.EOF, "", line, column);
    }

    private void advance() {
        if (currentChar == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        
        position++;
        currentChar = position < source.length() ? source.charAt(position) : '\0';
    }

    private char peek() {
        int nextPos = position + 1;
        return nextPos < source.length() ? source.charAt(nextPos) : '\0';
    }

    private void skipWhitespace() {
        while (currentChar != '\0' && Character.isWhitespace(currentChar)) {
            advance();
        }
    }

    private void skipLineComment() {
        while (currentChar != '\0' && currentChar != '\n') {
            advance();
        }
    }

    private void skipBlockComment() {
        advance(); // skip '/'
        advance(); // skip '*'
        
        while (currentChar != '\0') {
            if (currentChar == '*' && peek() == '/') {
                advance(); // skip '*'
                advance(); // skip '/'
                break;
            }
            advance();
        }
    }

    private Token identifier() {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();
        
        while (currentChar != '\0' && (Character.isLetterOrDigit(currentChar) || currentChar == '_')) {
            sb.append(currentChar);
            advance();
        }
        
        String value = sb.toString();
        TokenType type = KEYWORDS.getOrDefault(value, TokenType.IDENTIFIER);
        
        return new Token(type, value, startLine, startColumn);
    }

    private Token number() {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();
        
        while (currentChar != '\0' && Character.isDigit(currentChar)) {
            sb.append(currentChar);
            advance();
        }
        
        return new Token(TokenType.NUMBER, sb.toString(), startLine, startColumn);
    }

    private Token string() {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();
        
        advance(); // skip opening quote
        
        while (currentChar != '\0' && currentChar != '"') {
            if (currentChar == '\\') {
                advance();
                if (currentChar != '\0') {
                    switch (currentChar) {
                        case 'n': sb.append('\n'); break;
                        case 't': sb.append('\t'); break;
                        case 'r': sb.append('\r'); break;
                        case '\\': sb.append('\\'); break;
                        case '"': sb.append('"'); break;
                        default: sb.append(currentChar);
                    }
                    advance();
                }
            } else {
                sb.append(currentChar);
                advance();
            }
        }
        
        if (currentChar == '"') {
            advance(); // skip closing quote
        }
        
        return new Token(TokenType.STRING, sb.toString(), startLine, startColumn);
    }

    private Token operator() {
        int startLine = line;
        int startColumn = column;
        char ch = currentChar;
        
        advance();
        
        switch (ch) {
            case '+':
                if (currentChar == '+') {
                    advance();
                    return new Token(TokenType.INCREMENT, "++", startLine, startColumn);
                }
                return new Token(TokenType.PLUS, "+", startLine, startColumn);
            case '-':
                if (currentChar == '-') {
                    advance();
                    return new Token(TokenType.DECREMENT, "--", startLine, startColumn);
                }
                return new Token(TokenType.MINUS, "-", startLine, startColumn);
            case '*':
                return new Token(TokenType.MULTIPLY, "*", startLine, startColumn);
            case '/':
                return new Token(TokenType.DIVIDE, "/", startLine, startColumn);
            case '%':
                return new Token(TokenType.MODULO, "%", startLine, startColumn);
            case '=':
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.EQUALS, "==", startLine, startColumn);
                }
                return new Token(TokenType.ASSIGN, "=", startLine, startColumn);
            case '!':
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.NOT_EQUALS, "!=", startLine, startColumn);
                }
                return new Token(TokenType.NOT, "!", startLine, startColumn);
            case '<':
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.LESS_EQUAL, "<=", startLine, startColumn);
                }
                return new Token(TokenType.LESS_THAN, "<", startLine, startColumn);
            case '>':
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.GREATER_EQUAL, ">=", startLine, startColumn);
                }
                return new Token(TokenType.GREATER_THAN, ">", startLine, startColumn);
            case '&':
                if (currentChar == '&') {
                    advance();
                    return new Token(TokenType.AND, "&&", startLine, startColumn);
                }
                break;
            case '|':
                if (currentChar == '|') {
                    advance();
                    return new Token(TokenType.OR, "||", startLine, startColumn);
                }
                break;
            case '(':
                return new Token(TokenType.LPAREN, "(", startLine, startColumn);
            case ')':
                return new Token(TokenType.RPAREN, ")", startLine, startColumn);
            case '{':
                return new Token(TokenType.LBRACE, "{", startLine, startColumn);
            case '}':
                return new Token(TokenType.RBRACE, "}", startLine, startColumn);
            case '[':
                return new Token(TokenType.LBRACKET, "[", startLine, startColumn);
            case ']':
                return new Token(TokenType.RBRACKET, "]", startLine, startColumn);
            case ';':
                return new Token(TokenType.SEMICOLON, ";", startLine, startColumn);
            case ',':
                return new Token(TokenType.COMMA, ",", startLine, startColumn);
            case '.':
                return new Token(TokenType.DOT, ".", startLine, startColumn);
        }
        
        return new Token(TokenType.UNKNOWN, String.valueOf(ch), startLine, startColumn);
    }
}
