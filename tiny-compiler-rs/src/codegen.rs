use crate::ast::TransformedNode;
use crate::error::Result;

/// Generate JavaScript code from the transformed AST
pub fn codegen(node: &TransformedNode) -> Result<String> {
    match node {
        TransformedNode::Program { body } => {
            let mut code = String::new();
            for statement in body {
                code.push_str(&codegen(statement)?);
            }
            Ok(code)
        }
        TransformedNode::ExpressionStatement { expression } => {
            let expr_code = codegen(expression)?;
            Ok(format!("{};", expr_code))
        }
        TransformedNode::CallExpression { callee, arguments } => {
            let mut args = Vec::new();
            for arg in arguments {
                args.push(codegen(arg)?);
            }
            Ok(format!("{}({})", callee.name, args.join(", ")))
        }
        TransformedNode::NumberLiteral { value } => Ok(value.clone()),
        TransformedNode::StringLiteral { value } => Ok(format!("\"{}\"", value)),
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::ast::{Identifier, TransformedNode};

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
}
