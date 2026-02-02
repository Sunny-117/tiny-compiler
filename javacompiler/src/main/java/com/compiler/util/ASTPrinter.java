package com.compiler.util;

import com.compiler.ast.*;

public class ASTPrinter implements ASTVisitor<Void> {
    private int indent = 0;

    public void print(Program program) {
        program.accept(this);
    }

    private void println(String text) {
        for (int i = 0; i < indent; i++) {
            System.out.print("  ");
        }
        System.out.println(text);
    }

    @Override
    public Void visit(Program program) {
        println("Program");
        indent++;
        for (ClassDecl classDecl : program.getClasses()) {
            classDecl.accept(this);
        }
        indent--;
        return null;
    }

    @Override
    public Void visit(ClassDecl classDecl) {
        println("ClassDecl: " + classDecl.getName());
        indent++;
        
        if (!classDecl.getFields().isEmpty()) {
            println("Fields:");
            indent++;
            for (FieldDecl field : classDecl.getFields()) {
                field.accept(this);
            }
            indent--;
        }
        
        if (!classDecl.getMethods().isEmpty()) {
            println("Methods:");
            indent++;
            for (MethodDecl method : classDecl.getMethods()) {
                method.accept(this);
            }
            indent--;
        }
        
        indent--;
        return null;
    }

    @Override
    public Void visit(FieldDecl fieldDecl) {
        println("FieldDecl: " + fieldDecl.getType() + " " + fieldDecl.getName());
        if (fieldDecl.getInitializer() != null) {
            indent++;
            println("Initializer:");
            indent++;
            fieldDecl.getInitializer().accept(this);
            indent -= 2;
        }
        return null;
    }

    @Override
    public Void visit(MethodDecl methodDecl) {
        println("MethodDecl: " + methodDecl.getReturnType() + " " + methodDecl.getName());
        indent++;
        
        if (!methodDecl.getParameters().isEmpty()) {
            println("Parameters:");
            indent++;
            for (Parameter param : methodDecl.getParameters()) {
                param.accept(this);
            }
            indent--;
        }
        
        println("Body:");
        indent++;
        methodDecl.getBody().accept(this);
        indent--;
        
        indent--;
        return null;
    }

    @Override
    public Void visit(Parameter parameter) {
        println("Parameter: " + parameter.getType() + " " + parameter.getName());
        return null;
    }

    @Override
    public Void visit(BlockStmt blockStmt) {
        println("BlockStmt");
        indent++;
        for (Statement stmt : blockStmt.getStatements()) {
            stmt.accept(this);
        }
        indent--;
        return null;
    }

    @Override
    public Void visit(VarDeclStmt varDeclStmt) {
        println("VarDeclStmt: " + varDeclStmt.getType() + " " + varDeclStmt.getName());
        if (varDeclStmt.getInitializer() != null) {
            indent++;
            varDeclStmt.getInitializer().accept(this);
            indent--;
        }
        return null;
    }

    @Override
    public Void visit(IfStmt ifStmt) {
        println("IfStmt");
        indent++;
        
        println("Condition:");
        indent++;
        ifStmt.getCondition().accept(this);
        indent--;
        
        println("Then:");
        indent++;
        ifStmt.getThenStmt().accept(this);
        indent--;
        
        if (ifStmt.getElseStmt() != null) {
            println("Else:");
            indent++;
            ifStmt.getElseStmt().accept(this);
            indent--;
        }
        
        indent--;
        return null;
    }

    @Override
    public Void visit(WhileStmt whileStmt) {
        println("WhileStmt");
        indent++;
        
        println("Condition:");
        indent++;
        whileStmt.getCondition().accept(this);
        indent--;
        
        println("Body:");
        indent++;
        whileStmt.getBody().accept(this);
        indent--;
        
        indent--;
        return null;
    }

