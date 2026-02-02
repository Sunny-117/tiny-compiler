package com.compiler.ast;

import java.util.List;

public class MethodDecl implements ASTNode {
    private final String name;
    private final Type returnType;
    private final List<Parameter> parameters;
    private final BlockStmt body;
    private final int line;
    private final int column;

    public MethodDecl(String name, Type returnType, List<Parameter> parameters, BlockStmt body, int line, int column) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        this.body = body;
        this.line = line;
        this.column = column;
    }

    public String getName() {
        return name;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public BlockStmt getBody() {
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
