package com.compiler.ast;

import java.util.List;

public class NewExpr implements Expression {
    private final Type type;
    private final List<Expression> arguments;
    private final Expression arraySize;
    private final int line;
    private final int column;
    private Type exprType;

    public NewExpr(Type type, List<Expression> arguments, Expression arraySize, int line, int column) {
        this.type = type;
        this.arguments = arguments;
        this.arraySize = arraySize;
        this.line = line;
        this.column = column;
        this.exprType = type;
    }

    public Type getType() {
        return type;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public Expression getArraySize() {
        return arraySize;
    }

    public boolean isArrayCreation() {
        return arraySize != null;
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
