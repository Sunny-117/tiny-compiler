package com.compiler.semantic;

import com.compiler.ast.*;

import java.util.HashMap;
import java.util.Map;

public class SemanticAnalyzer implements ASTVisitor<Type> {
    private final SymbolTable symbolTable;
    private ClassDecl currentClass;
    private MethodDecl currentMethod;
    private final Map<String, ClassDecl> classes;

    public SemanticAnalyzer() {
        this.symbolTable = new SymbolTable();
        this.classes = new HashMap<>();
    }

    public void analyze(Program program) {
        // First pass: collect all class declarations
        for (ClassDecl classDecl : program.getClasses()) {
            if (classes.containsKey(classDecl.getName())) {
                throw new SemanticException("Duplicate class: " + classDecl.getName());
            }
            classes.put(classDecl.getName(), classDecl);
        }
        
        // Second pass: analyze each class
        program.accept(this);
    }

    @Override
    public Type visit(Program program) {
        for (ClassDecl classDecl : program.getClasses()) {
            classDecl.accept(this);
        }
        return null;
    }

    @Override
    public Type visit(ClassDecl classDecl) {
        currentClass = classDecl;
        symbolTable.enterScope();
        
        // Add fields to symbol table
        for (FieldDecl field : classDecl.getFields()) {
            field.accept(this);
        }
        
        // Analyze methods
        for (MethodDecl method : classDecl.getMethods()) {
            method.accept(this);
        }
        
        symbolTable.exitScope();
        currentClass = null;
        return null;
    }

    @Override
    public Type visit(FieldDecl fieldDecl) {
        if (symbolTable.lookupInCurrentScope(fieldDecl.getName()) != null) {
            throw new SemanticException("Duplicate field: " + fieldDecl.getName());
        }
        
        symbolTable.define(fieldDecl.getName(), fieldDecl.getType());
        
        if (fieldDecl.getInitializer() != null) {
            Type initType = fieldDecl.getInitializer().accept(this);
            if (!isAssignable(fieldDecl.getType(), initType)) {
                throw new SemanticException("Type mismatch in field initializer");
            }
        }
        
        return fieldDecl.getType();
    }

    @Override
    public Type visit(MethodDecl methodDecl) {
        currentMethod = methodDecl;
        symbolTable.enterScope();
        
        // Add parameters to symbol table
        for (Parameter param : methodDecl.getParameters()) {
            param.accept(this);
        }
        
        // Analyze method body
        methodDecl.getBody().accept(this);
        
        symbolTable.exitScope();
        currentMethod = null;
        return methodDecl.getReturnType();
    }

    @Override
    public Type visit(Parameter parameter) {
        if (symbolTable.lookupInCurrentScope(parameter.getName()) != null) {
            throw new SemanticException("Duplicate parameter: " + parameter.getName());
        }
        
        symbolTable.define(parameter.getName(), parameter.getType());
        return parameter.getType();
    }

    @Override
    public Type visit(BlockStmt blockStmt) {
        symbolTable.enterScope();
        
        for (Statement stmt : blockStmt.getStatements()) {
            stmt.accept(this);
        }
        
        symbolTable.exitScope();
        return null;
    }

    @Override
    public Type visit(VarDeclStmt varDeclStmt) {
        if (symbolTable.lookupInCurrentScope(varDeclStmt.getName()) != null) {
            throw new SemanticException("Duplicate variable: " + varDeclStmt.getName());
        }
        
        symbolTable.define(varDeclStmt.getName(), varDeclStmt.getType());
        
        if (varDeclStmt.getInitializer() != null) {
            Type initType = varDeclStmt.getInitializer().accept(this);
            if (!isAssignable(varDeclStmt.getType(), initType)) {
                throw new SemanticException("Type mismatch in variable declaration");
            }
        }
        
        return varDeclStmt.getType();
    }

