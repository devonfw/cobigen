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
 * TODO
 *
 */
public class TemplatesInstallationTest extends AbstractApiTest {

  /**
   * Tests that sources get overwritten if merge strategy override is configured.
   *
   * @throws Exception test fails.
   */
  @Test
  public void testInstallTemplatesAtStartupOLD() throws Exception {

    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {
      File templates = this.tmpFolder.newFolder("TemplateSetsInstalledTest", ConfigurationConstants.TEMPLATES_FOLDER);
      File cobigenDir = templates.toPath().resolve(ConfigurationConstants.COBIGEN_TEMPLATES).toFile();
      Files.createDirectories(cobigenDir.toPath());
      File target = new File(folder, ".cobigen");
      BufferedWriter writer = new BufferedWriter(new FileWriter(target));
      writer.write("template-sets.installed=com.devonfw.cobigen:templates-devon4j:2021.12.005");
      writer.close();
      CobiGen cobigen = CobiGenFactory.create(templates.toURI(), true);
      assertEquals(0, cobigenDir.listFiles().length);
    });
  }

  /**
   * Tests that sources get overwritten if merge strategy override is configured.
   *
   * @throws Exception test fails.
   */
  @Test
  public void testInstallTemplatesAtStartupNEW() throws Exception {

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
   * Tests that sources get overwritten if merge strategy override is configured.
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
   * Tests that sources get overwritten if merge strategy override is configured.
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
   * Tests that sources get overwritten if merge strategy override is configured.
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
