import gov.nasa.jpf.vm.Verify;

public class AddDemo {
    public static void main(String[] args) {
        int a = Verify.getInt(0, 5); //introduces nondeterminism — JPF explores all values 0 to 5
        int b = Verify.getInt(0, 5);
        int sum = a + b;
        assert sum >= a && sum >= b; // Safe property
    }
}