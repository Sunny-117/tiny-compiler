pub mod ast;
pub mod tokenizer;
pub mod parser;
pub mod traverser;
pub mod transformer;
pub mod codegen;
pub mod compiler;
pub mod error;

pub use compiler::compile;
pub use error::CompilerError;

fn main() -> std::result::Result<(), Box<dyn std::error::Error>> {
    let code = "(add 2 (subtract 4 2))";
    let result = compile(code)?;
    println!("Input:  {}", code);
    println!("Output: {}", result);
    Ok(())
}
