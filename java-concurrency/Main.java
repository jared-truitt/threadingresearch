import java.util.Random;

public class Main {
    private static final int MATRIX_SIZE = 10000;
    private static final int NUM_WORKERS = 1000;
    
    public static void main(String[] args) throws InterruptedException {
        // Create the matrices
        int[][] a = makeMatrix(MATRIX_SIZE);
        int[][] b = makeMatrix(MATRIX_SIZE);
        int[][] c = new int[MATRIX_SIZE][MATRIX_SIZE];
        
        // Set up a wait group to ensure all threads complete before program exits
        Thread[] threads = new Thread[NUM_WORKERS];
        
        // Divide the matrix into smaller submatrices and assign each submatrix to a thread
        int submatrixSize = MATRIX_SIZE / NUM_WORKERS;
        for (int i = 0; i < NUM_WORKERS; i++) {
            int startRow = i * submatrixSize;
            int endRow = (i + 1) * submatrixSize;
            threads[i] = new Thread(new Worker(startRow, endRow, submatrixSize, a, b, c));
            threads[i].start();
        }
        
        // Wait for all threads to complete
        for (int i = 0; i < NUM_WORKERS; i++) {
            threads[i].join();
        }
    }
    
    private static int[][] makeMatrix(int size) {
        Random rand = new Random();
        int[][] matrix = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rand.nextInt(10);
            }
        }
        return matrix;
    }
    
    private static class Worker implements Runnable {
        private final int startRow;
        private final int endRow;
        private final int submatrixSize;
        private final int[][] a;
        private final int[][] b;
        private final int[][] c;
        
        public Worker(int startRow, int endRow, int submatrixSize, int[][] a, int[][] b, int[][] c) {
            this.startRow = startRow;
            this.endRow = endRow;
            this.submatrixSize = submatrixSize;
            this.a = a;
            this.b = b;
            this.c = c;
        }
        
        @Override
        public void run() {
            // Multiply the submatrix
            long startTime = System.nanoTime();
            for (int i = startRow; i < endRow; i++) {
                for (int j = 0; j < MATRIX_SIZE; j++) {
                    for (int k = 0; k < MATRIX_SIZE; k++) {
                        c[i][j] += a[i][k] * b[k][j];
                    }
                }
            }
            
            // Print out the elapsed time
            long elapsedTime = System.nanoTime() - startTime;
            System.out.printf("Worker %d completed in %d ns\n", Thread.currentThread().getId(), elapsedTime);
        }
    }
}