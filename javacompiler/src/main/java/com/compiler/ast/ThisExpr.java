package com.compiler.ast;

public class ThisExpr implements Expression {
    private final int line;
    private final int column;
    private Type exprType;

    public ThisExpr(int line, int column) {
        this.line = line;
        this.column = column;
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
