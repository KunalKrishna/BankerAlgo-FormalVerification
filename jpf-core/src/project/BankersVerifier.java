import gov.nasa.jpf.vm.Verify;
import java.util.Arrays;

/**
 * Formal Verification of Banker's Algorithm using Java Pathfinder (JPF).
 * Randomly generates Available, Max, and Allocation matrices for P processes and R resources,
 * then verifies whether the system is in a safe state.
 */
public class BankersVerifier {

    static final int P = 2; // Number of processes (keep small for exploration)
    static final int R = 2; // Number of resource types

    public static void main(String[] args) {
        int[] available = new int[R];
        int[][] max = new int[P][R];
        int[][] allocation = new int[P][R];
        int[][] need = new int[P][R];

        // Randomly generate Available resources
        for (int j = 0; j < R; j++) {
            available[j] = Verify.getInt(0, 3); // Available amount between 0 and 3
        }

        // Randomly generate Max and Allocation matrices
        for (int i = 0; i < P; i++) {
            for (int j = 0; j < R; j++) {
                max[i][j] = Verify.getInt(0, 3); // Max demand between 0 and 3
                allocation[i][j] = Verify.getInt(0, max[i][j]); // Allocation cannot exceed Max
                need[i][j] = max[i][j] - allocation[i][j]; // Need is derived
            }
        }

        // Print generated matrices
        System.out.println("Available : ");
        System.out.println(Arrays.toString(available));
        System.out.println("Max : ");
        printMatrix(max);
        System.out.println("Allocation : ");
        printMatrix(allocation);
        System.out.println("Need : ");
        printMatrix(need);

        // Invariant check: Ensure that Need = Max - Allocation
        for (int i = 0; i < P; i++) {
            for (int j = 0; j < R; j++) {
                assert need[i][j] == max[i][j] - allocation[i][j] : "Need mismatch";
            }
        }

        // Check system safety
        boolean safe = isSafe(available, allocation, need);

        // If system is safe, validate by exhaustively checking all process execution orders
        if (safe) {
            assert validateSafeSequence(available, allocation, need) : "False positive: claimed safe but no sequence exists!";
            System.out.println("[Safe Sequence Validated]");
        } else {
            System.out.println("*** Unsafe to process for given input config ***");
        }
        System.out.println("-------");
    }

    /**
     * Core Banker's Algorithm to determine if the system is currently in a safe state.
     */
    static boolean isSafe(int[] available, int[][] allocation, int[][] need) {
        int[] work = Arrays.copyOf(available, R); // Work array to simulate resource availability
        boolean[] finish = new boolean[P]; // Tracks which processes have completed
        boolean progress;

        do {
            progress = false;
            // Try to find a process that can complete
            for (int i = 0; i < P; i++) {
                if (!finish[i] && canProceed(need[i], work)) {
                    // Simulate process completion
                    for (int j = 0; j < R; j++) {
                        work[j] += allocation[i][j]; // Release resources
                    }
                    finish[i] = true;
                    progress = true;
                }
            }
        } while (progress);

        // Check if all processes were able to complete
        for (boolean f : finish) {
            if (!f) return false;
        }
        return true;
    }

    /**
     * Helper function: Checks if a process can proceed with current available resources.
     */
    static boolean canProceed(int[] needRow, int[] work) {
        for (int j = 0; j < R; j++) {
            if (needRow[j] > work[j]) return false; // Not enough resources
        }
        return true;
    }

    /**
     * Brute-force check: Validate if there exists ANY order of process execution
     * that leads to all processes completing successfully.
     * Uses Depth First Search (DFS) to explore all possible orderings.
     */
    static boolean validateSafeSequence(int[] available, int[][] allocation, int[][] need) {
        return dfs(new boolean[P], Arrays.copyOf(available, R), allocation, need, 0);
    }

    /**
     * DFS search for any valid sequence leading to all processes completing.
     * Backtracking is used when a dead end is encountered.
     */
    static boolean dfs(boolean[] finished, int[] work, int[][] allocation, int[][] need, int depth) {
        if (depth == P) return true; // All processes finished successfully

        for (int i = 0; i < P; i++) {
            if (!finished[i] && canProceed(need[i], work)) {
                finished[i] = true;
                int[] savedWork = Arrays.copyOf(work, R); // Save current work state for backtracking

                // Simulate execution of process i
                for (int j = 0; j < R; j++) {
                    work[j] += allocation[i][j];
                }

                if (dfs(finished, work, allocation, need, depth + 1)) {
                    return true; // Found a valid sequence
                }

                // Backtrack
                finished[i] = false;
                work = savedWork;
            }
        }
        return false; // No valid sequence found
    }

    /**
     * Utility function: Nicely prints a 2D matrix.
     */
    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int element : row) {
                System.out.printf("%4d", element); // 4-character width for alignment
            }
            System.out.println(); // New line after each row
        }
    }
}
