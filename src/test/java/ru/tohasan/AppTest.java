package ru.tohasan;

import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * author LehaSan
 * date 02.04.2016
 */
public class AppTest {

    private final List<String> output = new ArrayList<>();

    private App app = new App() {

        @Override
        protected void print(String message) {
            super.print(message);
            output.add(message);
        }

        @Override
        protected void printHelp() {
            super.printHelp();
            output.add("Utility usage");
        }
    };

    @Before
    public void setUp() throws Exception {
        output.clear();
    }

    @Test
    public void itShouldBeSeveralDependentModules() throws Exception {
        URL directoryToTest = AppTest.class.getClassLoader().getResource("test-structure");
        assertNotNull(directoryToTest);

        app.run(new String[]{"--directory", directoryToTest.getPath(), "--search", "subModuleX1"});

        assertEquals(6, output.size());
        assertEquals("Dependents on subModuleX1:", output.get(0));
        assertEquals("    Module: moduleA.war [E:\\projects\\dependency-finder\\target\\test-classes\\test-structure\\moduleA\\pom.xml]", output.get(1));
        assertEquals("    Module: moduleB.ear [E:\\projects\\dependency-finder\\target\\test-classes\\test-structure\\moduleB\\pom.xml]", output.get(2));
        assertEquals("", output.get(3));
        assertEquals("Total count of processed files: 10", output.get(4));
        assertTrue(output.get(5).startsWith("Total time:"));
    }

    @Test
    public void helpShouldBeDisplayedIfThereAreNoArguments() throws Exception {
        app.run(new String[]{});

        assertEquals(1, output.size());
        assertEquals("Utility usage", output.get(0));
    }
}