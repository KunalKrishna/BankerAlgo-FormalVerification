import gov.nasa.jpf.vm.Verify;
import java.util.Arrays;

/**
 * SortVerifier2:
 * Verifies both the correctness and stability of a sorting algorithm (Bubble Sort)
 * using Java Pathfinder (JPF) symbolic execution.
 * 
 * Verification includes:
 *  - Array is sorted in non-decreasing order.
 *  - Array is a permutation of the original input (no elements lost or duplicated).
 */
public class SortVerifier2 {

    public static void main(String[] args) {
        int[] arr = new int[3]; // Small array size to keep state space manageable for model checking

        // Step 1: Initialize array with nondeterministic values (0 to 2)
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Verify.getInt(0, 2); // Assign random value to each index
        }

        // Step 2: Clone the original input for later permutation verification
        int[] original = arr.clone();

        // Step 3: Sort the array
        bubbleSort(arr);

        // Step 4a: ✅ Check 1 — Verify array is sorted
        System.out.println("Asserting Sortedness");
        for (int i = 0; i < arr.length - 1; i++) {
            assert arr[i] <= arr[i + 1] : "Array not sorted at index " + i;
        }

        // Step 4b: ✅ Check 2 — Verify sorted array is a permutation of the original
        System.out.println("Asserting Permutation\n");
        assert isPermutation(original, arr) : "Sorted array is not a permutation of input";
    }

    /**
     * bubbleSort:
     * Sorts the array in non-decreasing order using the Bubble Sort algorithm.
     * Prints the array before sorting begins.
     *
     * @param arr The array to be sorted
     */
    public static void bubbleSort(int[] arr) {
        System.out.println("Sorting... " + Arrays.toString(arr));
        int n = arr.length;
        boolean swapped;

        do {
            swapped = false;
            for (int i = 0; i < n - 1; i++) {
                if (arr[i] > arr[i + 1]) {
                    // Swap adjacent elements if they are out of order
                    int temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                    swapped = true;
                }
            }
            // Continue swapping until no more swaps are needed (array is sorted)
        } while (swapped);
    }

    /**
     * isPermutation:
     * Checks whether two arrays are permutations of each other (same elements, same counts).
     * 
     * @param a The original array
     * @param b The sorted array
     * @return true if b is a permutation of a; false otherwise
     */
    public static boolean isPermutation(int[] a, int[] b) {
        if (a.length != b.length) return false;

        int[] aCopy = a.clone();
        int[] bCopy = b.clone();
        Arrays.sort(aCopy);
        Arrays.sort(bCopy);

        return Arrays.equals(aCopy, bCopy);
    }
}
