# Tiny Compiler (Rust Implementation)

A Rust implementation of a tiny compiler that transforms LISP-like syntax into JavaScript function calls. This project demonstrates the fundamental concepts of compiler design including tokenization, parsing, AST transformation, and code generation.

## Overview

This compiler transforms expressions like:
```lisp
(add 2 (subtract 4 2))
```

Into JavaScript code:
```javascript
add(2, subtract(4, 2));
```

## Architecture

The compiler follows a traditional 4-stage pipeline:

1. **Tokenizer** (`src/tokenizer.rs`) - Converts source code into tokens
2. **Parser** (`src/parser.rs`) - Builds an Abstract Syntax Tree (AST) from tokens
3. **Transformer** (`src/transformer.rs`) - Transforms the original AST into a JavaScript-compatible AST
4. **Code Generator** (`src/codegen.rs`) - Generates JavaScript code from the transformed AST

## Features

- **LISP-like syntax parsing** - Supports nested function calls with parentheses
- **Multiple data types** - Numbers, strings, and function calls
- **Comprehensive error handling** - Custom error types with detailed messages
- **Extensive test coverage** - Unit tests, integration tests, and property-based tests
- **Rust best practices** - Memory safety, error handling with `Result`, and proper module organization

## Installation

Make sure you have Rust installed. If not, install it from [rustup.rs](https://rustup.rs/).

```bash
# Clone the repository
git clone <repository-url>
cd tiny-compiler-rs

# Build the project
cargo build

# Run tests
cargo test

# Run the example
cargo run
```

## Usage

### As a Library

```rust
use tiny_compiler_rs::compile;

fn main() {
    let code = "(add 2 (multiply 3 4))";
    match compile(code) {
        Ok(output) => println!("Compiled: {}", output),
        Err(e) => eprintln!("Error: {}", e),
    }
}
```

### As a Binary

```bash
cargo run
```

This will compile the example expression `(add 2 (subtract 4 2))` and output the result.

## Supported Syntax

### Function Calls
```lisp
(function_name arg1 arg2 ...)
```

### Numbers
```lisp
42
3.14
0
```

### Strings
```lisp
"hello world"
"string with \"escaped\" quotes"
```

### Nested Expressions
```lisp
(add 1 (multiply 2 (subtract 5 3)))
```

## Examples

| Input | Output |
|-------|--------|
| `42` | `42;` |
| `(add 1 2)` | `add(1, 2);` |
| `(multiply (add 1 2) 3)` | `multiply(add(1, 2), 3);` |
| `"hello"` | `"hello";` |

## Error Handling

The compiler provides detailed error messages for various error conditions:

- **Syntax errors** - Invalid characters, mismatched parentheses
- **Parse errors** - Unexpected tokens, malformed expressions
- **Semantic errors** - Empty expressions, invalid function names

Example error:
```
Error: Unexpected token ')' at position 5
```

## Testing

The project includes comprehensive tests:

```bash
# Run all tests
cargo test

# Run specific test suites
cargo test tokenizer
cargo test parser
cargo test transformer
cargo test codegen
cargo test integration

# Run with output
cargo test -- --nocapture
```

## Project Structure

```
tiny-compiler-rs/
├── src/
│   ├── lib.rs          # Library entry point
│   ├── main.rs         # Binary entry point
│   ├── ast.rs          # AST node definitions
│   ├── tokenizer.rs    # Tokenization logic
│   ├── parser.rs       # Parsing logic
│   ├── transformer.rs  # AST transformation
│   ├── traverser.rs    # AST traversal utilities
│   ├── codegen.rs      # Code generation
│   ├── compiler.rs     # Main compiler orchestration
│   └── error.rs        # Error types and handling
├── tests/              # Integration and unit tests
├── Cargo.toml          # Project configuration
└── README.md           # This file
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for your changes
4. Ensure all tests pass: `cargo test`
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

This Rust implementation is based on the concepts from "The Super Tiny Compiler" by James Kyle, adapted to demonstrate Rust's type system, error handling, and memory safety features.
