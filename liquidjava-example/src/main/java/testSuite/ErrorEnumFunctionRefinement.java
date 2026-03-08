// Refinement Error
package testSuite;

import liquidjava.specification.Refinement;

@SuppressWarnings("unused")
class ErrorEnumFunctionRefinement {
    enum Color { Red, Green, Blue }

	Color c;

	Color changeColor(@Refinement("newColor == Color.Red || newColor == Color.Green") Color newColor) {
		c = newColor; // correct
		return c;
	}
	
	public static void main(String[] args) {
		ErrorEnumFunctionRefinement e = new ErrorEnumFunctionRefinement();
		e.changeColor(Color.Red); // correct
		e.changeColor(Color.Blue); // error
	}
}