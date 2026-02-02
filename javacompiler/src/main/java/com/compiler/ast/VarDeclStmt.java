package com.compiler.ast;

public class VarDeclStmt implements Statement {
    private final String name;
    private final Type type;
    private final Expression initializer;
    private final int line;
    private final int column;

    public VarDeclStmt(String name, Type type, Expression initializer, int line, int column) {
        this.name = name;
        this.type = type;
        this.initializer = initializer;
        this.line = line;
        this.column = column;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Expression getInitializer() {
        return initializer;
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
