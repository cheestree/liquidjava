package testSuite;

import liquidjava.specification.Refinement;

public class ErrorInvalidRefinementParameter {

    void testInvalidRefinement(@Refinement("y + 1") int y) {} // Invalid Refinement Error

    int otherMethod() {
        return -1;
    }
}
