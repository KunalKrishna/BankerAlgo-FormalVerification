import gov.nasa.jpf.vm.Verify;

public class SortVerifier {

    public static void main(String[] args) {
        int[] arr = new int[3]; // Small size to keep the state space manageable

        // Fill array with nondeterministic values (0 to 2)
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Verify.getInt(0, 2);
        }

        bubbleSort(arr);

        // Verify the array is sorted in non-decreasing order
        for (int i = 0; i < arr.length - 1; i++) {
            assert arr[i] <= arr[i + 1] : "Array not sorted at index " + i;
        }
    }

    public static void bubbleSort(int[] arr) {
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
}
