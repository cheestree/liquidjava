package liquidjava.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {

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
     * Reads the expected error messages from the given file by looking for a comment containing the expected error
     * message.
     * 
     * @param filePath
     * 
     * @return list of expected error messages found in the file, or empty list if there was an error reading the file
     *         or if there are no expected error messages in the file
     */
    public static List<String> getExpectedErrorsFromFile(Path filePath) {
        List<String> expectedErrors = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int idx = line.indexOf("//");
                if (idx != -1 && line.substring(idx).contains("Error")) {
                    // only expects the error type, NOT the actual refinement error message, depends on deterministic
                    // variable names
                    String comment = line.substring(idx + 2).trim();
                    int dotIdx = comment.indexOf(":");
                    if (dotIdx != -1) {
                        comment = comment.substring(0, dotIdx).trim();
                    }
                    expectedErrors.add(comment);
                }
            }
        } catch (IOException e) {
            return List.of();
        }
        return expectedErrors;
    }

    /**
     * Reads the expected error messages from all files in the given directory and combines them into a single list
     * 
     * @param dirPath
     * 
     * @return list of expected error messages from all files in the directory, or empty list if there was an error
     *         reading the directory or if there are no files in the directory
     */
    public static List<String> getExpectedErrorsFromDirectory(Path dirPath) {
        List<String> expectedErrors = new ArrayList<>();
        try {
            List<Path> files = Files.list(dirPath).filter(Files::isRegularFile).toList();
            for (Path file : files) {
                getExpectedErrorsFromFile(file).forEach(expectedErrors::add);
            }
        } catch (IOException e) {
            return List.of();
        }
        return expectedErrors;
    }
}
