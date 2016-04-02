package ru.tohasan;

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

    private static final int ARGS_DIRECTORY = 0;
    private static final int ARGS_DEPENDENCY = 1;

    private static final String POM_XML_PATTERN = "pom.xml";

    public static void main(String[] args) {
        App app = new App();

        if (!app.checkArguments(args)) {
            return;
        }

        app.findDependent(args);
    }

    public boolean checkArguments(String[] args) {
        if (args.length == 0 || args.length == 1) {
            print();
            print("You have to specify two arguments:");
            print("   1. Directory where run search");
            print("   2. Name of dependency to find");
            return false;
        }
        return true;
    }

    public void findDependent(String[] args) {
        LOGGER.info("App started at {}", new Date());
        long startTime = System.currentTimeMillis();

        String directoryName = args[ARGS_DIRECTORY];
        String dependencyName = args[ARGS_DEPENDENCY];

        // Find all pom.xml files
        File dir = new File(directoryName);
        Collection<File> files = FileUtils.listFiles(dir, FileFilterUtils.nameFileFilter(POM_XML_PATTERN), TrueFileFilter.INSTANCE);

        DependencyFinder dependencyFinder = new DependencyFinder();
        List<File> dependentFiles = dependencyFinder.findDependent(files, dependencyName);

        print(String.format("Dependents of %s:", dependencyName));
        for (File dependentFile : dependentFiles) {
            print(String.format("    Module: %s [%s]", dependencyFinder.getModuleName(dependentFile), dependentFile.getAbsolutePath()));
        }

        long endTime = System.currentTimeMillis();

        print();
        print(String.format("Total count of processed files: %d, time: %d ms", files.size(), endTime - startTime));
        LOGGER.info("App finished at {}", new Date());
    }

    protected void print() {
        print("");
    }

    protected void print(String message) {
        CONSOLE.println(message);
    }
}
