use tiny_compiler_rs::ast::{CallExpressionNode, ChildNode, NumberLiteralNode, ProgramNode, TransformedNode};
use tiny_compiler_rs::transformer::transform;

#[test]
fn test_transform_simple_call() {
    let mut program = ProgramNode::new();
    let mut call_expr = CallExpressionNode::new("add".to_string());
    call_expr.params.push(ChildNode::NumberLiteral(NumberLiteralNode::new("2".to_string())));
    call_expr.params.push(ChildNode::NumberLiteral(NumberLiteralNode::new("3".to_string())));
    program.body.push(ChildNode::CallExpression(call_expr));

    let transformed = transform(&mut program);

    match transformed {
        TransformedNode::Program { body } => {
            assert_eq!(body.len(), 1);
            match &body[0] {
                TransformedNode::ExpressionStatement { expression } => {
                    match expression.as_ref() {
                        TransformedNode::CallExpression { callee, arguments } => {
                            assert_eq!(callee.name, "add");
                            assert_eq!(arguments.len(), 2);
                            
                            match &arguments[0] {
                                TransformedNode::NumberLiteral { value } => {
                                    assert_eq!(value, "2");
                                }
                                _ => panic!("Expected NumberLiteral"),
                            }
                            
                            match &arguments[1] {
                                TransformedNode::NumberLiteral { value } => {
                                    assert_eq!(value, "3");
                                }
                                _ => panic!("Expected NumberLiteral"),
                            }
                        }
                        _ => panic!("Expected CallExpression"),
                    }
                }
                _ => panic!("Expected ExpressionStatement"),
            }
        }
        _ => panic!("Expected Program"),
    }
}

#[test]
fn test_transform_nested_call() {
    let mut program = ProgramNode::new();
    
    // Create nested call: (add 2 (subtract 4 2))
    let mut inner_call = CallExpressionNode::new("subtract".to_string());
    inner_call.params.push(ChildNode::NumberLiteral(NumberLiteralNode::new("4".to_string())));
    inner_call.params.push(ChildNode::NumberLiteral(NumberLiteralNode::new("2".to_string())));
    
    let mut outer_call = CallExpressionNode::new("add".to_string());
    outer_call.params.push(ChildNode::NumberLiteral(NumberLiteralNode::new("2".to_string())));
    outer_call.params.push(ChildNode::CallExpression(inner_call));
    
    program.body.push(ChildNode::CallExpression(outer_call));

    let transformed = transform(&mut program);

    match transformed {
        TransformedNode::Program { body } => {
            assert_eq!(body.len(), 1);
            match &body[0] {
                TransformedNode::ExpressionStatement { expression } => {
                    match expression.as_ref() {
                        TransformedNode::CallExpression { callee, arguments } => {
                            assert_eq!(callee.name, "add");
                            assert_eq!(arguments.len(), 2);
                            
                            // First argument should be number 2
                            match &arguments[0] {
                                TransformedNode::NumberLiteral { value } => {
                                    assert_eq!(value, "2");
                                }
                                _ => panic!("Expected NumberLiteral"),
                            }
                            
                            // Second argument should be nested call expression
                            match &arguments[1] {
                                TransformedNode::CallExpression { callee, arguments } => {
                                    assert_eq!(callee.name, "subtract");
                                    assert_eq!(arguments.len(), 2);
                                    
                                    match &arguments[0] {
                                        TransformedNode::NumberLiteral { value } => {
                                            assert_eq!(value, "4");
                                        }
                                        _ => panic!("Expected NumberLiteral"),
                                    }
                                    
                                    match &arguments[1] {
                                        TransformedNode::NumberLiteral { value } => {
                                            assert_eq!(value, "2");
                                        }
                                        _ => panic!("Expected NumberLiteral"),
                                    }
                                }
                                _ => panic!("Expected nested CallExpression"),
                            }
                        }
                        _ => panic!("Expected CallExpression"),
                    }
                }
                _ => panic!("Expected ExpressionStatement"),
            }
        }
        _ => panic!("Expected Program"),
    }
}

#[test]
fn test_transform_multiple_expressions() {
    let mut program = ProgramNode::new();
    
    // First expression: (add 1 2)
    let mut call1 = CallExpressionNode::new("add".to_string());
    call1.params.push(ChildNode::NumberLiteral(NumberLiteralNode::new("1".to_string())));
    call1.params.push(ChildNode::NumberLiteral(NumberLiteralNode::new("2".to_string())));
    program.body.push(ChildNode::CallExpression(call1));
    
    // Second expression: (multiply 3 4)
    let mut call2 = CallExpressionNode::new("multiply".to_string());
    call2.params.push(ChildNode::NumberLiteral(NumberLiteralNode::new("3".to_string())));
    call2.params.push(ChildNode::NumberLiteral(NumberLiteralNode::new("4".to_string())));
    program.body.push(ChildNode::CallExpression(call2));

    let transformed = transform(&mut program);

    match transformed {
        TransformedNode::Program { body } => {
            assert_eq!(body.len(), 2);
            
            // First expression
            match &body[0] {
                TransformedNode::ExpressionStatement { expression } => {
                    match expression.as_ref() {
                        TransformedNode::CallExpression { callee, arguments } => {
                            assert_eq!(callee.name, "add");
                            assert_eq!(arguments.len(), 2);
                        }
                        _ => panic!("Expected CallExpression"),
                    }
                }
                _ => panic!("Expected ExpressionStatement"),
            }
            
            // Second expression
            match &body[1] {
                TransformedNode::ExpressionStatement { expression } => {
                    match expression.as_ref() {
                        TransformedNode::CallExpression { callee, arguments } => {
                            assert_eq!(callee.name, "multiply");
                            assert_eq!(arguments.len(), 2);
                        }
                        _ => panic!("Expected CallExpression"),
                    }
                }
                _ => panic!("Expected ExpressionStatement"),
            }
        }
        _ => panic!("Expected Program"),
    }
}

