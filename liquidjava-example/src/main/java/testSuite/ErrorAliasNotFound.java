package testSuite;

import liquidjava.specification.Refinement;

public class ErrorAliasNotFound {

    public static void main(String[] args) {
        @Refinement("UndefinedAlias(x)") // Expected: Not Found Error
        int x = 5;
    }
}
