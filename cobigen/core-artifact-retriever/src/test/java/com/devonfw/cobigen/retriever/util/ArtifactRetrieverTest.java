package com.devonfw.cobigen.retriever.util;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.retriever.ArtifactRetriever;
import com.devonfw.cobigen.retriever.settings.util.MavenSettingsUtil;
import com.devonfw.cobigen.retriever.settings.util.to.model.MavenSettingsModel;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class ArtifactRetrieverTest {

  /**
   * Test resource location
   */
  private final static String testdataRoot = "src/test/resources/testdata/unittest/ArtifactRetrieverTest";

  /**
   * {@link MavenSettingsModel} without a proxy
   */
  private static MavenSettingsModel modelNonProxy;

  /**
   * {@link MavenSettingsModel} with a proxy
   */
  private static MavenSettingsModel modelProxy;

  /**
   * Maven settings string without a proxy
   */
  private static String mavenSettingsNonProxy;

  /**
   * Maven settings string without a proxy
   */
  private static String mavenSettingsProxy;

  /**
   * WireMock rule to initialize
   */
  @Rule
  public WireMockRule wireMockRule = new WireMockRule(options().disableRequestJournal());

  /**
   * Used to initialize data needed for the tests
   */
  @BeforeClass
  public static void setUpClass() {

    try {
      mavenSettingsNonProxy = Files.readString(Paths.get(testdataRoot).resolve("settingsNonProxy.xml"));
      mavenSettingsProxy = Files.readString(Paths.get(testdataRoot).resolve("settingsProxy.xml"));
    } catch (IOException e) {
      throw new CobiGenRuntimeException("Unable to read test settings.xml", e);
    }
    modelNonProxy = MavenSettingsUtil.generateMavenSettingsModel(mavenSettingsNonProxy);
    modelProxy = MavenSettingsUtil.generateMavenSettingsModel(mavenSettingsProxy);
  }

  /**
   * Tests a retrieval of download URLs using using a settings.xml and basic authentication
   *
   * @throws IOException if test resource could not be read
   */
  @Test
  public void testRetrieveTemplateSetXmlDownloadLinksWithBasicAuthentication() throws IOException {

    this.wireMockRule.stubFor(get(urlMatching("/artifactory/api/search/gavc.*"))
        .withBasicAuth("testUsername", "testPassword").willReturn(aResponse().withStatus(200)
            .withBody(Files.readAllBytes(Paths.get(testdataRoot).resolve("jfrogJsonTest.json")))));

    List<String> groupIds = Arrays.asList("com.devonfw.cobigen.templates");

    List<URL> downloadUrls = ArtifactRetriever.retrieveTemplateSetXmlDownloadLinks(groupIds, mavenSettingsNonProxy);
    assertThat(downloadUrls).contains(new URL(
        "http://localhost:8080/artifactory/api/storage/libs-release-local/com/devonfw/cobigen/templates/crud-java-server-app/2021.08.001/crud-java-server-app-2021.08.001-template-set.xml"));
  }

  /**
   * Tests a retrieval of download URLs using using a settings.xml without a proxy
   *
   * @throws IOException if test resource could not be read
   */
  @Test
  public void testRetrieveTemplateSetXmlDownloadLinksWithoutProxy() throws IOException {

    this.wireMockRule.stubFor(get(urlMatching("/artifactory/api/search/gavc.*")).willReturn(aResponse().withStatus(200)
        .withBody(Files.readAllBytes(Paths.get(testdataRoot).resolve("jfrogJsonTest.json")))));

    List<String> groupIds = Arrays.asList("com.devonfw.cobigen.templates");

    List<URL> downloadUrls = ArtifactRetriever.retrieveTemplateSetXmlDownloadLinks(groupIds, mavenSettingsNonProxy);
    assertThat(downloadUrls).contains(new URL(
        "http://localhost:8080/artifactory/api/storage/libs-release-local/com/devonfw/cobigen/templates/crud-java-server-app/2021.08.001/crud-java-server-app-2021.08.001-template-set.xml"));
  }

  /**
   * Tests a retrieval of download URLs using using a settings.xml with a proxy
   *
   * @throws IOException if test resource could not be read
   */
  @Test
  public void testRetrieveTemplateSetXmlDownloadLinksWithProxy() throws IOException {

    this.wireMockRule.stubFor(get(urlMatching("/artifactory/api/search/gavc.*")).willReturn(aResponse().withStatus(200)
        .withBody(Files.readAllBytes(Paths.get(testdataRoot).resolve("jfrogJsonTest.json")))));

    List<String> groupIds = Arrays.asList("com.devonfw.cobigen.templates");

    List<URL> downloadUrls = ArtifactRetriever.retrieveTemplateSetXmlDownloadLinks(groupIds, mavenSettingsProxy);
    assertThat(downloadUrls).contains(new URL(
        "http://localhost:8080/artifactory/api/storage/libs-release-local/com/devonfw/cobigen/templates/crud-java-server-app/2021.08.001/crud-java-server-app-2021.08.001-template-set.xml"));
  }

}
