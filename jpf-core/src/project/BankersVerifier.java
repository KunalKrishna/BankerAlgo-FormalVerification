import gov.nasa.jpf.vm.Verify;
import java.util.Arrays;

public class BankersVerifier {

    static final int P = 2; // Number of processes (keep small for now)
    static final int R = 2; // Number of resource types (keep small)

    public static void main(String[] args) {
        int[] available = new int[R];
        int[][] max = new int[P][R];
        int[][] allocation = new int[P][R];
        int[][] need = new int[P][R];

        // Randomly generate Available, Max, Allocation
        for (int j = 0; j < R; j++) {
            available[j] = Verify.getInt(0, 3);
        }

        for (int i = 0; i < P; i++) {
            for (int j = 0; j < R; j++) {
                max[i][j] = Verify.getInt(0, 3); // max need
                allocation[i][j] = Verify.getInt(0, max[i][j]); // allocation cannot exceed max
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

        // Invariant check: need = max - allocation
        for (int i = 0; i < P; i++) {
            for (int j = 0; j < R; j++) {
                assert need[i][j] == max[i][j] - allocation[i][j] : "Need mismatch";
            }
        }

        boolean safe = isSafe(available, allocation, need);

        // Now we verify: if reported safe, there MUST exist a safe sequence
        if (safe) {
            assert validateSafeSequence(available, allocation, need) : "False positive: claimed safe but no sequence exists!";
            System.out.println("[Safe Sequence Validated]");
        } else {
            System.out.println("*** Unsafe to process for given input config ***");
        }
        System.out.println("-------");
    }

    // Implementation of the Banker's safety algorithm
    static boolean isSafe(int[] available, int[][] allocation, int[][] need) {
        int[] work = Arrays.copyOf(available, R);//copyOf(int[] original, int newLength)
        boolean[] finish = new boolean[P];
        boolean progress;

        do {
            progress = false;
            //can at least 1 process be completed?
            for (int i = 0; i < P; i++) {
                if (!finish[i] && canProceed(need[i], work)) {
                    // Simulate process completion
                    for (int j = 0; j < R; j++) {
                        work[j] += allocation[i][j];
                    }
                    finish[i] = true;
                    progress = true;
                }
            }
        } while (progress);

        // If all processes can finish => safe
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

    // Brute-force validate if ANY execution order can lead to success
    static boolean validateSafeSequence(int[] available, int[][] allocation, int[][] need) {
        return dfs(new boolean[P], Arrays.copyOf(available, R), allocation, need, 0);
    }

    static boolean dfs(boolean[] finished, int[] work, int[][] allocation, int[][] need, int depth) {
        if (depth == P) return true; // All processes finished

        for (int i = 0; i < P; i++) {
            if (!finished[i] && canProceed(need[i], work)) {
                finished[i] = true;
                int[] savedWork = Arrays.copyOf(work, R);

                // Simulate execution
                for (int j = 0; j < R; j++) {
                    work[j] += allocation[i][j];
                }

                if (dfs(finished, work, allocation, need, depth + 1)) {
                    return true;
                }

                // Backtrack
                finished[i] = false;
                work = savedWork;
            }
        }
        return false;
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
