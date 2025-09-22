use crate::ast::{CallExpressionNode, ChildNode, NumberLiteralNode, ProgramNode, StringLiteralNode};
use crate::error::{CompilerError, Result};
use crate::tokenizer::{Token, TokenType};

/// Parser that converts tokens into an AST
pub struct Parser {
    tokens: Vec<Token>,
    current: usize,
}

impl Parser {
    pub fn new(tokens: Vec<Token>) -> Self {
        Self { tokens, current: 0 }
    }

    /// Parse tokens into an AST
    pub fn parse(&mut self) -> Result<ProgramNode> {
        let mut root = ProgramNode::new();

        while self.current < self.tokens.len() {
            let node = self.walk()?;
            root.body.push(node);
        }

        Ok(root)
    }

    fn walk(&mut self) -> Result<ChildNode> {
        let token = self.current_token()?;

        match token.token_type {
            TokenType::Number => {
                let value = token.value.clone();
                self.advance();
                Ok(ChildNode::NumberLiteral(NumberLiteralNode::new(value)))
            }
            TokenType::String => {
                let value = token.value.clone();
                self.advance();
                Ok(ChildNode::StringLiteral(StringLiteralNode::new(value)))
            }
            TokenType::Paren if token.value == "(" => {
                self.advance(); // Skip opening paren
                
                let name_token = self.current_token()?;
                if name_token.token_type != TokenType::Name {
                    return Err(CompilerError::UnexpectedToken {
                        token: name_token.value.clone(),
                        position: self.current,
                    });
                }

                let mut node = CallExpressionNode::new(name_token.value.clone());
                self.advance(); // Skip function name

                // Parse parameters until we hit the closing paren
                while self.current < self.tokens.len() {
                    let token = self.current_token()?;
                    if token.token_type == TokenType::Paren && token.value == ")" {
                        break;
                    }
                    let param = self.walk()?;
                    node.params.push(param);
                }

                // Skip closing paren
                if self.current >= self.tokens.len() {
                    return Err(CompilerError::UnexpectedEof);
                }
                
                let closing_token = self.current_token()?;
                if closing_token.token_type != TokenType::Paren || closing_token.value != ")" {
                    return Err(CompilerError::UnexpectedToken {
                        token: closing_token.value.clone(),
                        position: self.current,
                    });
                }
                self.advance(); // Skip closing paren

                Ok(ChildNode::CallExpression(node))
            }
            _ => Err(CompilerError::UnexpectedToken {
                token: token.value.clone(),
                position: self.current,
            }),
        }
    }

    fn current_token(&self) -> Result<&Token> {
        self.tokens.get(self.current).ok_or(CompilerError::UnexpectedEof)
    }

    fn advance(&mut self) {
        self.current += 1;
    }
}

/// Convenience function to parse tokens into an AST
pub fn parse(tokens: Vec<Token>) -> Result<ProgramNode> {
    let mut parser = Parser::new(tokens);
    parser.parse()
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::ast::{AstNode, NodeType};
    use crate::tokenizer::tokenize;

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
    fn test_parse_nested_expression() {
        let code = "(add 2 (subtract 4 2))";
        let tokens = tokenize(code).unwrap();
        let ast = parse(tokens).unwrap();

        assert_eq!(ast.body.len(), 1);
        match &ast.body[0] {
            ChildNode::CallExpression(node) => {
                assert_eq!(node.name, "add");
                assert_eq!(node.params.len(), 2);
                
                // First parameter should be number 2
                match &node.params[0] {
                    ChildNode::NumberLiteral(n) => assert_eq!(n.value, "2"),
                    _ => panic!("Expected NumberLiteral"),
                }
                
                // Second parameter should be nested call expression
                match &node.params[1] {
                    ChildNode::CallExpression(nested) => {
                        assert_eq!(nested.name, "subtract");
                        assert_eq!(nested.params.len(), 2);
                        
                        match &nested.params[0] {
                            ChildNode::NumberLiteral(n) => assert_eq!(n.value, "4"),
                            _ => panic!("Expected NumberLiteral"),
                        }
                        
                        match &nested.params[1] {
                            ChildNode::NumberLiteral(n) => assert_eq!(n.value, "2"),
                            _ => panic!("Expected NumberLiteral"),
                        }
                    }
                    _ => panic!("Expected nested CallExpression"),
                }
            }
            _ => panic!("Expected CallExpression"),
        }
    }
}
