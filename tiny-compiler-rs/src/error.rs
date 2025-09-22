use thiserror::Error;

/// Errors that can occur during compilation
#[derive(Error, Debug, Clone, PartialEq)]
pub enum CompilerError {
    #[error("Tokenizer error: {message}")]
    TokenizerError { message: String },
    
    #[error("Parser error: {message}")]
    ParserError { message: String },
    
    #[error("Transformer error: {message}")]
    TransformerError { message: String },
    
    #[error("Code generation error: {message}")]
    CodegenError { message: String },
    
    #[error("Unexpected token: {token} at position {position}")]
    UnexpectedToken { token: String, position: usize },
    
    #[error("Unexpected end of input")]
    UnexpectedEof,
    
    #[error("Invalid character: '{character}' at position {position}")]
    InvalidCharacter { character: char, position: usize },
}

pub type Result<T> = std::result::Result<T, CompilerError>;
