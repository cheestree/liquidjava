package testSuite;

import liquidjava.specification.Refinement;

@SuppressWarnings("unused")
public class ErrorInvalidRefinement {

    void invalidRefinement1() {
        @Refinement("x") // Invalid Refinement Error
        int x = 0;
    }

    void invalidRefinement2(@Refinement("y + 1") int y) { // Invalid Refinement Error
    }

    @Refinement("_ * 2") // Invalid Refinement Error
    void invalidRefinement3() {
    }
}
