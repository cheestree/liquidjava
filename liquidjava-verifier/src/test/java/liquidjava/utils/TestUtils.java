package liquidjava.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import liquidjava.processor.context.Context;
import liquidjava.rj_language.Predicate;
import liquidjava.rj_language.opt.derivation_node.BinaryDerivationNode;
import liquidjava.rj_language.opt.derivation_node.DerivationNode;
import liquidjava.rj_language.opt.derivation_node.IteDerivationNode;
import liquidjava.rj_language.opt.derivation_node.UnaryDerivationNode;
import liquidjava.rj_language.opt.derivation_node.ValDerivationNode;
import liquidjava.rj_language.opt.derivation_node.VarDerivationNode;
import spoon.Launcher;
import spoon.reflect.factory.Factory;

public class TestUtils {

    public record ExpectedDiagnostic(String filePath, String title, int line) {
        @Override
        public String toString() {
            return title + " on line " + line;
        }
    }

    private final static Factory factory = new Launcher().getFactory();
    private final static Context context = Context.getInstance();

    /**
     * Determines if the given path indicates that the test should pass
     * 
     * @param path
     */
    public static boolean shouldPass(String path) {
        return path.toLowerCase().contains("correct") || path.toLowerCase().contains("warning");
    }

    /**
     * Determines if the given path indicates that the test should fail
     * 
     * @param path
     */
    public static boolean shouldFail(String path) {
        return path.toLowerCase().contains("error");
    }

    /**
     * Reads the expected diagnostics from the given file by looking for comments in the form: "// Expected: Error" or
     * "// Expected: Warning".
     *
     * @param filePath
     *
     * @return list of expected diagnostics of the given kind found in the file, or empty list if there was an error
     *         reading the file or if there are no expected diagnostics in the file
     */
    public static List<ExpectedDiagnostic> getExpectedDiagnosticsFromFile(Path filePath, String kind) {
        List<ExpectedDiagnostic> expectedDiagnostics = new ArrayList<>();
        String kindLower = kind == null ? "" : kind.toLowerCase();
        String normalizedFilePath = filePath.toAbsolutePath().normalize().toString();
        Pattern expectedPattern = Pattern.compile("//\\s*Expected\\s*:\\s*(.*?)\\s*(Error|Warning)\\b.*",
                Pattern.CASE_INSENSITIVE);

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                Matcher expectedMatcher = expectedPattern.matcher(line);
                if (!expectedMatcher.find())
                    continue;
                String kindText = expectedMatcher.group(2);
                String expectedKind = kindText.toLowerCase();
                if (!expectedKind.equals(kindLower))
                    continue;
                String title = expectedMatcher.group(1);
                String titleValue = null;
                if (title != null) {
                    String titleTrimmed = title.trim();
                    titleValue = titleTrimmed.isEmpty() ? null : (titleTrimmed + " " + kindText);
                }
                expectedDiagnostics.add(new ExpectedDiagnostic(normalizedFilePath, titleValue, lineNumber));
            }
        } catch (IOException e) {
            return List.of();
        }
        return expectedDiagnostics;
    }

    /**
     * Reads the expected diagnostics from all files in the given directory and combines them into a single list
     * 
     * @param dirPath
     * 
     * @return list of expected diagnostics from all files in the directory, or empty list if there was an error reading
     *         the directory or if there are no files in the directory
     */
    public static List<ExpectedDiagnostic> getExpectedDiagnosticsFromDirectory(Path dirPath, String kind) {
        List<ExpectedDiagnostic> expectedDiagnostics = new ArrayList<>();
        try {
            List<Path> files = Files.list(dirPath).filter(Files::isRegularFile).toList();
            for (Path file : files) {
                getExpectedDiagnosticsFromFile(file, kind).forEach(expectedDiagnostics::add);
            }
        } catch (IOException e) {
            return List.of();
        }
        return expectedDiagnostics;
    }

    /**
     * Helper method to compare two derivation nodes recursively
     */
    public static void assertDerivationEquals(DerivationNode expected, DerivationNode actual, String message) {
        if (expected == null && actual == null)
            return;

        assertNotNull(expected);
        assertEquals(expected.getClass(), actual.getClass(), message + ": node types should match");
        if (expected instanceof ValDerivationNode expectedVal) {
            ValDerivationNode actualVal = (ValDerivationNode) actual;
            assertEquals(expectedVal.getValue().toString(), actualVal.getValue().toString(),
                    message + ": values should match");
            assertDerivationEquals(expectedVal.getOrigin(), actualVal.getOrigin(), message + " > origin");
        } else if (expected instanceof BinaryDerivationNode expectedBin) {
            BinaryDerivationNode actualBin = (BinaryDerivationNode) actual;
            assertEquals(expectedBin.getOp(), actualBin.getOp(), message + ": operators should match");
            assertDerivationEquals(expectedBin.getLeft(), actualBin.getLeft(), message + " > left");
            assertDerivationEquals(expectedBin.getRight(), actualBin.getRight(), message + " > right");
        } else if (expected instanceof VarDerivationNode expectedVar) {
            VarDerivationNode actualVar = (VarDerivationNode) actual;
            assertEquals(expectedVar.getVar(), actualVar.getVar(), message + ": variables should match");
        } else if (expected instanceof UnaryDerivationNode expectedUnary) {
            UnaryDerivationNode actualUnary = (UnaryDerivationNode) actual;
            assertEquals(expectedUnary.getOp(), actualUnary.getOp(), message + ": operators should match");
            assertDerivationEquals(expectedUnary.getOperand(), actualUnary.getOperand(), message + " > operand");
        } else if (expected instanceof IteDerivationNode expectedIte) {
            IteDerivationNode actualIte = (IteDerivationNode) actual;
            assertDerivationEquals(expectedIte.getCondition(), actualIte.getCondition(), message + " > condition");
            assertDerivationEquals(expectedIte.getThenBranch(), actualIte.getThenBranch(), message + " > then");
            assertDerivationEquals(expectedIte.getElseBranch(), actualIte.getElseBranch(), message + " > else");
        }
    }

    /**
     * Helper method to add an integer variable to the context Needed for tests that rely on the SMT-based implication
     * checks The simplifier asks Z3 whether one conjunct implies another, so every variable in those expressions must
     * be in the context
     */
    public static void addIntVariableToContext(String name) {
        context.addVarToContext(name, factory.Type().INTEGER_PRIMITIVE, new Predicate(),
                factory.Code().createCodeSnippetStatement(""));
    }
}
