package com.devonfw.cobigen.systemtest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.internal.matchers.Any.ANY;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.InputReader;
import com.devonfw.cobigen.api.extension.MatcherInterpreter;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.systemtest.common.AbstractApiTest;
import com.devonfw.cobigen.test.matchers.MatcherToMatcher;

/**
 * Test suite, which tests activation of triggers due to matcher accumulation types.
 * @author mbrunnli (22.02.2015)
 */
public class TriggerActivationTest extends AbstractApiTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = apiTestsRootPath + "TriggerActivationTest/";

    /**
     * Tests that a trigger will not be activated in case of one of two AND Matchers matches.
     * @throws Exception
     *             test fails
     * @author mbrunnli (22.02.2015)
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testNoActivation_1Of2AND_MatcherMatches() throws Exception {

        Object input = new Object();

        // Pre-processing: Mocking
        GeneratorPluginActivator activator = mock(GeneratorPluginActivator.class);
        TriggerInterpreter triggerInterpreter = mock(TriggerInterpreter.class);
        MatcherInterpreter matcher = mock(MatcherInterpreter.class);
        InputReader inputReader = mock(InputReader.class);

        when(triggerInterpreter.getType()).thenReturn("test");
        when(triggerInterpreter.getMatcher()).thenReturn(matcher);
        when(triggerInterpreter.getInputReader()).thenReturn(inputReader);

        when(inputReader.isValidInput(any())).thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("and1"), ANY, sameInstance(input)))))
            .thenReturn(false);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("and2"), ANY, sameInstance(input)))))
            .thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("or"), ANY, sameInstance(input))))).thenReturn(false);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("not"), ANY, sameInstance(input)))))
            .thenReturn(false);

        PluginRegistry.registerTriggerInterpreter(triggerInterpreter, activator);

        // execution
        CobiGen cobigen = CobiGenFactory.create(new File(testFileRootPath + "templates").toURI());
        List<String> matchingTriggerIds = cobigen.getMatchingTriggerIds(input);

        assertThat(matchingTriggerIds, not(hasItem("triggerId")));
    }

    /**
     * Tests that a trigger will not be activated in case of one of two AND Matchers and one OR matcher
     * matches.
     * @throws Exception
     *             test fails
     * @author mbrunnli (22.02.2015)
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testNoActivation_1Of2AND_1OR_MatcherMatches() throws Exception {

        Object input = new Object();

        // Pre-processing: Mocking
        GeneratorPluginActivator activator = mock(GeneratorPluginActivator.class);
        TriggerInterpreter triggerInterpreter = mock(TriggerInterpreter.class);
        MatcherInterpreter matcher = mock(MatcherInterpreter.class);
        InputReader inputReader = mock(InputReader.class);

        when(triggerInterpreter.getType()).thenReturn("test");
        when(triggerInterpreter.getMatcher()).thenReturn(matcher);
        when(triggerInterpreter.getInputReader()).thenReturn(inputReader);

        when(inputReader.isValidInput(any())).thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("and1"), ANY, sameInstance(input)))))
            .thenReturn(false);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("and2"), ANY, sameInstance(input)))))
            .thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("or"), ANY, sameInstance(input))))).thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("not"), ANY, sameInstance(input)))))
            .thenReturn(false);

        PluginRegistry.registerTriggerInterpreter(triggerInterpreter, activator);

        // execution
        CobiGen cobigen = CobiGenFactory.create(new File(testFileRootPath + "templates").toURI());
        List<String> matchingTriggerIds = cobigen.getMatchingTriggerIds(input);

        assertThat(matchingTriggerIds, not(hasItem("triggerId")));
    }

    /**
     * Tests that a trigger will be activated in case of two of two AND Matchers matches.
     * @throws Exception
     *             test fails
     * @author mbrunnli (22.02.2015)
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testActivation_2Of2AND_MatcherMatches() throws Exception {

        Object input = new Object();

        // Pre-processing: Mocking
        GeneratorPluginActivator activator = mock(GeneratorPluginActivator.class);
        TriggerInterpreter triggerInterpreter = mock(TriggerInterpreter.class);
        MatcherInterpreter matcher = mock(MatcherInterpreter.class);
        InputReader inputReader = mock(InputReader.class);

        when(triggerInterpreter.getType()).thenReturn("test");
        when(triggerInterpreter.getMatcher()).thenReturn(matcher);
        when(triggerInterpreter.getInputReader()).thenReturn(inputReader);

        when(inputReader.isValidInput(any())).thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("and1"), ANY, sameInstance(input)))))
            .thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("and2"), ANY, sameInstance(input)))))
            .thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("or"), ANY, sameInstance(input))))).thenReturn(false);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("not"), ANY, sameInstance(input)))))
            .thenReturn(false);

        PluginRegistry.registerTriggerInterpreter(triggerInterpreter, activator);

        // execution
        CobiGen cobigen = CobiGenFactory.create(new File(testFileRootPath + "templates").toURI());
        List<String> matchingTriggerIds = cobigen.getMatchingTriggerIds(input);

        assertThat(matchingTriggerIds, hasItem("triggerId"));
    }

    /**
     * Tests that a trigger will not be activated in case of two of two AND matchers and one NOT matcher
     * matches.
     * @throws Exception
     *             test fails
     * @author mbrunnli (22.02.2015)
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testNoActivation_2Of2AND_1NOT_MatcherMatches() throws Exception {

        Object input = new Object();

        // Pre-processing: Mocking
        GeneratorPluginActivator activator = mock(GeneratorPluginActivator.class);
        TriggerInterpreter triggerInterpreter = mock(TriggerInterpreter.class);
        MatcherInterpreter matcher = mock(MatcherInterpreter.class);
        InputReader inputReader = mock(InputReader.class);

        when(triggerInterpreter.getType()).thenReturn("test");
        when(triggerInterpreter.getMatcher()).thenReturn(matcher);
        when(triggerInterpreter.getInputReader()).thenReturn(inputReader);

        when(inputReader.isValidInput(any())).thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("and1"), ANY, sameInstance(input)))))
            .thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("and2"), ANY, sameInstance(input)))))
            .thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("or"), ANY, sameInstance(input))))).thenReturn(false);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("not"), ANY, sameInstance(input))))).thenReturn(true);

        PluginRegistry.registerTriggerInterpreter(triggerInterpreter, activator);

        // execution
        CobiGen cobigen = CobiGenFactory.create(new File(testFileRootPath + "templates").toURI());
        List<String> matchingTriggerIds = cobigen.getMatchingTriggerIds(input);

        assertThat(matchingTriggerIds, not(hasItem("triggerId")));
    }

    /**
     * Tests that a trigger will not be activated in case of one OR matcher but no AND matcher matches.
     * @throws Exception
     *             test fails
     * @author mbrunnli (22.02.2015)
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testNoActivation_1OR_0AND_MatcherMatches() throws Exception {

        Object input = new Object();

        // Pre-processing: Mocking
        GeneratorPluginActivator activator = mock(GeneratorPluginActivator.class);
        TriggerInterpreter triggerInterpreter = mock(TriggerInterpreter.class);
        MatcherInterpreter matcher = mock(MatcherInterpreter.class);
        InputReader inputReader = mock(InputReader.class);

        when(triggerInterpreter.getType()).thenReturn("test");
        when(triggerInterpreter.getMatcher()).thenReturn(matcher);
        when(triggerInterpreter.getInputReader()).thenReturn(inputReader);

        when(inputReader.isValidInput(any())).thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("and1"), ANY, sameInstance(input)))))
            .thenReturn(false);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("and2"), ANY, sameInstance(input)))))
            .thenReturn(false);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("or"), ANY, sameInstance(input))))).thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("not"), ANY, sameInstance(input)))))
            .thenReturn(false);

        PluginRegistry.registerTriggerInterpreter(triggerInterpreter, activator);

        // execution
        CobiGen cobigen = CobiGenFactory.create(new File(testFileRootPath + "templates").toURI());
        List<String> matchingTriggerIds = cobigen.getMatchingTriggerIds(input);

        assertThat(matchingTriggerIds, not(hasItem("triggerId")));
    }

    /**
     * Tests that a trigger will not be activated in case of one OR matcher and one NOT matcher matches.
     * @throws Exception
     *             test fails
     * @author mbrunnli (22.02.2015)
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testNoActivation_1OR_1NOT_MatcherMatches() throws Exception {

        Object input = new Object();

        // Pre-processing: Mocking
        GeneratorPluginActivator activator = mock(GeneratorPluginActivator.class);
        TriggerInterpreter triggerInterpreter = mock(TriggerInterpreter.class);
        MatcherInterpreter matcher = mock(MatcherInterpreter.class);
        InputReader inputReader = mock(InputReader.class);

        when(triggerInterpreter.getType()).thenReturn("test");
        when(triggerInterpreter.getMatcher()).thenReturn(matcher);
        when(triggerInterpreter.getInputReader()).thenReturn(inputReader);

        when(inputReader.isValidInput(any())).thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("and1"), ANY, sameInstance(input)))))
            .thenReturn(false);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("and2"), ANY, sameInstance(input)))))
            .thenReturn(false);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("or"), ANY, sameInstance(input))))).thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("not"), ANY, sameInstance(input))))).thenReturn(true);

        PluginRegistry.registerTriggerInterpreter(triggerInterpreter, activator);

        // execution
        CobiGen cobigen = CobiGenFactory.create(new File(testFileRootPath + "templates").toURI());
        List<String> matchingTriggerIds = cobigen.getMatchingTriggerIds(input);

        assertThat(matchingTriggerIds, not(hasItem("triggerId")));
    }

    /**
     * Tests that a trigger will be activated in case of one OR matcher matches.
     * @throws Exception
     *             test fails
     * @author mbrunnli (22.02.2015)
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testActivation_1OR_MatcherMatches() throws Exception {

        Object input = new Object();

        // Pre-processing: Mocking
        GeneratorPluginActivator activator = mock(GeneratorPluginActivator.class);
        TriggerInterpreter triggerInterpreter = mock(TriggerInterpreter.class);
        MatcherInterpreter matcher = mock(MatcherInterpreter.class);
        InputReader inputReader = mock(InputReader.class);

        when(triggerInterpreter.getType()).thenReturn("test");
        when(triggerInterpreter.getMatcher()).thenReturn(matcher);
        when(triggerInterpreter.getInputReader()).thenReturn(inputReader);

        when(inputReader.isValidInput(any())).thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("or"), ANY, sameInstance(input))))).thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("not"), ANY, sameInstance(input)))))
            .thenReturn(false);

        PluginRegistry.registerTriggerInterpreter(triggerInterpreter, activator);

        // execution
        CobiGen cobigen = CobiGenFactory.create(new File(testFileRootPath + "templates").toURI());
        List<String> matchingTriggerIds = cobigen.getMatchingTriggerIds(input);

        assertThat(matchingTriggerIds, hasItem("triggerId2"));
    }
}
