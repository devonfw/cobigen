package com.capgemini.cobigen.systemtest;

import static com.capgemini.cobigen.test.assertj.CobiGenAsserts.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.internal.matchers.Any.ANY;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.api.extension.MatcherInterpreter;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.api.to.GenerationReportTo;
import com.capgemini.cobigen.api.to.MatcherTo;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.impl.CobiGenFactory;
import com.capgemini.cobigen.impl.extension.PluginRegistry;
import com.capgemini.cobigen.impl.model.ModelBuilderImpl;
import com.capgemini.cobigen.systemtest.common.AbstractApiTest;
import com.capgemini.cobigen.systemtest.util.PluginMockFactory;
import com.capgemini.cobigen.test.matchers.MatcherToMatcher;

/**
 * Test suite for generation purposes.
 */
public class GenerationTest extends AbstractApiTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = apiTestsRootPath + "GenerationTest/";

    /**
     * Tests that sources get overwritten if merge strategy override is configured.
     * @throws Exception
     *             test fails.
     */
    @Test
    public void testOverrideMergeStrategy() throws Exception {
        Object input = PluginMockFactory.createSimpleJavaConfigurationMock();

        File folder = tmpFolder.newFolder("GenerationTest");
        File target = new File(folder, "generated.txt");
        FileUtils.write(target, "base");

        CobiGen cobigen = CobiGenFactory.create(new File(testFileRootPath + "overrideMergeStrategy").toURI());
        List<TemplateTo> templates = cobigen.getMatchingTemplates(input);
        assertThat(templates).hasSize(1);

        GenerationReportTo report = cobigen.generate(input, templates.get(0), Paths.get(folder.toURI()));

        assertThat(report).isSuccessful();
        assertThat(target).hasContent("overwritten");
    }

    /**
     * Tests whether context properties as well as cobigen properties are correctly resolved to be served in
     * the template in the {@link ModelBuilderImpl#NS_VARIABLES} namespace.
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCobiGenVariableAvailabilityInTemplates() throws Exception {
        Object input = new Object() {
            @Override
            public String toString() {
                return "input object";
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
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("fqn"), ANY, sameInstance(input))))).thenReturn(true);

        // Simulate variable resolving of any plug-in
        HashMap<String, String> variables = new HashMap<>(1);
        variables.put("contextVar", "contextValue");
        when(matcher.resolveVariables(any(MatcherTo.class), any(List.class))).thenReturn(variables);

        PluginRegistry.registerTriggerInterpreter(triggerInterpreter);

        // further setup
        File folder = tmpFolder.newFolder();

        CobiGen cobigen = CobiGenFactory.create(new File(testFileRootPath + "variableAvailability").toURI());
        List<TemplateTo> templates = cobigen.getMatchingTemplates(input);
        TemplateTo targetTemplate = getTemplate(templates, "t1");

        // execute
        GenerationReportTo report = cobigen.generate(input, targetTemplate, Paths.get(folder.toURI()));

        // assert
        assertThat(report).isSuccessful();
        File target = new File(folder, "generated.txt");
        assertThat(target).hasContent("contextValue,cobigenPropValue");
    }

    /**
     * Tests whether the cobigen properties specified in the target folder are correctly resolved to be served
     * in the template in the {@link ModelBuilderImpl#NS_VARIABLES} namespace.
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCobiGenVariableAvailabilityInTemplates_cobigenPropertiesTargetLocation() throws Exception {
        Object input = new Object() {
            @Override
            public String toString() {
                return "input object";
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
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("fqn"), ANY, sameInstance(input))))).thenReturn(true);

        // Simulate variable resolving of any plug-in
        HashMap<String, String> variables = new HashMap<>(1);
        variables.put("contextVar", "contextValue");
        when(matcher.resolveVariables(any(MatcherTo.class), any(List.class))).thenReturn(variables);

        PluginRegistry.registerTriggerInterpreter(triggerInterpreter);

        // further setup
        File folder = tmpFolder.newFolder();
        Path cobigenPropTarget = folder.toPath().resolve("cobigen.properties");
        Files.createFile(cobigenPropTarget);
        try (FileWriter writer = new FileWriter(cobigenPropTarget.toFile())) {
            IOUtils.write("cobigenPropTarget=extValue", writer);
        }

        CobiGen cobigen = CobiGenFactory.create(new File(testFileRootPath + "variableAvailability").toURI());
        List<TemplateTo> templates = cobigen.getMatchingTemplates(input);
        TemplateTo targetTemplate = getTemplate(templates, "t2");

        // execute
        GenerationReportTo report = cobigen.generate(input, targetTemplate, Paths.get(folder.toURI()));

        // assert
        assertThat(report).isSuccessful();
        File target = new File(folder, "generated2.txt");
        assertThat(target).hasContent("contextValue,cobigenPropValue,extValue");
    }

    /**
     * Finds the template with the given id in the list of templates and assures it to be found.
     * @param templates
     *            list of templates to search in
     * @param id
     *            to search for
     * @return non null found template
     */
    private TemplateTo getTemplate(List<TemplateTo> templates, String id) {
        TemplateTo targetTemplate = null;
        for (TemplateTo t : templates) {
            if (t.getId().equals(id)) {
                targetTemplate = t;
                break;
            }
        }
        assertThat(targetTemplate).as("Template to be generated").isNotNull();
        return targetTemplate;
    }

}
