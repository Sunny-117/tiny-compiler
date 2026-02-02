package com.compiler.ast;

public class IfStmt implements Statement {
    private final Expression condition;
    private final Statement thenStmt;
    private final Statement elseStmt;
    private final int line;
    private final int column;

    public IfStmt(Expression condition, Statement thenStmt, Statement elseStmt, int line, int column) {
        this.condition = condition;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
        this.line = line;
        this.column = column;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getThenStmt() {
        return thenStmt;
    }

    public Statement getElseStmt() {
        return elseStmt;
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
