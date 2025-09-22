use crate::ast::{
    ChildNode, Identifier, ProgramNode, TransformedNode,
};

/// Transform the original AST into a new AST suitable for code generation
pub fn transform(ast: &mut ProgramNode) -> TransformedNode {
    // Use the simpler recursive transformation approach
    transform_recursive(ast)
}

fn transform_recursive(node: &ProgramNode) -> TransformedNode {
    let mut body = Vec::new();

    for child in &node.body {
        let transformed = transform_child_node(child, false);
        body.push(transformed);
    }

    TransformedNode::Program { body }
}

fn transform_child_node(node: &ChildNode, is_nested: bool) -> TransformedNode {
    match node {
        ChildNode::NumberLiteral(num_node) => {
            let literal = TransformedNode::NumberLiteral {
                value: num_node.value.clone(),
            };
            // If this is not nested (i.e., it's a top-level literal), wrap in ExpressionStatement
            if !is_nested {
                TransformedNode::ExpressionStatement {
                    expression: Box::new(literal),
                }
            } else {
                literal
            }
        },
        ChildNode::StringLiteral(str_node) => {
            let literal = TransformedNode::StringLiteral {
                value: str_node.value.clone(),
            };
            // If this is not nested (i.e., it's a top-level literal), wrap in ExpressionStatement
            if !is_nested {
                TransformedNode::ExpressionStatement {
                    expression: Box::new(literal),
                }
            } else {
                literal
            }
        },
        ChildNode::CallExpression(call_node) => {
            let mut arguments = Vec::new();
            
            for param in &call_node.params {
                let transformed_param = transform_child_node(param, true);
                arguments.push(transformed_param);
            }

            let call_expr = TransformedNode::CallExpression {
                callee: Identifier::new(call_node.name.clone()),
                arguments,
            };

            // If this is not nested (i.e., it's a top-level expression), wrap in ExpressionStatement
            if !is_nested {
                TransformedNode::ExpressionStatement {
                    expression: Box::new(call_expr),
                }
            } else {
                call_expr
            }
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::ast::{CallExpressionNode, NumberLiteralNode};

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
}
