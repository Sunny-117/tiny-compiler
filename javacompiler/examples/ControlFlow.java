class ControlFlow {
    int max(int a, int b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
    }
    
    int factorial(int n) {
        int result;
        result = 1;
        
        int i;
        for (i = 1; i <= n; i = i + 1) {
            result = result * i;
        }
        
        return result;
    }
    
    int sumWhile(int n) {
        int sum;
        sum = 0;
        
        int i;
        i = 1;
        
        while (i <= n) {
            sum = sum + i;
            i = i + 1;
        }
        
        return sum;
    }
    
    void main() {
        int a;
        int b;
        a = 10;
        b = 20;
        
        int maximum;
        maximum = max(a, b);
        
        int fact;
        fact = factorial(5);
        
        int total;
        total = sumWhile(100);
    }
}
