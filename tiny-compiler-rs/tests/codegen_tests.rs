use tiny_compiler_rs::ast::{Identifier, TransformedNode};
use tiny_compiler_rs::codegen::codegen;

#[test]
fn test_codegen_number_literal() {
    let node = TransformedNode::NumberLiteral {
        value: "42".to_string(),
    };
    let result = codegen(&node).unwrap();
    assert_eq!(result, "42");
}

#[test]
fn test_codegen_string_literal() {
    let node = TransformedNode::StringLiteral {
        value: "hello".to_string(),
    };
    let result = codegen(&node).unwrap();
    assert_eq!(result, "\"hello\"");
}

#[test]
fn test_codegen_empty_string() {
    let node = TransformedNode::StringLiteral {
        value: "".to_string(),
    };
    let result = codegen(&node).unwrap();
    assert_eq!(result, "\"\"");
}

#[test]
fn test_codegen_string_with_special_chars() {
    let node = TransformedNode::StringLiteral {
        value: "hello\nworld".to_string(),
    };
    let result = codegen(&node).unwrap();
    assert_eq!(result, "\"hello\nworld\"");
}

#[test]
fn test_codegen_call_expression() {
    let node = TransformedNode::CallExpression {
        callee: Identifier::new("add".to_string()),
        arguments: vec![
            TransformedNode::NumberLiteral {
                value: "1".to_string(),
            },
            TransformedNode::NumberLiteral {
                value: "2".to_string(),
            },
        ],
    };
    let result = codegen(&node).unwrap();
    assert_eq!(result, "add(1, 2)");
}

#[test]
fn test_codegen_call_expression_no_args() {
    let node = TransformedNode::CallExpression {
        callee: Identifier::new("func".to_string()),
        arguments: vec![],
    };
    let result = codegen(&node).unwrap();
    assert_eq!(result, "func()");
}

#[test]
fn test_codegen_call_expression_single_arg() {
    let node = TransformedNode::CallExpression {
        callee: Identifier::new("sqrt".to_string()),
        arguments: vec![
            TransformedNode::NumberLiteral {
                value: "16".to_string(),
            },
        ],
    };
    let result = codegen(&node).unwrap();
    assert_eq!(result, "sqrt(16)");
}

#[test]
fn test_codegen_call_expression_multiple_args() {
    let node = TransformedNode::CallExpression {
        callee: Identifier::new("max".to_string()),
        arguments: vec![
            TransformedNode::NumberLiteral {
                value: "1".to_string(),
            },
            TransformedNode::NumberLiteral {
                value: "2".to_string(),
            },
            TransformedNode::NumberLiteral {
                value: "3".to_string(),
            },
            TransformedNode::NumberLiteral {
                value: "4".to_string(),
            },
        ],
    };
    let result = codegen(&node).unwrap();
    assert_eq!(result, "max(1, 2, 3, 4)");
}

#[test]
fn test_codegen_expression_statement() {
    let node = TransformedNode::ExpressionStatement {
        expression: Box::new(TransformedNode::CallExpression {
            callee: Identifier::new("add".to_string()),
            arguments: vec![
                TransformedNode::NumberLiteral {
                    value: "1".to_string(),
                },
                TransformedNode::NumberLiteral {
                    value: "2".to_string(),
                },
            ],
        }),
    };
    let result = codegen(&node).unwrap();
    assert_eq!(result, "add(1, 2);");
}

#[test]
fn test_codegen_nested_call() {
    let node = TransformedNode::Program {
        body: vec![TransformedNode::ExpressionStatement {
            expression: Box::new(TransformedNode::CallExpression {
                callee: Identifier::new("add".to_string()),
                arguments: vec![
                    TransformedNode::NumberLiteral {
                        value: "2".to_string(),
                    },
                    TransformedNode::CallExpression {
                        callee: Identifier::new("subtract".to_string()),
                        arguments: vec![
                            TransformedNode::NumberLiteral {
                                value: "4".to_string(),
                            },
                            TransformedNode::NumberLiteral {
                                value: "2".to_string(),
                            },
                        ],
                    },
                ],
            }),
        }],
    };
    let result = codegen(&node).unwrap();
    assert_eq!(result, "add(2, subtract(4, 2));");
}

