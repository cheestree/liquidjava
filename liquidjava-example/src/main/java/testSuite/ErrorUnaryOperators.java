package testSuite;

import liquidjava.specification.Refinement;

@SuppressWarnings("unused")
public class ErrorUnaryOperators {
    public static void unaryOperator1() {
        @Refinement("_ < 10")
        int v = 3;
        v--;
        @Refinement("_ >= 10")
        int s = 10;
        s--; // Refinement Error
    }

    public static void unaryOperator2() {
        @Refinement("b > 0")
        int b = 8;
        b = -b; // Refinement Error
    }
}
