package ru.tohasan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * author LehaSan
 * date 02.04.2016
 */
public class DependencyFinder {
    private static final Logger LOGGER = LoggerFactory.getLogger(DependencyFinder.class);

    private static final int LEVEL_INITIAL = 0;

    /**
     * Method finds modules dependent on specified artifact.
     *
     * @param poms Collection of pom.xml to find dependent modules.
     * @param dependencyName Artifact id of dependency which modules must have.
     * @param onlyModules List of module names to search dependency among them. Can not be null.
     *                    If empty then parameter is ignored.
     * @return Returns set of modules (exclude duplicates) dependent on specified artifact.
     */
    public Set<File> findDependent(Collection<File> poms, String dependencyName, List<String> onlyModules) {
        Set<File> dependentFiles = new HashSet<>();
        findDependent(poms, dependencyName, onlyModules, LEVEL_INITIAL, null, dependentFiles);
        return dependentFiles;
    }

    /**
     * Method calculates name of module by pom.xml.
     *
     * @param pom pom.xml from which need to get module name.
     * @return Returns module name.
     */
    public String getModuleName(File pom) {
        return getArtifactId(pom) + "." + getPackaging(pom);
    }

    private boolean findDependent(Collection<File> poms, String dependencyName, List<String> onlyModules, int level, File dependentFile, Set<File> dependentFiles) {
        // Generate indent
        String indent = "";
        for (int i = 0; i < level; i++) {
            indent += "    ";
        }
        int subLevel = level + 1;

        LOGGER.info("{}DependencyFinder::findDependent started...", indent);

        // List found files
        boolean hasDependent = false;
        for (File file : poms) {
            LOGGER.info("{}Process file: {}", indent, file.getAbsoluteFile());
            // If file contains query string (name of dependency)
            // then check it is dependency instead of artifact name
            if (checkFileContainsDependencyName(file, dependencyName, indent) && checkFileHasDependency(file, dependencyName, indent)) {
                String artifactId = getArtifactId(file);
                LOGGER.debug("{}  - Dependent file: {}", indent, file.getAbsolutePath());
                LOGGER.debug("{}  - Artifact id to continue find dependent module: {}.{}", indent, artifactId, getPackaging(file));
                findDependent(poms, artifactId, onlyModules, subLevel, file, dependentFiles);
                hasDependent = true;
            }
        }

        // If list of specified modules is empty then check all modules in default way
        if (onlyModules.isEmpty()) {
            // If artifact has no dependent then we suppose that it is final (deployed) module
            if (dependentFile != null && !hasDependent) {
                addDependentFile(dependentFiles, dependentFile, indent, dependencyName);
            }
        } else {
            // Search dependence among specified modules
            if (onlyModules.contains(dependencyName)) {
                addDependentFile(dependentFiles, dependentFile, indent, dependencyName);
            }
        }

        LOGGER.info("{}DependencyFinder::findDependent finished...", indent);
        return hasDependent;
    }

    private void addDependentFile(Set<File> dependentFiles, File dependentFile, String indent, String dependencyName) {
        dependentFiles.add(dependentFile);
        LOGGER.info(String.format("%sModule: %s.%s [%s]", indent, dependencyName, getPackaging(dependentFile), dependentFile.getAbsolutePath()));
    }

    private boolean checkFileContainsDependencyName(File file, String dependencyName, String indent) {
        try (Scanner scanner = new Scanner(file)) {
            // Read the file line by line
            int lineNum = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineNum++;
                if (line.contains(dependencyName)) {
                    LOGGER.debug(String.format("%s  - Line %d contains dependency", indent, lineNum));
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("Error: {}", e);
        }
        return false;
    }

    private boolean checkFileHasDependency(File file, String dependencyName, String indent) {
        NodeList dependencies = (NodeList) executeXPathExpression(file, "//dependencies/dependency[artifactId='" + dependencyName + "']", XPathConstants.NODESET);
        boolean hasDependency = dependencies != null && 0 < dependencies.getLength();
        if (hasDependency) {
            LOGGER.debug(String.format("%s  - File has dependency", indent));
        }
        return hasDependency;
    }

    private String getArtifactId(File file) {
        return (String) executeXPathExpression(file, "/project/artifactId", XPathConstants.STRING);
    }

    private String getPackaging(File file) {
        return (String) executeXPathExpression(file, "/project/packaging", XPathConstants.STRING);
    }

    private Object executeXPathExpression(File file, String expressionStr, QName xpathType) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Object result = null;

        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            XPath xpath = XPathFactory.newInstance().newXPath();
            XPathExpression expression = xpath.compile(expressionStr);
            result = expression.evaluate(doc, xpathType);
        } catch (Exception e) {
            LOGGER.error("Error: {}", e);
        }
        return result;
    }
}
