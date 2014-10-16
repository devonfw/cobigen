package com.capgemini.cobigen;

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
import java.nio.charset.Charset;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.common.matchers.MatcherToMatcher;
import com.capgemini.cobigen.common.matchers.VariableAssignmentToMatcher;
import com.capgemini.cobigen.config.entity.ContainerMatcher;
import com.capgemini.cobigen.extension.IInputReader;
import com.capgemini.cobigen.extension.IMatcher;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.capgemini.cobigen.extension.to.TemplateTo;
import com.capgemini.cobigen.pluginmanager.PluginRegistry;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * This test suite concentrates on the {@link ContainerMatcher} support and semantics
 * @author mbrunnli (13.10.2014)
 */
public class ContainerMatcherTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/ContainerMatcherTest/";

    /**
     * Tests whether a container matcher will not match iff there are no other matchers
     * @throws IOException
     *             test fails
     * @author mbrunnli (13.10.2014)
     */
    @Test
    public void testContainerMatcherDoesNotMatchWithoutMatcher() throws IOException {

        // Mocking
        Object containerInput = createTestDataAndConfigureMock(false);

        // Execution
        File templatesFolder = new File(testFileRootPath + "templates");
        CobiGen target = new CobiGen(templatesFolder);
        List<String> matchingTriggerIds = target.getMatchingTriggerIds(containerInput);

        // Verification
        Assert.assertNotNull(matchingTriggerIds);
        Assert.assertEquals(0, matchingTriggerIds.size());

    }

    /**
     * Tests whether a container matcher will match iff there are matchers matching the child resources
     * @throws IOException
     *             test fails
     * @author mbrunnli (13.10.2014)
     */
    @Test
    public void testContainerMatcherMatches() throws IOException {

        // Mocking
        Object containerInput = createTestDataAndConfigureMock(true);

        // Execution
        File templatesFolder = new File(testFileRootPath + "templates");
        CobiGen target = new CobiGen(templatesFolder);
        List<String> matchingTriggerIds = target.getMatchingTriggerIds(containerInput);

        // Verification
        Assert.assertNotNull(matchingTriggerIds);
        Assert.assertEquals(1, matchingTriggerIds.size());

    }

    /**
     * Tests whether variable resolving works for a container's children as the container itself does not
     * include any variable resolving
     * @throws IOException
     *             test fails
     * @author mbrunnli (13.10.2014)
     */
    @Test
    public void testContextVariableResolvingForContainerMatches() throws IOException {

        // Mocking
        Object containerInput = createTestDataAndConfigureMock(true);

        // Execution
        File templatesFolder = new File(testFileRootPath + "templates");
        CobiGen target = new CobiGen(templatesFolder);
        List<TemplateTo> matchingTemplates = target.getMatchingTemplates(containerInput);

        // Verification
        Assert.assertNotNull(matchingTemplates);
        Assert.assertEquals(1, matchingTemplates.size());
    }

    // ######################### PRIVATE ##############################

    /**
     * Creates simple to debug test data, which includes on container object and one child of the container
     * object. A {@link ITriggerInterpreter TriggerInterpreter} will be mocked with all necessary supplier
     * classes to mock a simple java trigger interpreter. Furthermore, the mocked trigger interpreter will be
     * directly registered in the {@link PluginRegistry}.
     * @param containerChildMatchesTrigger
     *            defines whether the child of the container input should match any non-container matcher
     * @return the container as input for generation interpreter for
     * @author mbrunnli (16.10.2014)
     */
    @SuppressWarnings("unchecked")
    private Object createTestDataAndConfigureMock(boolean containerChildMatchesTrigger) {
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
        when(inputReader.getInputObjects(any(), any(Charset.class))).thenReturn(
            Lists.newArrayList(firstChildResource));

        when(
            matcher.matches(argThat(new MatcherToMatcher(equalTo("fqn"), ANY,
                sameInstance(firstChildResource))))).thenReturn(containerChildMatchesTrigger);

        // Simulate variable resolving of any plug-in
        when(
            matcher.resolveVariables(
                argThat(new MatcherToMatcher(equalTo("fqn"), ANY, sameInstance(firstChildResource))),
                argThat(hasItemsInList(
                    //
                    new VariableAssignmentToMatcher(equalTo("regex"), equalTo("rootPackage"), equalTo("1")),
                    new VariableAssignmentToMatcher(equalTo("regex"), equalTo("entityName"), equalTo("3"))))))
            .thenReturn(
                ImmutableMap.<String, String> builder().put("rootPackage", "com.capgemini")
                    .put("entityName", "Test").build());

        PluginRegistry.registerTriggerInterpreter(triggerInterpreter);

        return container;
    }
}
