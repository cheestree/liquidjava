package liquidjava.api;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import liquidjava.diagnostics.Diagnostics;
import liquidjava.diagnostics.errors.CustomError;
import liquidjava.diagnostics.warnings.CustomWarning;
import liquidjava.processor.RefinementProcessor;
import liquidjava.processor.context.ContextHistory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.processing.ProcessingManager;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;
import spoon.support.QueueProcessingManager;

public class CommandLineLauncher {

    private static final Diagnostics diagnostics = Diagnostics.getInstance();
    private static final ContextHistory contextHistory = ContextHistory.getInstance();

    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new CliArguments());
        int exitCode = commandLine.execute(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    @Command(name = "liquidjava", mixinStandardHelpOptions = true, customSynopsis = "./liquidjava <...paths> <options>")
    private static class CliArguments implements Callable<Integer> {

        @Option(names = { "-d", "--debug" }, description = "Enable debug mode for more detailed output")
        private boolean debugMode;

        @Parameters(arity = "1..*", paramLabel = "<...paths>", description = "Paths to be verified by LiquidJava")
        private List<String> paths;

        @Override
        public Integer call() {
            if (debugMode) {
                System.out.println("Debug mode enabled");
                System.out.println("Input paths: " + paths);
                diagnostics.setDebugMode();
            }

            launch(paths.stream().toArray(String[]::new));

            // print diagnostics
            if (diagnostics.foundWarning()) {
                System.out.println(diagnostics.getWarningOutput());
            }
            if (diagnostics.foundError()) {
                System.out.println(diagnostics.getErrorOutput());
                return 1;
            }

            System.out.println("Correct! Passed Verification.");
            return 0;
        }
    }

    public static void launch(String... paths) {
        System.out.println("Running LiquidJava on: " + Arrays.toString(paths).replaceAll("[\\[\\]]", ""));

        diagnostics.clear();
        contextHistory.clearHistory();
        Launcher launcher = new Launcher();
        for (String path : paths) {
            if (!new File(path).exists()) {
                diagnostics.add(new CustomError("The path " + path + " was not found"));
                return;
            }
            launcher.addInputResource(path);
        }

        Environment env = launcher.getEnvironment();
        env.setNoClasspath(true);
        env.setComplianceLevel(8);

        boolean buildSuccess = launcher.getModelBuilder().build();
        if (!buildSuccess && (env.getErrorCount() > 0 || env.getWarningCount() > 0)) {
            diagnostics.add(new CustomWarning("Java compilation encountered issues. Verification may be affected."));
        }

        final Factory factory = launcher.getFactory();
        final ProcessingManager processingManager = new QueueProcessingManager(factory);
        final RefinementProcessor processor = new RefinementProcessor(factory);
        processingManager.addProcessor(processor);

        // analyze all packages
        CtPackage root = factory.Package().getRootPackage();
        if (root != null)
            processingManager.process(root);
    }
}
