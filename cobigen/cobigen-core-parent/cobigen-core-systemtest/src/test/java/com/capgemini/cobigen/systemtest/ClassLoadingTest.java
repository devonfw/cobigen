package com.capgemini.cobigen.systemtest;

import static com.capgemini.cobigen.test.assertj.CobiGenAsserts.assertThat;
import static com.capgemini.cobigen.test.matchers.CustomHamcrestMatchers.hasItemsInList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.internal.matchers.Any.ANY;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.api.extension.MatcherInterpreter;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.api.to.GenerationReportTo;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.impl.CobiGenFactory;
import com.capgemini.cobigen.impl.extension.PluginRegistry;
import com.capgemini.cobigen.systemtest.common.AbstractApiTest;
import com.capgemini.cobigen.test.matchers.MatcherToMatcher;
import com.capgemini.cobigen.test.matchers.VariableAssignmentToMatcher;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * This test loads two compiled classes from a jar file that are added to the data model and called in a
 * template file.
 */
public class ClassLoadingTest extends AbstractApiTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = apiTestsRootPath + "ClassLoadTest/";

    /**
     * Tests the usage of sample logic classes to be used in a template.
     * @throws Exception
     *             test fails
     */
    @Test
    public void callClassLoadingTest() throws Exception {

        // Mocking
        Object containerInput = createTestDataAndConfigureMock();

        // Useful to see generates if necessary, comment the generationRootFolder above then
        File generationRootFolder = tmpFolder.newFolder("generationRootFolder");

        // pre-processing
        File templatesFolder = new File(testFileRootPath + "templates");
        CobiGen target = CobiGenFactory.create(templatesFolder.toURI());
        List<TemplateTo> templates = target.getMatchingTemplates(containerInput);

        // very manual way to load classes
        List<Class<?>> logicClasses = new ArrayList<>();
        logicClasses.add(getJarClass("JarredClass"));
        logicClasses.add(getJarClass("OtherJarredClass"));

        // Execution
        GenerationReportTo report = target.generate(containerInput, templates.get(0),
            Paths.get(generationRootFolder.toURI()), false, logicClasses);

        // Verification
        File expectedResult = new File(testFileRootPath, "expected/Test.java");
        File generatedFile = new File(generationRootFolder, "com/capgemini/Test.java");
        assertThat(report).isSuccessful();
        assertThat(generatedFile).exists();
        assertThat(generatedFile).isFile().hasSameContentAs(expectedResult);

    }

    /**
     * Creates simple to debug test data, which includes on container object and one child of the container
     * object. A {@link TriggerInterpreter TriggerInterpreter} will be mocked with all necessary supplier
     * classes to mock a simple java trigger interpreter. Furthermore, the mocked trigger interpreter will be
     * directly registered in the {@link PluginRegistry}.
     * @return the container as input for generation interpreter for
     */
    @SuppressWarnings("unchecked")
    private Object createTestDataAndConfigureMock() {
        // we only need any objects for inputs to have a unique object reference to affect the mocked method
        // calls as intended
        Object container = new Object() {
            @Override
            public String toString() {
                return "container";
            }
        };
        Object firstChildResource = new Object() {
            @Override
            public String toString() {
                return "child";
            }
        };

        // Pre-processing: Mocking
        TriggerInterpreter triggerInterpreter = mock(TriggerInterpreter.class);
        MatcherInterpreter matcher = mock(MatcherInterpreter.class);
        InputReader inputReader = mock(InputReader.class);

        when(triggerInterpreter.getType()).thenReturn("mockplugin");
        when(triggerInterpreter.getMatcher()).thenReturn(matcher);
        when(triggerInterpreter.getInputReader()).thenReturn(inputReader);

        when(inputReader.isValidInput(any())).thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("fqn"), ANY, sameInstance(container)))))
            .thenReturn(false);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("package"), ANY, sameInstance(container)))))
            .thenReturn(true);

        // Simulate container children resolution of any plug-in
        when(inputReader.getInputObjects(any(), any(Charset.class))).thenReturn(Lists.newArrayList(firstChildResource));

        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("fqn"), ANY, sameInstance(firstChildResource)))))
            .thenReturn(true);

        // Simulate variable resolving of any plug-in
        when(matcher.resolveVariables(
            argThat(new MatcherToMatcher(equalTo("fqn"), ANY, sameInstance(firstChildResource))),
            argThat(hasItemsInList(
                //
                new VariableAssignmentToMatcher(equalTo("regex"), equalTo("rootPackage"), equalTo("1")),
                new VariableAssignmentToMatcher(equalTo("regex"), equalTo("entityName"), equalTo("3"))))))
                    .thenReturn(ImmutableMap.<String, String> builder().put("rootPackage", "com.capgemini")
                        .put("entityName", "Test").build());

        PluginRegistry.registerTriggerInterpreter(triggerInterpreter);

        return container;
    }

    /**
     * An impractical way of loading classes from a jar file
     * @param name
     *            the name of the class to load from the jar file
     * @return the requested class
     * @author sroeger (Aug 12, 2016)
     */
    private Class<?> getJarClass(String name) {

        File file = new File(testFileRootPath + "jarredclasses/jarred.jar");

        URLClassLoader cl;
        Class<?> jarred = null;
        try {
            cl = URLClassLoader.newInstance(new URL[] { file.toURI().toURL() });
            jarred = cl.loadClass(name);
        } catch (MalformedURLException | ClassNotFoundException e) {
            fail();
        }
        return jarred;
    }
}