    @Override
    public Void visit(ForStmt forStmt) {
        println("ForStmt");
        indent++;
        
        if (forStmt.getInit() != null) {
            println("Init:");
            indent++;
            forStmt.getInit().accept(this);
            indent--;
        }
        
        if (forStmt.getCondition() != null) {
            println("Condition:");
            indent++;
            forStmt.getCondition().accept(this);
            indent--;
        }
        
        if (forStmt.getUpdate() != null) {
            println("Update:");
            indent++;
            forStmt.getUpdate().accept(this);
            indent--;
        }
        
        println("Body:");
        indent++;
        forStmt.getBody().accept(this);
        indent--;
        
        indent--;
        return null;
    }

    @Override
    public Void visit(ReturnStmt returnStmt) {
        println("ReturnStmt");
        if (returnStmt.getValue() != null) {
            indent++;
            returnStmt.getValue().accept(this);
            indent--;
        }
        return null;
    }

    @Override
    public Void visit(ExprStmt exprStmt) {
        println("ExprStmt");
        indent++;
        exprStmt.getExpression().accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(BinaryExpr binaryExpr) {
        println("BinaryExpr: " + binaryExpr.getOperator());
        indent++;
        binaryExpr.getLeft().accept(this);
        binaryExpr.getRight().accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(UnaryExpr unaryExpr) {
        println("UnaryExpr: " + unaryExpr.getOperator());
        indent++;
        unaryExpr.getOperand().accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(AssignExpr assignExpr) {
        println("AssignExpr");
        indent++;
        println("Target:");
        indent++;
        assignExpr.getTarget().accept(this);
        indent--;
        println("Value:");
        indent++;
        assignExpr.getValue().accept(this);
        indent--;
        indent--;
        return null;
    }

    @Override
    public Void visit(CallExpr callExpr) {
        println("CallExpr: " + callExpr.getMethodName());
        indent++;
        if (callExpr.getCallee() != null) {
            println("Callee:");
            indent++;
            callExpr.getCallee().accept(this);
            indent--;
        }
        if (!callExpr.getArguments().isEmpty()) {
            println("Arguments:");
            indent++;
            for (Expression arg : callExpr.getArguments()) {
                arg.accept(this);
            }
            indent--;
        }
        indent--;
        return null;
    }

    @Override
    public Void visit(FieldAccessExpr fieldAccessExpr) {
        println("FieldAccessExpr: " + fieldAccessExpr.getFieldName());
        indent++;
        fieldAccessExpr.getObject().accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(ArrayAccessExpr arrayAccessExpr) {
        println("ArrayAccessExpr");
        indent++;
        println("Array:");
        indent++;
        arrayAccessExpr.getArray().accept(this);
        indent--;
        println("Index:");
        indent++;
        arrayAccessExpr.getIndex().accept(this);
        indent--;
        indent--;
        return null;
    }

    @Override
    public Void visit(NewExpr newExpr) {
        println("NewExpr: " + newExpr.getType());
        indent++;
        if (newExpr.isArrayCreation()) {
            println("ArraySize:");
            indent++;
            newExpr.getArraySize().accept(this);
            indent--;
        }
        indent--;
        return null;
    }

    @Override
    public Void visit(IntLiteral intLiteral) {
        println("IntLiteral: " + intLiteral.getValue());
        return null;
    }

    @Override
    public Void visit(BoolLiteral boolLiteral) {
        println("BoolLiteral: " + boolLiteral.getValue());
        return null;
    }

    @Override
    public Void visit(StringLiteral stringLiteral) {
        println("StringLiteral: \"" + stringLiteral.getValue() + "\"");
        return null;
    }

    @Override
    public Void visit(NullLiteral nullLiteral) {
        println("NullLiteral");
        return null;
    }

    @Override
    public Void visit(IdentifierExpr identifierExpr) {
        println("IdentifierExpr: " + identifierExpr.getName());
        return null;
    }

    @Override
    public Void visit(ThisExpr thisExpr) {
        println("ThisExpr");
        return null;
    }
}
