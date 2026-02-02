package com.compiler.ast;

public interface Expression extends ASTNode {
    Type getExprType();
    void setExprType(Type type);
}
