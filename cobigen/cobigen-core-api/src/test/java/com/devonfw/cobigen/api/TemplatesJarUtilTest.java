package com.devonfw.cobigen.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;

public class TemplatesJarUtilTest {
  /**
   * Temp folder for test execution
   */
  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  /** Testdata root path */
  private static final String testdataRoot = "src/test/resources/testdata/unittest/MavenUtilTest";

  /**
   * Tests to check if a correct cache will be validated right
   *
   * @throws Exception
   */
  @Test
  public void testDownloadFile() throws Exception {

    Path tempFolder = this.temp.newFolder(ConfigurationConstants.TEMPLATE_SETS_FOLDER).toPath();
    Path test = TemplatesJarUtil.downloadFile(
        "https://repo1.maven.org/maven2/com/devonfw/cobigen/templates-devon4j/2021.12.006/templates-devon4j-2021.12.006.pom",
        tempFolder);
    assertThat(test).exists();

  }
}
