package testSuite;

import liquidjava.specification.Refinement;

public class ErrorInvalidRefinementReturn {

    @Refinement("_ * 2") // Invalid Refinement Error
    void test() {
    }
}
