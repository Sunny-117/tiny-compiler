package com.compiler.cli;

import com.compiler.ast.Program;
import com.compiler.backend.BytecodeGenerator;
import com.compiler.ir.IRGenerator;
import com.compiler.lexer.Lexer;
import com.compiler.lexer.Token;
import com.compiler.parser.Parser;
import com.compiler.semantic.SemanticAnalyzer;
import com.compiler.util.ASTPrinter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CompilerCLI {
    private boolean verbose = false;
    private boolean printAST = false;
    private boolean printIR = false;
    private boolean printTokens = false;
    private String outputDir = ".";
    private String inputFile;

    public static void main(String[] args) {
        CompilerCLI cli = new CompilerCLI();
        
        if (args.length == 0) {
            cli.printHelp();
            System.exit(1);
        }
        
        try {
            cli.parseArgs(args);
            cli.compile();
        } catch (Exception e) {
            System.err.println("编译错误: " + e.getMessage());
            if (cli.verbose) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }

    private void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            
            switch (arg) {
                case "-h":
                case "--help":
                    printHelp();
                    System.exit(0);
                    break;
                    
                case "-v":
                case "--verbose":
                    verbose = true;
                    break;
                    
                case "--ast":
                    printAST = true;
                    break;
                    
                case "--ir":
                    printIR = true;
                    break;
                    
                case "--tokens":
                    printTokens = true;
                    break;
                    
                case "-o":
                case "--output":
                    if (i + 1 < args.length) {
                        outputDir = args[++i];
                    } else {
                        throw new IllegalArgumentException("缺少输出目录参数");
                    }
                    break;
                    
                default:
                    if (arg.startsWith("-")) {
                        throw new IllegalArgumentException("未知选项: " + arg);
                    }
                    inputFile = arg;
            }
        }
        
        if (inputFile == null) {
            throw new IllegalArgumentException("未指定输入文件");
        }
    }

    private void compile() throws IOException {
        log("正在编译: " + inputFile);
        
        // 1. 读取源文件
        String source = new String(Files.readAllBytes(Paths.get(inputFile)));
        log("源文件大小: " + source.length() + " 字节");
        
        // 2. 词法分析
        log("\n=== 词法分析 ===");
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        log("生成 " + tokens.size() + " 个 token");
        
        if (printTokens) {
            System.out.println("\n--- Tokens ---");
            for (Token token : tokens) {
                System.out.println(token);
            }
        }
        
        // 3. 语法分析
        log("\n=== 语法分析 ===");
        Parser parser = new Parser(tokens);
        Program program = parser.parse();
        log("生成抽象语法树");
        
        if (printAST) {
            System.out.println("\n--- AST ---");
            ASTPrinter printer = new ASTPrinter();
            printer.print(program);
        }
        
        // 4. 语义分析
        log("\n=== 语义分析 ===");
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(program);
        log("语义分析完成");
        
        // 5. 中间表示生成
        log("\n=== 中间表示生成 ===");
        IRGenerator irGenerator = new IRGenerator();
        List<String> irCode = irGenerator.generate(program);
        log("生成 " + irCode.size() + " 条 IR 指令");
        
        if (printIR) {
            System.out.println("\n--- IR ---");
            for (String instruction : irCode) {
                System.out.println(instruction);
            }
        }
        
        // 6. 字节码生成
        log("\n=== 字节码生成 ===");
        BytecodeGenerator generator = new BytecodeGenerator();
        
        // 确保输出目录存在
        Files.createDirectories(Paths.get(outputDir));
        
        generator.generate(program, outputDir);
        log("字节码生成完成，输出到: " + outputDir);
        
        System.out.println("\n✓ 编译成功!");
    }

    private void log(String message) {
        if (verbose) {
            System.out.println(message);
        }
    }

    private void printHelp() {
        System.out.println("Java 编译器 - 从源代码到字节码");
        System.out.println();
        System.out.println("用法: javacompiler [选项] <源文件>");
        System.out.println();
        System.out.println("选项:");
        System.out.println("  -o, --output <目录>    指定输出目录（默认：当前目录）");
        System.out.println("  -v, --verbose          详细输出模式");
        System.out.println("  --ast                  打印抽象语法树");
        System.out.println("  --ir                   打印中间表示");
        System.out.println("  --tokens               打印词法分析结果");
        System.out.println("  -h, --help             显示帮助信息");
        System.out.println();
        System.out.println("示例:");
        System.out.println("  javacompiler HelloWorld.java");
        System.out.println("  javacompiler -v --ast HelloWorld.java");
        System.out.println("  javacompiler -o output/ HelloWorld.java");
    }
}
