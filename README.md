

# Install JPF

Clone JPF-core : https://github.com/javapathfinder/jpf-core.git 

# Create a System environment variable : 
JPF_CORE  = <path-to-jpf-core>

Add the variable to the path : %JPF_CORE%\bin 

# Test a small Program

Write a small Java program : ArrayTest.java
Write a JPF Configuration File : ArrayTest.jpf
## Run JPF on the Program
Compile java program : 
javac -cp "..\..\build\main" BankersAlgorithm.java (add classpath)
javac -cp "..\..\build\main;..\..\build\classes\java.base" ParallelBankersAlgorithm.java 


## Run JPF : jpf BankersAlgorithm.jpf 
To save output use : jpf BankersVerifier.jpf >> output.txt  (output = fileName)
Analyze the JPF Output 

### Step 1 : Write a small Java program

Create a file: TestProgram.java file with following content : 

public class TestProgram {
    public static void main(String[] args) {
        int x = 1000000;
        int y = 1000000;
        int result = x * y;  // Potential overflow


        if (result < 0) { // If overflow occurs, result becomes negative
            System.out.println("Overflow detected!");
        } else {
            System.out.println("Computation successful: " + result);
        }
    }
}


###  Step 2 : Write a JPF Configuration File
Create a file: TestProgram.jpf 

// JPF configuration file
target=TestProgram
classpath=.

### Step 3: Run JPF on the Program
Open the terminal (or command prompt).
Navigate to the folder where TestProgram.java is located.
Compile the program: javac TestProgram.java  
Run JPF: jpf TestProgram.jpf 

javac -d build\classes src\project\BankersVerifier.java
target=project.BankersVerifier
classpath=build\classes
jpf TestProgram.jpf


    Folder structure
    BankerAlgo-FormalVerification/
    │
    ├── jpf-core/
    │    ├── build/
    │    │    └── classes/
    │    └── src/
    │         └── project/
    │             └── BankersVerifier.java
    │
    ├── TestProgram.jpf
    └── README.md
