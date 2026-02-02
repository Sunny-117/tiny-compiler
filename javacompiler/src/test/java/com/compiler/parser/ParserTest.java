package com.compiler.parser;

import com.compiler.ast.*;
import com.compiler.lexer.Lexer;
import com.compiler.lexer.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    public void testSimpleClass() {
        String source = "class MyClass { }";
        Program program = parse(source);
        
        assertEquals(1, program.getClasses().size());
        assertEquals("MyClass", program.getClasses().get(0).getName());
    }

    @Test
    public void testClassWithField() {
        String source = "class MyClass { int x; }";
        Program program = parse(source);
        
        ClassDecl classDecl = program.getClasses().get(0);
        assertEquals(1, classDecl.getFields().size());
        
        FieldDecl field = classDecl.getFields().get(0);
        assertEquals("x", field.getName());
        assertEquals("int", field.getType().getName());
    }

    @Test
    public void testClassWithMethod() {
        String source = "class MyClass { int add(int a, int b) { return a + b; } }";
        Program program = parse(source);
        
        ClassDecl classDecl = program.getClasses().get(0);
        assertEquals(1, classDecl.getMethods().size());
        
        MethodDecl method = classDecl.getMethods().get(0);
        assertEquals("add", method.getName());
        assertEquals("int", method.getReturnType().getName());
        assertEquals(2, method.getParameters().size());
    }

    @Test
    public void testIfStatement() {
        String source = "class Test { void test() { if (x > 0) { return; } } }";
        Program program = parse(source);
        
        MethodDecl method = program.getClasses().get(0).getMethods().get(0);
        BlockStmt body = method.getBody();
        
        assertTrue(body.getStatements().get(0) instanceof IfStmt);
    }

    @Test
    public void testWhileStatement() {
        String source = "class Test { void test() { while (x < 10) { x = x + 1; } } }";
        Program program = parse(source);
        
        MethodDecl method = program.getClasses().get(0).getMethods().get(0);
        BlockStmt body = method.getBody();
        
        assertTrue(body.getStatements().get(0) instanceof WhileStmt);
    }

    @Test
    public void testForStatement() {
        String source = "class Test { void test() { for (int i = 0; i < 10; i = i + 1) { } } }";
        Program program = parse(source);
        
        MethodDecl method = program.getClasses().get(0).getMethods().get(0);
        BlockStmt body = method.getBody();
        
        assertTrue(body.getStatements().get(0) instanceof ForStmt);
    }

    @Test
    public void testBinaryExpression() {
        String source = "class Test { void test() { int x; x = 1 + 2 * 3; } }";
        Program program = parse(source);
        
        MethodDecl method = program.getClasses().get(0).getMethods().get(0);
        BlockStmt body = method.getBody();
        
        ExprStmt exprStmt = (ExprStmt) body.getStatements().get(1);
        assertTrue(exprStmt.getExpression() instanceof AssignExpr);
    }

    private Program parse(String source) {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);
        return parser.parse();
    }
}
