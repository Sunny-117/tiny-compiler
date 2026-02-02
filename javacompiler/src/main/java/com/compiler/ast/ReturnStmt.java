package com.compiler.ast;

public class ReturnStmt implements Statement {
    private final Expression value;
    private final int line;
    private final int column;

    public ReturnStmt(Expression value, int line, int column) {
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public Expression getValue() {
        return value;
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
