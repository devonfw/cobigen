package com.devonfw.cobigen.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

  /** Testdata root path */
  private static final String testdataRoot = "src/test/resources/testdata/unittest/MavenSettingsUtilTest";

  @Test
  public void testDetermineMavenSettingsPath() {

    Path result = MavenSettingsUtil.determineMavenSettingsPath();
    assertTrue(result.toString().contains("\\conf\\.m2\\settings.xml"));
  }

  @Test
  public void testGenerateMavenSettingsModelRepository() {

    Path path = Paths.get(testdataRoot).resolve("settings.xml");

    MavenSettingsModel result = MavenSettingsUtil.generateMavenSettingsModel(path);
    String testId = result.getProfiles().getProfileList().get(0).getRepositories().getRepository().get(0).getId();
    String testName = result.getProfiles().getProfileList().get(0).getRepositories().getRepository().get(0).getName();
    String testUrl = result.getProfiles().getProfileList().get(0).getRepositories().getRepository().get(0).getUrl()
        .toString();

    assertEquals("123", testId);

    assertEquals("devonfw SNAPSHOT releases", testName);

    assertEquals("https://s01.oss.sonatype.org/content/repositories/snapshots/", testUrl);
  }

  @Test
  public void testGenerateMavenSettingsModelServer() {

    Path path = Paths.get(testdataRoot).resolve("settings.xml");

    MavenSettingsModel result = MavenSettingsUtil.generateMavenSettingsModel(path);
    String testId = result.getServers().getServerList().get(0).getId();
    String testUsername = result.getServers().getServerList().get(0).getUsername();
    String testPassword = result.getServers().getServerList().get(0).getPassword();

    assertEquals("repository", testId);

    assertEquals("testUsername", testUsername);

    assertEquals("testPassword", testPassword);
  }

}
