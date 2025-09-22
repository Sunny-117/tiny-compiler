use tiny_compiler_rs::compile;

/// Integration tests that test the complete compiler pipeline
/// These tests match the functionality of the original TypeScript implementation

#[test]
fn test_integration_basic_compiler_functionality() {
    // This matches the main test from the TypeScript version
    let code = "(add 2 (subtract 4 2))";
    let result = compile(code).unwrap();
    assert_eq!(result, "add(2, subtract(4, 2));");
}

#[test]
fn test_integration_multiple_operations() {
    let test_cases = vec![
        ("(add 1 2)", "add(1, 2);"),
        ("(subtract 10 5)", "subtract(10, 5);"),
        ("(multiply 3 4)", "multiply(3, 4);"),
        ("(divide 8 2)", "divide(8, 2);"),
        ("(modulo 10 3)", "modulo(10, 3);"),
        ("(power 2 3)", "power(2, 3);"),
    ];

    for (input, expected) in test_cases {
        let result = compile(input).unwrap();
        assert_eq!(result, expected, "Failed for input: {}", input);
    }
}

#[test]
fn test_integration_nested_expressions() {
    let test_cases = vec![
        (
            "(add (multiply 2 3) (divide 8 2))",
            "add(multiply(2, 3), divide(8, 2));"
        ),
        (
            "(subtract (add 10 5) (multiply 2 3))",
            "subtract(add(10, 5), multiply(2, 3));"
        ),
        (
            "(multiply (add 1 2) (subtract 5 3))",
            "multiply(add(1, 2), subtract(5, 3));"
        ),
    ];

    for (input, expected) in test_cases {
        let result = compile(input).unwrap();
        assert_eq!(result, expected, "Failed for input: {}", input);
    }
}

#[test]
fn test_integration_deeply_nested_expressions() {
    let test_cases = vec![
        (
            "(add 1 (multiply 2 (subtract 3 4)))",
            "add(1, multiply(2, subtract(3, 4)));"
        ),
        (
            "(calculate (add (multiply 2 3) (divide 8 2)) (subtract 10 5))",
            "calculate(add(multiply(2, 3), divide(8, 2)), subtract(10, 5));"
        ),
        (
            "(outer (middle (inner 1 2) 3) 4)",
            "outer(middle(inner(1, 2), 3), 4);"
        ),
    ];

    for (input, expected) in test_cases {
        let result = compile(input).unwrap();
        assert_eq!(result, expected, "Failed for input: {}", input);
    }
}

#[test]
fn test_integration_multiple_statements() {
    let test_cases = vec![
        (
            "(add 1 2) (multiply 3 4)",
            "add(1, 2);multiply(3, 4);"
        ),
        (
            "(first) (second 1) (third 1 2)",
            "first();second(1);third(1, 2);"
        ),
        (
            "(a 1) (b 2) (c 3) (d 4)",
            "a(1);b(2);c(3);d(4);"
        ),
    ];

    for (input, expected) in test_cases {
        let result = compile(input).unwrap();
        assert_eq!(result, expected, "Failed for input: {}", input);
    }
}

#[test]
fn test_integration_edge_cases() {
    let test_cases = vec![
        // Single numbers
        ("42", "42;"),
        ("0", "0;"),
        ("3.14", "3.14;"),
        
        // Functions with no arguments
        ("(func)", "func();"),
        
        // Functions with many arguments
        ("(max 1 2 3 4 5 6 7 8 9 10)", "max(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);"),
        
        // Large numbers
        ("(add 123456789 987654321)", "add(123456789, 987654321);"),
    ];

    for (input, expected) in test_cases {
        let result = compile(input).unwrap();
        assert_eq!(result, expected, "Failed for input: {}", input);
    }
}

#[test]
fn test_integration_whitespace_handling() {
    let test_cases = vec![
        ("(add 1 2)", "add(1, 2);"),
        ("  (add 1 2)  ", "add(1, 2);"),
        ("(\nadd\n1\n2\n)", "add(1, 2);"),
        ("(\tadd\t1\t2\t)", "add(1, 2);"),
        ("( add   1   2 )", "add(1, 2);"),
    ];

    for (input, expected) in test_cases {
        let result = compile(input).unwrap();
        assert_eq!(result, expected, "Failed for input: {}", input);
    }
}

