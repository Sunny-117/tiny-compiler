class Fibonacci {
    int fib(int n) {
        if (n <= 1) {
            return n;
        }
        return fib(n - 1) + fib(n - 2);
    }
    
    void main() {
        int result;
        result = fib(10);
        System.out.println("Fibonacci(10) = ");
    }
}