    @Override
    public Type visit(IfStmt ifStmt) {
        Type condType = ifStmt.getCondition().accept(this);
        if (!condType.equals(new Type("boolean"))) {
            throw new SemanticException("If condition must be boolean");
        }
        
        ifStmt.getThenStmt().accept(this);
        
        if (ifStmt.getElseStmt() != null) {
            ifStmt.getElseStmt().accept(this);
        }
        
        return null;
    }

    @Override
    public Type visit(WhileStmt whileStmt) {
        Type condType = whileStmt.getCondition().accept(this);
        if (!condType.equals(new Type("boolean"))) {
            throw new SemanticException("While condition must be boolean");
        }
        
        whileStmt.getBody().accept(this);
        return null;
    }

    @Override
    public Type visit(ForStmt forStmt) {
        symbolTable.enterScope();
        
        if (forStmt.getInit() != null) {
            forStmt.getInit().accept(this);
        }
        
        if (forStmt.getCondition() != null) {
            Type condType = forStmt.getCondition().accept(this);
            if (!condType.equals(new Type("boolean"))) {
                throw new SemanticException("For condition must be boolean");
            }
        }
        
        if (forStmt.getUpdate() != null) {
            forStmt.getUpdate().accept(this);
        }
        
        forStmt.getBody().accept(this);
        
        symbolTable.exitScope();
        return null;
    }

    @Override
    public Type visit(ReturnStmt returnStmt) {
        if (currentMethod == null) {
            throw new SemanticException("Return statement outside method");
        }
        
        Type returnType = currentMethod.getReturnType();
        
        if (returnStmt.getValue() == null) {
            if (!returnType.equals(new Type("void"))) {
                throw new SemanticException("Missing return value");
            }
        } else {
            Type valueType = returnStmt.getValue().accept(this);
            if (!isAssignable(returnType, valueType)) {
                throw new SemanticException("Return type mismatch");
            }
        }
        
        return null;
    }

    @Override
    public Type visit(ExprStmt exprStmt) {
        exprStmt.getExpression().accept(this);
        return null;
    }

    @Override
    public Type visit(BinaryExpr binaryExpr) {
        Type leftType = binaryExpr.getLeft().accept(this);
        Type rightType = binaryExpr.getRight().accept(this);
        
        Type resultType;
        
        switch (binaryExpr.getOperator()) {
            case ADD:
            case SUB:
            case MUL:
            case DIV:
            case MOD:
                if (!leftType.equals(new Type("int")) || !rightType.equals(new Type("int"))) {
                    throw new SemanticException("Arithmetic operators require int operands");
                }
                resultType = new Type("int");
                break;
                
            case EQ:
            case NE:
                if (!isAssignable(leftType, rightType) && !isAssignable(rightType, leftType)) {
                    throw new SemanticException("Incompatible types for comparison");
                }
                resultType = new Type("boolean");
                break;
                
            case LT:
            case GT:
            case LE:
            case GE:
                if (!leftType.equals(new Type("int")) || !rightType.equals(new Type("int"))) {
                    throw new SemanticException("Relational operators require int operands");
                }
                resultType = new Type("boolean");
                break;
                
            case AND:
            case OR:
                if (!leftType.equals(new Type("boolean")) || !rightType.equals(new Type("boolean"))) {
                    throw new SemanticException("Logical operators require boolean operands");
                }
                resultType = new Type("boolean");
                break;
                
            default:
                throw new SemanticException("Unknown binary operator");
        }
        
        binaryExpr.setExprType(resultType);
        return resultType;
    }

    @Override
    public Type visit(UnaryExpr unaryExpr) {
        Type operandType = unaryExpr.getOperand().accept(this);
        
        Type resultType;
        
        switch (unaryExpr.getOperator()) {
            case NEG:
                if (!operandType.equals(new Type("int"))) {
                    throw new SemanticException("Negation requires int operand");
                }
                resultType = new Type("int");
                break;
                
            case NOT:
                if (!operandType.equals(new Type("boolean"))) {
                    throw new SemanticException("Logical NOT requires boolean operand");
                }
                resultType = new Type("boolean");
                break;
                
            default:
                throw new SemanticException("Unknown unary operator");
        }
        
        unaryExpr.setExprType(resultType);
        return resultType;
    }

