package testSuite;

import liquidjava.specification.Refinement;

@SuppressWarnings("unused")
public class ErrorInvalidRefinement {

    void test() {
        @Refinement("x") // Invalid Refinement Error
        int x = 0;
    }
}
