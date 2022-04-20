package com.devonfw.cobigen.unittest.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

import com.devonfw.cobigen.api.TemplateAdapter;
import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.adapter.TemplateAdapterImpl;

/**
 * Tests for {@link TemplateAdapterImpl}
 */
public class TemplateAdapterTest {

  /**
   * Root Path for tests with the monolithic template structure
   */
  private static final Path rootTestPathMonolithicTemplates = new File(
      "src/test/resources/testdata/unittest/TemplateAdapterTest/homeMonolithicTemplates/templates").toPath();

  /**
   * Root Path for tests with the template set structure
   */
  private static final Path rootTestPathTemplateSets = new File(
      "src/test/resources/testdata/unittest/TemplateAdapterTest/homeTemplateSets/template-sets").toPath();

  /**
   * Tests if the {@link TemplateAdapter} recognizes that it is an old monolithic template structure
   *
   */
  @Test
  public void testIsMonolithicTemplatesConfigurationWithOldConfig() {

    TemplateAdapter templateAdapter = new TemplateAdapterImpl(rootTestPathMonolithicTemplates);
    assertThat(templateAdapter.isMonolithicTemplatesConfiguration()).isTrue();
  }

  /**
   * Tests if the {@link TemplateAdapter} recognizes that it is a new template structure
   *
   */
  @Test
  public void testIsMonolithicTemplatesConfigurationWithNewConfig() {

    TemplateAdapter templateAdapter = new TemplateAdapterImpl(rootTestPathTemplateSets);
    assertThat(templateAdapter.isMonolithicTemplatesConfiguration()).isFalse();
  }

  /**
   * Tests if the {@link TemplateAdapter} is able to get the list of available template jars
   *
   */
  @Test
  public void testGetTemplateJarsToAdapt() {

    TemplateAdapter templateAdapter = new TemplateAdapterImpl(rootTestPathTemplateSets);
    List<Path> templateJars = templateAdapter.getTemplateSetJars();
    assertThat(templateJars.size()).isEqualTo(2);
  }

  /**
   * Tests if the {@link TemplateAdapter} recognizes which template sets are already adapted
   *
   */
  @Test
  public void testIsTemplateSetAlreadyAdapted() {

    TemplateAdapter templateAdapter = new TemplateAdapterImpl(rootTestPathTemplateSets);

    Path templateJarAdapted = rootTestPathTemplateSets.resolve(ConfigurationConstants.DOWNLOADED_FOLDER)
        .resolve("template-set-1.jar");
    Path templateJarNotAdapted = rootTestPathTemplateSets.resolve(ConfigurationConstants.DOWNLOADED_FOLDER)
        .resolve("template-set-2.jar");

    assertThat(templateAdapter.isTemplateSetAlreadyAdapted(templateJarAdapted)).isTrue();
    assertThat(templateAdapter.isTemplateSetAlreadyAdapted(templateJarNotAdapted)).isFalse();
  }

}
