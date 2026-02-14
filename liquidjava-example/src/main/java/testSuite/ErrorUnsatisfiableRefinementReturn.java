// Unsatisfiable Refinement Error
package testSuite;

import liquidjava.specification.Refinement;

public class ErrorUnsatisfiableRefinementReturn {
    @Refinement("_ % 2 == 3")
    int test() {
        return 1;
    }
}
