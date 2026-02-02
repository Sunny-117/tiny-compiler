package com.compiler.ir;

import com.compiler.ast.*;

import java.util.ArrayList;
import java.util.List;

public class IRGenerator implements ASTVisitor<String> {
    private final List<String> instructions;
    private int tempCounter;
    private int labelCounter;

    public IRGenerator() {
        this.instructions = new ArrayList<>();
        this.tempCounter = 0;
        this.labelCounter = 0;
    }

    public List<String> generate(Program program) {
        program.accept(this);
        return instructions;
    }

    private String newTemp() {
        return "t" + (tempCounter++);
    }

    private String newLabel() {
        return "L" + (labelCounter++);
    }

    private void emit(String instruction) {
        instructions.add(instruction);
    }

    @Override
    public String visit(Program program) {
        for (ClassDecl classDecl : program.getClasses()) {
            classDecl.accept(this);
        }
        return null;
    }

    @Override
    public String visit(ClassDecl classDecl) {
        emit("CLASS " + classDecl.getName());
        
        for (FieldDecl field : classDecl.getFields()) {
            field.accept(this);
        }
        
        for (MethodDecl method : classDecl.getMethods()) {
            method.accept(this);
        }
        
        emit("END_CLASS");
        return null;
    }

    @Override
    public String visit(FieldDecl fieldDecl) {
        emit("FIELD " + fieldDecl.getType() + " " + fieldDecl.getName());
        return null;
    }

    @Override
    public String visit(MethodDecl methodDecl) {
        emit("METHOD " + methodDecl.getReturnType() + " " + methodDecl.getName());
        
        for (Parameter param : methodDecl.getParameters()) {
            emit("PARAM " + param.getType() + " " + param.getName());
        }
        
        methodDecl.getBody().accept(this);
        emit("END_METHOD");
        return null;
    }

    @Override
    public String visit(Parameter parameter) {
        return parameter.getName();
    }

    @Override
    public String visit(BlockStmt blockStmt) {
        for (Statement stmt : blockStmt.getStatements()) {
            stmt.accept(this);
        }
        return null;
    }

    @Override
    public String visit(VarDeclStmt varDeclStmt) {
        if (varDeclStmt.getInitializer() != null) {
            String value = varDeclStmt.getInitializer().accept(this);
            emit(varDeclStmt.getName() + " = " + value);
        } else {
            emit("DECLARE " + varDeclStmt.getType() + " " + varDeclStmt.getName());
        }
        return null;
    }

    @Override
    public String visit(IfStmt ifStmt) {
        String condition = ifStmt.getCondition().accept(this);
        String elseLabel = newLabel();
        String endLabel = newLabel();
        
        emit("IF_FALSE " + condition + " GOTO " + elseLabel);
        ifStmt.getThenStmt().accept(this);
        emit("GOTO " + endLabel);
        
        emit(elseLabel + ":");
        if (ifStmt.getElseStmt() != null) {
            ifStmt.getElseStmt().accept(this);
        }
        
        emit(endLabel + ":");
        return null;
    }

    @Override
    public String visit(WhileStmt whileStmt) {
        String startLabel = newLabel();
        String endLabel = newLabel();
        
        emit(startLabel + ":");
        String condition = whileStmt.getCondition().accept(this);
        emit("IF_FALSE " + condition + " GOTO " + endLabel);
        
        whileStmt.getBody().accept(this);
        emit("GOTO " + startLabel);
        
        emit(endLabel + ":");
        return null;
    }

    @Override
    public String visit(ForStmt forStmt) {
        if (forStmt.getInit() != null) {
            forStmt.getInit().accept(this);
        }
        
        String startLabel = newLabel();
        String endLabel = newLabel();
        
        emit(startLabel + ":");
        
        if (forStmt.getCondition() != null) {
            String condition = forStmt.getCondition().accept(this);
            emit("IF_FALSE " + condition + " GOTO " + endLabel);
        }
        
        forStmt.getBody().accept(this);
        
        if (forStmt.getUpdate() != null) {
            forStmt.getUpdate().accept(this);
        }
        
        emit("GOTO " + startLabel);
        emit(endLabel + ":");
        return null;
    }

    @Override
    public String visit(ReturnStmt returnStmt) {
        if (returnStmt.getValue() != null) {
            String value = returnStmt.getValue().accept(this);
            emit("RETURN " + value);
        } else {
            emit("RETURN");
        }
        return null;
    }

    @Override
    public String visit(ExprStmt exprStmt) {
        exprStmt.getExpression().accept(this);
        return null;
    }

    @Override
    public String visit(BinaryExpr binaryExpr) {
        String left = binaryExpr.getLeft().accept(this);
        String right = binaryExpr.getRight().accept(this);
        String temp = newTemp();
        
        String op = binaryExpr.getOperator().toString();
        emit(temp + " = " + left + " " + op + " " + right);
        
        return temp;
    }

    @Override
    public String visit(UnaryExpr unaryExpr) {
        String operand = unaryExpr.getOperand().accept(this);
        String temp = newTemp();
        
        String op = unaryExpr.getOperator().toString();
        emit(temp + " = " + op + " " + operand);
        
        return temp;
    }

    @Override
    public String visit(AssignExpr assignExpr) {
        String target = assignExpr.getTarget().accept(this);
        String value = assignExpr.getValue().accept(this);
        
        emit(target + " = " + value);
        return target;
    }

    @Override
    public String visit(CallExpr callExpr) {
        List<String> args = new ArrayList<>();
        for (Expression arg : callExpr.getArguments()) {
            args.add(arg.accept(this));
        }
        
        String temp = newTemp();
        String callee = callExpr.getCallee() != null ? callExpr.getCallee().accept(this) : "";
        
        if (!callee.isEmpty()) {
            emit(temp + " = CALL " + callee + "." + callExpr.getMethodName() + "(" + String.join(", ", args) + ")");
        } else {
            emit(temp + " = CALL " + callExpr.getMethodName() + "(" + String.join(", ", args) + ")");
        }
        
        return temp;
    }

    @Override
    public String visit(FieldAccessExpr fieldAccessExpr) {
        String object = fieldAccessExpr.getObject().accept(this);
        String temp = newTemp();
        
        emit(temp + " = " + object + "." + fieldAccessExpr.getFieldName());
        return temp;
    }

    @Override
    public String visit(ArrayAccessExpr arrayAccessExpr) {
        String array = arrayAccessExpr.getArray().accept(this);
        String index = arrayAccessExpr.getIndex().accept(this);
        String temp = newTemp();
        
        emit(temp + " = " + array + "[" + index + "]");
        return temp;
    }

    @Override
    public String visit(NewExpr newExpr) {
        String temp = newTemp();
        
        if (newExpr.isArrayCreation()) {
            String size = newExpr.getArraySize().accept(this);
            emit(temp + " = NEW " + newExpr.getType() + "[" + size + "]");
        } else {
            emit(temp + " = NEW " + newExpr.getType());
        }
        
        return temp;
    }

    @Override
    public String visit(IntLiteral intLiteral) {
        return String.valueOf(intLiteral.getValue());
    }

    @Override
    public String visit(BoolLiteral boolLiteral) {
        return String.valueOf(boolLiteral.getValue());
    }

    @Override
    public String visit(StringLiteral stringLiteral) {
        return "\"" + stringLiteral.getValue() + "\"";
    }

    @Override
    public String visit(NullLiteral nullLiteral) {
        return "null";
    }

    @Override
    public String visit(IdentifierExpr identifierExpr) {
        return identifierExpr.getName();
    }

    @Override
    public String visit(ThisExpr thisExpr) {
        return "this";
    }
}
