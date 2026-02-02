package com.compiler.ast;

public class ArrayAccessExpr implements Expression {
    private final Expression array;
    private final Expression index;
    private final int line;
    private final int column;
    private Type exprType;

    public ArrayAccessExpr(Expression array, Expression index, int line, int column) {
        this.array = array;
        this.index = index;
        this.line = line;
        this.column = column;
    }

    public Expression getArray() {
        return array;
    }

    public Expression getIndex() {
        return index;
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