    @Override
    public Type visit(AssignExpr assignExpr) {
        Type targetType = assignExpr.getTarget().accept(this);
        Type valueType = assignExpr.getValue().accept(this);
        
        if (!isAssignable(targetType, valueType)) {
            throw new SemanticException("Type mismatch in assignment");
        }
        
        assignExpr.setExprType(targetType);
        return targetType;
    }

    @Override
    public Type visit(CallExpr callExpr) {
        // Simplified: look up method in current class
        Type resultType = new Type("int"); // Default to int for simplicity
        
        if (currentClass != null) {
            for (MethodDecl method : currentClass.getMethods()) {
                if (method.getName().equals(callExpr.getMethodName())) {
                    resultType = method.getReturnType();
                    break;
                }
            }
        }
        
        for (Expression arg : callExpr.getArguments()) {
            arg.accept(this);
        }
        
        callExpr.setExprType(resultType);
        return resultType;
    }

    @Override
    public Type visit(FieldAccessExpr fieldAccessExpr) {
        Type objectType = fieldAccessExpr.getObject().accept(this);
        
        // Simplified: assume field exists and return int
        Type fieldType = new Type("int");
        
        fieldAccessExpr.setExprType(fieldType);
        return fieldType;
    }

    @Override
    public Type visit(ArrayAccessExpr arrayAccessExpr) {
        Type arrayType = arrayAccessExpr.getArray().accept(this);
        Type indexType = arrayAccessExpr.getIndex().accept(this);
        
        if (!indexType.equals(new Type("int"))) {
            throw new SemanticException("Array index must be int");
        }
        
        if (!arrayType.isArray()) {
            throw new SemanticException("Cannot index non-array type");
        }
        
        Type elementType = new Type(arrayType.getName(), false);
        arrayAccessExpr.setExprType(elementType);
        return elementType;
    }

    @Override
    public Type visit(NewExpr newExpr) {
        for (Expression arg : newExpr.getArguments()) {
            arg.accept(this);
        }
        
        if (newExpr.getArraySize() != null) {
            Type sizeType = newExpr.getArraySize().accept(this);
            if (!sizeType.equals(new Type("int"))) {
                throw new SemanticException("Array size must be int");
            }
        }
        
        return newExpr.getType();
    }

    @Override
    public Type visit(IntLiteral intLiteral) {
        return intLiteral.getExprType();
    }

    @Override
    public Type visit(BoolLiteral boolLiteral) {
        return boolLiteral.getExprType();
    }

    @Override
    public Type visit(StringLiteral stringLiteral) {
        return stringLiteral.getExprType();
    }

    @Override
    public Type visit(NullLiteral nullLiteral) {
        Type type = new Type("null");
        nullLiteral.setExprType(type);
        return type;
    }

    @Override
    public Type visit(IdentifierExpr identifierExpr) {
        Type type = symbolTable.lookup(identifierExpr.getName());
        
        if (type == null) {
            throw new SemanticException("Undefined variable: " + identifierExpr.getName());
        }
        
        identifierExpr.setExprType(type);
        return type;
    }

    @Override
    public Type visit(ThisExpr thisExpr) {
        if (currentClass == null) {
            throw new SemanticException("'this' outside class");
        }
        
        Type type = new Type(currentClass.getName());
        thisExpr.setExprType(type);
        return type;
    }

    private boolean isAssignable(Type target, Type source) {
        if (target.equals(source)) {
            return true;
        }
        
        // null can be assigned to any reference type
        if (source.getName().equals("null") && !target.isPrimitive()) {
            return true;
        }
        
        return false;
    }
}
