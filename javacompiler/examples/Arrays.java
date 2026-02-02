class Arrays {
    int sumArray(int[] arr, int length) {
        int sum;
        sum = 0;
        
        int i;
        for (i = 0; i < length; i = i + 1) {
            sum = sum + arr[i];
        }
        
        return sum;
    }
    
    void main() {
        int[] numbers;
        numbers = new int[10];
        
        int i;
        for (i = 0; i < 10; i = i + 1) {
            numbers[i] = i * 2;
        }
        
        int total;
        total = sumArray(numbers, 10);
    }
}
