package com.compiler.ast;

public class BoolLiteral implements Expression {
    private final boolean value;
    private final int line;
    private final int column;
    private Type exprType = new Type("boolean");

    public BoolLiteral(boolean value, int line, int column) {
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public boolean getValue() {
        return value;
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
