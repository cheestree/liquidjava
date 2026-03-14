package liquidjava.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static List<Pair<String, Integer>> getExpectedErrorsFromFile(Path filePath) {
        List<Pair<String, Integer>> expectedErrors = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                Pattern p = Pattern.compile("//\\s*(.*?\\bError\\b)", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(line);
                if (m.find()) {
                    expectedErrors.add(new Pair<>(m.group(1).trim(), lineNumber));
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
    public static List<Pair<String, Integer>> getExpectedErrorsFromDirectory(Path dirPath) {
        List<Pair<String, Integer>> expectedErrors = new ArrayList<>();
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
