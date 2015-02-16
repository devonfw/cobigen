package com.capgemini.cobigen.systemtest;

import static com.capgemini.cobigen.unittest.common.matchers.CustomHamcrestMatchers.hasItemsInList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.internal.matchers.Any.ANY;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.CobiGen;
import com.capgemini.cobigen.config.ContextConfiguration.ContextSetting;
import com.capgemini.cobigen.extension.IInputReader;
import com.capgemini.cobigen.extension.IMatcher;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.capgemini.cobigen.extension.to.TemplateTo;
import com.capgemini.cobigen.pluginmanager.PluginRegistry;
import com.capgemini.cobigen.systemtest.common.AbstractApiTest;
import com.capgemini.cobigen.unittest.common.matchers.MatcherToMatcher;
import com.capgemini.cobigen.unittest.common.matchers.VariableAssignmentToMatcher;
import com.google.common.collect.ImmutableMap;

/**
 * Test suite for template-scan related system tests
 * @author mbrunnli (07.12.2014)
 */
public class TemplateScanTest extends AbstractApiTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = apiTestsRootPath + "TemplateScanTest/";

    /**
     * Tests the correct destination resolution for resources obtained by template-scans
     * @throws Exception
     *             test fails
     * @author mbrunnli (07.12.2014)
     */
    @Test
    public void testCorrectDestinationResoution() throws Exception {
        Object input = createTestInputAndConfigureMock();

        File generationRootFolder = tmpFolder.newFolder("generationRootFolder");
        // Useful to see generates if necessary, comment the generationRootFolder above then
        // File generationRootFolder = new File(testFileRootPath + "generates");

        // pre-processing
        File templatesFolder = new File(testFileRootPath);
        CobiGen target = new CobiGen(templatesFolder.toURI());
        target.setContextSetting(ContextSetting.GenerationTargetRootPath,
            generationRootFolder.getAbsolutePath());
        List<TemplateTo> templates = target.getMatchingTemplates(input);
        Assert.assertNotNull(templates);
        Assert.assertEquals(1, templates.size());

        // Execution
        target.generate(input, templates.get(0), false);

        // Validation
        Assert.assertTrue(new File(generationRootFolder.getAbsolutePath() + SystemUtils.FILE_SEPARATOR
            + "src" + SystemUtils.FILE_SEPARATOR + "main" + SystemUtils.FILE_SEPARATOR + "java"
            + SystemUtils.FILE_SEPARATOR + "TestCOMP1" + SystemUtils.FILE_SEPARATOR + "CompONE.java")
            .exists());
    }

    /**
     *
     * @throws Exception
     *             test fails
     * @author mbrunnli (16.02.2015)
     */
    @Test
    public void testScanTemplatesFromArchivFile() throws Exception {

        // pre-processing: mocking
        Object input = createTestInputAndConfigureMock();

        // test processing
        CobiGen cobigen = new CobiGen(new File(testFileRootPath + "valid.zip").toURI());
        List<TemplateTo> templates = cobigen.getMatchingTemplates(input);

        // checking
        assertThat(templates, notNullValue());
        assertThat(templates.size(), equalTo(7));
    }

    /**
     * Creates simple to debug test data, which includes only one object as input. A
     * {@link ITriggerInterpreter TriggerInterpreter} will be mocked with all necessary supplier classes to
     * mock a simple java trigger interpreter. Furthermore, the mocked trigger interpreter will be directly
     * registered in the {@link PluginRegistry}.
     * @return the input for generation
     * @author mbrunnli (16.10.2014)
     */
    @SuppressWarnings("unchecked")
    private Object createTestInputAndConfigureMock() {
        // we only need any objects for inputs to have a unique object reference to affect the mocked method
        // calls as intended
        Object input = new Object() {
            @Override
            public String toString() {
                return "input object";
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
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("fqn"), ANY, sameInstance(input)))))
            .thenReturn(false);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("package"), ANY, sameInstance(input)))))
            .thenReturn(true);

        // Simulate container children resolution of any plug-in
        // when(inputReader.combinesMultipleInputObjects(argThat(sameInstance(input)))).thenReturn(false);

        // simulate return of method map
        // Map<String, Object> methodMap = new HashMap<>();
        // when(inputReader.getTemplateMethods(any())).thenReturn(methodMap);

        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("fqn"), ANY, sameInstance(input)))))
            .thenReturn(true);

        // Simulate variable resolving of any plug-in
        when(
            matcher.resolveVariables(
                argThat(new MatcherToMatcher(equalTo("fqn"), ANY, sameInstance(input))),
                argThat(hasItemsInList(
                    //
                    new VariableAssignmentToMatcher(equalTo("regex"), equalTo("rootPackage"), equalTo("1")),
                    new VariableAssignmentToMatcher(equalTo("regex"), equalTo("entityName"), equalTo("3"))))))
            .thenReturn(
                ImmutableMap.<String, String> builder().put("rootPackage", "com.capgemini")
                    .put("component", "comp1").build());

        PluginRegistry.registerTriggerInterpreter(triggerInterpreter);

        return input;
    }
}
