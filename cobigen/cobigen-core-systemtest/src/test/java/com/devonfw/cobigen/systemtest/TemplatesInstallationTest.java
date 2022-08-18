package com.devonfw.cobigen.systemtest;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

import org.hamcrest.Matchers;
import org.junit.Test;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.systemtest.common.AbstractApiTest;

/**
 * Test suite for template installation with the cobigen startup
 *
 */
public class TemplatesInstallationTest extends AbstractApiTest {

  /**
   * Tests if the templates specified in the .cobigen file won´t be loaded with the monolithic structure.
   *
   * @throws Exception test fails.
   */
  @Test
  public void testInstallTemplatesAtStartupMonolithicStructure() throws Exception {

    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {
      File templates = this.tmpFolder.newFolder("TemplateSetsInstalledTest", ConfigurationConstants.TEMPLATES_FOLDER);
      File cobigenDir = templates.toPath().resolve(ConfigurationConstants.COBIGEN_TEMPLATES).toFile();
      Files.createDirectories(cobigenDir.toPath());
      File target = new File(folder, ".cobigen");
      BufferedWriter writer = new BufferedWriter(new FileWriter(target));
      writer.write("template-sets.installed=com.devonfw.cobigen:templates-devon4j:2021.12.005");
      writer.close();
      CobiGenFactory.create(templates.toURI(), true);
      assertEquals(0, cobigenDir.listFiles().length);
    });
  }

  /**
   * Tests if the templates specified in the .cobigen file will be loaded at startup with the template-set structure and
   * an existing downloaded folder.
   *
   * @throws Exception test fails.
   */
  @Test
  public void testInstallTemplatesAtStartup() throws Exception {

    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {
      File templateSets = this.tmpFolder.newFolder("TemplateSetsInstalledTest",
          ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      File downloaded = this.tmpFolder.newFolder("TemplateSetsInstalledTest",
          ConfigurationConstants.TEMPLATE_SETS_FOLDER, "downloaded");
      File target = new File(folder, ".cobigen");
      BufferedWriter writer = new BufferedWriter(new FileWriter(target));
      writer.write("template-sets.installed=com.devonfw.cobigen:templates-devon4j:2021.12.006");
      writer.close();
      CobiGen cobigen = CobiGenFactory.create(templateSets.toURI());
      assertEquals(2, downloaded.listFiles().length);
      for (File f : downloaded.listFiles()) {
        assertThat(f.getName(), Matchers.either(Matchers.is("templates-devon4j-2021.12.006.jar"))
            .or(Matchers.is("templates-devon4j-2021.12.006-sources.jar")));
      }
    });

  }

  /**
   * Tests if the templates specified in the .cobigen file won´t be loaded when a adapted folder already exists.
   *
   * @throws Exception test fails.
   */
  @Test
  public void testInstallTemplatesAtStartupAdapted() throws Exception {

    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {
      File templateSets = this.tmpFolder.newFolder("TemplateSetsInstalledTest",
          ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      File downloaded = this.tmpFolder.newFolder("TemplateSetsInstalledTest",
          ConfigurationConstants.TEMPLATE_SETS_FOLDER, "adapted");
      File target = new File(folder, ".cobigen");
      BufferedWriter writer = new BufferedWriter(new FileWriter(target));
      writer.write("template-sets.installed=com.devonfw.cobigen:templates-devon4j:2021.12.006");
      writer.close();
      CobiGen cobigen = CobiGenFactory.create(templateSets.toURI());
      assertEquals(0, downloaded.listFiles().length);
    });

  }

  /**
   * Tests if the templates specified in the .cobigen file will be loaded at startup with the template-set structure
   * without an existing downloaded folder.
   *
   * @throws Exception test fails.
   */
  @Test
  public void testInstallTemplatesAtStartupWithoutDownloaded() throws Exception {

    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {
      File templateSets = this.tmpFolder.newFolder("TemplateSetsInstalledTest",
          ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      File downloaded = templateSets.toPath().resolve(ConfigurationConstants.DOWNLOADED_FOLDER).toFile();
      File target = new File(folder, ".cobigen");
      BufferedWriter writer = new BufferedWriter(new FileWriter(target));
      writer.write("template-sets.installed=com.devonfw.cobigen:templates-devon4j:2021.12.006");
      writer.close();
      CobiGen cobigen = CobiGenFactory.create(templateSets.toURI());
      assertEquals(2, downloaded.listFiles().length);
      for (File f : downloaded.listFiles()) {
        assertThat(f.getName(), Matchers.either(Matchers.is("templates-devon4j-2021.12.006.jar"))
            .or(Matchers.is("templates-devon4j-2021.12.006-sources.jar")));
      }
    });
  }

  /**
   * Tests if the templates specified in the .cobigen file will checked for the correct format defined in the
   * documentation.
   *
   * @throws Exception test fails.
   */
  @Test
  public void testInstallTemplatesAtStartupWitWrongCoordinates() throws Exception {

    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {
      File templateSets = this.tmpFolder.newFolder("TemplateSetsInstalledTest",
          ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      File downloaded = this.tmpFolder.newFolder("TemplateSetsInstalledTest",
          ConfigurationConstants.TEMPLATE_SETS_FOLDER, "downloaded");
      File target = new File(folder, ".cobigen");
      BufferedWriter writer = new BufferedWriter(new FileWriter(target));
      writer.write("template-sets.installed=com.com.com:com-com:com.com");
      writer.close();
      CobiGen cobigen = CobiGenFactory.create(templateSets.toURI());
      assertEquals(0, downloaded.listFiles().length);
    });
  }
}
