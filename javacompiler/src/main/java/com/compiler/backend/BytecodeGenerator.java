package com.compiler.backend;

import com.compiler.ast.*;
import org.objectweb.asm.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class BytecodeGenerator implements ASTVisitor<Void> {
    private ClassWriter classWriter;
    private MethodVisitor methodVisitor;
    private String currentClassName;
    private final Map<String, Integer> localVariables;
    private int localVarIndex;

    public BytecodeGenerator() {
        this.localVariables = new HashMap<>();
    }

    public void generate(Program program, String outputDir) throws IOException {
        for (ClassDecl classDecl : program.getClasses()) {
            generateClass(classDecl, outputDir);
        }
    }

    private void generateClass(ClassDecl classDecl, String outputDir) throws IOException {
        currentClassName = classDecl.getName();
        classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        
        classWriter.visit(V1_8, ACC_PUBLIC, currentClassName, null, "java/lang/Object", null);
        
        // Generate default constructor
        MethodVisitor constructor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        constructor.visitCode();
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(RETURN);
        constructor.visitMaxs(0, 0);
        constructor.visitEnd();
        
        // Generate fields
        for (FieldDecl field : classDecl.getFields()) {
            generateField(field);
        }
        
        // Generate methods
        for (MethodDecl method : classDecl.getMethods()) {
            generateMethod(method);
        }
        
        classWriter.visitEnd();
        
        // Write to file
        byte[] bytecode = classWriter.toByteArray();
        String outputPath = outputDir + "/" + currentClassName + ".class";
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(bytecode);
        }
    }

    private void generateField(FieldDecl field) {
        String descriptor = getTypeDescriptor(field.getType());
        classWriter.visitField(ACC_PUBLIC, field.getName(), descriptor, null, null).visitEnd();
    }

    private void generateMethod(MethodDecl method) {
        localVariables.clear();
        localVarIndex = 1; // 0 is 'this'
        
        String descriptor = getMethodDescriptor(method);
        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, method.getName(), descriptor, null, null);
        methodVisitor.visitCode();
        
        // Add parameters to local variables
        for (Parameter param : method.getParameters()) {
            localVariables.put(param.getName(), localVarIndex++);
        }
        
        // Generate method body
        method.getBody().accept(this);
        
        // Add default return if void
        if (method.getReturnType().getName().equals("void")) {
            methodVisitor.visitInsn(RETURN);
        }
        
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
    }

    @Override
    public Void visit(Program program) {
        return null;
    }

    @Override
    public Void visit(ClassDecl classDecl) {
        return null;
    }

    @Override
    public Void visit(FieldDecl fieldDecl) {
        return null;
    }

    @Override
    public Void visit(MethodDecl methodDecl) {
        return null;
    }

    @Override
    public Void visit(Parameter parameter) {
        return null;
    }

    @Override
    public Void visit(BlockStmt blockStmt) {
        for (Statement stmt : blockStmt.getStatements()) {
            stmt.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(VarDeclStmt varDeclStmt) {
        int index = localVarIndex++;
        localVariables.put(varDeclStmt.getName(), index);
        
        if (varDeclStmt.getInitializer() != null) {
            varDeclStmt.getInitializer().accept(this);
            storeVariable(varDeclStmt.getType(), index);
        }
        
        return null;
    }

    @Override
    public Void visit(IfStmt ifStmt) {
        Label elseLabel = new Label();
        Label endLabel = new Label();
        
        ifStmt.getCondition().accept(this);
        methodVisitor.visitJumpInsn(IFEQ, elseLabel);
        
        ifStmt.getThenStmt().accept(this);
        methodVisitor.visitJumpInsn(GOTO, endLabel);
        
        methodVisitor.visitLabel(elseLabel);
        if (ifStmt.getElseStmt() != null) {
            ifStmt.getElseStmt().accept(this);
        }
        
        methodVisitor.visitLabel(endLabel);
        return null;
    }

    @Override
    public Void visit(WhileStmt whileStmt) {
        Label startLabel = new Label();
        Label endLabel = new Label();
        
        methodVisitor.visitLabel(startLabel);
        whileStmt.getCondition().accept(this);
        methodVisitor.visitJumpInsn(IFEQ, endLabel);
        
        whileStmt.getBody().accept(this);
        methodVisitor.visitJumpInsn(GOTO, startLabel);
        
        methodVisitor.visitLabel(endLabel);
        return null;
    }

    @Override
    public Void visit(ForStmt forStmt) {
        if (forStmt.getInit() != null) {
            forStmt.getInit().accept(this);
        }
        
        Label startLabel = new Label();
        Label endLabel = new Label();
        
        methodVisitor.visitLabel(startLabel);
        
        if (forStmt.getCondition() != null) {
            forStmt.getCondition().accept(this);
            methodVisitor.visitJumpInsn(IFEQ, endLabel);
        }
        
        forStmt.getBody().accept(this);
        
        if (forStmt.getUpdate() != null) {
            forStmt.getUpdate().accept(this);
        }
        
        methodVisitor.visitJumpInsn(GOTO, startLabel);
        methodVisitor.visitLabel(endLabel);
        return null;
    }

    @Override
    public Void visit(ReturnStmt returnStmt) {
        if (returnStmt.getValue() != null) {
            returnStmt.getValue().accept(this);
            com.compiler.ast.Type type = returnStmt.getValue().getExprType();
            
            if (type.getName().equals("int") || type.getName().equals("boolean")) {
                methodVisitor.visitInsn(IRETURN);
            } else {
                methodVisitor.visitInsn(ARETURN);
            }
        } else {
            methodVisitor.visitInsn(RETURN);
        }
        return null;
    }

    @Override
    public Void visit(ExprStmt exprStmt) {
        exprStmt.getExpression().accept(this);
        // Pop result if not used
        if (exprStmt.getExpression().getExprType() != null) {
            methodVisitor.visitInsn(POP);
        }
        return null;
    }

    @Override
    public Void visit(BinaryExpr binaryExpr) {
        binaryExpr.getLeft().accept(this);
        binaryExpr.getRight().accept(this);
        
        switch (binaryExpr.getOperator()) {
            case ADD:
                methodVisitor.visitInsn(IADD);
                break;
            case SUB:
                methodVisitor.visitInsn(ISUB);
                break;
            case MUL:
                methodVisitor.visitInsn(IMUL);
                break;
            case DIV:
                methodVisitor.visitInsn(IDIV);
                break;
            case MOD:
                methodVisitor.visitInsn(IREM);
                break;
            case EQ:
            case NE:
            case LT:
            case GT:
            case LE:
            case GE:
                generateComparison(binaryExpr.getOperator());
                break;
            case AND:
                methodVisitor.visitInsn(IAND);
                break;
            case OR:
                methodVisitor.visitInsn(IOR);
                break;
        }
        
        return null;
    }

    private void generateComparison(BinaryExpr.BinaryOp op) {
        Label trueLabel = new Label();
        Label endLabel = new Label();
        
        int opcode;
        switch (op) {
            case EQ: opcode = IF_ICMPEQ; break;
            case NE: opcode = IF_ICMPNE; break;
            case LT: opcode = IF_ICMPLT; break;
            case GT: opcode = IF_ICMPGT; break;
            case LE: opcode = IF_ICMPLE; break;
            case GE: opcode = IF_ICMPGE; break;
            default: throw new RuntimeException("Unknown comparison operator");
        }
        
        methodVisitor.visitJumpInsn(opcode, trueLabel);
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitJumpInsn(GOTO, endLabel);
        methodVisitor.visitLabel(trueLabel);
        methodVisitor.visitInsn(ICONST_1);
        methodVisitor.visitLabel(endLabel);
    }

    @Override
    public Void visit(UnaryExpr unaryExpr) {
        unaryExpr.getOperand().accept(this);
        
        switch (unaryExpr.getOperator()) {
            case NEG:
                methodVisitor.visitInsn(INEG);
                break;
            case NOT:
                methodVisitor.visitInsn(ICONST_1);
                methodVisitor.visitInsn(IXOR);
                break;
        }
        
        return null;
    }

    @Override
    public Void visit(AssignExpr assignExpr) {
        assignExpr.getValue().accept(this);
        
        if (assignExpr.getTarget() instanceof IdentifierExpr) {
            IdentifierExpr target = (IdentifierExpr) assignExpr.getTarget();
            Integer index = localVariables.get(target.getName());
            
            if (index != null) {
                methodVisitor.visitInsn(DUP);
                storeVariable(target.getExprType(), index);
            }
        }
        
        return null;
    }

    @Override
    public Void visit(CallExpr callExpr) {
        // Simplified: handle System.out.println
        if (callExpr.getMethodName().equals("println")) {
            methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            
            if (!callExpr.getArguments().isEmpty()) {
                callExpr.getArguments().get(0).accept(this);
            }
            
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", 
                "(Ljava/lang/String;)V", false);
        }
        
        return null;
    }

    @Override
    public Void visit(FieldAccessExpr fieldAccessExpr) {
        fieldAccessExpr.getObject().accept(this);
        String descriptor = getTypeDescriptor(fieldAccessExpr.getExprType());
        methodVisitor.visitFieldInsn(GETFIELD, currentClassName, fieldAccessExpr.getFieldName(), descriptor);
        return null;
    }

    @Override
    public Void visit(ArrayAccessExpr arrayAccessExpr) {
        arrayAccessExpr.getArray().accept(this);
        arrayAccessExpr.getIndex().accept(this);
        methodVisitor.visitInsn(IALOAD);
        return null;
    }

    @Override
    public Void visit(NewExpr newExpr) {
        if (newExpr.isArrayCreation()) {
            newExpr.getArraySize().accept(this);
            methodVisitor.visitIntInsn(NEWARRAY, T_INT);
        } else {
            methodVisitor.visitTypeInsn(NEW, newExpr.getType().getName());
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, newExpr.getType().getName(), "<init>", "()V", false);
        }
        return null;
    }

    @Override
    public Void visit(IntLiteral intLiteral) {
        int value = intLiteral.getValue();
        
        if (value >= -1 && value <= 5) {
            methodVisitor.visitInsn(ICONST_0 + value);
        } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            methodVisitor.visitIntInsn(BIPUSH, value);
        } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            methodVisitor.visitIntInsn(SIPUSH, value);
        } else {
            methodVisitor.visitLdcInsn(value);
        }
        
        return null;
    }

    @Override
    public Void visit(BoolLiteral boolLiteral) {
        methodVisitor.visitInsn(boolLiteral.getValue() ? ICONST_1 : ICONST_0);
        return null;
    }

    @Override
    public Void visit(StringLiteral stringLiteral) {
        methodVisitor.visitLdcInsn(stringLiteral.getValue());
        return null;
    }

    @Override
    public Void visit(NullLiteral nullLiteral) {
        methodVisitor.visitInsn(ACONST_NULL);
        return null;
    }

    @Override
    public Void visit(IdentifierExpr identifierExpr) {
        Integer index = localVariables.get(identifierExpr.getName());
        
        if (index != null) {
            loadVariable(identifierExpr.getExprType(), index);
        }
        
        return null;
    }

    @Override
    public Void visit(ThisExpr thisExpr) {
        methodVisitor.visitVarInsn(ALOAD, 0);
        return null;
    }

    private void loadVariable(com.compiler.ast.Type type, int index) {
        if (type.getName().equals("int") || type.getName().equals("boolean")) {
            methodVisitor.visitVarInsn(ILOAD, index);
        } else {
            methodVisitor.visitVarInsn(ALOAD, index);
        }
    }

    private void storeVariable(com.compiler.ast.Type type, int index) {
        if (type.getName().equals("int") || type.getName().equals("boolean")) {
            methodVisitor.visitVarInsn(ISTORE, index);
        } else {
            methodVisitor.visitVarInsn(ASTORE, index);
        }
    }

    private String getTypeDescriptor(com.compiler.ast.Type type) {
        if (type.isArray()) {
            return "[" + getBaseTypeDescriptor(type.getName());
        }
        return getBaseTypeDescriptor(type.getName());
    }

    private String getBaseTypeDescriptor(String typeName) {
        switch (typeName) {
            case "int": return "I";
            case "boolean": return "Z";
            case "void": return "V";
            default: return "L" + typeName + ";";
        }
    }

    private String getMethodDescriptor(MethodDecl method) {
        StringBuilder sb = new StringBuilder("(");
        
        for (Parameter param : method.getParameters()) {
            sb.append(getTypeDescriptor(param.getType()));
        }
        
        sb.append(")");
        sb.append(getTypeDescriptor(method.getReturnType()));
        
        return sb.toString();
    }
}
