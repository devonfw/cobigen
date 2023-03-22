package com.devonfw.cobigen.unittest.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.TemplateAdapter;
import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.adapter.TemplateAdapterImpl;

/**
 * Tests for {@link TemplateAdapterImpl}
 */
public class TemplateAdapterTest {

  /** Temporary files rule to create temporary folders or files */
  @ClassRule
  public static TemporaryFolder tempFolder = new TemporaryFolder();

  /**
   * Tests if the {@link TemplateAdapter} isMonolithicTemplatesConfiguration() recognizes that it is an old monolithic
   * template structure
   *
   * @throws IOException test fails
   *
   */
  @Test
  public void testIsMonolithicTemplatesConfigurationWithOldConfig() throws IOException {

    Path rootTestPathMonolithicTemplates = tempFolder
        .newFolder("homeMonolithicTemplates", ConfigurationConstants.TEMPLATES_FOLDER).toPath();

    TemplateAdapter templateAdapter = new TemplateAdapterImpl(rootTestPathMonolithicTemplates);
    assertThat(templateAdapter.isMonolithicTemplatesConfiguration()).isTrue();
  }

  /**
   * Tests if the {@link TemplateAdapter} isMonolithicTemplatesConfiguration() recognizes that it is a new template
   * structure
   *
   * @throws IOException test fails
   *
   */
  @Test
  public void testIsMonolithicTemplatesConfigurationWithNewConfig() throws IOException {

    Path rootTestPathTemplateSets = tempFolder.newFolder("NotMonolithic", ConfigurationConstants.TEMPLATE_SETS_FOLDER)
        .toPath();
    Files.createDirectory(rootTestPathTemplateSets.resolve(ConfigurationConstants.DOWNLOADED_FOLDER));

    TemplateAdapter templateAdapter = new TemplateAdapterImpl(rootTestPathTemplateSets);
    assertThat(templateAdapter.isMonolithicTemplatesConfiguration()).isFalse();
  }

  /**
   * Tests if the {@link TemplateAdapter} getTemplateSetJars() is able to get two adaptable template sets (consisting of
   * classes and sources)
   *
   * @throws IOException test fails
   *
   */
  @Test
  public void testGetTemplateJarsToAdaptFindsTwoAdaptableTemplateSets() throws IOException {

    Path rootTestPathTemplateSets = tempFolder.newFolder("TwoAdaptable", ConfigurationConstants.TEMPLATE_SETS_FOLDER)
        .toPath();

    Path downloadedFolder = Files
        .createDirectory(rootTestPathTemplateSets.resolve(ConfigurationConstants.DOWNLOADED_FOLDER));
    Files.createFile(downloadedFolder.resolve("template-set-1.jar"));
    Files.createFile(downloadedFolder.resolve("template-set-1-sources.jar"));
    Files.createFile(downloadedFolder.resolve("template-set-2.jar"));
    Files.createFile(downloadedFolder.resolve("template-set-2-sources.jar"));
    TemplateAdapter templateAdapter = new TemplateAdapterImpl(rootTestPathTemplateSets);
    List<Path> templateJars = templateAdapter.getTemplateSetJars();
    assertThat(templateJars.size()).isEqualTo(2);
  }

  /**
   * Tests if the {@link TemplateAdapter} getTemplateSetJars() returns an empty list if no adaptable template set was
   * found (missing sources)
   *
   * @throws IOException test fails
   */
  @Test
  public void testGetTemplateJarsToAdaptWithMissingSourceFileReturnsEmptyList() throws IOException {

    Path rootTestPathTemplateSets = tempFolder.newFolder("MissingSource", ConfigurationConstants.TEMPLATE_SETS_FOLDER)
        .toPath();

    Path downloadedFolder = Files
        .createDirectory(rootTestPathTemplateSets.resolve(ConfigurationConstants.DOWNLOADED_FOLDER));
    Files.createFile(downloadedFolder.resolve("template-set-1.jar"));
    TemplateAdapter templateAdapter = new TemplateAdapterImpl(rootTestPathTemplateSets);
    List<Path> templateJars = templateAdapter.getTemplateSetJars();
    assertThat(templateJars).isEmpty();
  }

  /**
   * Tests if the {@link TemplateAdapter} getTemplateSetJars() returns an empty list if no adaptable template set was
   * found (empty folder)
   *
   * @throws IOException test fails
   */
  @Test
  public void testGetTemplateJarsToAdaptReturnsEmptyListForEmptyFolder() throws IOException {

    Path rootTestPathTemplateSets = tempFolder.newFolder("EmptyFolder", ConfigurationConstants.TEMPLATE_SETS_FOLDER)
        .toPath();

    Files.createDirectory(rootTestPathTemplateSets.resolve(ConfigurationConstants.DOWNLOADED_FOLDER));
    TemplateAdapter templateAdapter = new TemplateAdapterImpl(rootTestPathTemplateSets);
    List<Path> templateJars = templateAdapter.getTemplateSetJars();
    assertThat(templateJars).isEmpty();
  }

  /**
   * Tests if the {@link TemplateAdapter} isTemplateSetAlreadyAdapted() recognizes which template sets are already
   * adapted
   *
   * @throws IOException test fails
   *
   */
  @Test
  public void testIsTemplateSetAlreadyAdapted() throws IOException {

    Path rootTestPathTemplateSets = tempFolder.newFolder("AlreadyAdapted", ConfigurationConstants.TEMPLATE_SETS_FOLDER)
        .toPath();
    Path downloadedFolder = Files
        .createDirectory(rootTestPathTemplateSets.resolve(ConfigurationConstants.DOWNLOADED_FOLDER));
    Files.createFile(downloadedFolder.resolve("template-set-1.jar"));
    Files.createFile(downloadedFolder.resolve("template-set-1-sources.jar"));
    Files.createFile(downloadedFolder.resolve("template-set-2.jar"));
    Path adaptedFolder = Files.createDirectory(rootTestPathTemplateSets.resolve(ConfigurationConstants.ADAPTED_FOLDER));
    Files.createDirectory(adaptedFolder.resolve("template-set-1"));

    TemplateAdapter templateAdapter = new TemplateAdapterImpl(rootTestPathTemplateSets);

    Path templateJarAdapted = rootTestPathTemplateSets.resolve(ConfigurationConstants.DOWNLOADED_FOLDER)
        .resolve("template-set-1.jar");

    Path templateJarNotAdapted = rootTestPathTemplateSets.resolve(ConfigurationConstants.DOWNLOADED_FOLDER)
        .resolve("template-set-2.jar");

    assertThat(templateAdapter.isTemplateSetAlreadyAdapted(templateJarAdapted)).isTrue();
    assertThat(templateAdapter.isTemplateSetAlreadyAdapted(templateJarNotAdapted)).isFalse();
  }

}
