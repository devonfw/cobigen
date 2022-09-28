package com.devonfw.cobigen.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.to.model.MavenSettingsModel;
import com.devonfw.cobigen.api.util.MavenSettingsUtil;
import com.devonfw.cobigen.api.util.MavenUtil;

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

    String content;
    try {
      content = Files.readString(Paths.get(testdataRoot).resolve("settings.xml"));
    } catch (IOException e) {
      throw new CobiGenRuntimeException("Unable to read test settings.xml", e);
    }

    MavenSettingsModel result = MavenSettingsUtil.generateMavenSettingsModel(content);
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

    String content;
    try {
      content = Files.readString(Paths.get(testdataRoot).resolve("settings.xml"));
    } catch (IOException e) {
      throw new CobiGenRuntimeException("Unable to read test settings.xml", e);
    }

    MavenSettingsModel result = MavenSettingsUtil.generateMavenSettingsModel(content);
    String testId = result.getServers().getServerList().get(0).getId();
    String testUsername = result.getServers().getServerList().get(0).getUsername();
    String testPassword = result.getServers().getServerList().get(0).getPassword();

    assertThat(testId).isEqualTo("repository");

    assertThat(testUsername).isEqualTo("testUsername");

    assertThat(testPassword).isEqualTo("testPassword");
  }

  /**
   * Tests, whether the the server elements of maven's settings.xml are mapped correctly to a java class
   */
  @Test
  public void testGenerateMavenSettingsModelMirror() {

    String content;
    try {
      content = Files.readString(Paths.get(testdataRoot).resolve("settings.xml"));
    } catch (IOException e) {
      throw new CobiGenRuntimeException("Unable to read test settings.xml", e);
    }

    MavenSettingsModel result = MavenSettingsUtil.generateMavenSettingsModel(content);
    String mirrorOf = result.getMirrors().getMirrorList().get(0).getMirrorOf();
    String url = result.getMirrors().getMirrorList().get(0).getUrl();
    String id = result.getMirrors().getMirrorList().get(0).getId();
    String blocked = result.getMirrors().getMirrorList().get(0).getBlocked();

    assertThat(mirrorOf).isEqualTo("external:http:*");

    assertThat(url).isEqualTo("http://0.0.0.0/");

    assertThat(id).isEqualTo("maven-default-http-blocker");

    assertThat(blocked).isEqualTo("true");
  }

  @Test
  public void testDetermineMavenSettings() {

    String test = MavenUtil.determineMavenSettings();
    System.out.println(test);
  }

}
