# Java Compiler

一个完整的 Java 编译器实现，支持从源代码到字节码的完整编译流程。

## 项目特性

- **完整的编译流程**：词法分析 → 语法分析 → 语义分析 → 中间表示 → 字节码生成
- **模块化设计**：清晰的模块划分，易于理解和扩展
- **语法支持**：支持类定义、方法、变量、控制流、表达式等核心 Java 语法
- **字节码生成**：使用 ASM 库生成标准 JVM 字节码
- **命令行工具**：提供友好的 CLI 接口
- **完整测试**：包含 JUnit 测试用例

## 项目结构

```
javacompiler/
├── src/main/java/com/compiler/
│   ├── lexer/          # 词法分析器 - 将源代码转换为 Token 流
│   ├── parser/         # 语法分析器 - 构建抽象语法树
│   ├── ast/            # AST 节点定义
│   ├── semantic/       # 语义分析 - 类型检查、符号表
│   ├── ir/             # 中间表示 - 优化和转换
│   ├── backend/        # 后端 - 字节码生成
│   ├── cli/            # 命令行接口
│   └── util/           # 工具类
├── src/test/           # 测试用例
├── examples/           # 示例源文件
└── pom.xml            # Maven 配置
```

## 快速开始

### 环境要求

- JDK 11 或更高版本
- Maven 3.6+

### 构建项目

```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包（生成可执行 JAR）
mvn package
```

### 使用编译器

#### 方式 1：使用 Maven 运行

```bash
mvn exec:java -Dexec.mainClass="com.compiler.cli.CompilerCLI" -Dexec.args="input.java -o output.class"
```

#### 方式 2：使用打包后的 JAR

```bash
# 编译单个文件
java -jar target/javacompiler-1.0.0-jar-with-dependencies.jar examples/HelloWorld.java

# 指定输出目录
java -jar target/javacompiler-1.0.0-jar-with-dependencies.jar examples/HelloWorld.java -o output/

# 查看 AST
java -jar target/javacompiler-1.0.0-jar-with-dependencies.jar examples/HelloWorld.java --ast

# 查看中间表示
java -jar target/javacompiler-1.0.0-jar-with-dependencies.jar examples/HelloWorld.java --ir

# 详细输出
java -jar target/javacompiler-1.0.0-jar-with-dependencies.jar examples/HelloWorld.java -v
```

## CLI 选项

```
用法: javacompiler [选项] <源文件>

选项:
  -o, --output <目录>    指定输出目录（默认：当前目录）
  -v, --verbose          详细输出模式
  --ast                  打印抽象语法树
  --ir                   打印中间表示
  --tokens               打印词法分析结果
  -h, --help             显示帮助信息
```

## 支持的语法特性

### 基本类型
- `int`, `boolean`, `void`
- 字符串字面量

### 类和方法
```java
class MyClass {
    int field;
    
    int method(int param) {
        return param + 1;
    }
}
```

### 控制流
```java
// if-else
if (condition) {
    // ...
} else {
    // ...
}

// while 循环
while (condition) {
    // ...
}

// for 循环
for (int i = 0; i < 10; i = i + 1) {
    // ...
}
```

### 表达式
- 算术运算：`+`, `-`, `*`, `/`, `%`
- 比较运算：`==`, `!=`, `<`, `>`, `<=`, `>=`
- 逻辑运算：`&&`, `||`, `!`
- 赋值：`=`
- 方法调用

## 编译流程详解

### 1. 词法分析（Lexer）
将源代码字符流转换为 Token 流：
```
"int x = 42;" → [INT, IDENTIFIER(x), ASSIGN, NUMBER(42), SEMICOLON]
```

### 2. 语法分析（Parser）
根据语法规则构建抽象语法树（AST）：
```
VarDecl
├── Type: int
├── Name: x
└── Init: IntLiteral(42)
```

### 3. 语义分析（Semantic）
- 符号表构建
- 类型检查
- 作用域分析
- 错误检测

### 4. 中间表示（IR）
将 AST 转换为更接近机器的中间表示，便于优化：
- 三地址码
- 控制流图
- 基本块

### 5. 代码生成（Backend）
使用 ASM 库生成 JVM 字节码（.class 文件）

## 示例

### 示例 1：Hello World

```java
class HelloWorld {
    void main() {
        System.out.println("Hello, World!");
    }
}
```

编译：
```bash
java -jar target/javacompiler-1.0.0-jar-with-dependencies.jar examples/HelloWorld.java
```

### 示例 2：计算斐波那契数

```java
class Fibonacci {
    int fib(int n) {
        if (n <= 1) {
            return n;
        }
        return fib(n - 1) + fib(n - 2);
    }
}
```

## 测试

运行所有测试：
```bash
mvn test
```

运行特定测试：
```bash
mvn test -Dtest=LexerTest
mvn test -Dtest=ParserTest
mvn test -Dtest=SemanticAnalyzerTest
```

## 架构设计

### 设计模式
- **访问者模式**：用于 AST 遍历和处理
- **构建器模式**：用于 IR 和字节码构建
- **策略模式**：用于不同的优化策略

### 扩展性
- 易于添加新的语法特性
- 支持自定义优化 pass
- 可插拔的后端实现

## 限制和未来改进

### 当前限制
- 仅支持基本的 Java 语法子集
- 不支持泛型、注解、lambda 表达式
- 有限的标准库支持

### 未来计划
- [ ] 支持更多 Java 语法特性
- [ ] 实现更多优化（常量折叠、死代码消除等）
- [ ] 添加调试信息生成
- [ ] 支持增量编译
- [ ] 改进错误报告和诊断

## 参考资料

- [Java Language Specification](https://docs.oracle.com/javase/specs/)
- [JVM Specification](https://docs.oracle.com/javase/specs/jvms/se11/html/)
- [ASM Library](https://asm.ow2.io/)
- [Compilers: Principles, Techniques, and Tools (Dragon Book)](https://en.wikipedia.org/wiki/Compilers:_Principles,_Techniques,_and_Tools)
