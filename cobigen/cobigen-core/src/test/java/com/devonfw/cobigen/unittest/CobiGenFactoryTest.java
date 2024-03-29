package com.devonfw.cobigen.unittest;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.CobiGenFactory;

import junit.framework.TestCase;

/**
 * This {@link TestCase} tests the {@link CobiGenFactory}
 */
public class CobiGenFactoryTest {

  /**
   * JUnit Rule to temporarily create files and folders, which will be automatically removed after test execution
   */
  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  /**
   * Tests whether a valid configuration can be read from template-sets/adapted folder does not throw a
   * {@link InvalidConfigurationException}
   *
   * @throws Exception test fails
   */
  @Test
  public void testTemplateSetDownloadedLoadedFromNewConfiguration() throws Exception {

    Path downloadedPath = Paths.get(
        "src/test/resources/testdata/unittest/config/reader/TemplateSetConfigurationReaderTest/valid_template_sets_downloaded/template-sets");

    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    FileUtils.copyDirectory(downloadedPath.getParent().toFile(), folder);
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {
      CobiGenFactory.create(folder.toPath().resolve("template-sets").toUri(), false);
    });
  }

  /**
   * Tests whether a valid configuration can be read from template-sets/adapted folder does not throw a
   * {@link InvalidConfigurationException}
   *
   * @throws Exception test fails
   */
  @Test
  public void testTemplateSetAdaptedLoadedFromNewConfiguration() throws Exception {

    Path adaptedPath = Paths.get(
        "src/test/resources/testdata/unittest/config/reader/TemplateSetConfigurationReaderTest/valid_template_sets_adapted/template-sets");
    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    FileUtils.copyDirectory(adaptedPath.getParent().toFile(), folder);
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {
      CobiGenFactory.create(folder.toPath().resolve("template-sets").toUri(), false);
    });
  }
}