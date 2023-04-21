package com.devonfw.cobigen.unittest.config.upgrade;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.SAXException;

import com.devonfw.cobigen.api.constants.BackupPolicy;
import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.config.upgrade.ContextConfigurationUpgrader;
import com.devonfw.cobigen.impl.config.upgrade.TemplateSetUpgrader;
import com.devonfw.cobigen.unittest.config.common.AbstractUnitTest;

/**
 * Test suite for {@link TemplateSetUpgrader}
 */
public class TemplateSetUpgraderTest extends AbstractUnitTest {

  /** Root path to all resources used in this test case */
  private static String testFileRootPath = "src/test/resources/testdata/unittest/config/upgrade/TemplateSetUpgraderTest/";

  /** JUnit Rule to create and automatically cleanup temporarily files/folders */
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  /**
   * Test the correct folder creation
   *
   * TODO: Check if this test is still valid, see: https://github.com/devonfw/cobigen/issues/1682
   *
   * @throws Exception test fails
   */
  @Test
  @Ignore // TODO: re-enable when upgrader was implemented, see: https://github.com/devonfw/cobigen/issues/1595
  public void testTemplateSetUpgrade() throws Exception {

    Path currentHome = this.tempFolder.newFolder(ConfigurationConstants.DEFAULT_HOME_DIR_NAME).toPath();

    FileUtils.copyDirectory(new File(testFileRootPath + "valid-2.1"), currentHome.toFile());
    Path templateLocation = currentHome.resolve(ConfigurationConstants.TEMPLATES_FOLDER)
        .resolve(ConfigurationConstants.COBIGEN_TEMPLATES);

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, currentHome.toString()).execute(() -> {
      TemplateSetUpgrader templateSetUpgrader = new TemplateSetUpgrader();
      templateSetUpgrader.upgradeTemplatesToTemplateSets(templateLocation);

      Path templateSetsPath = templateLocation.getParent().getParent()
          .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      Path backup = templateLocation.getParent().getParent().resolve(ConfigurationConstants.BACKUP_FOLDER);
      Path templateSetsAdapted = templateSetsPath.resolve(ConfigurationConstants.ADAPTED_FOLDER);
      assertThat(templateSetsPath).exists();
      assertThat(templateSetsAdapted).exists();
      assertThat(backup).exists();
    });
  }

  /**
   * Test the correct folder creation
   *
   * TODO: Check if this test is still valid, see: https://github.com/devonfw/cobigen/issues/1682
   *
   * @throws Exception test fails
   */
  @Test
  public void testTemplateSetUpgradeWithoutTemplatesFolder() throws Exception {

    Path currentHome = this.tempFolder.newFolder(ConfigurationConstants.DEFAULT_HOME_DIR_NAME).toPath();

    FileUtils.copyDirectory(new File(testFileRootPath + "valid-2.1/templates"), currentHome.toFile());
    Path templateLocation = currentHome.resolve(ConfigurationConstants.COBIGEN_TEMPLATES);

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, currentHome.toString()).execute(() -> {
      TemplateSetUpgrader templateSetUpgrader = new TemplateSetUpgrader();
      templateSetUpgrader.upgradeTemplatesToTemplateSets(templateLocation);

      Path templateSetsPath = templateLocation.getParent().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      Path templateSetsAdapted = templateSetsPath.resolve(ConfigurationConstants.ADAPTED_FOLDER);
      Path backup = templateLocation.getParent().resolve(ConfigurationConstants.BACKUP_FOLDER);
      assertThat(templateSetsPath).exists();
      assertThat(templateSetsAdapted).exists();
      assertThat(backup).exists();
    });
  }

  /**
   * Tests if the Template files have been correctly copied into both the new template set and the backup folder
   *
   * @throws Exception test fails
   */
  @Test
  @Ignore // TODO: re-enable when upgrader was implemented, see: https://github.com/devonfw/cobigen/issues/1595
  public void testTemplateSetUpgradeCopyOfTemplates() throws Exception {

    Path currentHome = this.tempFolder.newFolder(ConfigurationConstants.DEFAULT_HOME_DIR_NAME).toPath();

    FileUtils.copyDirectory(new File(testFileRootPath + "valid-2.1"), currentHome.toFile());
    Path templateLocation = currentHome.resolve(ConfigurationConstants.TEMPLATES_FOLDER)
        .resolve(ConfigurationConstants.COBIGEN_TEMPLATES);

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, currentHome.toString()).execute(() -> {
      Path templatesFolder = templateLocation.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
      int OldTemplatesFileCount = templatesFolder.toFile().list().length;
      List<Path> templatesFolderPaths = Files.walk(currentHome.resolve(ConfigurationConstants.TEMPLATES_FOLDER))
          .map(Path::getFileName).collect(Collectors.toList());

      TemplateSetUpgrader templateSetUpgrader = new TemplateSetUpgrader();
      Map<com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration, Path> newContextConfigurations = templateSetUpgrader
          .upgradeTemplatesToTemplateSets(templateLocation);

      Path newTemplatesPath = templateLocation.getParent().getParent()
          .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER).resolve(ConfigurationConstants.ADAPTED_FOLDER);
      Set<File> NewPathFilesSet = new HashSet<>(Arrays.asList(newTemplatesPath.toFile().listFiles()));

      assertEquals(OldTemplatesFileCount - 1, NewPathFilesSet.size());

      for (Path contextpath : newContextConfigurations.values()) {
        assertThat(contextpath).exists();

        validateContextConfigurationFile(contextpath, "v3.0");

      }
      List<Path> backupFolderFiles = Files.walk(
          currentHome.resolve(ConfigurationConstants.BACKUP_FOLDER).resolve(ConfigurationConstants.TEMPLATES_FOLDER))
          .map(Path::getFileName).collect(Collectors.toList());
      assertThat(backupFolderFiles).containsAll(templatesFolderPaths);

      for (File file : NewPathFilesSet) {
        if (!file.getName().equals(ConfigurationConstants.CONTEXT_CONFIG_FILENAME)) {
          assertThat(templatesFolderPaths).contains(file.toPath().getFileName());
        }
      }
    });

  }

  /**
   * Validates a context configuration file with given xsd schema version and fails the test if the validation failed.
   *
   * @param contextpath Path to context configuration file.
   * @param schemaVersion String version to validate against f.e. : v2.2.
   * @throws SAXException if a fatal error is found.
   * @throws IOException if the underlying reader throws an IOException.
   */
  @Ignore // TODO: re-enable when upgrader was implemented, see: https://github.com/devonfw/cobigen/issues/1595
  private void validateContextConfigurationFile(Path contextpath, String schemaVersion)
      throws SAXException, IOException {

    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    try (InputStream schemaStream = getClass()
        .getResourceAsStream("/schema/" + schemaVersion + "/contextConfiguration.xsd")) {
      StreamSource schemaSourceStream = new StreamSource(schemaStream);
      Schema schema = schemaFactory.newSchema(schemaSourceStream);
      Validator validator = schema.newValidator();
      StreamSource contextStream = new StreamSource(contextpath.toFile());
      try {
        validator.validate(contextStream);
      } catch (SAXException e) {
        fail("Exception shows that validator has found an error with the context configuration file \n" + e);
        contextStream.getInputStream().close();
        schemaStream.close();
      }
      schemaStream.close();
    }

  }

  /**
   * Tests if the context.xml has been created in the correct location and that it was correctly created as a v3.0
   * schema
   *
   * @throws Exception test fails
   */
  @Test
  @Ignore // TODO: re-enable when upgrader was implemented, see: https://github.com/devonfw/cobigen/issues/1595
  public void testTemplateSetUpgradeContextSplit() throws Exception {

    Path currentHome = this.tempFolder.newFolder(ConfigurationConstants.DEFAULT_HOME_DIR_NAME).toPath();

    FileUtils.copyDirectory(new File(testFileRootPath + "valid-2.1"), currentHome.toFile());
    Path templateLocation = currentHome.resolve(ConfigurationConstants.TEMPLATES_FOLDER)
        .resolve(ConfigurationConstants.COBIGEN_TEMPLATES);

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, currentHome.toString()).execute(() -> {
      ContextConfigurationUpgrader upgrader = new ContextConfigurationUpgrader();
      upgrader.upgradeConfigurationToLatestVersion(templateLocation, BackupPolicy.ENFORCE_BACKUP);

      Path newTemplatesPath = templateLocation.getParent().getParent()
          .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      newTemplatesPath = newTemplatesPath.resolve(ConfigurationConstants.ADAPTED_FOLDER);

      for (String s : newTemplatesPath.toFile().list()) {
        Path newContextPath = newTemplatesPath.resolve(s).resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
        newContextPath = newContextPath.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
        assertThat(newContextPath.toFile()).exists();

        validateContextConfigurationFile(newContextPath, "v3.0");
      }
    });
  }

}
