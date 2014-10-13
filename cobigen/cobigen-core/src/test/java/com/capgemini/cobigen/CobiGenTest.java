package com.capgemini.cobigen;

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
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.extension.IInputReader;
import com.capgemini.cobigen.extension.IMatcher;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.capgemini.cobigen.pluginmanager.PluginRegistry;
import com.google.common.collect.Lists;

/**
 * This test suite should mainly focus on API tests to avoid inconsistencies
 * @author mbrunnli (13.10.2014)
 */
public class CobiGenTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/CobiGenTest/";

    /**
     * Tests whether a container matcher will not match iff there are no other matchers
     * @throws InvalidConfigurationException
     *             test fails
     * @throws IOException
     *             test fails
     * @author mbrunnli (13.10.2014)
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testContainerMatcherDoesNotMatchWithoutMatcher() throws InvalidConfigurationException,
        IOException {

        // we only need any objects for inputs to have a unique object reference to affect the mocked method
        // calls as intended
        Object input = new Object();
        Object firstChildResource = new Object();

        // Pre-processing: Mocking
        ITriggerInterpreter triggerInterpreter = mock(ITriggerInterpreter.class);
        IMatcher matcher = mock(IMatcher.class);
        IInputReader inputReader = mock(IInputReader.class);

        when(triggerInterpreter.getType()).thenReturn("java");
        when(triggerInterpreter.getMatcher()).thenReturn(matcher);
        when(triggerInterpreter.getInputReader()).thenReturn(inputReader);

        when(inputReader.isValidInput(any())).thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("fqn"), ANY, sameInstance(input)))))
            .thenReturn(false);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("package"), ANY, sameInstance(input)))))
            .thenReturn(true);
        when(inputReader.getInputObjects(any(), any(Charset.class))).thenReturn(
            Lists.newArrayList(firstChildResource));
        when(
            matcher.matches(argThat(new MatcherToMatcher(equalTo("fqn"), ANY,
                sameInstance(firstChildResource))))).thenReturn(false);

        // Execution
        PluginRegistry.registerTriggerInterpreter(triggerInterpreter);

        File templatesFolder = new File(testFileRootPath + "templates");
        CobiGen target = new CobiGen(templatesFolder);
        List<String> matchingTriggerIds = target.getMatchingTriggerIds(input);

        // Verification
        Assert.assertNotNull(matchingTriggerIds);
        Assert.assertEquals(0, matchingTriggerIds.size());

    }

    /**
     * Tests whether a container matcher will match iff there are matchers matching the child resources
     * @throws InvalidConfigurationException
     *             test fails
     * @throws IOException
     *             test fails
     * @author mbrunnli (13.10.2014)
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testContainerMatcherMatches() throws InvalidConfigurationException, IOException {

        // we only need any objects for inputs to have a unique object reference to affect the mocked method
        // calls as intended
        Object input = new Object();
        Object firstChildResource = new Object();

        // Pre-processing: Mocking
        ITriggerInterpreter triggerInterpreter = mock(ITriggerInterpreter.class);
        IMatcher matcher = mock(IMatcher.class);
        IInputReader inputReader = mock(IInputReader.class);

        when(triggerInterpreter.getType()).thenReturn("java");
        when(triggerInterpreter.getMatcher()).thenReturn(matcher);
        when(triggerInterpreter.getInputReader()).thenReturn(inputReader);

        when(inputReader.isValidInput(any())).thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("fqn"), ANY, sameInstance(input)))))
            .thenReturn(false);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("package"), ANY, sameInstance(input)))))
            .thenReturn(true);
        when(inputReader.getInputObjects(any(), any(Charset.class))).thenReturn(
            Lists.newArrayList(firstChildResource));
        when(
            matcher.matches(argThat(new MatcherToMatcher(equalTo("fqn"), ANY,
                sameInstance(firstChildResource))))).thenReturn(true);

        // Execution
        PluginRegistry.registerTriggerInterpreter(triggerInterpreter);

        File templatesFolder = new File(testFileRootPath + "templates");
        CobiGen target = new CobiGen(templatesFolder);
        List<String> matchingTriggerIds = target.getMatchingTriggerIds(input);

        // Verification
        Assert.assertNotNull(matchingTriggerIds);
        Assert.assertEquals(1, matchingTriggerIds.size());

    }
}
