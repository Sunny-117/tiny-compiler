use crate::codegen::codegen;
use crate::error::Result;
use crate::parser::parse;
use crate::tokenizer::tokenize;
use crate::transformer::transform;

/// Main compiler function that orchestrates the entire compilation pipeline
/// 
/// Takes LISP-like syntax and compiles it to JavaScript function calls
/// 
/// # Example
/// ```
/// use tiny_compiler_rs::compile;
/// 
/// let code = "(add 2 (subtract 4 2))";
/// let result = compile(code).unwrap();
/// assert_eq!(result, "add(2, subtract(4, 2));");
/// ```
pub fn compile(code: &str) -> Result<String> {
    // Step 1: Tokenize - convert source code into tokens
    let tokens = tokenize(code)?;
    
    // Step 2: Parse - convert tokens into AST
    let mut ast = parse(tokens)?;
    
    // Step 3: Transform - convert original AST to new AST suitable for codegen
    let transformed_ast = transform(&mut ast);
    
    // Step 4: Code generation - convert transformed AST to JavaScript code
    let output = codegen(&transformed_ast)?;
    
    Ok(output)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_compile_simple_expression() {
        let code = "(add 2 (subtract 4 2))";
        let result = compile(code).unwrap();
        assert_eq!(result, "add(2, subtract(4, 2));");
    }

    #[test]
    fn test_compile_simple_call() {
        let code = "(add 1 2)";
        let result = compile(code).unwrap();
        assert_eq!(result, "add(1, 2);");
    }

    #[test]
    fn test_compile_single_number() {
        let code = "42";
        let result = compile(code).unwrap();
        assert_eq!(result, "42;");
    }

    #[test]
    fn test_compile_nested_calls() {
        let code = "(multiply (add 1 2) (subtract 5 3))";
        let result = compile(code).unwrap();
        assert_eq!(result, "multiply(add(1, 2), subtract(5, 3));");
    }

    #[test]
    fn test_compile_multiple_expressions() {
        let code = "(add 1 2) (multiply 3 4)";
        let result = compile(code).unwrap();
        assert_eq!(result, "add(1, 2);multiply(3, 4);");
    }

    #[test]
    fn test_compile_with_larger_numbers() {
        let code = "(add 123 456)";
        let result = compile(code).unwrap();
        assert_eq!(result, "add(123, 456);");
    }

    #[test]
    fn test_compile_deeply_nested() {
        let code = "(add 1 (multiply 2 (subtract 3 (divide 4 2))))";
        let result = compile(code).unwrap();
        assert_eq!(result, "add(1, multiply(2, subtract(3, divide(4, 2))));");
    }

    #[test]
    fn test_compile_error_handling() {
        // Test invalid syntax
        let code = "(add 1 2";  // Missing closing paren
        let result = compile(code);
        assert!(result.is_err());
    }

    #[test]
    fn test_compile_empty_expression() {
        let code = "()";
        let result = compile(code);
        assert!(result.is_err());  // Should fail because no function name
    }

    #[test]
    fn test_compile_invalid_character() {
        let code = "(add 1 @)";  // Invalid character @
        let result = compile(code);
        assert!(result.is_err());
    }
}
