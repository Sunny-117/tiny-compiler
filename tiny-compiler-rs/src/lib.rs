//! # Tiny Compiler - A Rust implementation of a simple compiler
//!
//! This library provides a complete compiler pipeline that transforms LISP-like
//! syntax into JavaScript function calls.
//!
//! ## Quick Start
//!
//! ```rust
//! use tiny_compiler_rs::compile;
//!
//! let code = "(add 2 (multiply 3 4))";
//! let result = compile(code).unwrap();
//! assert_eq!(result, "add(2, multiply(3, 4));");
//! ```
//!
//! ## Architecture
//!
//! The compiler follows a traditional 4-stage pipeline:
//!
//! 1. **Tokenizer** - Converts source code into tokens
//! 2. **Parser** - Builds an Abstract Syntax Tree (AST) from tokens
//! 3. **Transformer** - Transforms the original AST into a JavaScript-compatible AST
//! 4. **Code Generator** - Generates JavaScript code from the transformed AST
//!
//! ## Supported Syntax
//!
//! - Function calls: `(function_name arg1 arg2 ...)`
//! - Numbers: `42`, `3.14`
//! - Strings: `"hello world"`
//! - Nested expressions: `(add 1 (multiply 2 3))`

pub mod ast;
pub mod tokenizer;
pub mod parser;
pub mod traverser;
pub mod transformer;
pub mod codegen;
pub mod compiler;
pub mod error;

pub use compiler::compile;
pub use error::{CompilerError, Result};
