package org.elasticsearch.test.rest.yaml;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

import org.elasticsearch.test.ESTestCase;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;

public class ESClientYamlSuiteTestCaseTests extends ESTestCase {

    public void testLoadAllYamlSuites() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        Map<String, Collection<URL>> yamlSuites = ESClientYamlSuiteTestCase.loadSuites(classLoader,"");
        assertEquals(2, yamlSuites.size());
    }

    public void testLoadSingleYamlSuite() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        Map<String, Collection<URL>> yamlSuites = ESClientYamlSuiteTestCase.loadSuites(classLoader,"suite1/10_basic");
        assertSingleFile(yamlSuites, "suite1", "10_basic.yml");
    }

    public void testLoadMultipleYamlSuites() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();

        //single directory
        Map<String, Collection<URL>> yamlSuites = ESClientYamlSuiteTestCase.loadSuites(classLoader,"suite1");
        assertThat(yamlSuites, notNullValue());
        assertThat(yamlSuites.size(), equalTo(1));
        assertThat(yamlSuites.containsKey("suite1"), equalTo(true));
        assertThat(yamlSuites.get("suite1").size(), greaterThan(1));

        //multiple directories
        yamlSuites = ESClientYamlSuiteTestCase.loadSuites(classLoader, "suite1", "suite2");
        assertThat(yamlSuites, notNullValue());
        assertThat(yamlSuites.size(), equalTo(2));
        assertThat(yamlSuites.containsKey("suite1"), equalTo(true));
        assertEquals(2, yamlSuites.get("suite1").size());
        assertThat(yamlSuites.containsKey("suite2"), equalTo(true));
        assertEquals(2, yamlSuites.get("suite2").size());

        //multiple paths, which can be both directories or yaml test suites (with optional file extension)
        yamlSuites = ESClientYamlSuiteTestCase.loadSuites(classLoader, "suite2/10_basic", "suite1");
        assertThat(yamlSuites, notNullValue());
        assertThat(yamlSuites.size(), equalTo(2));
        assertThat(yamlSuites.containsKey("suite2"), equalTo(true));
        assertThat(yamlSuites.get("suite2").size(), equalTo(1));
        assertSingleFile(yamlSuites.get("suite2"), "suite2", "10_basic.yml");
        assertThat(yamlSuites.containsKey("suite1"), equalTo(true));
        assertThat(yamlSuites.get("suite1").size(), greaterThan(1));

        //files can be loaded from classpath and from file system too
        Path dir = createTempDir();
        Path file = dir.resolve("test_loading.yml");
        Files.createFile(file);
    }

    private static void assertSingleFile(Map<String, Collection<URL>> yamlSuites, String dirName, String fileName) {
        assertThat(yamlSuites, notNullValue());
        assertThat(yamlSuites.size(), equalTo(1));
        assertThat(yamlSuites.containsKey(dirName), equalTo(true));
        assertSingleFile(yamlSuites.get(dirName), dirName, fileName);
    }

    private static void assertSingleFile(Collection<URL> files, String dirName, String fileName) {
        assertThat(files.size(), equalTo(1));
        URL url = files.iterator().next();
        // commented out because an URL does not have the concept of dir name or file name. It does not matter.
        //assertThat(file.getFileName().toString(), equalTo(fileName));
        //assertThat(file.toAbsolutePath().getParent().getFileName().toString(), equalTo(dirName));
        // we check instead of existence of the string in the URL representation
        assertTrue(url.toString().contains(dirName));
        assertTrue(url.toString().contains(fileName));
    }
}
