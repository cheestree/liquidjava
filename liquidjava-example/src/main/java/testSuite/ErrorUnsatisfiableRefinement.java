// Unsatisfiable Refinement Error
package testSuite;

import liquidjava.specification.Refinement;

@SuppressWarnings("unused")
public class ErrorUnsatisfiableRefinement {
    void test() {
        @Refinement("_ > 0 && _ < 0")
        int x = 5;
    }
}
