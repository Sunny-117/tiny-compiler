package com.compiler.ast;

public class BinaryExpr implements Expression {
    private final Expression left;
    private final BinaryOp operator;
    private final Expression right;
    private final int line;
    private final int column;
    private Type exprType;

    public BinaryExpr(Expression left, BinaryOp operator, Expression right, int line, int column) {
        this.left = left;
        this.operator = operator;
        this.right = right;
        this.line = line;
        this.column = column;
    }

    public Expression getLeft() {
        return left;
    }

    public BinaryOp getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
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

    public enum BinaryOp {
        ADD, SUB, MUL, DIV, MOD,
        EQ, NE, LT, GT, LE, GE,
        AND, OR
    }
}
