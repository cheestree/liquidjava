package testSuite;

import liquidjava.specification.Refinement;

public class ErrorGhostNotFound {

    public void test() {
        @Refinement("notFound(x)")
        int x = 5; // Not Found Error
    }
}
