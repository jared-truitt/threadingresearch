#include <iostream>
#include <vector>
#include <chrono>
#include <thread>
#include <mutex>

const int matrixSize = 330;
const int numWorkers = 4;

std::vector<std::vector<int>> makeMatrix(int size) {
    std::vector<std::vector<int>> matrix(size, std::vector<int>(size));
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            matrix[i][j] = rand() % 10;
        }
    }
    return matrix;
}

void worker(int id, int startRow, int endRow, int submatrixSize, std::vector<std::vector<int>>& a, std::vector<std::vector<int>>& b, std::vector<std::vector<int>>& c, std::vector<int>& results, std::mutex& mtx) {
    auto startTime = std::chrono::steady_clock::now();

    // Multiply the submatrix
    for (int i = startRow; i < endRow; i++) {
        for (int j = 0; j < matrixSize; j++) {
            for (int k = 0; k < matrixSize; k++) {
                c[i][j] += a[i][k] * b[k][j];
            }
        }
    }

    // Send the result to the results vector and print out the elapsed time
    auto elapsed = std::chrono::steady_clock::now() - startTime;
    std::lock_guard<std::mutex> guard(mtx);
    results[id] = id;
    std::cout << "Worker " << id << " completed in " << std::chrono::duration<double, std::milli>(elapsed).count() << "ms\n";
}

int main() {
    // Seed the random number generator
    srand(time(nullptr));

    // Create the matrices
    auto a = makeMatrix(matrixSize);
    auto b = makeMatrix(matrixSize);
    auto c = makeMatrix(matrixSize);

    // Set up a mutex to protect the results vector
    std::mutex mtx;

    // Set up a vector to receive results from each worker
    std::vector<int> results(numWorkers);

    // Divide the matrix into smaller submatrices and assign each submatrix to a worker
    int submatrixSize = matrixSize / numWorkers;
    std::vector<std::thread> threads;
    for (int i = 0; i < numWorkers; i++) {
        int startRow = i * submatrixSize;
        int endRow = (i + 1) * submatrixSize;
        threads.emplace_back(worker, i, startRow, endRow, submatrixSize, std::ref(a), std::ref(b), std::ref(c), std::ref(results), std::ref(mtx));
    }

    // Wait for all workers to complete
    for (auto& thread : threads) {
        thread.join();
    }

    return 0;
}
