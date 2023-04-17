package main

import (
	"fmt"
	"math/rand"
	"sync"
	"time"
)

const (
	matrixSize = 10000     // the size of the square matrix
	numWorkers = 200    // the number of worker goroutines to use
)

func main() {
	// Create the matrices
	var a = makeMatrix(matrixSize)
	var b = makeMatrix(matrixSize)
	var c = make([][]int, matrixSize)
	for i := 0; i < matrixSize; i++ {
		c[i] = make([]int, matrixSize)
	}

	// Set up a wait group to ensure all goroutines complete before program exits
	var wg sync.WaitGroup

	// Set up a channel to receive results from each worker
	results := make(chan int, numWorkers)

	// Divide the matrix into smaller submatrices and assign each submatrix to a worker
	submatrixSize := matrixSize / numWorkers
	for i := 0; i < numWorkers; i++ {
		wg.Add(1)
		startRow := i * submatrixSize
		endRow := (i + 1) * submatrixSize
		go worker(i, startRow, endRow, submatrixSize, a, b, c, results, &wg)
	}

	// Wait for all workers to complete
	wg.Wait()

	close(results)

	for i := 0; i < numWorkers; i++ {
		<-results
	}
}

func worker(id, startRow, endRow, submatrixSize int, a, b, c [][]int, results chan int, wg *sync.WaitGroup) {
	defer wg.Done()

	// Multiply the submatrix
	startTime := time.Now()
	for i := startRow; i < endRow; i++ {
		for j := 0; j < matrixSize; j++ {
			for k := 0; k < matrixSize; k++ {
				c[i][j] += a[i][k] * b[k][j]
			}
		}
	}

	// Send the result to the results channel and print out the elapsed time
	elapsed := time.Since(startTime)
	results <- id
	fmt.Printf("Worker %d completed in %v\n", id, elapsed)
}

func makeMatrix(size int) [][]int {
	matrix := make([][]int, size)
	for i := 0; i < size; i++ {
		matrix[i] = make([]int, size)
		for j := 0; j < size; j++ {
			matrix[i][j] = rand.Intn(10)
		}
	}
	return matrix
}
