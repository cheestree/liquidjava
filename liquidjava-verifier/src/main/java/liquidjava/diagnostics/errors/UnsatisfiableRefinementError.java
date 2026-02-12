package liquidjava.diagnostics.errors;

import spoon.reflect.cu.SourcePosition;

/**
 * Error indicating that a refinement is intrinsically unsatisfiable (e.g., x > 0 && x < 0)
 * 
 * @see LJError
 */
public class UnsatisfiableRefinementError extends LJError {

    private final String refinement;

    public UnsatisfiableRefinementError(SourcePosition position, String refinement) {
        super("Unsatisfiable Refinement", "This predicate can never be true", position, null);
        this.refinement = refinement;
    }

    public String getRefinement() {
        return refinement;
    }
}
