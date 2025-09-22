use tiny_compiler_rs::compile;
use tiny_compiler_rs::error::CompilerError;

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
fn test_compile_no_arguments() {
    let code = "(func)";
    let result = compile(code).unwrap();
    assert_eq!(result, "func();");
}

#[test]
fn test_compile_single_argument() {
    let code = "(sqrt 16)";
    let result = compile(code).unwrap();
    assert_eq!(result, "sqrt(16);");
}

#[test]
fn test_compile_many_arguments() {
    let code = "(max 1 2 3 4 5)";
    let result = compile(code).unwrap();
    assert_eq!(result, "max(1, 2, 3, 4, 5);");
}

#[test]
fn test_compile_with_whitespace() {
    let code = "  (  add   1   2  )  ";
    let result = compile(code).unwrap();
    assert_eq!(result, "add(1, 2);");
}

#[test]
fn test_compile_decimal_numbers() {
    let code = "(add 3.14 2.71)";
    let result = compile(code).unwrap();
    assert_eq!(result, "add(3.14, 2.71);");
}

#[test]
fn test_compile_function_names_with_underscores() {
    let code = "(my_function 1 2)";
    let result = compile(code).unwrap();
    assert_eq!(result, "my_function(1, 2);");
}

#[test]
fn test_compile_case_sensitive_function_names() {
    let code = "(Add 1 2) (ADD 3 4) (add 5 6)";
    let result = compile(code).unwrap();
    assert_eq!(result, "Add(1, 2);ADD(3, 4);add(5, 6);");
}

#[test]
fn test_compile_complex_nested_expression() {
    let code = "(calculate (add (multiply 2 3) (divide 8 2)) (subtract (power 2 3) (modulo 10 3)))";
    let result = compile(code).unwrap();
    assert_eq!(result, "calculate(add(multiply(2, 3), divide(8, 2)), subtract(power(2, 3), modulo(10, 3)));");
}

#[test]
fn test_compile_multiple_complex_expressions() {
    let code = "(first (add 1 2)) (second (multiply 3 4)) (third (subtract 5 6))";
    let result = compile(code).unwrap();
    assert_eq!(result, "first(add(1, 2));second(multiply(3, 4));third(subtract(5, 6));");
}

#[test]
fn test_compile_with_string_literals() {
    let code = r#"(print "hello world")"#;
    let result = compile(code).unwrap();
    assert_eq!(result, r#"print("hello world");"#);
}

#[test]
fn test_compile_mixed_types() {
    let code = r#"(func 42 "hello" (nested 1))"#;
    let result = compile(code).unwrap();
    assert_eq!(result, r#"func(42, "hello", nested(1));"#);
}

#[test]
fn test_compile_string_with_escapes() {
    let code = r#"(print "hello\nworld")"#;
    let result = compile(code).unwrap();
    assert_eq!(result, "print(\"hello\nworld\");");
}

#[test]
fn test_compile_empty_string() {
    let code = r#"(print "")"#;
    let result = compile(code).unwrap();
    assert_eq!(result, r#"print("");"#);
}

// Error handling tests

#[test]
fn test_compile_missing_closing_paren() {
    let code = "(add 1 2";
    let result = compile(code);
    assert!(result.is_err());
    
    match result.unwrap_err() {
        CompilerError::UnexpectedEof => {}
        _ => panic!("Expected UnexpectedEof error"),
    }
}

#[test]
fn test_compile_missing_opening_paren() {
    let code = "add 1 2)";
    let result = compile(code);
    assert!(result.is_err());

    // The error should be about the unexpected token after parsing "add" as a name
    match result.unwrap_err() {
        CompilerError::UnexpectedToken { .. } => {
            // This is expected - the parser will encounter an unexpected token
        }
        _ => panic!("Expected UnexpectedToken error"),
    }
}

#[test]
fn test_compile_empty_expression() {
    let code = "()";
    let result = compile(code);
    assert!(result.is_err());
    
    match result.unwrap_err() {
        CompilerError::UnexpectedToken { .. } => {}
        _ => panic!("Expected UnexpectedToken error"),
    }
}

#[test]
fn test_compile_invalid_character() {
    let code = "(add 1 @)";
    let result = compile(code);
    assert!(result.is_err());
    
    match result.unwrap_err() {
        CompilerError::InvalidCharacter { character, position } => {
            assert_eq!(character, '@');
            assert_eq!(position, 7);
        }
        _ => panic!("Expected InvalidCharacter error"),
    }
}

#[test]
fn test_compile_unterminated_string() {
    let code = r#"(print "hello"#;
    let result = compile(code);
    assert!(result.is_err());
    
    match result.unwrap_err() {
        CompilerError::UnexpectedEof => {}
        _ => panic!("Expected UnexpectedEof error"),
    }
}

#[test]
fn test_compile_mismatched_parens() {
    let code = "(add (multiply 2 3) 4))";
    let result = compile(code);
    assert!(result.is_err());
    
    match result.unwrap_err() {
        CompilerError::UnexpectedToken { token, .. } => {
            assert_eq!(token, ")");
        }
        _ => panic!("Expected UnexpectedToken error"),
    }
}

#[test]
fn test_compile_empty_input() {
    let code = "";
    let result = compile(code).unwrap();
    assert_eq!(result, "");
}

#[test]
fn test_compile_only_whitespace() {
    let code = "   \n\t  ";
    let result = compile(code).unwrap();
    assert_eq!(result, "");
}

#[test]
fn test_compile_number_starting_function_name() {
    let code = "(2add 1 2)";
    let result = compile(code);
    assert!(result.is_err());
    
    match result.unwrap_err() {
        CompilerError::UnexpectedToken { token, .. } => {
            assert_eq!(token, "2");
        }
        _ => panic!("Expected UnexpectedToken error"),
    }
}

#[test]
fn test_compile_nested_empty_expressions() {
    let code = "(add () 2)";
    let result = compile(code);
    assert!(result.is_err());
    
    match result.unwrap_err() {
        CompilerError::UnexpectedToken { token, .. } => {
            assert_eq!(token, ")");
        }
        _ => panic!("Expected UnexpectedToken error"),
    }
}
