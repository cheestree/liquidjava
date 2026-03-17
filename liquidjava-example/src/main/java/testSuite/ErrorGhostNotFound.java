package testSuite;

import liquidjava.specification.Refinement;

public class ErrorGhostNotFound {

    public static void main(String[] args) {
        @Refinement("notFound(x)")
        int x = 5; // Not Found Error
    }
}
