package ru.tohasan;

import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * author LehaSan
 * date 02.04.2016
 */
public class AppTest {

    private final List<String> output = new ArrayList<>();

    private App app = new App() {

        @Override
        protected void print(String message) {
            output.add(message);
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

        app.findDependent(new String[]{directoryToTest.getPath(), "subModuleX1"});

        assertEquals(5, output.size());
        assertEquals("Dependents of subModuleX1:", output.get(0));
        assertEquals("    Module: moduleA.war [E:\\projects\\dependency-finder\\target\\test-classes\\test-structure\\moduleA\\pom.xml]", output.get(1));
        assertEquals("    Module: moduleB.ear [E:\\projects\\dependency-finder\\target\\test-classes\\test-structure\\moduleB\\pom.xml]", output.get(2));
        assertEquals("", output.get(3));
        assertTrue(output.get(4).startsWith("Total count of processed files: 10"));
    }
}