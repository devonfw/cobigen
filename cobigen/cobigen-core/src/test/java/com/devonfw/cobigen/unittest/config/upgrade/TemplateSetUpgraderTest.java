package com.devonfw.cobigen.unittest.config.upgrade;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
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

  /** Path to the template folder */
  private Path templateLocation;

  /** JUnit Rule to create and automatically cleanup temporarily files/folders */
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  private Path currentHome;

  @Before
  public void prepare() throws IOException {

    this.currentHome = this.tempFolder.newFolder(ConfigurationConstants.DEFAULT_HOME_DIR_NAME).toPath();
    FileUtils.copyDirectory(new File(testFileRootPath + "valid-2.1"), this.currentHome.toFile());
    this.templateLocation = this.currentHome.resolve(ConfigurationConstants.TEMPLATES_FOLDER)
        .resolve(ConfigurationConstants.COBIGEN_TEMPLATES);

  }

  /**
   * Test the correct folder creation
   *
   * @throws Exception
   */
  @Test
  public void testTemplateSetUpgrade() throws Exception {

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, this.currentHome.toString()).execute(() -> {
      TemplateSetUpgrader templateSetUpgrader = new TemplateSetUpgrader();
      templateSetUpgrader.upgradeTemplatesToTemplateSets(this.templateLocation);

      Path templateSetsPath = this.templateLocation.getParent().getParent()
          .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      Path templateSetsAdapted = templateSetsPath.resolve(ConfigurationConstants.ADAPTED_FOLDER);
      assertThat(templateSetsPath).exists();
      assertThat(templateSetsAdapted).exists();
    });
  }
  // TODO Fall CobigenTemplates ohne parent Templates folder als test implementieren

  /**
   * Tests if the Template files have been correctly copied into both the new template set and the backup folder
   *
   * @throws Exception
   */
  @Test
  public void testTemplateSetUpgradeCopyOfTemplates() throws Exception {

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, this.currentHome.toString()).execute(() -> {
      Path oldTemplatesPath = this.templateLocation.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
      int OldTemplatesFileCount = oldTemplatesPath.toFile().list().length;
      Set<String> OldPathFilesSet = new HashSet<>(Arrays.asList(oldTemplatesPath.toFile().list()));

      TemplateSetUpgrader templateSetUpgrader = new TemplateSetUpgrader();
      Map<com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration, Path> newContextConfigurations = templateSetUpgrader
          .upgradeTemplatesToTemplateSets(this.templateLocation);

      Path newTemplatesPath = this.templateLocation.getParent().getParent()
          .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER).resolve(ConfigurationConstants.ADAPTED_FOLDER);
      Set<String> NewPathFilesSet = new HashSet<>(Arrays.asList(newTemplatesPath.toFile().list()));

      assertEquals(OldTemplatesFileCount - 1, NewPathFilesSet.size());

      for (Path contextpath : newContextConfigurations.values()) {
        assertThat(contextpath).exists();

        validateContextConfigurationFile(contextpath, "v3.0");

      }

      for (String s : OldPathFilesSet) {
        if (!s.equals(ConfigurationConstants.CONTEXT_CONFIG_FILENAME)) {
          assertThat(NewPathFilesSet).contains(s);
          NewPathFilesSet.remove(s);
        }
      }
      assertThat(NewPathFilesSet).hasSize(0);
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
   * @throws Exception
   */
  @Test
  public void testTemplateSetUpgradeContextSplit() throws Exception {

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, this.currentHome.toString()).execute(() -> {
      ContextConfigurationUpgrader upgrader = new ContextConfigurationUpgrader();
      upgrader.upgradeConfigurationToLatestVersion(this.templateLocation, BackupPolicy.ENFORCE_BACKUP);

      Path newTemplatesPath = this.templateLocation.getParent().getParent()
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
