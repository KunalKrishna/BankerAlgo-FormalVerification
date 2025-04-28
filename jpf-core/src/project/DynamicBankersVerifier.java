import gov.nasa.jpf.vm.Verify;
import java.util.Arrays;

public class DynamicBankersVerifier {

    static final int P = 2; // Number of processes
    static final int R = 2; // Number of resources

    public static void main(String[] args) {
        int[] available = new int[R];
        int[][] max = new int[P][R];
        int[][] allocation = new int[P][R];
        int[][] need = new int[P][R];

        // Step 1: Initialize symbolic values
        for (int j = 0; j < R; j++) {
            available[j] = Verify.getInt(0, 3);
        }

        for (int i = 0; i < P; i++) {
            for (int j = 0; j < R; j++) {
                max[i][j] = Verify.getInt(0, 3);
                allocation[i][j] = Verify.getInt(0, max[i][j]);
                need[i][j] = max[i][j] - allocation[i][j];
            }
        }

        System.out.println("Available : ");
        System.out.println(Arrays.toString(available));
        System.out.println("Max : ");
        printMatrix(max);
        System.out.println("Allocation : ");
        printMatrix(allocation);
        System.out.println("Need : ");
        printMatrix(need);

        // Initial invariant check
        for (int i = 0; i < P; i++) {
            for (int j = 0; j < R; j++) {
                assert need[i][j] == max[i][j] - allocation[i][j] : "Need mismatch at start";
            }
        }

        // Step 2: Simulate multiple resource requests
        for (int step = 0; step < 2; step++) {  // simulate 2 dynamic requests
            int process = Verify.getInt(0, P-1); // Randomly pick a process

            int[] request = new int[R];
            for (int j = 0; j < R; j++) {
                // Random request: between 0 and need[i][j]
                request[j] = (need[process][j] == 0) ? 0 : Verify.getInt(0, need[process][j]);
            }

            System.out.println("Process " + process + " requests: " + Arrays.toString(request));

            // Step 3: Attempt the request
            if (requestGranted(available, allocation, need, process, request)) {
                System.out.println("Request granted.");
            } else {
                System.out.println("Request denied to avoid unsafe state.");
            }

            // Step 4: Invariant check after each operation
            for (int i = 0; i < P; i++) {
                for (int j = 0; j < R; j++) {
                    assert need[i][j] == max[i][j] - allocation[i][j] : "Need mismatch after request";
                }
            }
        }
    }

    // Try to grant the request using Banker's safety check
    static boolean requestGranted(int[] available, int[][] allocation, int[][] need, int process, int[] request) {
        // Check if request <= available
        for (int j = 0; j < R; j++) {
            if (request[j] > available[j]) return false; // Cannot even fulfill physically
        }

        // Tentatively allocate
        for (int j = 0; j < R; j++) {
            available[j] -= request[j];
            allocation[process][j] += request[j];
            need[process][j] -= request[j];
        }

        // Check if system remains safe
        boolean safe = isSafe(available, allocation, need);

        // If not safe, rollback
        if (!safe) {
            for (int j = 0; j < R; j++) {
                available[j] += request[j];
                allocation[process][j] -= request[j];
                need[process][j] += request[j];
            }
        }

        return safe;
    }

    static boolean isSafe(int[] available, int[][] allocation, int[][] need) {
        int[] work = Arrays.copyOf(available, R);
        boolean[] finish = new boolean[P];
        boolean progress;

        do {
            progress = false;
            for (int i = 0; i < P; i++) {
                if (!finish[i] && canProceed(need[i], work)) {
                    for (int j = 0; j < R; j++) {
                        work[j] += allocation[i][j];
                    }
                    finish[i] = true;
                    progress = true;
                }
            }
        } while (progress);

        for (boolean f : finish) {
            if (!f) return false;
        }
        return true;
    }

    static boolean canProceed(int[] needRow, int[] work) {
        for (int j = 0; j < R; j++) {
            if (needRow[j] > work[j]) return false;
        }
        return true;
    }

    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int element : row) {
                System.out.printf("%4d", element); // Adjust the width (4 in this case) as needed
            }
            System.out.println(); // New line after each row
        }
    }
}