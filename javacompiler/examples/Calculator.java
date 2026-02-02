class Calculator {
    int add(int a, int b) {
        return a + b;
    }
    
    int subtract(int a, int b) {
        return a - b;
    }
    
    int multiply(int a, int b) {
        return a * b;
    }
    
    int divide(int a, int b) {
        if (b == 0) {
            return 0;
        }
        return a / b;
    }
    
    void main() {
        int x;
        int y;
        x = 10;
        y = 5;
        
        int sum;
        sum = add(x, y);
        
        int diff;
        diff = subtract(x, y);
        
        int product;
        product = multiply(x, y);
        
        int quotient;
        quotient = divide(x, y);
    }
}
