package com.devonfw.cobigen.systemtest;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

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
      assertEquals(2, cobigenDir.listFiles().length);
      for (File f : cobigenDir.listFiles()) {
        assertTrue(f.getName().contains("templates-devon4j.2021.12.005"));
      }
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
        assertTrue(f.getName().contains("templates-devon4j.2021.12.006"));
      }
    });

  }

  /**
   * Tests that sources get overwritten if merge strategy override is configured.
   *
   * @throws Exception test fails.
   */
  @Test
  public void testNOInstallAtStartup() throws Exception {

    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {
      CobiGen cobigen = CobiGenFactory.create();
      Path templateSets = folder.toPath().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      assertEquals(templateSets.toFile().listFiles().length, 0);
    });
  }

}
