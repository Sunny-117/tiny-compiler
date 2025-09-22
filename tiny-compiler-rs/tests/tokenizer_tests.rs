use tiny_compiler_rs::tokenizer::{tokenize, Token, TokenType};
use tiny_compiler_rs::error::CompilerError;

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
fn test_tokenize_left_paren() {
    let code = "(";
    let tokens = tokenize(code).unwrap();
    let expected = vec![Token::new(TokenType::Paren, "(".to_string())];
    assert_eq!(tokens, expected);
}

#[test]
fn test_tokenize_right_paren() {
    let code = ")";
    let tokens = tokenize(code).unwrap();
    let expected = vec![Token::new(TokenType::Paren, ")".to_string())];
    assert_eq!(tokens, expected);
}

#[test]
fn test_tokenize_name() {
    let code = "add";
    let tokens = tokenize(code).unwrap();
    let expected = vec![Token::new(TokenType::Name, "add".to_string())];
    assert_eq!(tokens, expected);
}

#[test]
fn test_tokenize_number() {
    let code = "22";
    let tokens = tokenize(code).unwrap();
    let expected = vec![Token::new(TokenType::Number, "22".to_string())];
    assert_eq!(tokens, expected);
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

#[test]
fn test_tokenize_with_whitespace() {
    let code = "  (  add   1   2  )  ";
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

#[test]
fn test_tokenize_decimal_numbers() {
    let code = "3.14";
    let tokens = tokenize(code).unwrap();
    let expected = vec![Token::new(TokenType::Number, "3.14".to_string())];
    assert_eq!(tokens, expected);
}

#[test]
fn test_tokenize_underscore_in_name() {
    let code = "my_function";
    let tokens = tokenize(code).unwrap();
    let expected = vec![Token::new(TokenType::Name, "my_function".to_string())];
    assert_eq!(tokens, expected);
}

#[test]
fn test_tokenize_string_literal() {
    let code = r#""hello world""#;
    let tokens = tokenize(code).unwrap();
    let expected = vec![Token::new(TokenType::String, "hello world".to_string())];
    assert_eq!(tokens, expected);
}

#[test]
fn test_tokenize_string_with_escapes() {
    let code = r#""hello\nworld""#;
    let tokens = tokenize(code).unwrap();
    let expected = vec![Token::new(TokenType::String, "hello\nworld".to_string())];
    assert_eq!(tokens, expected);
}

#[test]
fn test_tokenize_empty_string() {
    let code = r#""""#;
    let tokens = tokenize(code).unwrap();
    let expected = vec![Token::new(TokenType::String, "".to_string())];
    assert_eq!(tokens, expected);
}

#[test]
fn test_tokenize_multiple_expressions() {
    let code = "(add 1 2) (multiply 3 4)";
    let tokens = tokenize(code).unwrap();
    
    let expected = vec![
        Token::new(TokenType::Paren, "(".to_string()),
        Token::new(TokenType::Name, "add".to_string()),
        Token::new(TokenType::Number, "1".to_string()),
        Token::new(TokenType::Number, "2".to_string()),
        Token::new(TokenType::Paren, ")".to_string()),
        Token::new(TokenType::Paren, "(".to_string()),
        Token::new(TokenType::Name, "multiply".to_string()),
        Token::new(TokenType::Number, "3".to_string()),
        Token::new(TokenType::Number, "4".to_string()),
        Token::new(TokenType::Paren, ")".to_string()),
    ];
    
    assert_eq!(tokens, expected);
}

#[test]
fn test_tokenize_invalid_character() {
    let code = "@";
    let result = tokenize(code);
    assert!(result.is_err());
    
    match result.unwrap_err() {
        CompilerError::InvalidCharacter { character, position } => {
            assert_eq!(character, '@');
            assert_eq!(position, 0);
        }
        _ => panic!("Expected InvalidCharacter error"),
    }
}

#[test]
fn test_tokenize_unterminated_string() {
    let code = r#""hello"#;
    let result = tokenize(code);
    assert!(result.is_err());
    
    match result.unwrap_err() {
        CompilerError::UnexpectedEof => {}
        _ => panic!("Expected UnexpectedEof error"),
    }
}

#[test]
fn test_tokenize_empty_input() {
    let code = "";
    let tokens = tokenize(code).unwrap();
    assert_eq!(tokens, vec![]);
}

#[test]
fn test_tokenize_only_whitespace() {
    let code = "   \n\t  ";
    let tokens = tokenize(code).unwrap();
    assert_eq!(tokens, vec![]);
}

#[test]
fn test_tokenize_case_sensitive_names() {
    let code = "Add ADD add";
    let tokens = tokenize(code).unwrap();
    
    let expected = vec![
        Token::new(TokenType::Name, "Add".to_string()),
        Token::new(TokenType::Name, "ADD".to_string()),
        Token::new(TokenType::Name, "add".to_string()),
    ];
    
    assert_eq!(tokens, expected);
}