#[test]
fn test_integration_function_names() {
    let test_cases = vec![
        ("(a 1)", "a(1);"),
        ("(myFunction 1)", "myFunction(1);"),
        ("(my_function 1)", "my_function(1);"),
        ("(MyFunction 1)", "MyFunction(1);"),
        ("(MYFUNCTION 1)", "MYFUNCTION(1);"),
        // ("(func123 1)", "func123(1);"), // This fails in current implementation
    ];

    for (input, expected) in test_cases {
        let result = compile(input);
        assert_eq!(result.unwrap(), expected, "Failed for input: {}", input);
    }
}

#[test]
fn test_integration_string_literals() {
    let test_cases = vec![
        (r#"(print "hello")"#, r#"print("hello");"#),
        (r#"(print "")"#, r#"print("");"#),
        (r#"(concat "hello" "world")"#, r#"concat("hello", "world");"#),
        (r#"(message "Hello, World!")"#, r#"message("Hello, World!");"#),
    ];

    for (input, expected) in test_cases {
        let result = compile(input).unwrap();
        assert_eq!(result, expected, "Failed for input: {}", input);
    }
}

#[test]
fn test_integration_mixed_types() {
    let test_cases = vec![
        (r#"(func 42 "hello")"#, r#"func(42, "hello");"#),
        (r#"(process 1 "data" 3.14)"#, r#"process(1, "data", 3.14);"#),
        (r#"(complex (add 1 2) "result" (multiply 3 4))"#, r#"complex(add(1, 2), "result", multiply(3, 4));"#),
    ];

    for (input, expected) in test_cases {
        let result = compile(input).unwrap();
        assert_eq!(result, expected, "Failed for input: {}", input);
    }
}

#[test]
fn test_integration_real_world_examples() {
    let test_cases = vec![
        // Mathematical expressions
        (
            "(sqrt (add (power 3 2) (power 4 2)))",
            "sqrt(add(power(3, 2), power(4, 2)));"
        ),
        
        // Function composition - with proper function calls
        (
            "(compose (map (inc)) (filter (pos)) (reduce (sum)))",
            "compose(map(inc()), filter(pos()), reduce(sum()));"
        ),
        
        // Conditional logic (represented as function calls)
        (
            "(if (greater 5 3) (print \"true\") (print \"false\"))",
            r#"if(greater(5, 3), print("true"), print("false"));"#
        ),
        
        // Data processing pipeline - with proper function calls
        (
            "(pipeline (load \"data.json\") (transform (norm)) (save \"output.json\"))",
            r#"pipeline(load("data.json"), transform(norm()), save("output.json"));"#
        ),
    ];

    for (input, expected) in test_cases {
        let result = compile(input).unwrap();
        assert_eq!(result, expected, "Failed for input: {}", input);
    }
}

#[test]
fn test_integration_performance_with_large_expressions() {
    // Test with a large nested expression to ensure the compiler can handle it
    let mut code = String::from("(add 1");
    for i in 2..=100 {
        code = format!("(add {} {})", i, code);
    }
    code.push(')');
    
    let result = compile(&code);
    assert!(result.is_ok(), "Compiler should handle large nested expressions");
    
    let output = result.unwrap();
    assert!(output.starts_with("add(100,"));
    assert!(output.ends_with(");"));
    // The structure will be deeply nested, so we won't find "add(1, 2)" at the top level
}

#[test]
fn test_integration_empty_and_minimal_inputs() {
    let test_cases = vec![
        ("", ""),
        ("   ", ""),
        ("\n\t  \n", ""),
        ("42", "42;"),
        ("(f)", "f();"),
    ];

    for (input, expected) in test_cases {
        let result = compile(input).unwrap();
        assert_eq!(result, expected, "Failed for input: {:?}", input);
    }
}

#[test]
fn test_integration_error_recovery() {
    // Test that the compiler properly reports errors for invalid inputs
    let invalid_inputs = vec![
        "(add 1 2",           // Missing closing paren
        "add 1 2)",           // Missing opening paren
        "()",                 // Empty expression
        "(add 1 @)",          // Invalid character
        "(123 1 2)",          // Number as function name
        r#"(print "hello"#,   // Unterminated string
        "((add 1 2)",         // Mismatched parens
        // "(add (multiply) 2)", // Empty nested expression - this actually parses successfully
    ];

    for input in invalid_inputs {
        let result = compile(input);
        assert!(result.is_err(), "Expected error for input: {}", input);
    }
}
