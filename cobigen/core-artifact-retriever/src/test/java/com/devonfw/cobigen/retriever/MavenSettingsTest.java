package com.devonfw.cobigen.retriever;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sonatype.plexus.components.sec.dispatcher.model.SettingsSecurity;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.retriever.settings.MavenSettings;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsModel;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsProxyModel;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsRepositoryModel;

/**
 * Test class for MavenSettings
 *
 */
public class MavenSettingsTest {

  /** Test data root path */
  private static final String testdataRoot = "src/test/resources/testdata/unittest/MavenSettingsTest";

  /** Test model */
  private static MavenSettingsModel model;

  /** Content of the test setting.xmls */
  private static String mavenSettings;

  /**
   * Used to initialize data needed for the tests
   */
  @BeforeClass
  public static void setUpClass() {

    String content;
    try {
      content = Files.readString(Paths.get(testdataRoot).resolve("settings.xml"));
    } catch (IOException e) {
      throw new CobiGenRuntimeException("Unable to read test settings.xml", e);
    }
    model = MavenSettings.generateMavenSettingsModel(content);
    mavenSettings = content;
  }

  /**
   * Tests, whether the the repository elements of maven's settings.xml are mapped correctly to a java class
   */
  @Test
  public void testGenerateMavenSettingsModelRepository() {

    String testId = model.getProfiles().getProfileList().get(0).getRepositories().getRepositoryList().get(0).getId();
    String testName = model.getProfiles().getProfileList().get(0).getRepositories().getRepositoryList().get(0)
        .getName();
    String testUrl = model.getProfiles().getProfileList().get(0).getRepositories().getRepositoryList().get(0).getUrl()
        .toString();

    assertThat(testId).isEqualTo("123");

    assertThat(testName).isEqualTo("devonfw SNAPSHOT releases");

    assertThat(testUrl).isEqualTo("https://s01.oss.sonatype.org/content/repositories/snapshots/");
  }

  /**
   * Tests, whether the the activation element of a profile is mapped correctly to a java class
   */
  @Test
  public void testGenerateMavenSettingsModelProfile() {

    String activationStatus = model.getProfiles().getProfileList().get(0).getActivation().getActiveByDefault();

    assertThat(activationStatus).isEqualTo("true");
  }

  /**
   * Tests, whether the the activeProfiles element is mapped correctly to a java class
   */
  @Test
  public void testGenerateMavenSettingsModelActiveProfiles() {

    List<String> activeProfiles = model.getActiveProfiles().getActiveProfilesList();

    assertThat(activeProfiles.get(0)).isEqualTo("profile-1");
    assertThat(activeProfiles.get(1)).isEqualTo("profile-3");
  }

  /**
   * Tests, whether the the server elements of maven's settings.xml are mapped correctly to a java class
   */
  @Test
  public void testGenerateMavenSettingsModelServer() {

    String testId = model.getServers().getServerList().get(0).getId();
    String testUsername = model.getServers().getServerList().get(0).getUsername();
    String testPassword = model.getServers().getServerList().get(0).getPassword();

    assertThat(testId).isEqualTo("repository");

    assertThat(testUsername).isEqualTo("testUsername");

    assertThat(testPassword).isEqualTo("testPassword");
  }

  /**
   * Tests, whether the the server elements of maven's settings.xml are mapped correctly to a java class
   */
  @Test
  public void testGenerateMavenSettingsModelMirror() {

    String mirrorOf = model.getMirrors().getMirrorList().get(0).getMirrorOf();
    String url = model.getMirrors().getMirrorList().get(0).getUrl();
    String id = model.getMirrors().getMirrorList().get(0).getId();
    String blocked = model.getMirrors().getMirrorList().get(0).getBlocked();

    assertThat(mirrorOf).isEqualTo("external:http:*");

    assertThat(url).isEqualTo("http://0.0.0.0/");

    assertThat(id).isEqualTo("maven-default-http-blocker");

    assertThat(blocked).isEqualTo("true");
  }

  /**
   * Tests, whether the the proxy elements of maven's settings.xml are mapped correctly to a java class
   */
  @Test
  public void testGenerateMavenSettingsModelProxy() {

    String id = model.getProxies().getProxyList().get(0).getId();
    String active = model.getProxies().getProxyList().get(0).getActive();
    String protocol = model.getProxies().getProxyList().get(0).getProtocol();
    String host = model.getProxies().getProxyList().get(0).getHost();
    String port = model.getProxies().getProxyList().get(0).getPort();
    String nonProxyHosts = model.getProxies().getProxyList().get(0).getNonProxyHosts();

    assertThat(id).isEqualTo("example-proxy2");

    assertThat(active).isEqualTo("false");

    assertThat(protocol).isEqualTo("https");

    assertThat(host).isEqualTo("proxy.example.com");

    assertThat(port).isEqualTo("8080");

    assertThat(nonProxyHosts).isEqualTo("www.google.com|*.example.com");
  }

  /**
   * Tests, whether the only active proxy is returned
   */
  @Test
  public void testGetActiveProxy() {

    MavenSettingsProxyModel result = MavenSettings.getActiveProxy(model);

    assertThat(result.getId()).isEqualTo("example-proxy");
  }

  /**
   * Tests, whether only repositories of active profiles are returned
   */
  @Test
  public void testGetRepositoriesFromMavenSettings() {

    List<MavenSettingsRepositoryModel> result = MavenSettings.getRepositoriesFromMavenSettings(mavenSettings);
    assertThat(result.size()).isEqualTo(3);
    assertThat(result.get(0).getId()).isEqualTo("123");
    assertThat(result.get(1).getId()).isEqualTo("repository");
    assertThat(result.get(2).getId()).isEqualTo("repository1");
  }

  /**
   * Tests a simple read of the master password inside the settings security file
   *
   * @throws Exception
   */
  @Test
  public void testReadSettingsSecurityMasterPasswordFromSecuritiesFile() throws Exception {

    String securitiesFile = Paths.get(testdataRoot).resolve("settings-security.xml").toAbsolutePath().toString();
    SettingsSecurity settingsSecurity = MavenSettings.readSettingsSecurity(securitiesFile);
    assertThat(settingsSecurity.getMaster()).isEqualTo("{/1SNYthinCEHRwQkYnzyoqKp3rbjGyZg/LbW7tOFTEg=}");
  }

  /**
   * Tests the decryption process of the settings security master password
   *
   * @throws Exception
   */
  @Test
  public void testReadSettingsSecurityMasterPassword() throws Exception {

    String decodedMasterPassword = MavenSettings
        .decryptMasterPassword("{/1SNYthinCEHRwQkYnzyoqKp3rbjGyZg/LbW7tOFTEg=}");
    assertThat(decodedMasterPassword).isEqualTo("testpassword");
  }

  /**
   * Tests the decryption of a password using the decrypted master password as key
   *
   * @throws Exception
   */
  @Test
  public void testReadSettingsPassword() throws Exception {

    String decodedMasterPassword = MavenSettings
        .decryptMasterPassword("{/1SNYthinCEHRwQkYnzyoqKp3rbjGyZg/LbW7tOFTEg=}");
    String decodedPassword = MavenSettings.decryptPassword("{jrdiSuRPexMHvBd66Hsiy3nR1VpCc8TDREVCj+6AYrU=}",
        decodedMasterPassword);
    assertThat(decodedPassword).isEqualTo("thisisapassword");
  }

  // @Test
  // public void testGetSettingsFromMavenSettings() {
  //
  // // Waiting for Eduards solution
  // String test = MavenUtil.determineMavenSettings();
  // }

}
