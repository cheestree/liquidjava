package liquidjava.api.tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import liquidjava.api.CommandLineLauncher;
import liquidjava.diagnostics.Diagnostics;
import liquidjava.diagnostics.LJDiagnostic;
import liquidjava.utils.TestUtils.ExpectedDiagnostic;
import static liquidjava.utils.TestUtils.getExpectedDiagnosticsFromDirectory;
import static liquidjava.utils.TestUtils.getExpectedDiagnosticsFromFile;

public class TestExamples {

    Diagnostics diagnostics = Diagnostics.getInstance();

    /**
     * Test the file at the given path by launching the verifier and checking for errors. The file/directory is expected
     * to be either correct or contain an error based on its name.
     *
     * @param path
     *            path to the file to test
     */
    @ParameterizedTest
    @MethodSource("sourcePaths")
    public void testPath(final Path path) {
        String pathName = path.getFileName().toString();
        boolean isDirectory = Files.isDirectory(path);

        // run verification
        CommandLineLauncher.launch(path.toFile().toString());

        List<ExpectedDiagnostic> expectedErrors = isDirectory ? getExpectedDiagnosticsFromDirectory(path, "error")
                : getExpectedDiagnosticsFromFile(path, "error");
        List<ExpectedDiagnostic> expectedWarnings = isDirectory ? getExpectedDiagnosticsFromDirectory(path, "warning")
                : getExpectedDiagnosticsFromFile(path, "warning");

        boolean expectsFailure = !expectedErrors.isEmpty();

        if (!expectsFailure && diagnostics.foundError()) {
            System.out.println("Error in: " + pathName + " --- should pass but an error was found. \n"
                    + diagnostics.getErrorOutput());
            fail();
        } else if (expectsFailure && !diagnostics.foundError()) {
            System.out.println("Error in: " + pathName + " --- should fail but no errors were found. \n"
                    + diagnostics.getErrorOutput());
            fail();
        }

        if (expectedErrors.isEmpty() && expectedWarnings.isEmpty()
                && (diagnostics.foundError() || diagnostics.foundWarning())) {
            System.out.println("No expected diagnostics found for: " + pathName);
            System.out.println(
                    "Please specify each expected diagnostic in the test file as a comment like \"// Expected: Error\" or \"// Expected: Warning\" on the line where it should be reported.");
            fail();
        }

        assertExpectedDiagnostics(pathName, expectedErrors, expectedWarnings);
    }

    private void assertExpectedDiagnostics(String pathName, List<ExpectedDiagnostic> expectedErrors,
            List<ExpectedDiagnostic> expectedWarnings) {
        int actualErrorCount = diagnostics.getErrors().size();
        if (actualErrorCount != expectedErrors.size()) {
            System.out.println("Error count mismatch in: " + pathName + " --- expected " + expectedErrors.size()
                    + " errors, but found " + actualErrorCount + ". \n" + diagnostics.getErrorOutput());
            fail();
        }

        int actualWarningCount = diagnostics.getWarnings().size();
        if (actualWarningCount != expectedWarnings.size()) {
            System.out.println("Warning count mismatch in: " + pathName + " --- expected " + expectedWarnings.size()
                    + " warnings, but found " + actualWarningCount + ". \n" + diagnostics.getWarningOutput());
            fail();
        }

        assertDiagnosticsMatch(pathName, "Error", expectedErrors, diagnostics.getErrors(),
                diagnostics.getErrorOutput());
        assertDiagnosticsMatch(pathName, "Warning", expectedWarnings, diagnostics.getWarnings(),
                diagnostics.getWarningOutput());
    }

    private <T extends LJDiagnostic> void assertDiagnosticsMatch(String pathName, String label,
            List<ExpectedDiagnostic> expected, Iterable<T> actual, String output) {
        for (T diagnostic : actual) {
            String foundTitle = diagnostic.getTitle();
            int foundLine = diagnostic.getPosition() != null ? diagnostic.getPosition().getLine() : -1;
            String foundFile = diagnostic.getFile();
            boolean match = matchesExpected(expected, foundFile, foundTitle, foundLine);

            if (!match) {
                System.out.println(label + " in: " + pathName + " --- expected " + label.toLowerCase() + "s: "
                        + expected + ", but found: " + foundTitle + " at " + foundLine + ". \n" + output);
                fail();
            }
        }
    }

    private boolean matchesExpected(List<ExpectedDiagnostic> expected, String actualFile, String actualTitle,
            int actualLine) {
        return expected.stream().anyMatch(item -> {
            if (item.line() != actualLine)
                return false;
            if (!item.filePath().equals(actualFile))
                return false;
            String expectedTitle = item.title();
            return expectedTitle == null || expectedTitle.isBlank() || expectedTitle.equals(actualTitle);
        });
    }

    /**
     * Provides the paths to test. This includes both individual files and directories. Directories
     * contain either .java files or subdirectories with .java files, but not both.
     * 
     * @return stream of paths to test
     * 
     **/
    private static Stream<Path> sourcePaths() throws IOException {
        Path root = Paths.get("../liquidjava-example/src/main/java/testSuite/");

        List<Path> suiteDirs = Files.walk(root, Integer.MAX_VALUE).filter(Files::isDirectory)
                .filter(p -> !p.equals(root)).filter(p -> directlyContainsJava(p)).toList();
        Set<Path> suiteParents = new HashSet<>(suiteDirs);
        List<Path> standaloneFiles = Files.walk(root, Integer.MAX_VALUE).filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".java")).filter(p -> !suiteParents.contains(p.getParent()))
                .toList();

        return Stream.concat(standaloneFiles.stream(), suiteDirs.stream());
    }

    private static boolean directlyContainsJava(Path dir) {
        try (Stream<Path> children = Files.list(dir)) {
            return children.anyMatch(p -> Files.isRegularFile(p) && p.toString().endsWith(".java"));
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Test multiple paths at once, including both files and directories. This test ensures that the verifier can handle
     * multiple inputs correctly and that no errors are found in files/directories that are expected to be correct.
     */
    @Test
    public void testMultiplePaths() {
        String[] paths = { "../liquidjava-example/src/main/java/testSuite/CorrectSimple.java",
                "../liquidjava-example/src/main/java/testSuite/classes/arraylist_correct", };
        CommandLineLauncher.launch(paths);
        // Check if any of the paths that should be correct found an error
        if (diagnostics.foundError()) {
            System.out.println("Error found in files that should be correct. \n" + diagnostics.getErrorOutput());
            fail();
        }
    }
}
