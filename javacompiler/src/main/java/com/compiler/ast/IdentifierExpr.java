package com.compiler.ast;

public class IdentifierExpr implements Expression {
    private final String name;
    private final int line;
    private final int column;
    private Type exprType;

    public IdentifierExpr(String name, int line, int column) {
        this.name = name;
        this.line = line;
        this.column = column;
    }

    public String getName() {
        return name;
    }

    @Override
    public Type getExprType() {
        return exprType;
    }

    @Override
    public void setExprType(Type type) {
        this.exprType = type;
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
