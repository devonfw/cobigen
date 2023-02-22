package com.devonfw.cobigen.systemtest;

import static com.devonfw.cobigen.api.assertj.CobiGenAsserts.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.InputReader;
import com.devonfw.cobigen.api.extension.MatcherInterpreter;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.api.matchers.MatcherToMatcher;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.api.to.MatcherTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.impl.model.ModelBuilderImpl;
import com.devonfw.cobigen.systemtest.common.AbstractApiTest;
import com.devonfw.cobigen.systemtest.util.PluginMockFactory;

/**
 * Test suite for generation purposes.
 */
public class GenerationIT extends AbstractApiTest {

  /**
   * Root path to the resources used in this test case with context.xml and templates.xml
   */
  private static String testFileRootPath = apiTestsRootPath + "GenerationTest/";

  /**
   * Root path to the resources used in this test case with template-set.xml
   */
  private static String testFileRootPathTemplateSetXml = apiTestsRootPath + "GenerationTestTemplateSetsXml/";

  /**
   * Tests that sources get overwritten if merge strategy override is configured.
   *
   * @throws Exception test fails.
   */
  @Test
  public void testOverrideMergeStrategy() throws Exception {

    Object input = PluginMockFactory.createSimpleJavaConfigurationMock();

    File folder = this.tmpFolder.newFolder("GenerationTest");
    File target = new File(folder, "generated.txt");
    FileUtils.write(target, "base");

    CobiGen cobigen = CobiGenFactory.create(new File(testFileRootPath + "overrideMergeStrategy").toURI(), true);
    List<TemplateTo> templates = cobigen.getMatchingTemplates(input);
    assertThat(templates).hasSize(2);

    GenerationReportTo report = cobigen.generate(input, templates.get(0), Paths.get(folder.toURI()));

    assertThat(report).isSuccessful();
    assertThat(target).hasContent("overwritten");
  }

  /**
   * Tests that multiple template sets with templates, increments and triggers get read and generate of all matching
   * templates is successful
   *
   * @throws Exception test fails.
   */
  @Test
  public void testReadMultipleTemplateSetXmls() throws Exception {

    CobiGen cobigen = CobiGenFactory.create(new File(testFileRootPathTemplateSetXml + "template-sets").toURI());

    Object input = cobigen.read(
        new File("src/test/java/com/devonfw/cobigen/systemtest/testobjects/io/generator/logic/api/to/InputEto.java")
            .toPath(),
        Charset.forName("UTF-8"), getClass().getClassLoader());

    File folder = this.tmpFolder.newFolder("GenerationTest");
    File target = new File(folder, "generated.txt");
    FileUtils.write(target, "base");

    List<TemplateTo> templates = cobigen.getMatchingTemplates(input);
    List<IncrementTo> increments = cobigen.getMatchingIncrements(input);
    List<String> triggersIds = cobigen.getMatchingTriggerIds(input);
    assertThat(templates).hasSize(5);
    assertThat(increments).hasSize(5);
    assertThat(triggersIds).hasSize(5);

    GenerationReportTo report1 = cobigen.generate(input, templates.get(0), Paths.get(folder.toURI()));
    GenerationReportTo report2 = cobigen.generate(input, templates.get(1), Paths.get(folder.toURI()));
    GenerationReportTo report3 = cobigen.generate(input, templates.get(2), Paths.get(folder.toURI()));
    GenerationReportTo report4 = cobigen.generate(input, templates.get(3), Paths.get(folder.toURI()));
    GenerationReportTo report5 = cobigen.generate(input, templates.get(4), Paths.get(folder.toURI()));

    assertThat(report1).isSuccessful();
    assertThat(report2).isSuccessful();
    assertThat(report3).isSuccessful();
    assertThat(report4).isSuccessful();
    assertThat(report5).isSuccessful();
  }

  /**
   * Tests whether the generation of external increments works properly.
   *
   * @throws Exception test fails
   */
  @Test
  public void testGenerationWithExternalIncrements() throws Exception {

    // given
    Object input = PluginMockFactory.createSimpleJavaConfigurationMock();

    File folder = this.tmpFolder.newFolder("GenerationTest");
    File target = new File(folder, "generated.txt");
    FileUtils.write(target, "base");

    // when
    CobiGen cobigen = CobiGenFactory.create(new File(testFileRootPath + "externalIncrementsGeneration").toURI(), true);
    List<TemplateTo> templates = cobigen.getMatchingTemplates(input);
    List<IncrementTo> increments = cobigen.getMatchingIncrements(input);
    List<String> triggersIds = cobigen.getMatchingTriggerIds(input);

    // assert
    IncrementTo externalIncrement = null;
    // we try to get an increment containing external increments
    for (IncrementTo inc : increments) {
      if (inc.getId().equals("3")) {
        externalIncrement = inc;
      }
    }

    assertThat(templates).hasSize(5);
    // We expect increment 3 to have an external increment 0 containing one template
    assertThat(externalIncrement).isNotNull();
    assertThat(externalIncrement.getDependentIncrements().get(0).getTemplates().size()).isEqualTo(1);
    // We expect two triggers, the main one and the external one
    assertThat(triggersIds.size()).isEqualTo(2);
  }

