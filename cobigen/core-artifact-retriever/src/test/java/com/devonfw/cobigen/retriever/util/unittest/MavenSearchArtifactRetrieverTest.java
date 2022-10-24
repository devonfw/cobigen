package com.devonfw.cobigen.retriever.util.unittest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import com.devonfw.cobigen.retriever.mavensearch.util.MavenSearchArtifactRetriever;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * Test class for {@link MavenSearchArtifactRetriever}
 */
public class MavenSearchArtifactRetrieverTest {

  /**
   * WireMock rule to initialize
   */
  @Rule
  public WireMockRule wireMockRule = new WireMockRule(options().disableRequestJournal());

  /** Testdata root path */
  private static final String testdataRoot = "src/test/resources/testdata/unittest/MavenSearchTest";

  /**
   * Tests if a request to maven search REST API returns a list of download URLs
   *
   * @throws IOException if an error occurred while reading the test json file
   */
  @Test
  public void testMavenSearchRequestGetsValidDownloadLinks() throws IOException {

    // given
    List<URL> downloadList;

    this.wireMockRule.stubFor(get(urlMatching("/solrsearch/select.*")).willReturn(aResponse().withStatus(200)
        .withBody(Files.readAllBytes(Paths.get(testdataRoot).resolve("mavenJsonTest.json")))));

    this.wireMockRule
        .stubFor(get(urlMatching("/artifactory/api/search/gavc.*")).willReturn(aResponse().withStatus(404)));

    this.wireMockRule
        .stubFor(get(urlMatching("/service/local/lucene/search/.*")).willReturn(aResponse().withStatus(404)));

    this.wireMockRule.stubFor(get(urlMatching("/service/rest/v1/search.*")).willReturn(aResponse().withStatus(404)));

    // when
    downloadList = MavenSearchArtifactRetriever.retrieveMavenArtifactDownloadUrls("http://localhost:8080", null, null,
        "", 0, null, null, "com.devonfw.cobigen");

    // then
    assertThat(downloadList).contains(new URL(
        "https://repo1.maven.org/maven2/com/devonfw/cobigen/templates/crud-java-server-app/2021.08.001/crud-java-server-app-2021.08.001-template-set.xml"));
  }

  /**
   * Tests if a request to nexus2 search REST API returns a list of download URLs
   *
   * @throws IOException if an error occurred while reading the test json file
   */
  @Test
  public void testNexus2SearchRequestGetsValidDownloadLinks() throws IOException {

    // given
    List<URL> downloadList;

    this.wireMockRule.stubFor(get(urlMatching("/solrsearch/select.*")).willReturn(aResponse().withStatus(404)));

    this.wireMockRule
        .stubFor(get(urlMatching("/artifactory/api/search/gavc.*")).willReturn(aResponse().withStatus(404)));

    this.wireMockRule.stubFor(get(urlMatching("/service/local/lucene/search.*")).willReturn(aResponse().withStatus(200)
        .withBody(Files.readAllBytes(Paths.get(testdataRoot).resolve("nexus2JsonTest.json")))));

    this.wireMockRule.stubFor(get(urlMatching("/service/rest/v1/search.*")).willReturn(aResponse().withStatus(404)));

    // when
    downloadList = MavenSearchArtifactRetriever.retrieveMavenArtifactDownloadUrls("http://localhost:8080", null, null,
        "", 0, null, null, "com.devonfw.cobigen");

    // then
    assertThat(downloadList).contains(new URL(
        "https://s01.oss.sonatype.org/service/local/repositories/releases/content/com/devonfw/cobigen/templates/crud-java-server-app/2021.08.001/crud-java-server-app-2021.08.001-template-set.xml"));
  }

  /**
   * Tests if a request to nexus3 search REST API returns a list of download URLs
   *
   * @throws IOException if an error occurred while reading the test json file
   */
  @Test
  public void testNexus3SearchRequestGetsValidDownloadLinks() throws IOException {

    // given
    List<URL> downloadList;

    this.wireMockRule.stubFor(get(urlMatching("/solrsearch/select.*")).willReturn(aResponse().withStatus(404)));

    this.wireMockRule
        .stubFor(get(urlMatching("/artifactory/api/search/gavc.*")).willReturn(aResponse().withStatus(404)));

    this.wireMockRule
        .stubFor(get(urlMatching("/service/local/lucene/search/.*")).willReturn(aResponse().withStatus(404)));

    this.wireMockRule.stubFor(get(urlMatching("/service/rest/v1/search.*")).willReturn(aResponse().withStatus(200)
        .withBody(Files.readAllBytes(Paths.get(testdataRoot).resolve("nexus3JsonTest.json")))));

    // when
    downloadList = MavenSearchArtifactRetriever.retrieveMavenArtifactDownloadUrls("http://localhost:8080", null, null,
        "", 0, null, null, "com.devonfw.cobigen");

    // then
    assertThat(downloadList).contains(new URL(
        "http://localhost:8081/repository/com/devonfw/cobigen/templates/crud-java-server-app/2021.08.001/crud-java-server-app-2021.08.001-template-set.xml"));
  }

  /**
   * Tests if a request to jfrog search REST API returns a list of download URLs
   *
   * @throws IOException if an error occurred while reading the test json file
   */
  @Test
  public void testJfrogSearchRequestGetsValidDownloadLinks() throws IOException {

    // given
    List<URL> downloadList;

    this.wireMockRule.stubFor(get(urlMatching("/solrsearch/select.*")).willReturn(aResponse().withStatus(404)));

    this.wireMockRule.stubFor(get(urlMatching("/artifactory/api/search/gavc.*")).willReturn(aResponse().withStatus(200)
        .withBody(Files.readAllBytes(Paths.get(testdataRoot).resolve("jfrogJsonTest.json")))));

    this.wireMockRule
        .stubFor(get(urlMatching("/service/local/lucene/search/.*")).willReturn(aResponse().withStatus(404)));

    this.wireMockRule.stubFor(get(urlMatching("/service/rest/v1/search.*")).willReturn(aResponse().withStatus(404)));

    // when
    downloadList = MavenSearchArtifactRetriever.retrieveMavenArtifactDownloadUrls("http://localhost:8080", null, null,
        "", 0, null, null, "com.devonfw.cobigen");

    // then
    assertThat(downloadList).contains(new URL(
        "http://localhost:8081/artifactory/api/storage/libs-release-local/com/devonfw/cobigen/templates/crud-java-server-app/2021.08.001/crud-java-server-app-2021.08.001-template-set.xml"));
  }
}
