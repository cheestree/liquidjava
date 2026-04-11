package testSuite;

import liquidjava.specification.StateRefinement;

public class ErrorSyntaxStateRefinement {
    
    @StateRefinement(from="$", to="#") // Expected: Syntax Error
    void test() {}
}
