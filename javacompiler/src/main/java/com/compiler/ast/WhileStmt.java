package com.compiler.ast;

public class WhileStmt implements Statement {
    private final Expression condition;
    private final Statement body;
    private final int line;
    private final int column;

    public WhileStmt(Expression condition, Statement body, int line, int column) {
        this.condition = condition;
        this.body = body;
        this.line = line;
        this.column = column;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getBody() {
        return body;
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
