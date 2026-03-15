package testSuite;

import liquidjava.specification.Refinement;

@SuppressWarnings("unused")
public class ErrorBoxedTypes {
    public static void boxedBoolean() {
        @Refinement("_ == true")
        Boolean b = false; // Refinement Error
    }

    public static void boxedInteger() {
        @Refinement("_ > 0")
        Integer j = -1; // Refinement Error
    }

    public static void boxedDouble() {
        @Refinement("_ > 0")
        Double d = -1.0; // Refinement Error
    }
}
