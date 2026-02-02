package com.compiler.ast;

import java.util.List;

public class ClassDecl implements ASTNode {
    private final String name;
    private final List<FieldDecl> fields;
    private final List<MethodDecl> methods;
    private final int line;
    private final int column;

    public ClassDecl(String name, List<FieldDecl> fields, List<MethodDecl> methods, int line, int column) {
        this.name = name;
        this.fields = fields;
        this.methods = methods;
        this.line = line;
        this.column = column;
    }

    public String getName() {
        return name;
    }

    public List<FieldDecl> getFields() {
        return fields;
    }

    public List<MethodDecl> getMethods() {
        return methods;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getColumn() {
        return column;
    }
}
