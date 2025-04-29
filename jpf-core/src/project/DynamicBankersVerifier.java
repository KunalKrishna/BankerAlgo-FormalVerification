import gov.nasa.jpf.vm.Verify;
import java.util.Arrays;

/**
 * DynamicBankersVerifier:
 * A dynamic simulation of the Banker's Algorithm with random resource requests,
 * verified using Java Pathfinder (JPF) to explore different execution paths.
 */
public class DynamicBankersVerifier {

    static final int P = 2; // Number of processes
    static final int R = 2; // Number of resource types

    public static void main(String[] args) {
        int[] available = new int[R];       // Resources available
        int[][] max = new int[P][R];         // Maximum demand of each process
        int[][] allocation = new int[P][R];  // Resources currently allocated to each process
        int[][] need = new int[P][R];         // Resources still needed by each process

        // Step 1: Initialize system state with symbolic (randomized) values
        for (int j = 0; j < R; j++) {
            available[j] = Verify.getInt(0, 3); // Available amount between 0 and 3
        }

        for (int i = 0; i < P; i++) {
            for (int j = 0; j < R; j++) {
                max[i][j] = Verify.getInt(0, 3);                  // Random maximum claim
                allocation[i][j] = Verify.getInt(0, max[i][j]);    // Allocation cannot exceed maximum
                need[i][j] = max[i][j] - allocation[i][j];         // Remaining need
            }
        }

        // Print the initial system state
        System.out.println("Available : ");
        System.out.println(Arrays.toString(available));
        System.out.println("Max : ");
        printMatrix(max);
        System.out.println("Allocation : ");
        printMatrix(allocation);
        System.out.println("Need : ");
        printMatrix(need);

        // Step 1.5: Initial invariant check
        for (int i = 0; i < P; i++) {
            for (int j = 0; j < R; j++) {
                assert need[i][j] == max[i][j] - allocation[i][j] : "Need mismatch at start";
            }
        }

        // Step 2: Simulate multiple dynamic resource requests
        for (int step = 0; step < 2; step++) {  // Simulate 2 random dynamic requests
            int process = Verify.getInt(0, P-1); // Randomly select a process

            int[] request = new int[R];
            for (int j = 0; j < R; j++) {
                request[j] = (need[process][j] == 0) ? 0 : Verify.getInt(0, need[process][j]); // Request up to what is needed
            }

            System.out.println("Process " + process + " requests: " + Arrays.toString(request));

            // Step 3: Attempt to grant the request
            if (requestGranted(available, allocation, need, process, request)) {
                System.out.println("Request granted.");
            } else {
                System.out.println("Request denied to avoid unsafe state.");
            }

            // Step 4: Invariant check after each request handling
            for (int i = 0; i < P; i++) {
                for (int j = 0; j < R; j++) {
                    assert need[i][j] == max[i][j] - allocation[i][j] : "Need mismatch after request";
                }
            }
        }
    }

    /**
     * Attempt to grant a resource request:
     * - Check if available resources can satisfy the request
     * - Tentatively allocate the resources
     * - Check if system remains in a safe state
     * - Rollback if unsafe
     */
    static boolean requestGranted(int[] available, int[][] allocation, int[][] need, int process, int[] request) {
        // Step 1: Check if request can be satisfied with currently available resources
        for (int j = 0; j < R; j++) {
            if (request[j] > available[j]) return false; // Immediate denial if request exceeds availability
        }

        // Step 2: Tentatively allocate the resources
        for (int j = 0; j < R; j++) {
            available[j] -= request[j];
            allocation[process][j] += request[j];
            need[process][j] -= request[j];
        }

        // Step 3: Check system safety after allocation
        boolean safe = isSafe(available, allocation, need);

        // Step 4: If not safe, rollback tentative allocation
        if (!safe) {
            for (int j = 0; j < R; j++) {
                available[j] += request[j];
                allocation[process][j] -= request[j];
                need[process][j] += request[j];
            }
        }

        return safe;
    }

    /**
     * Banker's Algorithm safety check:
     * Determines if all processes can complete with current resource availability.
     */
    static boolean isSafe(int[] available, int[][] allocation, int[][] need) {
        int[] work = Arrays.copyOf(available, R); // Work vector simulates available resources
        boolean[] finish = new boolean[P];         // Tracks which processes have completed
        boolean progress;

        do {
            progress = false;
            for (int i = 0; i < P; i++) {
                if (!finish[i] && canProceed(need[i], work)) {
                    // Simulate process i finishing
                    for (int j = 0; j < R; j++) {
                        work[j] += allocation[i][j]; // Release resources
                    }
                    finish[i] = true;
                    progress = true;
                }
            }
        } while (progress);

        // If all processes finished, system is safe
        for (boolean f : finish) {
            if (!f) return false;
        }
        return true;
    }

    /**
     * Helper function:
     * Check if a process can proceed with its current needs and available work.
     */
    static boolean canProceed(int[] needRow, int[] work) {
        for (int j = 0; j < R; j++) {
            if (needRow[j] > work[j]) return false;
        }
        return true;
    }

    /**
     * Utility function:
     * Nicely prints a 2D matrix for Max, Allocation, or Need.
     */
    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int element : row) {
                System.out.printf("%4d", element); // Align elements with 4-space width
            }
            System.out.println(); // New line after each row
        }
    }
}
