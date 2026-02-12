// Refinement Error
package testSuite;

import liquidjava.specification.Refinement;

public class ErrorNullCheckFieldAssignment {
    
    String s; // implicit null

    void test() {
        mustBeNull(s);
        s = "hello"; // implicit non-null after assignment
        mustBeNull(s); // error
    }

    void mustBeNull(@Refinement("_ == null") String s) {}
}
