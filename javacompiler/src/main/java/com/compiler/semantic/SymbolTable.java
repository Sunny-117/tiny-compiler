package com.compiler.semantic;

import com.compiler.ast.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SymbolTable {
    private final Stack<Map<String, Type>> scopes;

    public SymbolTable() {
        this.scopes = new Stack<>();
    }

    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    public void exitScope() {
        if (!scopes.isEmpty()) {
            scopes.pop();
        }
    }

    public void define(String name, Type type) {
        if (scopes.isEmpty()) {
            enterScope();
        }
        scopes.peek().put(name, type);
    }

    public Type lookup(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            Type type = scopes.get(i).get(name);
            if (type != null) {
                return type;
            }
        }
        return null;
    }

    public Type lookupInCurrentScope(String name) {
        if (scopes.isEmpty()) {
            return null;
        }
        return scopes.peek().get(name);
    }
}
