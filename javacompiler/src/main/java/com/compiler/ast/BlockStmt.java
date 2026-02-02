package com.compiler.ast;

import java.util.List;

public class BlockStmt implements Statement {
    private final List<Statement> statements;
    private final int line;
    private final int column;

    public BlockStmt(List<Statement> statements, int line, int column) {
        this.statements = statements;
        this.line = line;
        this.column = column;
    }

    public List<Statement> getStatements() {
        return statements;
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
