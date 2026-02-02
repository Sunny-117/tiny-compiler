package com.compiler.ast;

public interface ASTVisitor<T> {
    T visit(Program program);
    T visit(ClassDecl classDecl);
    T visit(FieldDecl fieldDecl);
    T visit(MethodDecl methodDecl);
    T visit(Parameter parameter);
    T visit(BlockStmt blockStmt);
    T visit(VarDeclStmt varDeclStmt);
    T visit(IfStmt ifStmt);
    T visit(WhileStmt whileStmt);
    T visit(ForStmt forStmt);
    T visit(ReturnStmt returnStmt);
    T visit(ExprStmt exprStmt);
    T visit(BinaryExpr binaryExpr);
    T visit(UnaryExpr unaryExpr);
    T visit(AssignExpr assignExpr);
    T visit(CallExpr callExpr);
    T visit(FieldAccessExpr fieldAccessExpr);
    T visit(ArrayAccessExpr arrayAccessExpr);
    T visit(NewExpr newExpr);
    T visit(IntLiteral intLiteral);
    T visit(BoolLiteral boolLiteral);
    T visit(StringLiteral stringLiteral);
    T visit(NullLiteral nullLiteral);
    T visit(IdentifierExpr identifierExpr);
    T visit(ThisExpr thisExpr);
}
