import gov.nasa.jpf.vm.Verify;
import java.util.Arrays;

public class SortVerifier2 {

    public static void main(String[] args) {
        int[] arr = new int[3];

        // Fill array with nondeterministic values (0 to 2)
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Verify.getInt(0, 2);
        }

        // Clone original input for permutation check
        int[] original = arr.clone();

        bubbleSort(arr);

        // ✅ Check 1: Sorted order
        System.out.println("Asserting Sortedness");
        for (int i = 0; i < arr.length - 1; i++) {
            assert arr[i] <= arr[i + 1] : "Array not sorted at index " + i;
        }

        // ✅ Check 2: Permutation of original
        System.out.println("Asserting Permutation\n");
        assert isPermutation(original, arr) : "Sorted array is not a permutation of input";
    }

    public static void bubbleSort(int[] arr) {
        System.out.println("Sorting... "+Arrays.toString(arr));
        int n = arr.length;
        boolean swapped;
        do {
            swapped = false;
            for (int i = 0; i < n - 1; i++) {
                if (arr[i] > arr[i + 1]) {
                    int temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                    swapped = true;
                }
            }
        } while (swapped);
    }

    // ✅ Check if two arrays are permutations of each other
    public static boolean isPermutation(int[] a, int[] b) {
        if (a.length != b.length) return false;

        int[] aCopy = a.clone();
        int[] bCopy = b.clone();
        Arrays.sort(aCopy);
        Arrays.sort(bCopy);
        return Arrays.equals(aCopy, bCopy);
    }
}
