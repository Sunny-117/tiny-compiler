package com.compiler.ast;

public class AssignExpr implements Expression {
    private final Expression target;
    private final Expression value;
    private final int line;
    private final int column;
    private Type exprType;

    public AssignExpr(Expression target, Expression value, int line, int column) {
        this.target = target;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public Expression getTarget() {
        return target;
    }

    public Expression getValue() {
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
