package testSuite;

import liquidjava.specification.RefinementAlias;

@RefinementAlias("Positive(v) { v > 0 }") // Expected: Syntax Error
public class ErrorMissingAliasTypeParameter {}
