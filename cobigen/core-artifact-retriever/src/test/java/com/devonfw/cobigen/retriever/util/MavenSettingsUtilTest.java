package com.devonfw.cobigen.retriever.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.retriever.to.model.MavenSettingsModel;
import com.devonfw.cobigen.retriever.util.MavenSettingsUtil;

/**
 * Test class for MavenSettingsUtil
 *
 */
public class MavenSettingsUtilTest {

  /** Test data root path */
  private static final String testdataRoot = "src/test/resources/testdata/unittest/MavenSettingsUtilTest";

  /** Test model */
  private static MavenSettingsModel model;

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
    model = MavenSettingsUtil.generateMavenSettingsModel(content);
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
   * Tests, whether the the server elements of maven's settings.xml are mapped correctly to a java class
   */
  @Test
  public void testGenerateMavenSettingsModelServer() {

    String testId = model.getServers().getServerList().get(0).getId();
    String testUsername = model.getServers().getServerList().get(0).getUsername();
    String testPassword = model.getServers().getServerList().get(0).getPassword();
    String privateKey = model.getServers().getServerList().get(0).getPrivateKey();
    String passphrase = model.getServers().getServerList().get(0).getPassphrase();

    assertThat(testId).isEqualTo("repository");

    assertThat(testUsername).isEqualTo("testUsername");

    assertThat(testPassword).isEqualTo("testPassword");

    assertThat(privateKey).isEqualTo("testKey");

    assertThat(passphrase).isEqualTo("testPassphrase");
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

    assertThat(id).isEqualTo("example-proxy");

    assertThat(active).isEqualTo("true");

    assertThat(protocol).isEqualTo("http");

    assertThat(host).isEqualTo("proxy.example.com");

    assertThat(port).isEqualTo("8080");

    assertThat(nonProxyHosts).isEqualTo("www.google.com|*.example.com");
  }

  // @Test
  // public void testGetRepositoriesFromMavenSettings() {
  //
  // // Waiting for Eduards solution
  // String test = MavenUtil.determineMavenSettings();
  // }

}