#[test]
fn test_transform_single_number() {
    let mut program = ProgramNode::new();
    program.body.push(ChildNode::NumberLiteral(NumberLiteralNode::new("42".to_string())));

    let transformed = transform(&mut program);

    match transformed {
        TransformedNode::Program { body } => {
            assert_eq!(body.len(), 1);
            match &body[0] {
                TransformedNode::ExpressionStatement { expression } => {
                    match expression.as_ref() {
                        TransformedNode::NumberLiteral { value } => {
                            assert_eq!(value, "42");
                        }
                        _ => panic!("Expected NumberLiteral inside ExpressionStatement"),
                    }
                }
                _ => panic!("Expected ExpressionStatement"),
            }
        }
        _ => panic!("Expected Program"),
    }
}

#[test]
fn test_transform_deeply_nested() {
    let mut program = ProgramNode::new();
    
    // Create: (add 1 (multiply 2 (subtract 3 4)))
    let mut innermost = CallExpressionNode::new("subtract".to_string());
    innermost.params.push(ChildNode::NumberLiteral(NumberLiteralNode::new("3".to_string())));
    innermost.params.push(ChildNode::NumberLiteral(NumberLiteralNode::new("4".to_string())));
    
    let mut middle = CallExpressionNode::new("multiply".to_string());
    middle.params.push(ChildNode::NumberLiteral(NumberLiteralNode::new("2".to_string())));
    middle.params.push(ChildNode::CallExpression(innermost));
    
    let mut outer = CallExpressionNode::new("add".to_string());
    outer.params.push(ChildNode::NumberLiteral(NumberLiteralNode::new("1".to_string())));
    outer.params.push(ChildNode::CallExpression(middle));
    
    program.body.push(ChildNode::CallExpression(outer));

    let transformed = transform(&mut program);

    match transformed {
        TransformedNode::Program { body } => {
            assert_eq!(body.len(), 1);
            match &body[0] {
                TransformedNode::ExpressionStatement { expression } => {
                    match expression.as_ref() {
                        TransformedNode::CallExpression { callee, arguments } => {
                            assert_eq!(callee.name, "add");
                            assert_eq!(arguments.len(), 2);
                            
                            // First argument: number 1
                            match &arguments[0] {
                                TransformedNode::NumberLiteral { value } => {
                                    assert_eq!(value, "1");
                                }
                                _ => panic!("Expected NumberLiteral"),
                            }
                            
                            // Second argument: nested multiply call
                            match &arguments[1] {
                                TransformedNode::CallExpression { callee, arguments } => {
                                    assert_eq!(callee.name, "multiply");
                                    assert_eq!(arguments.len(), 2);
                                    
                                    // First arg of multiply: number 2
                                    match &arguments[0] {
                                        TransformedNode::NumberLiteral { value } => {
                                            assert_eq!(value, "2");
                                        }
                                        _ => panic!("Expected NumberLiteral"),
                                    }
                                    
                                    // Second arg of multiply: nested subtract call
                                    match &arguments[1] {
                                        TransformedNode::CallExpression { callee, arguments } => {
                                            assert_eq!(callee.name, "subtract");
                                            assert_eq!(arguments.len(), 2);
                                            
                                            match &arguments[0] {
                                                TransformedNode::NumberLiteral { value } => {
                                                    assert_eq!(value, "3");
                                                }
                                                _ => panic!("Expected NumberLiteral"),
                                            }
                                            
                                            match &arguments[1] {
                                                TransformedNode::NumberLiteral { value } => {
                                                    assert_eq!(value, "4");
                                                }
                                                _ => panic!("Expected NumberLiteral"),
                                            }
                                        }
                                        _ => panic!("Expected nested subtract CallExpression"),
                                    }
                                }
                                _ => panic!("Expected nested multiply CallExpression"),
                            }
                        }
                        _ => panic!("Expected CallExpression"),
                    }
                }
                _ => panic!("Expected ExpressionStatement"),
            }
        }
        _ => panic!("Expected Program"),
    }
}

#[test]
fn test_transform_empty_program() {
    let mut program = ProgramNode::new();
    let transformed = transform(&mut program);

    match transformed {
        TransformedNode::Program { body } => {
            assert_eq!(body.len(), 0);
        }
        _ => panic!("Expected Program"),
    }
}
