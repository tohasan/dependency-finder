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

        assertArrayEquals(new String[]{
                "Dependents on subModuleX1:",
                "    Module: moduleA.war [E:\\projects\\dependency-finder\\target\\test-classes\\test-structure\\moduleA\\pom.xml]",
                "    Module: moduleB.ear [E:\\projects\\dependency-finder\\target\\test-classes\\test-structure\\moduleB\\pom.xml]",
                "    Module: subModuleA2.war [E:\\projects\\dependency-finder\\target\\test-classes\\test-structure\\moduleA\\subModuleA2\\pom.xml]",
                "",
                "Total count of processed files: 12",
                output.get(output.size() - 1)
        }, output.toArray());
    }

    @Test
    public void helpShouldBeDisplayedIfThereAreNoArguments() throws Exception {
        app.run(new String[]{});

        assertEquals(1, output.size());
        assertEquals("Utility usage", output.get(0));
    }
}