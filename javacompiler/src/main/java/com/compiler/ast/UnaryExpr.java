package com.compiler.ast;

public class UnaryExpr implements Expression {
    private final UnaryOp operator;
    private final Expression operand;
    private final int line;
    private final int column;
    private Type exprType;

    public UnaryExpr(UnaryOp operator, Expression operand, int line, int column) {
        this.operator = operator;
        this.operand = operand;
        this.line = line;
        this.column = column;
    }

    public UnaryOp getOperator() {
        return operator;
    }

    public Expression getOperand() {
        return operand;
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

    public enum UnaryOp {
        NEG, NOT
    }
}
