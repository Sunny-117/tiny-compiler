use crate::error::{CompilerError, Result};
use serde::{Deserialize, Serialize};

/// Token types that can be produced by the tokenizer
#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
pub enum TokenType {
    Paren,
    Name,
    Number,
    String,
}

/// A token with its type and value
#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
pub struct Token {
    pub token_type: TokenType,
    pub value: String,
}

impl Token {
    pub fn new(token_type: TokenType, value: String) -> Self {
        Self { token_type, value }
    }
}

/// Tokenizer that converts source code into tokens
pub struct Tokenizer {
    input: Vec<char>,
    current: usize,
}

impl Tokenizer {
    pub fn new(input: &str) -> Self {
        Self {
            input: input.chars().collect(),
            current: 0,
        }
    }

    /// Tokenize the input string into a vector of tokens
    pub fn tokenize(&mut self) -> Result<Vec<Token>> {
        let mut tokens = Vec::new();

        while self.current < self.input.len() {
            let ch = self.current_char();

            // Skip whitespace
            if ch.is_whitespace() {
                self.advance();
                continue;
            }

            // Handle parentheses
            if ch == '(' || ch == ')' {
                tokens.push(Token::new(TokenType::Paren, ch.to_string()));
                self.advance();
                continue;
            }

            // Handle letters (names/identifiers)
            if ch.is_alphabetic() {
                let value = self.read_name();
                tokens.push(Token::new(TokenType::Name, value));
                continue;
            }

            // Handle numbers
            if ch.is_ascii_digit() {
                let value = self.read_number();
                tokens.push(Token::new(TokenType::Number, value));
                continue;
            }

            // Handle strings (quoted)
            if ch == '"' {
                let value = self.read_string()?;
                tokens.push(Token::new(TokenType::String, value));
                continue;
            }

            // Invalid character
            return Err(CompilerError::InvalidCharacter {
                character: ch,
                position: self.current,
            });
        }

        Ok(tokens)
    }

    fn current_char(&self) -> char {
        self.input[self.current]
    }

    fn advance(&mut self) {
        self.current += 1;
    }



    fn read_name(&mut self) -> String {
        let mut value = String::new();
        
        while self.current < self.input.len() {
            let ch = self.current_char();
            if ch.is_alphabetic() || ch == '_' {
                value.push(ch);
                self.advance();
            } else {
                break;
            }
        }
        
        value
    }

    fn read_number(&mut self) -> String {
        let mut value = String::new();
        
        while self.current < self.input.len() {
            let ch = self.current_char();
            if ch.is_ascii_digit() || ch == '.' {
                value.push(ch);
                self.advance();
            } else {
                break;
            }
        }
        
        value
    }

    fn read_string(&mut self) -> Result<String> {
        let mut value = String::new();
        self.advance(); // Skip opening quote
        
        while self.current < self.input.len() {
            let ch = self.current_char();
            if ch == '"' {
                self.advance(); // Skip closing quote
                return Ok(value);
            }
            if ch == '\\' {
                self.advance();
                if self.current >= self.input.len() {
                    return Err(CompilerError::UnexpectedEof);
                }
                let escaped = self.current_char();
                match escaped {
                    'n' => value.push('\n'),
                    't' => value.push('\t'),
                    'r' => value.push('\r'),
                    '\\' => value.push('\\'),
                    '"' => value.push('"'),
                    _ => {
                        value.push('\\');
                        value.push(escaped);
                    }
                }
            } else {
                value.push(ch);
            }
            self.advance();
        }
        
        Err(CompilerError::UnexpectedEof)
    }
}

/// Convenience function to tokenize a string
pub fn tokenize(input: &str) -> Result<Vec<Token>> {
    let mut tokenizer = Tokenizer::new(input);
    tokenizer.tokenize()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_tokenize_simple_expression() {
        let code = "(add 2 (subtract 4 2))";
        let tokens = tokenize(code).unwrap();
        
        let expected = vec![
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
        
        assert_eq!(tokens, expected);
    }

    #[test]
    fn test_tokenize_parentheses() {
        assert_eq!(
            tokenize("(").unwrap(),
            vec![Token::new(TokenType::Paren, "(".to_string())]
        );
        assert_eq!(
            tokenize(")").unwrap(),
            vec![Token::new(TokenType::Paren, ")".to_string())]
        );
    }

    #[test]
    fn test_tokenize_name() {
        assert_eq!(
            tokenize("add").unwrap(),
            vec![Token::new(TokenType::Name, "add".to_string())]
        );
    }

    #[test]
    fn test_tokenize_number() {
        assert_eq!(
            tokenize("22").unwrap(),
            vec![Token::new(TokenType::Number, "22".to_string())]
        );
    }

    #[test]
    fn test_tokenize_expression() {
        let code = "(add 1 2)";
        let tokens = tokenize(code).unwrap();
        
        let expected = vec![
            Token::new(TokenType::Paren, "(".to_string()),
            Token::new(TokenType::Name, "add".to_string()),
            Token::new(TokenType::Number, "1".to_string()),
            Token::new(TokenType::Number, "2".to_string()),
            Token::new(TokenType::Paren, ")".to_string()),
        ];
        
        assert_eq!(tokens, expected);
    }
}
