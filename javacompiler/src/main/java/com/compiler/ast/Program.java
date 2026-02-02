package com.compiler.ast;

import java.util.List;

public class Program implements ASTNode {
    private final List<ClassDecl> classes;

    public Program(List<ClassDecl> classes) {
        this.classes = classes;
    }

    public List<ClassDecl> getClasses() {
        return classes;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int getLine() {
        return classes.isEmpty() ? 0 : classes.get(0).getLine();
    }

    @Override
    public int getColumn() {
        return classes.isEmpty() ? 0 : classes.get(0).getColumn();
    }
}
