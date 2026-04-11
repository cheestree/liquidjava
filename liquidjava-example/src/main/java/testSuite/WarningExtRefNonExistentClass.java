package testSuite;

import liquidjava.specification.ExternalRefinementsFor;

@ExternalRefinementsFor("non.existent.Class") // Expected: Warning
public interface WarningExtRefNonExistentClass {
    public void NonExistentClass(); 
}
