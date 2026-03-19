package liquidjava.api;

import java.util.List;
import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "liquidjava", mixinStandardHelpOptions = false, customSynopsis = "./liquidjava <...paths> <options>")
public class CommandLineArgs {
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message")
    public boolean help;

    @Option(names = { "-d", "--debug" }, description = "Enable debug mode for more detailed output")
    public boolean debugMode;

    @Parameters(arity = "1..*", paramLabel = "<...paths>", description = "Paths to be verified by LiquidJava")
    public List<String> paths;
}
