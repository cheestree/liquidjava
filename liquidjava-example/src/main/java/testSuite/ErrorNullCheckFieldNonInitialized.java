// Refinement Error
package testSuite;

import liquidjava.specification.Refinement;

@SuppressWarnings("unused")
public class ErrorNullCheckFieldNonInitialized {
    
    Double d; // implicit null

    void test() {
        @Refinement("_ != null")
        Double d2 = d; // should be error
    }
}
