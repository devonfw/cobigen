package com.devonfw.cobigen.unittest.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.TemplateAdapter;
import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.util.mavencoordinate.MavenCoordinateStatePair;
import com.devonfw.cobigen.impl.adapter.TemplateAdapterImpl;

/**
 * Tests for {@link TemplateAdapterImpl}
 */
// TODO: fix tests for new structure
public class TemplateAdapterTest {

  /**
   * Root Path for tests with the monolithic template structure
   */
  private static Path rootTestPathMonolithicTemplates;

  /**
   * Root Path for tests with the template set structure
   */
  private static Path rootTestPathTemplateSets;

  /** Temporary files rule to create temporary folders or files */
  @ClassRule
  public static TemporaryFolder tempFolder = new TemporaryFolder();

  /**
   * Creates a temporary directory structure for the tests
   *
   * @throws IOException if an Exception occurs
   */
  @BeforeClass
  public static void prepare() throws IOException {

    rootTestPathMonolithicTemplates = tempFolder
        .newFolder("homeMonolithicTemplates", ConfigurationConstants.TEMPLATES_FOLDER).toPath();
    Files.createFile(rootTestPathMonolithicTemplates.resolve("template.jar"));

    rootTestPathTemplateSets = tempFolder.newFolder("homeTemplateSets", ConfigurationConstants.TEMPLATE_SETS_FOLDER)
        .toPath();
    Path downloadedFolder = Files
        .createDirectory(rootTestPathTemplateSets.resolve(ConfigurationConstants.DOWNLOADED_FOLDER));
    Files.createFile(downloadedFolder.resolve("template-set-1.jar"));
    Files.createFile(downloadedFolder.resolve("template-set-2.jar"));
    Path adaptedFolder = Files.createDirectory(rootTestPathTemplateSets.resolve(ConfigurationConstants.ADAPTED_FOLDER));
    Files.createDirectory(adaptedFolder.resolve("template-set-1"));
  }

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
    List<MavenCoordinateStatePair> templateJars = templateAdapter.getTemplateSetMavenCoordinateStatePairs();
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
