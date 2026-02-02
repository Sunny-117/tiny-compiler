package com.compiler.ast;

public class FieldAccessExpr implements Expression {
    private final Expression object;
    private final String fieldName;
    private final int line;
    private final int column;
    private Type exprType;

    public FieldAccessExpr(Expression object, String fieldName, int line, int column) {
        this.object = object;
        this.fieldName = fieldName;
        this.line = line;
        this.column = column;
    }

    public Expression getObject() {
        return object;
    }

    public String getFieldName() {
        return fieldName;
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