  /**
   * Tests generation of external increments where its trigger does not match
   *
   * @throws IOException test fails
   */
  public void testGenerationWithExternalIncrementsFailsWhenExternalTriggerNotMatch() throws IOException {

    // given
    Object input = PluginMockFactory.createSimpleJavaConfigurationMock();

    File folder = this.tmpFolder.newFolder("GenerationTest");
    File target = new File(folder, "generated.txt");
    FileUtils.write(target, "base");

    // when
    CobiGen cobigen = CobiGenFactory
        .create(new File(testFileRootPath + "externalIncrementsGenerationException").toURI());

    // exception is thrown while getting all increments
    assertThatThrownBy(() -> {
      cobigen.getMatchingIncrements(input);
    }).isInstanceOf(InvalidConfigurationException.class).hasMessageContaining(
        "An external incrementRef to valid_increment_composition::0 is referenced from external_incrementref but its trigger does not match");
  }

  /**
   * Tests whether context properties as well as cobigen properties are correctly resolved to be served in the template
   * in the {@link ModelBuilderImpl#NS_VARIABLES} namespace.
   *
   * @throws Exception test fails
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
    GeneratorPluginActivator activator = mock(GeneratorPluginActivator.class);
    TriggerInterpreter triggerInterpreter = mock(TriggerInterpreter.class);
    MatcherInterpreter matcher = mock(MatcherInterpreter.class);
    InputReader inputReader = mock(InputReader.class);

    when(triggerInterpreter.getType()).thenReturn("mockplugin");
    when(triggerInterpreter.getMatcher()).thenReturn(matcher);
    when(triggerInterpreter.getInputReader()).thenReturn(inputReader);

    when(inputReader.isValidInput(ArgumentMatchers.any())).thenReturn(true);
    when(matcher.matches(argThat(new MatcherToMatcher(equalTo("fqn"), any(String.class), sameInstance(input)))))
        .thenReturn(true);

    // Simulate variable resolving of any plug-in
    HashMap<String, String> variables = new HashMap<>(1);
    variables.put("contextVar", "contextValue");
    when(matcher.resolveVariables(ArgumentMatchers.any(MatcherTo.class), ArgumentMatchers.any(List.class),
        ArgumentMatchers.any())).thenReturn(variables);

    PluginRegistry.registerTriggerInterpreter(triggerInterpreter, activator);

    // further setup
    File folder = this.tmpFolder.newFolder();

    CobiGen cobigen = CobiGenFactory.create(new File(testFileRootPath + "variableAvailability").toURI(), true);
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
   * Tests whether the cobigen properties specified in the target folder are correctly resolved to be served in the
   * template in the {@link ModelBuilderImpl#NS_VARIABLES} namespace.
   *
   * @throws Exception test fails
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
    GeneratorPluginActivator activator = mock(GeneratorPluginActivator.class);
    TriggerInterpreter triggerInterpreter = mock(TriggerInterpreter.class);
    MatcherInterpreter matcher = mock(MatcherInterpreter.class);
    InputReader inputReader = mock(InputReader.class);

    when(triggerInterpreter.getType()).thenReturn("mockplugin");
    when(triggerInterpreter.getMatcher()).thenReturn(matcher);
    when(triggerInterpreter.getInputReader()).thenReturn(inputReader);

    when(inputReader.isValidInput(ArgumentMatchers.any())).thenReturn(true);
    when(matcher.matches(argThat(new MatcherToMatcher(equalTo("fqn"), any(String.class), sameInstance(input)))))
        .thenReturn(true);

    // Simulate variable resolving of any plug-in
    HashMap<String, String> variables = new HashMap<>(1);
    variables.put("contextVar", "contextValue");
    when(matcher.resolveVariables(ArgumentMatchers.any(MatcherTo.class), ArgumentMatchers.any(List.class),
        ArgumentMatchers.any())).thenReturn(variables);

    PluginRegistry.registerTriggerInterpreter(triggerInterpreter, activator);

    // further setup
    File folder = this.tmpFolder.newFolder();
    Path cobigenPropTarget = folder.toPath().resolve("cobigen.properties");
    Files.createFile(cobigenPropTarget);
    try (FileWriter writer = new FileWriter(cobigenPropTarget.toFile())) {
      IOUtils.write("cobigenPropTarget=extValue", writer);
    }

    CobiGen cobigen = CobiGenFactory.create(new File(testFileRootPath + "variableAvailability").toURI(), true);
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
   *
   * @param templates list of templates to search in
   * @param id to search for
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
