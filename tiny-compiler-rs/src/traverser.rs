use crate::ast::{AstNode, CallExpressionNode, ChildNode, NodeType, ProgramNode};

/// Parent node types for traversal context
#[derive(Debug)]
pub enum ParentNode<'a> {
    Program(&'a ProgramNode),
    CallExpression(&'a CallExpressionNode),
}

/// Method function type for visitor pattern using boxed closures
pub type MethodFn = Box<dyn Fn(&ChildNode, Option<&ParentNode>)>;
pub type ProgramMethodFn = Box<dyn Fn(&ProgramNode, Option<&ParentNode>)>;

/// Visitor option with enter and optional exit methods
pub struct VisitorOption {
    pub enter: Option<MethodFn>,
    pub exit: Option<MethodFn>,
}

/// Program visitor option
pub struct ProgramVisitorOption {
    pub enter: Option<ProgramMethodFn>,
    pub exit: Option<ProgramMethodFn>,
}

/// Visitor pattern for AST traversal
pub struct Visitor {
    pub program: Option<ProgramVisitorOption>,
    pub number_literal: Option<VisitorOption>,
    pub string_literal: Option<VisitorOption>,
    pub call_expression: Option<VisitorOption>,
}

impl Visitor {
    pub fn new() -> Self {
        Self {
            program: None,
            number_literal: None,
            string_literal: None,
            call_expression: None,
        }
    }

    /// Set program visitor
    pub fn program(mut self, enter: Option<ProgramMethodFn>, exit: Option<ProgramMethodFn>) -> Self {
        self.program = Some(ProgramVisitorOption { enter, exit });
        self
    }

    /// Set number literal visitor
    pub fn number_literal(mut self, enter: Option<MethodFn>, exit: Option<MethodFn>) -> Self {
        self.number_literal = Some(VisitorOption { enter, exit });
        self
    }

    /// Set string literal visitor
    pub fn string_literal(mut self, enter: Option<MethodFn>, exit: Option<MethodFn>) -> Self {
        self.string_literal = Some(VisitorOption { enter, exit });
        self
    }

    /// Set call expression visitor
    pub fn call_expression(mut self, enter: Option<MethodFn>, exit: Option<MethodFn>) -> Self {
        self.call_expression = Some(VisitorOption { enter, exit });
        self
    }
}

/// Traverse the AST using the visitor pattern
pub fn traverse(root: &ProgramNode, visitor: &Visitor) {
    traverse_node_program(root, None, visitor);
}

fn traverse_node_program(node: &ProgramNode, parent: Option<&ParentNode>, visitor: &Visitor) {
    // Enter
    if let Some(program_visitor) = &visitor.program {
        if let Some(enter) = &program_visitor.enter {
            enter(node, parent);
        }
    }

    // Traverse children
    let current_parent = ParentNode::Program(node);
    traverse_array(&node.body, Some(&current_parent), visitor);

    // Exit
    if let Some(program_visitor) = &visitor.program {
        if let Some(exit) = &program_visitor.exit {
            exit(node, parent);
        }
    }
}

fn traverse_array(array: &[ChildNode], parent: Option<&ParentNode>, visitor: &Visitor) {
    for node in array {
        traverse_node(node, parent, visitor);
    }
}

fn traverse_node(node: &ChildNode, parent: Option<&ParentNode>, visitor: &Visitor) {
    match node.node_type() {
        NodeType::Program => {
            // This shouldn't happen in normal traversal
            unreachable!("Program nodes should not appear as child nodes");
        }
        NodeType::CallExpression => {
            if let ChildNode::CallExpression(call_node) = node {
                // Enter
                if let Some(call_visitor) = &visitor.call_expression {
                    if let Some(enter) = &call_visitor.enter {
                        enter(node, parent);
                    }
                }

                // Traverse children
                let current_parent = ParentNode::CallExpression(call_node);
                traverse_array(&call_node.params, Some(&current_parent), visitor);

                // Exit
                if let Some(call_visitor) = &visitor.call_expression {
                    if let Some(exit) = &call_visitor.exit {
                        exit(node, parent);
                    }
                }
            }
        }
        NodeType::NumberLiteral => {
            // Enter
            if let Some(num_visitor) = &visitor.number_literal {
                if let Some(enter) = &num_visitor.enter {
                    enter(node, parent);
                }
            }

            // No children to traverse

            // Exit
            if let Some(num_visitor) = &visitor.number_literal {
                if let Some(exit) = &num_visitor.exit {
                    exit(node, parent);
                }
            }
        }
        NodeType::StringLiteral => {
            // Enter
            if let Some(str_visitor) = &visitor.string_literal {
                if let Some(enter) = &str_visitor.enter {
                    enter(node, parent);
                }
            }

            // No children to traverse

            // Exit
            if let Some(str_visitor) = &visitor.string_literal {
                if let Some(exit) = &str_visitor.exit {
                    exit(node, parent);
                }
            }
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::ast::{NumberLiteralNode, CallExpressionNode};
    use std::cell::RefCell;
    use std::rc::Rc;

    #[test]
    fn test_traverse_simple_program() {
        let mut program = ProgramNode::new();
        program.body.push(ChildNode::NumberLiteral(NumberLiteralNode::new("42".to_string())));

        let visited = Rc::new(RefCell::new(Vec::new()));
        let visited_clone1 = visited.clone();
        let visited_clone2 = visited.clone();
        let visited_clone3 = visited.clone();
        let visited_clone4 = visited.clone();

        let visitor = Visitor::new()
            .program(
                Some(Box::new(move |_node, _parent| {
                    visited_clone1.borrow_mut().push("program_enter".to_string());
                })),
                Some(Box::new(move |_node, _parent| {
                    visited_clone2.borrow_mut().push("program_exit".to_string());
                })),
            )
            .number_literal(
                Some(Box::new(move |_node, _parent| {
                    visited_clone3.borrow_mut().push("number_enter".to_string());
                })),
                Some(Box::new(move |_node, _parent| {
                    visited_clone4.borrow_mut().push("number_exit".to_string());
                })),
            );

        traverse(&program, &visitor);

        let visits = visited.borrow();
        assert_eq!(visits.len(), 4);
        assert_eq!(visits[0], "program_enter");
        assert_eq!(visits[1], "number_enter");
        assert_eq!(visits[2], "number_exit");
        assert_eq!(visits[3], "program_exit");
    }

    #[test]
    fn test_traverse_call_expression() {
        let mut program = ProgramNode::new();
        let mut call_expr = CallExpressionNode::new("add".to_string());
        call_expr.params.push(ChildNode::NumberLiteral(NumberLiteralNode::new("1".to_string())));
        call_expr.params.push(ChildNode::NumberLiteral(NumberLiteralNode::new("2".to_string())));
        program.body.push(ChildNode::CallExpression(call_expr));

        let visited = Rc::new(RefCell::new(Vec::new()));
        let visited_clone1 = visited.clone();
        let visited_clone2 = visited.clone();
        let visited_clone3 = visited.clone();

        let visitor = Visitor::new()
            .call_expression(
                Some(Box::new(move |_node, _parent| {
                    visited_clone1.borrow_mut().push("call_enter".to_string());
                })),
                Some(Box::new(move |_node, _parent| {
                    visited_clone2.borrow_mut().push("call_exit".to_string());
                })),
            )
            .number_literal(
                Some(Box::new(move |_node, _parent| {
                    visited_clone3.borrow_mut().push("number_enter".to_string());
                })),
                None,
            );

        traverse(&program, &visitor);

        let visits = visited.borrow();
        assert!(visits.contains(&"call_enter".to_string()));
        assert!(visits.contains(&"call_exit".to_string()));
        assert_eq!(visits.iter().filter(|&x| x == "number_enter").count(), 2);
    }
}
