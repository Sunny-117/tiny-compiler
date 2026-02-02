package com.compiler.ast;

public class ForStmt implements Statement {
    private final Statement init;
    private final Expression condition;
    private final Expression update;
    private final Statement body;
    private final int line;
    private final int column;

    public ForStmt(Statement init, Expression condition, Expression update, Statement body, int line, int column) {
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.body = body;
        this.line = line;
        this.column = column;
    }

    public Statement getInit() {
        return init;
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getUpdate() {
        return update;
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
