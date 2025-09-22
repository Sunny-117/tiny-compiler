use tiny_compiler_rs::ast::{AstNode, ChildNode, NodeType};
use tiny_compiler_rs::parser::parse;
use tiny_compiler_rs::tokenizer::{tokenize, Token, TokenType};
use tiny_compiler_rs::error::CompilerError;

#[test]
fn test_parse_tokens_to_ast() {
    let tokens = vec![
        Token::new(TokenType::Paren, "(".to_string()),
        Token::new(TokenType::Name, "add".to_string()),
        Token::new(TokenType::Number, "2".to_string()),
        Token::new(TokenType::Paren, "(".to_string()),
        Token::new(TokenType::Name, "subtract".to_string()),
        Token::new(TokenType::Number, "4".to_string()),
        Token::new(TokenType::Number, "2".to_string()),
        Token::new(TokenType::Paren, ")".to_string()),
        Token::new(TokenType::Paren, ")".to_string()),
    ];
    
    let ast = parse(tokens).unwrap();
    
    assert_eq!(ast.node_type(), NodeType::Program);
    assert_eq!(ast.body.len(), 1);
    
    match &ast.body[0] {
        ChildNode::CallExpression(call_node) => {
            assert_eq!(call_node.name, "add");
            assert_eq!(call_node.params.len(), 2);
            
            match &call_node.params[0] {
                ChildNode::NumberLiteral(num_node) => {
                    assert_eq!(num_node.value, "2");
                }
                _ => panic!("Expected NumberLiteral"),
            }
            
            match &call_node.params[1] {
                ChildNode::CallExpression(nested_call) => {
                    assert_eq!(nested_call.name, "subtract");
                    assert_eq!(nested_call.params.len(), 2);
                    
                    match &nested_call.params[0] {
                        ChildNode::NumberLiteral(num_node) => {
                            assert_eq!(num_node.value, "4");
                        }
                        _ => panic!("Expected NumberLiteral"),
                    }
                    
                    match &nested_call.params[1] {
                        ChildNode::NumberLiteral(num_node) => {
                            assert_eq!(num_node.value, "2");
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

#[test]
fn test_parse_number() {
    let tokens = vec![Token::new(TokenType::Number, "2".to_string())];
    let ast = parse(tokens).unwrap();
    
    assert_eq!(ast.node_type(), NodeType::Program);
    assert_eq!(ast.body.len(), 1);
    
    match &ast.body[0] {
        ChildNode::NumberLiteral(node) => {
            assert_eq!(node.value, "2");
        }
        _ => panic!("Expected NumberLiteral"),
    }
}

#[test]
fn test_parse_string() {
    let tokens = vec![Token::new(TokenType::String, "hello".to_string())];
    let ast = parse(tokens).unwrap();
    
    assert_eq!(ast.body.len(), 1);
    match &ast.body[0] {
        ChildNode::StringLiteral(node) => {
            assert_eq!(node.value, "hello");
        }
        _ => panic!("Expected StringLiteral"),
    }
}

#[test]
fn test_parse_call_expression() {
    let tokens = vec![
        Token::new(TokenType::Paren, "(".to_string()),
        Token::new(TokenType::Name, "add".to_string()),
        Token::new(TokenType::Number, "1".to_string()),
        Token::new(TokenType::Number, "1".to_string()),
        Token::new(TokenType::Paren, ")".to_string()),
    ];
    
    let ast = parse(tokens).unwrap();
    
    assert_eq!(ast.body.len(), 1);
    match &ast.body[0] {
        ChildNode::CallExpression(node) => {
            assert_eq!(node.name, "add");
            assert_eq!(node.params.len(), 2);
            
            match &node.params[0] {
                ChildNode::NumberLiteral(n) => assert_eq!(n.value, "1"),
                _ => panic!("Expected NumberLiteral"),
            }
            
            match &node.params[1] {
                ChildNode::NumberLiteral(n) => assert_eq!(n.value, "1"),
                _ => panic!("Expected NumberLiteral"),
            }
        }
        _ => panic!("Expected CallExpression"),
    }
}

#[test]
fn test_parse_multiple_expressions() {
    let code = "(add 1 2) (multiply 3 4)";
    let tokens = tokenize(code).unwrap();
    let ast = parse(tokens).unwrap();
    
    assert_eq!(ast.body.len(), 2);
    
    // First expression
    match &ast.body[0] {
        ChildNode::CallExpression(node) => {
            assert_eq!(node.name, "add");
            assert_eq!(node.params.len(), 2);
        }
        _ => panic!("Expected CallExpression"),
    }
    
    // Second expression
    match &ast.body[1] {
        ChildNode::CallExpression(node) => {
            assert_eq!(node.name, "multiply");
            assert_eq!(node.params.len(), 2);
        }
        _ => panic!("Expected CallExpression"),
    }
}

#[test]
fn test_parse_nested_expressions() {
    let code = "(add (multiply 2 3) (subtract 10 5))";
    let tokens = tokenize(code).unwrap();
    let ast = parse(tokens).unwrap();
    
    assert_eq!(ast.body.len(), 1);
    
    match &ast.body[0] {
        ChildNode::CallExpression(node) => {
            assert_eq!(node.name, "add");
            assert_eq!(node.params.len(), 2);
            
            // Both parameters should be call expressions
            match &node.params[0] {
                ChildNode::CallExpression(nested) => {
                    assert_eq!(nested.name, "multiply");
                    assert_eq!(nested.params.len(), 2);
                }
                _ => panic!("Expected nested CallExpression"),
            }
            
            match &node.params[1] {
                ChildNode::CallExpression(nested) => {
                    assert_eq!(nested.name, "subtract");
                    assert_eq!(nested.params.len(), 2);
                }
                _ => panic!("Expected nested CallExpression"),
            }
        }
        _ => panic!("Expected CallExpression"),
    }
}

#[test]
fn test_parse_empty_call() {
    let tokens = vec![
        Token::new(TokenType::Paren, "(".to_string()),
        Token::new(TokenType::Name, "func".to_string()),
        Token::new(TokenType::Paren, ")".to_string()),
    ];
    
    let ast = parse(tokens).unwrap();
    
    assert_eq!(ast.body.len(), 1);
    match &ast.body[0] {
        ChildNode::CallExpression(node) => {
            assert_eq!(node.name, "func");
            assert_eq!(node.params.len(), 0);
        }
        _ => panic!("Expected CallExpression"),
    }
}

#[test]
fn test_parse_mixed_types() {
    let code = r#"(func 42 "hello" (nested 1))"#;
    let tokens = tokenize(code).unwrap();
    let ast = parse(tokens).unwrap();
    
    assert_eq!(ast.body.len(), 1);
    match &ast.body[0] {
        ChildNode::CallExpression(node) => {
            assert_eq!(node.name, "func");
            assert_eq!(node.params.len(), 3);
            
            match &node.params[0] {
                ChildNode::NumberLiteral(n) => assert_eq!(n.value, "42"),
                _ => panic!("Expected NumberLiteral"),
            }
            
            match &node.params[1] {
                ChildNode::StringLiteral(s) => assert_eq!(s.value, "hello"),
                _ => panic!("Expected StringLiteral"),
            }
            
            match &node.params[2] {
                ChildNode::CallExpression(nested) => {
                    assert_eq!(nested.name, "nested");
                    assert_eq!(nested.params.len(), 1);
                }
                _ => panic!("Expected nested CallExpression"),
            }
        }
        _ => panic!("Expected CallExpression"),
    }
}

// Error cases

#[test]
fn test_parse_missing_closing_paren() {
    let tokens = vec![
        Token::new(TokenType::Paren, "(".to_string()),
        Token::new(TokenType::Name, "add".to_string()),
        Token::new(TokenType::Number, "1".to_string()),
        Token::new(TokenType::Number, "2".to_string()),
    ];
    
    let result = parse(tokens);
    assert!(result.is_err());
    
    match result.unwrap_err() {
        CompilerError::UnexpectedEof => {}
        _ => panic!("Expected UnexpectedEof error"),
    }
}

#[test]
fn test_parse_missing_function_name() {
    let tokens = vec![
        Token::new(TokenType::Paren, "(".to_string()),
        Token::new(TokenType::Number, "1".to_string()),
        Token::new(TokenType::Paren, ")".to_string()),
    ];
    
    let result = parse(tokens);
    assert!(result.is_err());
    
    match result.unwrap_err() {
        CompilerError::UnexpectedToken { token, position } => {
            assert_eq!(token, "1");
            assert_eq!(position, 1);
        }
        _ => panic!("Expected UnexpectedToken error"),
    }
}

#[test]
fn test_parse_unexpected_closing_paren() {
    let tokens = vec![
        Token::new(TokenType::Paren, ")".to_string()),
    ];
    
    let result = parse(tokens);
    assert!(result.is_err());
    
    match result.unwrap_err() {
        CompilerError::UnexpectedToken { token, .. } => {
            assert_eq!(token, ")");
        }
        _ => panic!("Expected UnexpectedToken error"),
    }
}

#[test]
fn test_parse_empty_input() {
    let tokens = vec![];
    let ast = parse(tokens).unwrap();
    
    assert_eq!(ast.body.len(), 0);
}