#[test]
fn test_codegen_program_multiple_statements() {
    let node = TransformedNode::Program {
        body: vec![
            TransformedNode::ExpressionStatement {
                expression: Box::new(TransformedNode::CallExpression {
                    callee: Identifier::new("add".to_string()),
                    arguments: vec![
                        TransformedNode::NumberLiteral {
                            value: "1".to_string(),
                        },
                        TransformedNode::NumberLiteral {
                            value: "2".to_string(),
                        },
                    ],
                }),
            },
            TransformedNode::ExpressionStatement {
                expression: Box::new(TransformedNode::CallExpression {
                    callee: Identifier::new("multiply".to_string()),
                    arguments: vec![
                        TransformedNode::NumberLiteral {
                            value: "3".to_string(),
                        },
                        TransformedNode::NumberLiteral {
                            value: "4".to_string(),
                        },
                    ],
                }),
            },
        ],
    };
    let result = codegen(&node).unwrap();
    assert_eq!(result, "add(1, 2);multiply(3, 4);");
}

#[test]
fn test_codegen_deeply_nested() {
    let node = TransformedNode::Program {
        body: vec![TransformedNode::ExpressionStatement {
            expression: Box::new(TransformedNode::CallExpression {
                callee: Identifier::new("add".to_string()),
                arguments: vec![
                    TransformedNode::NumberLiteral {
                        value: "1".to_string(),
                    },
                    TransformedNode::CallExpression {
                        callee: Identifier::new("multiply".to_string()),
                        arguments: vec![
                            TransformedNode::NumberLiteral {
                                value: "2".to_string(),
                            },
                            TransformedNode::CallExpression {
                                callee: Identifier::new("subtract".to_string()),
                                arguments: vec![
                                    TransformedNode::NumberLiteral {
                                        value: "3".to_string(),
                                    },
                                    TransformedNode::CallExpression {
                                        callee: Identifier::new("divide".to_string()),
                                        arguments: vec![
                                            TransformedNode::NumberLiteral {
                                                value: "4".to_string(),
                                            },
                                            TransformedNode::NumberLiteral {
                                                value: "2".to_string(),
                                            },
                                        ],
                                    },
                                ],
                            },
                        ],
                    },
                ],
            }),
        }],
    };
    let result = codegen(&node).unwrap();
    assert_eq!(result, "add(1, multiply(2, subtract(3, divide(4, 2))));");
}

#[test]
fn test_codegen_mixed_argument_types() {
    let node = TransformedNode::Program {
        body: vec![TransformedNode::ExpressionStatement {
            expression: Box::new(TransformedNode::CallExpression {
                callee: Identifier::new("func".to_string()),
                arguments: vec![
                    TransformedNode::NumberLiteral {
                        value: "42".to_string(),
                    },
                    TransformedNode::StringLiteral {
                        value: "hello".to_string(),
                    },
                    TransformedNode::CallExpression {
                        callee: Identifier::new("nested".to_string()),
                        arguments: vec![
                            TransformedNode::NumberLiteral {
                                value: "1".to_string(),
                            },
                        ],
                    },
                ],
            }),
        }],
    };
    let result = codegen(&node).unwrap();
    assert_eq!(result, "func(42, \"hello\", nested(1));");
}

#[test]
fn test_codegen_empty_program() {
    let node = TransformedNode::Program {
        body: vec![],
    };
    let result = codegen(&node).unwrap();
    assert_eq!(result, "");
}

#[test]
fn test_codegen_decimal_numbers() {
    let node = TransformedNode::CallExpression {
        callee: Identifier::new("add".to_string()),
        arguments: vec![
            TransformedNode::NumberLiteral {
                value: "3.14".to_string(),
            },
            TransformedNode::NumberLiteral {
                value: "2.71".to_string(),
            },
        ],
    };
    let result = codegen(&node).unwrap();
    assert_eq!(result, "add(3.14, 2.71)");
}

#[test]
fn test_codegen_long_function_name() {
    let node = TransformedNode::CallExpression {
        callee: Identifier::new("very_long_function_name_with_underscores".to_string()),
        arguments: vec![
            TransformedNode::NumberLiteral {
                value: "1".to_string(),
            },
        ],
    };
    let result = codegen(&node).unwrap();
    assert_eq!(result, "very_long_function_name_with_underscores(1)");
}
