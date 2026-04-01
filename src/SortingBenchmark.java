import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

public class SortingBenchmark {
    private static final long SEED = 42L;
    private static final int[] SIZES = {1000, 5000, 10000, 20000, 50000, 100000};
    private static final int REPEATS = 10;

    static class SortResult {
        long comparisons;
    }

    public static void main(String[] args) throws Exception {
        String outDir = args.length > 0 ? args[0] : "out";
        benchmark(outDir);
    }

    private static void warmup() {
        Random rnd = new Random(SEED);
        for (int k = 0; k < 5; k++) {
            int[] base = new int[5000];
            for (int i = 0; i < base.length; i++) {
                base[i] = rnd.nextInt();
            }

            mergeSort(Arrays.copyOf(base, base.length));
            quickSort(Arrays.copyOf(base, base.length));
            heapSort(Arrays.copyOf(base, base.length));
        }
    }

    private static void benchmark(String outDir) throws IOException {
        warmup();

        File dir = new File(outDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Cannot create output directory: " + outDir);
        }

        try (PrintWriter timeCsv = new PrintWriter(new FileWriter(new File(dir, "time_results.csv")));
             PrintWriter opsCsv = new PrintWriter(new FileWriter(new File(dir, "operation_results.csv")))) {

            timeCsv.println("size,merge_ns,quick_ns,heap_ns");
            opsCsv.println("size,merge_comparisons,quick_comparisons,heap_comparisons");

            for (int n : SIZES) {
                long mergeTimeSum = 0;
                long quickTimeSum = 0;
                long heapTimeSum = 0;

                long mergeCompSum = 0;
                long quickCompSum = 0;
                long heapCompSum = 0;

                Random rnd = new Random(SEED + n);
                int[][] testArrays = new int[REPEATS][n];
                for (int r = 0; r < REPEATS; r++) {
                    for (int i = 0; i < n; i++) {
                        testArrays[r][i] = rnd.nextInt(1_000_000);
                    }
                }

                for (int r = 0; r < REPEATS; r++) {
                    int[] base = testArrays[r];

                    int[] mergeArray = Arrays.copyOf(base, base.length);
                    long t1 = System.nanoTime();
                    SortResult mergeResult = mergeSort(mergeArray);
                    long t2 = System.nanoTime();
                    ensureSorted(mergeArray);
                    mergeTimeSum += (t2 - t1);
                    mergeCompSum += mergeResult.comparisons;

                    int[] quickArray = Arrays.copyOf(base, base.length);
                    long t3 = System.nanoTime();
                    SortResult quickResult = quickSort(quickArray);
                    long t4 = System.nanoTime();
                    ensureSorted(quickArray);
                    quickTimeSum += (t4 - t3);
                    quickCompSum += quickResult.comparisons;

                    int[] heapArray = Arrays.copyOf(base, base.length);
                    long t5 = System.nanoTime();
                    SortResult heapResult = heapSort(heapArray);
                    long t6 = System.nanoTime();
                    ensureSorted(heapArray);
                    heapTimeSum += (t6 - t5);
                    heapCompSum += heapResult.comparisons;
                }

                long mergeAvgTime = mergeTimeSum / REPEATS;
                long quickAvgTime = quickTimeSum / REPEATS;
                long heapAvgTime = heapTimeSum / REPEATS;

                long mergeAvgComp = mergeCompSum / REPEATS;
                long quickAvgComp = quickCompSum / REPEATS;
                long heapAvgComp = heapCompSum / REPEATS;

                timeCsv.printf("%d,%d,%d,%d%n", n, mergeAvgTime, quickAvgTime, heapAvgTime);
                opsCsv.printf("%d,%d,%d,%d%n", n, mergeAvgComp, quickAvgComp, heapAvgComp);

                System.out.printf(
                        "n=%d | time(ns): merge=%d quick=%d heap=%d | comps: merge=%d quick=%d heap=%d%n",
                        n,
                        mergeAvgTime,
                        quickAvgTime,
                        heapAvgTime,
                        mergeAvgComp,
                        quickAvgComp,
                        heapAvgComp
                );
            }
        }
    }

    public static SortResult mergeSort(int[] list) {
        SortResult result = new SortResult();
        int[] temp = new int[list.length];
        mergeSort(list, temp, 0, list.length - 1, result);
        return result;
    }

    private static void mergeSort(int[] list, int[] temp, int first, int last, SortResult result) {
        if (first < last) {
            int mid = (first + last) / 2;
            mergeSort(list, temp, first, mid, result);
            mergeSort(list, temp, mid + 1, last, result);
            merge(list, temp, first, mid, last, result);
        }
    }

    private static void merge(int[] list, int[] temp, int first, int mid, int last, SortResult result) {
        int first1 = first;
        int last1 = mid;
        int first2 = mid + 1;
        int last2 = last;
        int index = first1;

        while (first1 <= last1 && first2 <= last2) {
            result.comparisons++;
            if (list[first1] <= list[first2]) {
                temp[index++] = list[first1++];
            } else {
                temp[index++] = list[first2++];
            }
        }

        while (first1 <= last1) {
            temp[index++] = list[first1++];
        }

        while (first2 <= last2) {
            temp[index++] = list[first2++];
        }

        for (int i = first; i <= last; i++) {
            list[i] = temp[i];
        }
    }

    public static SortResult quickSort(int[] list) {
        SortResult result = new SortResult();
        quickSort(list, 0, list.length - 1, result);
        return result;
    }

    private static void quickSort(int[] list, int first, int last, SortResult result) {
        if (last > first) {
            int pivotIndex = partition(list, first, last, result);
            quickSort(list, first, pivotIndex - 1, result);
            quickSort(list, pivotIndex + 1, last, result);
        }
    }

    private static int partition(int[] list, int first, int last, SortResult result) {
        int pivot = list[last];
        int low = first - 1;

        for (int high = first; high < last; high++) {
            result.comparisons++;
            if (list[high] <= pivot) {
                low++;
                swap(list, low, high);
            }
        }

        swap(list, low + 1, last);
        return low + 1;
    }

    public static SortResult heapSort(int[] list) {
        SortResult result = new SortResult();
        int heapSize = list.length;

        for (int i = heapSize / 2 - 1; i >= 0; i--) {
            heapify(list, heapSize, i, result);
        }

        for (int i = list.length - 1; i > 0; i--) {
            swap(list, 0, i);
            heapify(list, i, 0, result);
        }

        return result;
    }

    private static void heapify(int[] list, int heapSize, int root, SortResult result) {
        int largest = root;
        int leftChild = 2 * root + 1;
        int rightChild = 2 * root + 2;

        if (leftChild < heapSize) {
            result.comparisons++;
            if (list[leftChild] > list[largest]) {
                largest = leftChild;
            }
        }

        if (rightChild < heapSize) {
            result.comparisons++;
            if (list[rightChild] > list[largest]) {
                largest = rightChild;
            }
        }

        if (largest != root) {
            swap(list, root, largest);
            heapify(list, heapSize, largest, result);
        }
    }

    private static void swap(int[] list, int i, int j) {
        int temp = list[i];
        list[i] = list[j];
        list[j] = temp;
    }

    private static void ensureSorted(int[] list) {
        for (int i = 1; i < list.length; i++) {
            if (list[i - 1] > list[i]) {
                throw new IllegalStateException("Array is not sorted at index " + i);
            }
        }
    }
}
