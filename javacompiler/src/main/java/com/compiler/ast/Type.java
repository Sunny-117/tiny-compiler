package com.compiler.ast;

public class Type {
    private final String name;
    private final boolean isArray;

    public Type(String name) {
        this(name, false);
    }

    public Type(String name, boolean isArray) {
        this.name = name;
        this.isArray = isArray;
    }

    public String getName() {
        return name;
    }

    public boolean isArray() {
        return isArray;
    }

    public boolean isPrimitive() {
        return name.equals("int") || name.equals("boolean") || name.equals("void");
    }

    @Override
    public String toString() {
        return name + (isArray ? "[]" : "");
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Type)) return false;
        Type other = (Type) obj;
        return name.equals(other.name) && isArray == other.isArray;
    }

    @Override
    public int hashCode() {
        return name.hashCode() * 31 + (isArray ? 1 : 0);
    }
}
