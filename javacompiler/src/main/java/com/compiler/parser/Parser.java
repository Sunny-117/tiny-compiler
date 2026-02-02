package com.compiler.parser;

import com.compiler.ast.*;
import com.compiler.ast.BinaryExpr.BinaryOp;
import com.compiler.ast.UnaryExpr.UnaryOp;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int position;
    private Token currentToken;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
        this.currentToken = tokens.isEmpty() ? null : tokens.get(0);
    }

    public Program parse() {
        List<ClassDecl> classes = new ArrayList<>();
        
        while (!isAtEnd()) {
            classes.add(parseClass());
        }
        
        return new Program(classes);
    }

    private ClassDecl parseClass() {
        int line = currentToken.getLine();
        int column = currentToken.getColumn();
        
        expect(TokenType.CLASS);
        String name = expect(TokenType.IDENTIFIER).getValue();
        expect(TokenType.LBRACE);
        
        List<FieldDecl> fields = new ArrayList<>();
        List<MethodDecl> methods = new ArrayList<>();
        
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            if (isType() && peek().getType() == TokenType.IDENTIFIER && peekAhead(2).getType() == TokenType.LPAREN) {
                methods.add(parseMethod());
            } else if (isType()) {
                fields.add(parseField());
            } else {
                throw new ParseException("Unexpected token: " + currentToken);
            }
        }
        
        expect(TokenType.RBRACE);
        
        return new ClassDecl(name, fields, methods, line, column);
    }

    private FieldDecl parseField() {
        int line = currentToken.getLine();
        int column = currentToken.getColumn();
        
        Type type = parseType();
        String name = expect(TokenType.IDENTIFIER).getValue();
        
        Expression initializer = null;
        if (match(TokenType.ASSIGN)) {
            initializer = parseExpression();
        }
        
        expect(TokenType.SEMICOLON);
        
        return new FieldDecl(name, type, initializer, line, column);
    }

    private MethodDecl parseMethod() {
        int line = currentToken.getLine();
        int column = currentToken.getColumn();
        
        Type returnType = parseType();
        String name = expect(TokenType.IDENTIFIER).getValue();
        
        expect(TokenType.LPAREN);
        List<Parameter> parameters = new ArrayList<>();
        
        if (!check(TokenType.RPAREN)) {
            do {
                int pLine = currentToken.getLine();
                int pColumn = currentToken.getColumn();
                Type paramType = parseType();
                String paramName = expect(TokenType.IDENTIFIER).getValue();
                parameters.add(new Parameter(paramName, paramType, pLine, pColumn));
            } while (match(TokenType.COMMA));
        }
        
        expect(TokenType.RPAREN);
        BlockStmt body = parseBlock();
        
        return new MethodDecl(name, returnType, parameters, body, line, column);
    }

    private BlockStmt parseBlock() {
        int line = currentToken.getLine();
        int column = currentToken.getColumn();
        
        expect(TokenType.LBRACE);
        List<Statement> statements = new ArrayList<>();
        
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            statements.add(parseStatement());
        }
        
        expect(TokenType.RBRACE);
        
        return new BlockStmt(statements, line, column);
    }

    private Statement parseStatement() {
        if (check(TokenType.IF)) {
            return parseIfStatement();
        } else if (check(TokenType.WHILE)) {
            return parseWhileStatement();
        } else if (check(TokenType.FOR)) {
            return parseForStatement();
        } else if (check(TokenType.RETURN)) {
            return parseReturnStatement();
        } else if (check(TokenType.LBRACE)) {
            return parseBlock();
        } else if (isType() && peek().getType() == TokenType.IDENTIFIER) {
            return parseVarDeclStatement();
        } else {
            return parseExpressionStatement();
        }
    }

    private Statement parseIfStatement() {
        int line = currentToken.getLine();
        int column = currentToken.getColumn();
        
        expect(TokenType.IF);
        expect(TokenType.LPAREN);
        Expression condition = parseExpression();
        expect(TokenType.RPAREN);
        
        Statement thenStmt = parseStatement();
        Statement elseStmt = null;
        
        if (match(TokenType.ELSE)) {
            elseStmt = parseStatement();
        }
        
        return new IfStmt(condition, thenStmt, elseStmt, line, column);
    }

    private Statement parseWhileStatement() {
        int line = currentToken.getLine();
        int column = currentToken.getColumn();
        
        expect(TokenType.WHILE);
        expect(TokenType.LPAREN);
        Expression condition = parseExpression();
        expect(TokenType.RPAREN);
        
        Statement body = parseStatement();
        
        return new WhileStmt(condition, body, line, column);
    }

    private Statement parseForStatement() {
        int line = currentToken.getLine();
        int column = currentToken.getColumn();
        
        expect(TokenType.FOR);
        expect(TokenType.LPAREN);
        
        Statement init = null;
        if (!check(TokenType.SEMICOLON)) {
            if (isType() && peek().getType() == TokenType.IDENTIFIER) {
                init = parseVarDeclStatement();
            } else {
                Expression expr = parseExpression();
                expect(TokenType.SEMICOLON);
                init = new ExprStmt(expr, expr.getLine(), expr.getColumn());
            }
        } else {
            advance();
        }
        
        Expression condition = null;
        if (!check(TokenType.SEMICOLON)) {
            condition = parseExpression();
        }
        expect(TokenType.SEMICOLON);
        
        Expression update = null;
        if (!check(TokenType.RPAREN)) {
            update = parseExpression();
        }
        expect(TokenType.RPAREN);
        
        Statement body = parseStatement();
        
        return new ForStmt(init, condition, update, body, line, column);
    }

    private Statement parseReturnStatement() {
        int line = currentToken.getLine();
        int column = currentToken.getColumn();
        
        expect(TokenType.RETURN);
        
        Expression value = null;
        if (!check(TokenType.SEMICOLON)) {
            value = parseExpression();
        }
        
        expect(TokenType.SEMICOLON);
        
        return new ReturnStmt(value, line, column);
    }

    private Statement parseVarDeclStatement() {
        int line = currentToken.getLine();
        int column = currentToken.getColumn();
        
        Type type = parseType();
        String name = expect(TokenType.IDENTIFIER).getValue();
        
        Expression initializer = null;
        if (match(TokenType.ASSIGN)) {
            initializer = parseExpression();
        }
        
        expect(TokenType.SEMICOLON);
        
        return new VarDeclStmt(name, type, initializer, line, column);
    }

    private Statement parseExpressionStatement() {
        int line = currentToken.getLine();
        int column = currentToken.getColumn();
        
        Expression expr = parseExpression();
        expect(TokenType.SEMICOLON);
        
        return new ExprStmt(expr, line, column);
    }

    private Expression parseExpression() {
        return parseAssignment();
    }

    private Expression parseAssignment() {
        Expression expr = parseLogicalOr();
        
        if (match(TokenType.ASSIGN)) {
            int line = currentToken.getLine();
            int column = currentToken.getColumn();
            Expression value = parseAssignment();
            return new AssignExpr(expr, value, line, column);
        }
        
        return expr;
    }

    private Expression parseLogicalOr() {
        Expression expr = parseLogicalAnd();
        
        while (match(TokenType.OR)) {
            int line = currentToken.getLine();
            int column = currentToken.getColumn();
            Expression right = parseLogicalAnd();
            expr = new BinaryExpr(expr, BinaryOp.OR, right, line, column);
        }
        
        return expr;
    }

    private Expression parseLogicalAnd() {
        Expression expr = parseEquality();
        
        while (match(TokenType.AND)) {
            int line = currentToken.getLine();
            int column = currentToken.getColumn();
            Expression right = parseEquality();
            expr = new BinaryExpr(expr, BinaryOp.AND, right, line, column);
        }
        
        return expr;
    }

    private Expression parseEquality() {
        Expression expr = parseRelational();
        
        while (true) {
            int line = currentToken.getLine();
            int column = currentToken.getColumn();
            
            if (match(TokenType.EQUALS)) {
                Expression right = parseRelational();
                expr = new BinaryExpr(expr, BinaryOp.EQ, right, line, column);
            } else if (match(TokenType.NOT_EQUALS)) {
                Expression right = parseRelational();
                expr = new BinaryExpr(expr, BinaryOp.NE, right, line, column);
            } else {
                break;
            }
        }
        
        return expr;
    }

    private Expression parseRelational() {
        Expression expr = parseAdditive();
        
        while (true) {
            int line = currentToken.getLine();
            int column = currentToken.getColumn();
            
            if (match(TokenType.LESS_THAN)) {
                Expression right = parseAdditive();
                expr = new BinaryExpr(expr, BinaryOp.LT, right, line, column);
            } else if (match(TokenType.GREATER_THAN)) {
                Expression right = parseAdditive();
                expr = new BinaryExpr(expr, BinaryOp.GT, right, line, column);
            } else if (match(TokenType.LESS_EQUAL)) {
                Expression right = parseAdditive();
                expr = new BinaryExpr(expr, BinaryOp.LE, right, line, column);
            } else if (match(TokenType.GREATER_EQUAL)) {
                Expression right = parseAdditive();
                expr = new BinaryExpr(expr, BinaryOp.GE, right, line, column);
            } else {
                break;
            }
        }
        
        return expr;
    }

    private Expression parseAdditive() {
        Expression expr = parseMultiplicative();
        
        while (true) {
            int line = currentToken.getLine();
            int column = currentToken.getColumn();
            
            if (match(TokenType.PLUS)) {
                Expression right = parseMultiplicative();
                expr = new BinaryExpr(expr, BinaryOp.ADD, right, line, column);
            } else if (match(TokenType.MINUS)) {
                Expression right = parseMultiplicative();
                expr = new BinaryExpr(expr, BinaryOp.SUB, right, line, column);
            } else {
                break;
            }
        }
        
        return expr;
    }

    private Expression parseMultiplicative() {
        Expression expr = parseUnary();
        
        while (true) {
            int line = currentToken.getLine();
            int column = currentToken.getColumn();
            
            if (match(TokenType.MULTIPLY)) {
                Expression right = parseUnary();
                expr = new BinaryExpr(expr, BinaryOp.MUL, right, line, column);
            } else if (match(TokenType.DIVIDE)) {
                Expression right = parseUnary();
                expr = new BinaryExpr(expr, BinaryOp.DIV, right, line, column);
            } else if (match(TokenType.MODULO)) {
                Expression right = parseUnary();
                expr = new BinaryExpr(expr, BinaryOp.MOD, right, line, column);
            } else {
                break;
            }
        }
        
        return expr;
    }

    private Expression parseUnary() {
        int line = currentToken.getLine();
        int column = currentToken.getColumn();
        
        if (match(TokenType.MINUS)) {
            Expression operand = parseUnary();
            return new UnaryExpr(UnaryOp.NEG, operand, line, column);
        } else if (match(TokenType.NOT)) {
            Expression operand = parseUnary();
            return new UnaryExpr(UnaryOp.NOT, operand, line, column);
        }
        
        return parsePostfix();
    }

    private Expression parsePostfix() {
        Expression expr = parsePrimary();
        
        while (true) {
            int line = currentToken.getLine();
            int column = currentToken.getColumn();
            
            if (match(TokenType.DOT)) {
                String fieldName = expect(TokenType.IDENTIFIER).getValue();
                
                if (match(TokenType.LPAREN)) {
                    List<Expression> arguments = parseArguments();
                    expect(TokenType.RPAREN);
                    expr = new CallExpr(expr, fieldName, arguments, line, column);
                } else {
                    expr = new FieldAccessExpr(expr, fieldName, line, column);
                }
            } else if (match(TokenType.LBRACKET)) {
                Expression index = parseExpression();
                expect(TokenType.RBRACKET);
                expr = new ArrayAccessExpr(expr, index, line, column);
            } else {
                break;
            }
        }
        
        return expr;
    }

    private Expression parsePrimary() {
        int line = currentToken.getLine();
        int column = currentToken.getColumn();
        
        if (match(TokenType.NUMBER)) {
            int value = Integer.parseInt(tokens.get(position - 1).getValue());
            return new IntLiteral(value, line, column);
        }
        
        if (match(TokenType.TRUE)) {
            return new BoolLiteral(true, line, column);
        }
        
        if (match(TokenType.FALSE)) {
            return new BoolLiteral(false, line, column);
        }
        
        if (match(TokenType.NULL)) {
            return new NullLiteral(line, column);
        }
        
        if (match(TokenType.STRING)) {
            String value = tokens.get(position - 1).getValue();
            return new StringLiteral(value, line, column);
        }
        
        if (match(TokenType.THIS)) {
            return new ThisExpr(line, column);
        }
        
        if (match(TokenType.NEW)) {
            return parseNewExpression();
        }
        
        if (match(TokenType.IDENTIFIER)) {
            String name = tokens.get(position - 1).getValue();
            
            if (match(TokenType.LPAREN)) {
                List<Expression> arguments = parseArguments();
                expect(TokenType.RPAREN);
                return new CallExpr(null, name, arguments, line, column);
            }
            
            return new IdentifierExpr(name, line, column);
        }
        
        if (match(TokenType.LPAREN)) {
            Expression expr = parseExpression();
            expect(TokenType.RPAREN);
            return expr;
        }
        
        throw new ParseException("Unexpected token: " + currentToken);
    }

    private Expression parseNewExpression() {
        int line = currentToken.getLine();
        int column = currentToken.getColumn();
        
        Type type = parseType();
        
        if (match(TokenType.LBRACKET)) {
            Expression size = parseExpression();
            expect(TokenType.RBRACKET);
            return new NewExpr(new Type(type.getName(), true), null, size, line, column);
        } else if (match(TokenType.LPAREN)) {
            List<Expression> arguments = parseArguments();
            expect(TokenType.RPAREN);
            return new NewExpr(type, arguments, null, line, column);
        }
        
        throw new ParseException("Expected '(' or '[' after 'new'");
    }

    private List<Expression> parseArguments() {
        List<Expression> arguments = new ArrayList<>();
        
        if (!check(TokenType.RPAREN)) {
            do {
                arguments.add(parseExpression());
            } while (match(TokenType.COMMA));
        }
        
        return arguments;
    }

    private Type parseType() {
        String typeName;
        
        if (match(TokenType.INT)) {
            typeName = "int";
        } else if (match(TokenType.BOOLEAN)) {
            typeName = "boolean";
        } else if (match(TokenType.VOID)) {
            typeName = "void";
        } else if (match(TokenType.IDENTIFIER)) {
            typeName = tokens.get(position - 1).getValue();
        } else {
            throw new ParseException("Expected type, got: " + currentToken);
        }
        
        boolean isArray = match(TokenType.LBRACKET);
        if (isArray) {
            expect(TokenType.RBRACKET);
        }
        
        return new Type(typeName, isArray);
    }

    private boolean isType() {
        return check(TokenType.INT) || check(TokenType.BOOLEAN) || 
               check(TokenType.VOID) || check(TokenType.IDENTIFIER);
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return currentToken.getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            position++;
            currentToken = position < tokens.size() ? tokens.get(position) : null;
        }
        return tokens.get(position - 1);
    }

    private Token expect(TokenType type) {
        if (check(type)) {
            return advance();
        }
        throw new ParseException("Expected " + type + ", got: " + currentToken);
    }

    private Token peek() {
        return position + 1 < tokens.size() ? tokens.get(position + 1) : tokens.get(tokens.size() - 1);
    }

    private Token peekAhead(int offset) {
        int pos = position + offset;
        return pos < tokens.size() ? tokens.get(pos) : tokens.get(tokens.size() - 1);
    }

    private boolean isAtEnd() {
        return currentToken == null || currentToken.getType() == TokenType.EOF;
    }
}
