package com.compiler.ast;

import java.util.List;

public class CallExpr implements Expression {
    private final Expression callee;
    private final String methodName;
    private final List<Expression> arguments;
    private final int line;
    private final int column;
    private Type exprType;

    public CallExpr(Expression callee, String methodName, List<Expression> arguments, int line, int column) {
        this.callee = callee;
        this.methodName = methodName;
        this.arguments = arguments;
        this.line = line;
        this.column = column;
    }

    public Expression getCallee() {
        return callee;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<Expression> getArguments() {
        return arguments;
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
