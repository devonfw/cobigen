package com.devonfw.cobigen.unittest.config.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.impl.config.entity.ContainerMatcher;
import com.devonfw.cobigen.impl.config.entity.Matcher;
import com.devonfw.cobigen.impl.config.entity.Template;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.entity.VariableAssignment;
import com.devonfw.cobigen.impl.config.entity.io.AccumulationType;
import com.devonfw.cobigen.impl.config.reader.TemplateSetConfigurationReader;
import com.devonfw.cobigen.unittest.config.common.AbstractUnitTest;

import junit.framework.TestCase;

/**
 * This {@link TestCase} tests the {@link TemplateSetConfigurationReader}
 */
public class TemplateSetConfigurationReaderTest extends AbstractUnitTest {

  /**
   * Root path to all resources used in this test case
   */
  private static String testFileRootPath = "src/test/resources/testdata/unittest/config/reader/TemplateSetConfigurationReaderTest/";

  private static Path invalidConfigurationPath = Paths.get(new File(testFileRootPath + "invalid_template_sets").toURI())
      .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);

  private static Path validConfigurationPath = Paths.get(new File(testFileRootPath + "valid_template_sets").toURI())
      .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);

  /**
   * Tests whether an invalid configuration results in an {@link InvalidConfigurationException}
   *
   * @throws InvalidConfigurationException expected
   */
  @Test(expected = InvalidConfigurationException.class)
  public void testErrorOnInvalidConfiguration() throws InvalidConfigurationException {

    new TemplateSetConfigurationReader(Paths.get(new File(testFileRootPath + "faulty").toURI()));
  }

  /**
   * Tests whether an {@link InvalidConfigurationException} will be thrown when no template set configuration is found
   * in the template-sets directory
   *
   * @throws InvalidConfigurationException if no template set configuration is found
   *
   */
  @Test
  public void testInvalidTemplateSets() throws InvalidConfigurationException {

    Throwable invalidException = assertThrows(InvalidConfigurationException.class, () -> {
      new TemplateSetConfigurationReader(invalidConfigurationPath);
    });

    assertThat(invalidException instanceof InvalidConfigurationException);
    assertThat(invalidException.getMessage())
        .contains("Could not find any template set configuration file in the given folder.");
  }

  /**
   * Tests whether a valid configuration can be read from template-sets/adapted folder
   *
   * @throws Exception test fails
   */
  @Test
  public void testTemplateSetLoadedFromNewConfiguration() throws Exception {

    CobiGenFactory.create(new File(testFileRootPath + "valid_template_sets_adapted").toURI().resolve("template-sets"));
  }

  /**
   * Tests if a template set configuration can be found from a template set jar file
   *
   */
  @Test
  public void testTemplateSetsDownloaded() {

    Path templateSetPath = Paths.get(new File(testFileRootPath + "valid_template_sets").toURI())
        .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    // templateSetPath = Path.of(templateSetPath + "/downloaded");
    TemplateSetConfigurationReader templateSet = new TemplateSetConfigurationReader(templateSetPath);
    assertThat(templateSet.getTemplateSetFiles().size()).isEqualTo(1);
  }

  /**
   * Tests if template-set configuration can be found in both adapted and downloaded folder of the template sets
   * directory
   *
   */
  @Test
  public void testTemplateSetsAdaptedAndDownloaded() {

    TemplateSetConfigurationReader templateSet = new TemplateSetConfigurationReader(validConfigurationPath);

    assertThat(templateSet.getTemplateSetFiles().size()).isEqualTo(3);
  }

  /**
   * Tests whether a valid template set configuration can be read from a zip file.
   *
   * @throws Exception test fails
   */
  @Test
  public void testReadConfigurationFromZip() throws Exception {

    CobiGenFactory.create(new File(testFileRootPath + "valid.zip").toURI(), true);
  }

  /**
   * Tests if loadTriggers actually loads all (and the right) triggers
   */
  @Test
  public void testLoadTriggersShouldLoadAllTriggers() {

    // given
    TemplateSetConfigurationReader templateSet = new TemplateSetConfigurationReader(validConfigurationPath);

    // when
    LinkedList<Matcher> matchers = new LinkedList<>();
    List<VariableAssignment> variableAssignments = new LinkedList<>();
    Matcher testMatcher = new Matcher("fqn", "*", variableAssignments, AccumulationType.OR);
    matchers.add(testMatcher);
    Trigger trigger = new Trigger("valid", "java", "src/main/templates", Charset.forName("UTF-8"), matchers,
        new LinkedList<ContainerMatcher>());
    Map<String, Trigger> testMap = templateSet.loadTriggers();

    // then
    Trigger loadedTrigger = testMap.values().iterator().next();
    assertThat(testMap.size()).isEqualTo(1);
    assertThat(trigger.getId()).isEqualTo(loadedTrigger.getId());
    assertThat(trigger.getType()).isEqualTo(loadedTrigger.getType());
    assertThat(trigger.getTemplateFolder()).isEqualTo(loadedTrigger.getTemplateFolder());
    assertThat(trigger.getInputCharset()).isEqualTo(loadedTrigger.getInputCharset());
    assertThat(trigger.getMatcher() == loadedTrigger.getMatcher());
    assertThat(trigger.getContainerMatchers()).isEqualTo(loadedTrigger.getContainerMatchers());
  }

  /**
   * Tests if getConfigLocationForTrigger returns the right location
   */
  @Test
  public void testGetConfigLocationForTrigger() {

    TemplateSetConfigurationReader templateSet = new TemplateSetConfigurationReader(validConfigurationPath);
    templateSet.loadTriggers();

    Trigger trigger = new Trigger("valid", "java", "src/main/templates", Charset.forName("UTF-8"),
        new LinkedList<Matcher>(), new LinkedList<ContainerMatcher>());
    Path configPath = Paths.get(System.getProperty("user.dir"),
        "src/test/resources/testdata/unittest/config/reader/TemplateSetConfigurationReaderTest/valid_template_sets/template-sets/adapted/test_template");

    assertThat(templateSet.getConfigLocationForTrigger(trigger.getId())).isEqualTo(configPath);
  }

  /**
   * Tests that templates will be correctly resolved by the template-scan mechanism.
   *
   * @throws Exception test fails
   */
  @Test
  public void testTemplateScan() throws Exception {

    // given
    TemplateSetConfigurationReader templateSet = new TemplateSetConfigurationReader(validConfigurationPath);
    // TemplateSetConfigurationReader templateSet = new TemplateSetConfigurationReader(new
    // File(testFileRootPath).toPath(),
    // "valid_template_sets/template-sets/adapted/test_template/src/main/templates");

    Trigger trigger = new Trigger("valid", "java", "src/main/templates", Charset.forName("UTF-8"),
        new LinkedList<Matcher>(), new LinkedList<ContainerMatcher>());

    // when
    Map<String, Template> templates = templateSet.loadTemplates(trigger);

    // then
    assertThat(templates).isNotNull().hasSize(6);

    String templateIdFooClass = "prefix_FooClass.java";
    Template templateFooClass = templates.get(templateIdFooClass);
    assertThat(templateFooClass).isNotNull();
    assertThat(templateFooClass.getName()).isEqualTo(templateIdFooClass);
    assertThat(templateFooClass.getRelativeTemplatePath()).isEqualTo("foo/FooClass.java.ftl");
    assertThat(templateFooClass.getUnresolvedTargetPath()).isEqualTo("src/main/java/foo/FooClass.java");
    assertThat(templateFooClass.getMergeStrategy()).isNull();
  }

}