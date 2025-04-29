import gov.nasa.jpf.vm.Verify;

/**
 * SortVerifier:
 * Verifies that a Bubble Sort implementation correctly sorts an array.
 * Uses Java Pathfinder (JPF) to explore all possible initial array values (0 to 2).
 */
public class SortVerifier {

    public static void main(String[] args) {
        int[] arr = new int[3]; // Array of size 3 to keep the verification state space small

        // Step 1: Initialize the array with nondeterministic values (between 0 and 2)
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Verify.getInt(0, 2); // Randomized input for verification
        }

        // Step 2: Sort the array using bubble sort
        bubbleSort(arr);

        // Step 3: Verify that the array is sorted in non-decreasing order
        for (int i = 0; i < arr.length - 1; i++) {
            assert arr[i] <= arr[i + 1] : "Array not sorted at index " + i;
        }
    }

    /**
     * bubbleSort:
     * A simple Bubble Sort implementation that sorts an array in non-decreasing order.
     *
     * @param arr The array to be sorted
     */
    public static void bubbleSort(int[] arr) {
        int n = arr.length;
        boolean swapped;

        do {
            swapped = false;
            for (int i = 0; i < n - 1; i++) {
                if (arr[i] > arr[i + 1]) {
                    // Swap adjacent elements if they are in the wrong order
                    int temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                    swapped = true;
                }
            }
            // After each full pass, the largest element moves to the end
            // Continue passes until no swaps are needed (array is sorted)
        } while (swapped);
    }
}
