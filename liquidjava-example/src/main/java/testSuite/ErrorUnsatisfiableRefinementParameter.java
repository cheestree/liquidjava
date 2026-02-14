// Unsatisfiable Refinement Error
package testSuite;

import liquidjava.specification.Refinement;

public class ErrorUnsatisfiableRefinementParameter {

    void test(@Refinement("x == 0 && x == 1") int x) {}
}
