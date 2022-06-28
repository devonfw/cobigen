package com.devonfw.cobigen.unittest.config.upgrade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
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

  @Before
  public void prepare() throws IOException {

    Path playground = this.tempFolder.newFolder(".cobigen").toPath();
    FileUtils.copyDirectory(new File(testFileRootPath + "valid-2.1"), playground.toFile());
    this.templateLocation = playground.resolve(ConfigurationConstants.TEMPLATES_FOLDER);
  }

  /**
   * Test the correct folder creation
   *
   * @throws Exception
   */
  @Test
  public void testTemplateSetUpgrade() throws Exception {

    TemplateSetUpgrader templateSetUpgrader = new TemplateSetUpgrader();
    templateSetUpgrader.upgradeTemplatesToTemplateSets(this.templateLocation);

    Path templateSetsPath = this.templateLocation.getParent().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    Path templateSetsAdapted = templateSetsPath.resolve(ConfigurationConstants.ADAPTED_FOLDER);
    assertThat(templateSetsPath).exists();
    assertThat(templateSetsAdapted).exists();

  }

  /**
   * Tests if the Template files have been correctly copied into both the new template set and the backup folder
   *
   * @throws Exception
   */
  @Test
  public void testTemplateSetUpgradeCopyOfTemplates() throws Exception {

    Path oldTemplatesPath = this.templateLocation.resolve(ConfigurationConstants.COBIGEN_TEMPLATES)
        .resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
    int OldTemplatesFileCount = oldTemplatesPath.toFile().list().length;
    Set<String> OldPathFilesSet = new HashSet<>(Arrays.asList(oldTemplatesPath.toFile().list()));

    TemplateSetUpgrader templateSetUpgrader = new TemplateSetUpgrader();
    templateSetUpgrader.upgradeTemplatesToTemplateSets(this.templateLocation);

    Path backupPath = this.templateLocation.getParent().resolve("backup")
        .resolve(ConfigurationConstants.TEMPLATES_FOLDER).resolve(ConfigurationConstants.COBIGEN_TEMPLATES)
        .resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
    Set<String> backupPathFilesSet = new HashSet<>(Arrays.asList(backupPath.toFile().list()));
    Path newTemplatesPath = this.templateLocation.getParent().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER)
        .resolve(ConfigurationConstants.ADAPTED_FOLDER);
    Set<String> NewPathFilesSet = new HashSet<>(Arrays.asList(newTemplatesPath.toFile().list()));

    assertEquals(OldTemplatesFileCount - 1, NewPathFilesSet.size());
    assertEquals(OldTemplatesFileCount, backupPathFilesSet.size());

    for (String s : OldPathFilesSet) {
      if (!s.equals("context.xml")) {
        assertThat(NewPathFilesSet).contains(s);
        NewPathFilesSet.remove(s);
      }
      assertThat(backupPathFilesSet).contains(s);
      backupPathFilesSet.remove(s);

    }
    assertThat(NewPathFilesSet).hasSize(0);
    assertThat(backupPathFilesSet).hasSize(0);
  }

  /**
   * Tests if the context.xml has been created in the correct location and that it was correctly created as a v3.0
   * schema
   *
   * @throws Exception
   */
  @Test
  public void testTemplateSetUpgradeContextSplit() throws Exception {

    Path contextLocation = this.templateLocation.resolve(ConfigurationConstants.COBIGEN_TEMPLATES)
        .resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
    ContextConfigurationUpgrader upgrader = new ContextConfigurationUpgrader();
    upgrader.upgradeConfigurationToLatestVersion(contextLocation, BackupPolicy.ENFORCE_BACKUP);

    Path newTemplatesPath = this.templateLocation.getParent().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    newTemplatesPath = newTemplatesPath.resolve(ConfigurationConstants.ADAPTED_FOLDER);

    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    try (InputStream schemaStream = getClass().getResourceAsStream("/schema/v3.0/contextConfiguration.xsd")) {
      StreamSource schemaSourceStream = new StreamSource(schemaStream);
      Schema schema = schemaFactory.newSchema(schemaSourceStream);
      Validator validator = schema.newValidator();

      for (String s : newTemplatesPath.toFile().list()) {
        Path newContextPath = newTemplatesPath.resolve(s + "/" + "src/main/resources");
        newContextPath = newContextPath.resolve("context.xml");
        assertThat(newContextPath.toFile()).exists();
        StreamSource contextStream = new StreamSource(newContextPath.toFile());
        try {
          validator.validate(contextStream);
        } catch (SAXException e) {
          fail("Exception show that validator has found an fault");
          contextStream.getInputStream().close();
          schemaStream.close();
        }

      }
      schemaStream.close();
    }

  }

}
