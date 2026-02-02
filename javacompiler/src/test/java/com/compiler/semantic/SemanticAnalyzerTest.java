package com.compiler.semantic;

import com.compiler.ast.Program;
import com.compiler.lexer.Lexer;
import com.compiler.lexer.Token;
import com.compiler.parser.Parser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SemanticAnalyzerTest {

    @Test
    public void testValidProgram() {
        String source = "class Test { int x; int getX() { return x; } }";
        Program program = parse(source);
        
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        assertDoesNotThrow(() -> analyzer.analyze(program));
    }

    @Test
    public void testUndefinedVariable() {
        String source = "class Test { void test() { int x; y = 5; } }";
        Program program = parse(source);
        
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        assertThrows(SemanticException.class, () -> analyzer.analyze(program));
    }

    @Test
    public void testTypeMismatch() {
        String source = "class Test { void test() { int x; x = true; } }";
        Program program = parse(source);
        
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        assertThrows(SemanticException.class, () -> analyzer.analyze(program));
    }

    @Test
    public void testDuplicateVariable() {
        String source = "class Test { void test() { int x; int x; } }";
        Program program = parse(source);
        
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        assertThrows(SemanticException.class, () -> analyzer.analyze(program));
    }

    @Test
    public void testReturnTypeMismatch() {
        String source = "class Test { int test() { return true; } }";
        Program program = parse(source);
        
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        assertThrows(SemanticException.class, () -> analyzer.analyze(program));
    }

    @Test
    public void testValidArithmetic() {
        String source = "class Test { int test() { int x; int y; return x + y * 2; } }";
        Program program = parse(source);
        
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        assertDoesNotThrow(() -> analyzer.analyze(program));
    }

    private Program parse(String source) {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);
        return parser.parse();
    }
}
