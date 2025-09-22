use serde::{Deserialize, Serialize};

/// Node types in the AST
#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
pub enum NodeType {
    Program,
    NumberLiteral,
    StringLiteral,
    CallExpression,
}

/// Base trait for all AST nodes
pub trait AstNode {
    fn node_type(&self) -> NodeType;
}

/// A number literal node
#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
pub struct NumberLiteralNode {
    pub value: String,
}

impl AstNode for NumberLiteralNode {
    fn node_type(&self) -> NodeType {
        NodeType::NumberLiteral
    }
}

/// A string literal node
#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
pub struct StringLiteralNode {
    pub value: String,
}

impl AstNode for StringLiteralNode {
    fn node_type(&self) -> NodeType {
        NodeType::StringLiteral
    }
}

/// A call expression node
#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
pub struct CallExpressionNode {
    pub name: String,
    pub params: Vec<ChildNode>,
    /// Context field used during transformation
    #[serde(skip_serializing_if = "Option::is_none")]
    pub context: Option<Vec<TransformedNode>>,
}

impl AstNode for CallExpressionNode {
    fn node_type(&self) -> NodeType {
        NodeType::CallExpression
    }
}

/// The root program node
#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
pub struct ProgramNode {
    pub body: Vec<ChildNode>,
    /// Context field used during transformation
    #[serde(skip_serializing_if = "Option::is_none")]
    pub context: Option<Vec<TransformedNode>>,
}

impl AstNode for ProgramNode {
    fn node_type(&self) -> NodeType {
        NodeType::Program
    }
}

/// Child nodes that can appear in the AST
#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
pub enum ChildNode {
    NumberLiteral(NumberLiteralNode),
    StringLiteral(StringLiteralNode),
    CallExpression(CallExpressionNode),
}

impl AstNode for ChildNode {
    fn node_type(&self) -> NodeType {
        match self {
            ChildNode::NumberLiteral(node) => node.node_type(),
            ChildNode::StringLiteral(node) => node.node_type(),
            ChildNode::CallExpression(node) => node.node_type(),
        }
    }
}

/// Transformed AST nodes for code generation
#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
pub enum TransformedNode {
    Program {
        body: Vec<TransformedNode>,
    },
    ExpressionStatement {
        expression: Box<TransformedNode>,
    },
    CallExpression {
        callee: Identifier,
        arguments: Vec<TransformedNode>,
    },
    NumberLiteral {
        value: String,
    },
    StringLiteral {
        value: String,
    },
}

/// Identifier node for transformed AST
#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
pub struct Identifier {
    pub name: String,
}

// Factory functions for creating AST nodes
impl NumberLiteralNode {
    pub fn new(value: String) -> Self {
        Self { value }
    }
}

impl StringLiteralNode {
    pub fn new(value: String) -> Self {
        Self { value }
    }
}

impl CallExpressionNode {
    pub fn new(name: String) -> Self {
        Self {
            name,
            params: Vec::new(),
            context: None,
        }
    }
}

impl ProgramNode {
    pub fn new() -> Self {
        Self {
            body: Vec::new(),
            context: None,
        }
    }
}

impl Default for ProgramNode {
    fn default() -> Self {
        Self::new()
    }
}

impl Identifier {
    pub fn new(name: String) -> Self {
        Self { name }
    }
}
