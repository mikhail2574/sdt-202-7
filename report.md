# Homework 4 Report

Code and benchmark implementation are in [src/Main.java](/Users/mykhailo/Documents/GitHub/sdt-202-7/src/Main.java) and [src/SortingBenchmark.java](/Users/mykhailo/Documents/GitHub/sdt-202-7/src/SortingBenchmark.java). Measured benchmark data are in [out/time_results.csv](/Users/mykhailo/Documents/GitHub/sdt-202-7/out/time_results.csv) and [out/operation_results.csv](/Users/mykhailo/Documents/GitHub/sdt-202-7/out/operation_results.csv).

## Method

The benchmark follows the Homework 3 approach. For each input size, one set of random arrays is generated with a fixed seed, and identical copies of the same arrays are passed to Merge Sort, Quick Sort, and Heap Sort. Array sizes were `1,000`, `5,000`, `10,000`, `20,000`, `50,000`, and `100,000`, with `10` runs per size.

Two measurements were collected:

1. Execution time in nanoseconds.
2. Count of the dominant comparison operation.

The dominant counted operation for each algorithm was:

- Merge Sort: comparisons inside `merge`
- Quick Sort: comparisons with the pivot inside `partition`
- Heap Sort: comparisons inside `heapify`

## Results

### A. Execution time

| Size | Merge Sort | Quick Sort | Heap Sort |
| --- | ---: | ---: | ---: |
| 1,000 | 140,004 ns | 65,308 ns | 252,595 ns |
| 5,000 | 320,395 ns | 255,199 ns | 396,087 ns |
| 10,000 | 636,762 ns | 447,979 ns | 728,029 ns |
| 20,000 | 1,368,454 ns | 957,949 ns | 1,439,837 ns |
| 50,000 | 3,941,462 ns | 3,060,933 ns | 5,214,812 ns |
| 100,000 | 8,168,254 ns | 5,600,171 ns | 12,162,412 ns |

### B. Dominant operation count

| Size | Merge Sort | Quick Sort | Heap Sort |
| --- | ---: | ---: | ---: |
| 1,000 | 8,702 | 10,861 | 16,845 |
| 5,000 | 55,219 | 74,030 | 107,650 |
| 10,000 | 120,457 | 156,004 | 235,343 |
| 20,000 | 260,892 | 335,644 | 510,795 |
| 50,000 | 718,114 | 937,739 | 1,409,845 |
| 100,000 | 1,536,392 | 2,025,037 | 3,019,627 |

## Answers

The order of growth suggested by both approaches is `Theta(n log n)` for all three algorithms. The timing curves rise much slower than quadratic growth, and the comparison counts scale proportionally to `n log n`, which matches the textbook analysis.

Based on execution time, **Quick Sort** was the most efficient algorithm in this experiment. It had the lowest runtime for every tested array size.

Based on execution time, **Heap Sort** was the least efficient algorithm in this experiment. It was consistently the slowest and also had the largest comparison count.

Merge Sort was in between: it used fewer comparisons than Quick Sort, but the extra work during merging made it slower in actual runtime.
