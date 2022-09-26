package com.devonfw.cobigen.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.devonfw.cobigen.api.to.model.MavenSettingsModel;
import com.devonfw.cobigen.api.util.MavenSettingsUtil;

/**
 * Test class for MavenSettingsUtil
 *
 */
public class MavenSettingsUtilTest {

  /** Test data root path */
  private static final String testdataRoot = "src/test/resources/testdata/unittest/MavenSettingsUtilTest";

  /**
   * Tests, whether the path of maven's settings.xml is determined correctly
   */
  // @Test
  // public void testDetermineMavenSettingsPath() {
  //
  // Path result = MavenSettingsUtil.determineMavenSettingsPath();
  // assertThat(result).toString().contains("\\conf\\.m2\\settings.xml");
  // }

  /**
   * Tests, whether the the repository elements of maven's settings.xml are mapped correctly to a java class
   */
  @Test
  public void testGenerateMavenSettingsModelRepository() {

    Path path = Paths.get(testdataRoot).resolve("settings.xml");

    MavenSettingsModel result = MavenSettingsUtil.generateMavenSettingsModel();
    String testId = result.getProfiles().getProfileList().get(0).getRepositories().getRepository().get(0).getId();
    String testName = result.getProfiles().getProfileList().get(0).getRepositories().getRepository().get(0).getName();
    String testUrl = result.getProfiles().getProfileList().get(0).getRepositories().getRepository().get(0).getUrl()
        .toString();

    assertThat(testId).isEqualTo("123");

    assertThat(testName).isEqualTo("devonfw SNAPSHOT releases");

    assertThat(testUrl).isEqualTo("https://s01.oss.sonatype.org/content/repositories/snapshots/");
  }

  /**
   * Tests, whether the the server elements of maven's settings.xml are mapped correctly to a java class
   */
  @Test
  public void testGenerateMavenSettingsModelServer() {

    Path path = Paths.get(testdataRoot).resolve("settings.xml");

    MavenSettingsModel result = MavenSettingsUtil.generateMavenSettingsModel();
    String testId = result.getServers().getServerList().get(0).getId();
    String testUsername = result.getServers().getServerList().get(0).getUsername();
    String testPassword = result.getServers().getServerList().get(0).getPassword();

    assertThat(testId).isEqualTo("repository");

    assertThat(testUsername).isEqualTo("testUsername");

    assertThat(testPassword).isEqualTo("testPassword");
  }

}
