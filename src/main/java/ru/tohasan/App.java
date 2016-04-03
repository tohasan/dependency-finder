package ru.tohasan;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * App finds
 */
public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final PrintStream CONSOLE = System.out; // NOSONAR

    private static final String UTILITY_NAME = "df";

    private static final String OPT_DIRECTORY = "directory";
    private static final String OPT_SEARCH = "search";

    private static final String POM_XML_PATTERN = "pom.xml";

    private Options options = null;

    public static void main(String[] args) {
        App app = new App();
        app.run(args);
    }

    public void run(String[] args) {
        LOGGER.info("App started at {}", new Date());
        long startTime = System.currentTimeMillis();

        // Parse command line arguments
        CommandLine commandLine;
        try {
            commandLine = parseCommandLineArgs(args);
        } catch (ParseException e) {
            LOGGER.error("Error: {}", e);
            printHelp();
            LOGGER.info("App finished at {}", new Date());
            return;
        }

        // Get values of options
        String directoryName = commandLine.hasOption(OPT_DIRECTORY) ? commandLine.getOptionValue(OPT_DIRECTORY) : "";
        String dependencyName = commandLine.hasOption(OPT_SEARCH) ? commandLine.getOptionValue(OPT_SEARCH) : "";

        this.findDependent(directoryName, dependencyName);

        long endTime = System.currentTimeMillis();
        print(String.format("Total time: %d ms", endTime - startTime));
        LOGGER.info("App finished at {}", new Date());
    }

    /**
     * Method finds dependent modules on specified module in arguments.
     */
    private List<File> findDependent(String directoryName, String dependencyName) {
        // Find all pom.xml files
        File dir = new File(directoryName);
        Collection<File> files = FileUtils.listFiles(
                dir,
                FileFilterUtils.nameFileFilter(POM_XML_PATTERN),
                TrueFileFilter.INSTANCE
        );

        DependencyFinder dependencyFinder = new DependencyFinder();
        List<File> dependentFiles = dependencyFinder.findDependent(files, dependencyName);

        if (!dependentFiles.isEmpty()) {
            print(String.format("Dependents on %s:", dependencyName));
            for (File dependentFile : dependentFiles) {
                print(String.format(
                        "    Module: %s [%s]",
                        dependencyFinder.getModuleName(dependentFile),
                        dependentFile.getAbsolutePath()
                ));
            }
        } else {
            print("There are no dependent modules on " + dependencyName);
        }

        print();
        print(String.format("Total count of processed files: %d", files.size()));

        return dependentFiles;
    }

    protected void print() {
        print("");
    }

    protected void print(String message) {
        CONSOLE.println(message);
    }

    /**
     * Method prints help for usage of utility.
     */
    protected void printHelp() {
        // Automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(UTILITY_NAME, getOptions());
    }

    /**
     * Method creates command line options which utility uses to specify launching parameters.
     *
     * @return Returns command line options.
     */
    private Options getOptions() {
        if (options == null) {
            // Create the Options
            Option optDirectory = Option.builder("d")
                    .longOpt(OPT_DIRECTORY)
                    .desc("search dependent modules in pom.xml in this directory including subdirectories. " +
                            "For example, -d /opt/my-project")
                    .required()
                    .optionalArg(false)
                    .numberOfArgs(1)
                    .build();

            Option optDependencyName = Option.builder("s")
                    .longOpt(OPT_SEARCH)
                    .desc("search modules that have this module as dependency (directly or indirectly). " +
                            "For example, -s modBar")
                    .required()
                    .optionalArg(false)
                    .numberOfArgs(1)
                    .build();

            options = new Options();
            options.addOption(optDirectory);
            options.addOption(optDependencyName);
        }
        return options;
    }

    /**
     * Method parses command line args using command line options.
     *
     * @param args Arguments user specifies.
     * @return Returns prepared command line that contains information about used options.
     * @throws ParseException Throws if arguments can not be parsed.
     */
    private CommandLine parseCommandLineArgs(String[] args) throws ParseException {
        // Create the command line parser
        CommandLineParser parser = new DefaultParser();
        return parser.parse(getOptions(), args);
    }
}
