package com.capgemini.cobigen.systemtest;

import static com.capgemini.cobigen.common.matchers.CustomHamcrestMatchers.hasItemsInList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.internal.matchers.Any.ANY;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.capgemini.cobigen.CobiGen;
import com.capgemini.cobigen.common.matchers.MatcherToMatcher;
import com.capgemini.cobigen.common.matchers.VariableAssignmentToMatcher;
import com.capgemini.cobigen.config.ContextConfiguration.ContextSetting;
import com.capgemini.cobigen.extension.IInputReader;
import com.capgemini.cobigen.extension.IMatcher;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.capgemini.cobigen.extension.to.TemplateTo;
import com.capgemini.cobigen.pluginmanager.PluginRegistry;
import com.capgemini.cobigen.systemtest.common.AbstractApiTest;
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
     * An impractical way of loading classes from a jar file
     * @param name
     *            the name of the class to load from the jar file
     * @return the requested class
     * @author sroeger (Aug 12, 2016)
     * @throws IOException
     */
    public Class<?> getJarClass(String name) throws IOException {

        File currentDirFile = new File(".");
        String helper = currentDirFile.getAbsolutePath();
        String currentDir = helper.substring(0, helper.length() - 1);
        String filePath = new String(
            currentDir + "src/test/resources/testdata/systemtest/ClassLoadTest/jarredclasses/jarred.jar");

        URL myJarFile = null;
        try {
            myJarFile = new URL("file:///" + filePath);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }

        URLClassLoader cl = URLClassLoader.newInstance(new URL[] { myJarFile });

        Class<?> jarred = null;
        try {
            jarred = cl.loadClass(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return jarred;
    }

    @Test
    public void callClassLoadingTest() throws Exception {

        // Mocking
        Object containerInput = createTestDataAndConfigureMock(true, false);
        // File generationRootFolder = tmpFolder.newFolder("generationRootFolder");
        // Useful to see generates if necessary, comment the generationRootFolder above then
        File generationRootFolder = new File(testFileRootPath + "generates");

        // pre-processing
        File templatesFolder = new File(testFileRootPath + "templates");
        CobiGen target = new CobiGen(templatesFolder.toURI());
        target.setContextSetting(ContextSetting.GenerationTargetRootPath,
            generationRootFolder.getAbsolutePath());
        List<TemplateTo> templates = target.getMatchingTemplates(containerInput);

        // very manual way to load classes
        List<Class<?>> logicClasses = new ArrayList<>();
        logicClasses.add(getJarClass("JarredClass"));
        logicClasses.add(getJarClass("OtherJarredClass"));
        // Execution
        // should not throw any Exceptions
        target.generate(containerInput, templates.get(0), false, logicClasses);
    }

    @SuppressWarnings("unchecked")
    private Object createTestDataAndConfigureMock(boolean containerChildMatchesTrigger,
        boolean multipleContainerChildren) {
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
        ITriggerInterpreter triggerInterpreter = mock(ITriggerInterpreter.class);
        IMatcher matcher = mock(IMatcher.class);
        IInputReader inputReader = mock(IInputReader.class);

        when(triggerInterpreter.getType()).thenReturn("java");
        when(triggerInterpreter.getMatcher()).thenReturn(matcher);
        when(triggerInterpreter.getInputReader()).thenReturn(inputReader);

        when(inputReader.isValidInput(any())).thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("fqn"), ANY, sameInstance(container)))))
            .thenReturn(false);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("package"), ANY, sameInstance(container)))))
            .thenReturn(true);

        // Simulate container children resolution of any plug-in
        when(inputReader.combinesMultipleInputObjects(argThat(sameInstance(container)))).thenReturn(true);
        if (multipleContainerChildren) {
            Object secondChildResource = new Object() {
                @Override
                public String toString() {
                    return "child2";
                }
            };
            when(inputReader.getInputObjects(any(), any(Charset.class)))
                .thenReturn(Lists.newArrayList(firstChildResource, secondChildResource));
        } else {
            when(inputReader.getInputObjects(any(), any(Charset.class)))
                .thenReturn(Lists.newArrayList(firstChildResource));
        }

        when(matcher
            .matches(argThat(new MatcherToMatcher(equalTo("fqn"), ANY, sameInstance(firstChildResource)))))
                .thenReturn(containerChildMatchesTrigger);

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

}
