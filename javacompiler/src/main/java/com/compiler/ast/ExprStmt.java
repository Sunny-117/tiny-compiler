package com.compiler.ast;

public class ExprStmt implements Statement {
    private final Expression expression;
    private final int line;
    private final int column;

    public ExprStmt(Expression expression, int line, int column) {
        this.expression = expression;
        this.line = line;
        this.column = column;
    }

    public Expression getExpression() {
        return expression;
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
