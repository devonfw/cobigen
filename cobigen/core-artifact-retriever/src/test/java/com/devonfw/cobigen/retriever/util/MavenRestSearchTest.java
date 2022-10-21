package com.devonfw.cobigen.retriever.util;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import com.devonfw.cobigen.retriever.mavensearch.constants.MavenSearchRepositoryType;
import com.devonfw.cobigen.retriever.mavensearch.exception.RestSearchResponseException;
import com.devonfw.cobigen.retriever.mavensearch.util.MavenSearchArtifactRetriever;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.AbstractSearchResponse;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.SearchResponseFactory;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.ServerCredentials;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.jfrog.JfrogSearchResponse;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.maven.MavenSearchResponse;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.nexus2.Nexus2SearchResponse;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.nexus3.Nexus3SearchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * Test class for maven utilities
 */
public class MavenRestSearchTest {

  /**
   * WireMock rule to initialize
   */
  @Rule
  public WireMockRule wireMockRule = new WireMockRule(options().disableRequestJournal());

  /** Testdata root path */
  private static final String testdataRoot = "src/test/resources/testdata/unittest/MavenSearchTest";

  /**
   * Tests if retrieving maven artifacts with an invalid link cancels the process and returns an empty list.
   */
  @Test
  public void testRetrieveMavenArtifactsWithInvalidLinkCancelsAndReturnsEmptyList() {

    ServerCredentials serverCredentials = new ServerCredentials("this/is/not/a/link", null, null, null, 0, "", "");
    assertThat(SearchResponseFactory.searchArtifactDownloadLinks(serverCredentials, "test")).isEmpty();

  }

  /**
   * Tests if retrieving maven artifacts with basic authentication returns a list of download urls
   *
   * @throws IOException if the test resource could not be read
   */
  @Test
  public void testRetrieveMavenArtifactsWithBasicAuthenticationReturnsAResponse() throws IOException {

    this.wireMockRule.stubFor(get(urlMatching("/solrsearch/select.*")).willReturn(aResponse().withStatus(200)
        .withBody(Files.readAllBytes(Paths.get(testdataRoot).resolve("mavenJsonTest.json")))));

    ServerCredentials serverCredentials = new ServerCredentials("http://localhost:8080", "testuser", "testpassword",
        null, 0, "", "");
    assertThat(SearchResponseFactory.searchArtifactDownloadLinks(serverCredentials, "test")).contains(new URL(
        "https://repo1.maven.org/maven2/com/devonfw/cobigen/templates/crud-java-server-app/2021.08.001/crud-java-server-app-2021.08.001-template-set.xml"));

  }

  /**
   * Tests if retrieving a json response with a connection problem (connection reset) cancels the process and returns an
   * empty response String
   *
   * @throws IOException if the test resource could not be read
   */
  @Test
  public void testRetrieveJsonResponseWithConnectionProblemCancelsAndReturnsEmptyString() throws IOException {

    this.wireMockRule.stubFor(
        get(urlMatching("/solrsearch/select.*")).willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

    ServerCredentials serverCredentials = new ServerCredentials("http://localhost:8080", null, null, null, 0, "", "");

    assertThat(AbstractSearchResponse.retrieveJsonResponseWithAuthentication(
        "http://localhost:8080/solrsearch/select.*", MavenSearchRepositoryType.MAVEN, serverCredentials)).isEmpty();

  }

  /**
   * Tests if retrieving maven artifacts using a valid proxy returns an list of download URLs.
   *
   * @throws IOException if the test resource could not be read
   */
  @Test
  public void testRetrieveMavenArtifactsWithProxyReturnsAResponse() throws IOException {

    // origin server using port 8081
    new WireMockServer(options().port(8081));

    // proxied server using port 8080
    this.wireMockRule.stubFor(get(urlMatching("/solrsearch/select.*")).willReturn(aResponse().withStatus(200)
        .withBody(Files.readAllBytes(Paths.get(testdataRoot).resolve("mavenJsonTest.json")))));

    ServerCredentials serverCredentials = new ServerCredentials("http://localhost:8081", null, null, "localhost", 8080,
        "", "");

    assertThat(SearchResponseFactory.searchArtifactDownloadLinks(serverCredentials, "test")).contains(new URL(
        "https://repo1.maven.org/maven2/com/devonfw/cobigen/templates/crud-java-server-app/2021.08.001/crud-java-server-app-2021.08.001-template-set.xml"));

  }

  /**
   * Tests if retrieving maven artifacts using an invalid proxy name returns an empty list of download URLs.
   *
   * @throws IOException if the test resource could not be read
   */
  @Test
  public void testRetrieveMavenArtifactsWithInvalidProxyNameReturnsAnEmptyList() throws IOException {

    WireMockServer wm = new WireMockServer(options().port(8081));

    this.wireMockRule.stubFor(get(urlMatching("/solrsearch/select.*")).willReturn(aResponse().withStatus(200)));

    ServerCredentials serverCredentials = new ServerCredentials("http://localhost:8081", null, null, "http://localhost",
        8080, "", "");

    assertThat(SearchResponseFactory.searchArtifactDownloadLinks(serverCredentials, "test")).isEmpty();

  }

  /**
   * Tests if retrieving maven artifacts using empty proxy credentials returns an empty list of download URLs.
   *
   * @throws IOException if the test resource could not be read
   */
  @Test
  public void testRetrieveMavenArtifactsWithEmptyProxyCredentialsReturnsAnEmptyList() throws IOException {

    WireMockServer wm = new WireMockServer(options().port(8081));

    this.wireMockRule.stubFor(get(urlMatching("/solrsearch/select.*")).willReturn(aResponse().withStatus(200)));

    ServerCredentials serverCredentials = new ServerCredentials("http://localhost:8081", null, null, "http://localhost",
        8080, null, null);

    assertThat(SearchResponseFactory.searchArtifactDownloadLinks(serverCredentials, "test")).isEmpty();

  }

  /**
   * Tests if retrieving maven artifacts using invalid proxy credentials returns an empty list of download URLs.
   *
   * @throws IOException if the test resource could not be read
   */
  @Test
  public void testRetrieveMavenArtifactsWithInvalidProxyCredentialsReturnsAnEmptyList() throws IOException {

    WireMockServer wm = new WireMockServer(options().port(8081));

    this.wireMockRule.stubFor(get(urlMatching("/solrsearch/select.*")).willReturn(aResponse().withStatus(401)));

    ServerCredentials serverCredentials = new ServerCredentials("http://localhost:8081", null, null, "localhost", 8080,
        "badusername", "badpassword");

    assertThat(SearchResponseFactory.searchArtifactDownloadLinks(serverCredentials, "test")).isEmpty();

  }

  /**
   * Tests if retrieving maven artifacts with an invalid username and password and returns an empty list of download
   * URLs.
   */
  @Test
  public void testRetrieveMavenArtifactsWithInvalidBasicCredentialsReturnsEmptyList() {

    this.wireMockRule.stubFor(get(urlMatching("/solrsearch/select.*")).willReturn(aResponse().withStatus(401)));

    ServerCredentials serverCredentials = new ServerCredentials("http://localhost:8080", null, null, null, 0, "", "");
    assertThat(SearchResponseFactory.searchArtifactDownloadLinks(serverCredentials, "test")).isEmpty();

  }

  /**
   * Tests if a maven artifact search with empty server credentials cancels the process and returns an empty list
   */
  @Test
  public void testEmptyServerCredentialsCancelsAndReturnsEmptyList() {

    assertThat(SearchResponseFactory.searchArtifactDownloadLinks(null, "test")).isEmpty();
  }

  /**
   * Tests if a maven artifact search with server credentials and empty base URL cancels the process and returns an
   * empty list of download URLs.
   */
  @Test
  public void testEmptyServerCredentialsBaseUrlCancelsAndReturnsEmptyList() {

    ServerCredentials serverCredentials = new ServerCredentials("", "", "", "", 0, "", "");

    assertThat(SearchResponseFactory.searchArtifactDownloadLinks(serverCredentials, "test")).isEmpty();
  }

  /**
   * Tests if a maven artifact search with an empty download list as response cancels and returns an empty list.
   */
  @Test
  public void testMavenEmptyDownloadLinksAfterSearchRequestCancelsAndReturnsEmptyList() {

    this.wireMockRule
        .stubFor(get(urlMatching("/solrsearch/select.*")).willReturn(aResponse().withStatus(200).withBody("{}")));

    ServerCredentials serverCredentials = new ServerCredentials("http://localhost:8080", null, null, null, 0, "", "");
    assertThat(SearchResponseFactory.searchArtifactDownloadLinks(serverCredentials, "test")).isEmpty();

    this.wireMockRule.stubFor(
        get(urlMatching("/artifactory/api/search/gavc.*")).willReturn(aResponse().withStatus(200).withBody("{}")));

    assertThat(SearchResponseFactory.searchArtifactDownloadLinks(serverCredentials, "test")).isEmpty();
  }

  /**
   * Tests if a jfrog artifact search with an empty body as response cancels and returns an empty list.
   */
  @Test
  public void testJfrogEmptyDownloadLinksAfterSearchRequestCancelsAndReturnsEmptyList() {

    ServerCredentials serverCredentials = new ServerCredentials("http://localhost:8080", null, null, null, 0, "", "");

    this.wireMockRule.stubFor(
        get(urlMatching("/artifactory/api/search/gavc.*")).willReturn(aResponse().withStatus(200).withBody("{}")));

    assertThat(SearchResponseFactory.searchArtifactDownloadLinks(serverCredentials, "test")).isEmpty();
  }

  /**
   * Tests if a nexus2 artifact search with an empty body as response cancels and returns an empty list.
   */
  @Test
  public void testNexus2EmptyDownloadLinksAfterSearchRequestCancelsAndReturnsEmptyList() {

    ServerCredentials serverCredentials = new ServerCredentials("http://localhost:8080", null, null, null, 0, "", "");

    this.wireMockRule.stubFor(
        get(urlMatching("/service/local/lucene/search.*")).willReturn(aResponse().withStatus(200).withBody("{}")));

    assertThat(SearchResponseFactory.searchArtifactDownloadLinks(serverCredentials, "test")).isEmpty();
  }

  /**
   * Tests if a nexus3 artifact search with an empty download list as response cancels and returns an empty list.
   */
  @Test
  public void testNexus3EmptyDownloadLinksAfterSearchRequestCancelsAndReturnsEmptyList() {

    ServerCredentials serverCredentials = new ServerCredentials("http://localhost:8080", null, null, null, 0, "", "");

    this.wireMockRule
        .stubFor(get(urlMatching("/service/rest/v1/search.*")).willReturn(aResponse().withStatus(200).withBody("{}")));

    assertThat(SearchResponseFactory.searchArtifactDownloadLinks(serverCredentials, "test")).isEmpty();
  }

  /**
   * Tests if an empty String gets returned when a faulty target link was used
   */
  @Test
  public void testWrongTargetLinkReturnsEmptyString() {

    ServerCredentials serverCredentials = new ServerCredentials(null, null, null, null, 0, "", "");
    assertThat(AbstractSearchResponse.retrieveJsonResponseWithAuthentication("this/is/not/a/link",
        MavenSearchRepositoryType.MAVEN, serverCredentials)).isEmpty();
  }

  /**
   * Tests if an {@link RestSearchResponseException} gets thrown when a status code was not 200 but 400 instead
   */
  @Test
  public void testWrongResponseStatusCodeThrowsException() {

    // given
    this.wireMockRule.stubFor(get(urlMatching("/solrsearch/select.*")).willReturn(aResponse().withStatus(400)));

    ServerCredentials serverCredentials = new ServerCredentials(null, null, null, null, 0, "", "");

    assertThatThrownBy(() -> {
      AbstractSearchResponse.retrieveJsonResponseWithAuthentication("http://localhost:8080",
          MavenSearchRepositoryType.MAVEN, serverCredentials);
    }).isInstanceOf(RestSearchResponseException.class).hasMessageContaining("http://localhost:8080");

  }

  /**
   * Tests if an empty list gets returned if the search request received an empty json string as a response
   *
   */
  @Test
  public void testSearchRequestWithEmptyJsonResponseReturnsEmptyList() {

    // given
    this.wireMockRule
        .stubFor(get(urlMatching("/solrsearch/select.*")).willReturn(aResponse().withStatus(200).withBody("")));

    ServerCredentials serverCredentials = new ServerCredentials("http://localhost:8080", null, null, null, 0, "", "");
    assertThat(SearchResponseFactory.searchArtifactDownloadLinks(serverCredentials, "com.devonfw.cobigen.templates"))
        .isEmpty();

  }

  /**
   * Tests if maven json response can properly be parsed and converted to a list of download URLs
   *
   * @throws IOException if an error occurred while reading the test json file
   */
  @Test
  public void testMavenParseDownloadLinks() throws IOException {

    // given
    ObjectMapper mapper = new ObjectMapper();
    MavenSearchResponse response = new MavenSearchResponse();

    String jsonResponse = new String(Files.readAllBytes(Paths.get(testdataRoot).resolve("mavenJsonTest.json")));

    response = mapper.readValue(jsonResponse, MavenSearchResponse.class);
    // when
    List<URL> downloadLinks = response.retrieveTemplateSetXmlDownloadURLs();

    // then
    assertThat(downloadLinks).contains(new URL(
        "https://repo1.maven.org/maven2/com/devonfw/cobigen/templates/crud-java-server-app/2021.08.001/crud-java-server-app-2021.08.001-template-set.xml"));
  }

  /**
   * Tests if nexus2 json response can properly be parsed and converted to a list of download URLs
   *
   * @throws IOException if an error occurred while reading the test json file
   */
  @Test
  public void testNexus2ParseDownloadLinks() throws IOException {

    // given
    ObjectMapper mapper = new ObjectMapper();
    Nexus2SearchResponse response = new Nexus2SearchResponse();

    String jsonResponse = new String(Files.readAllBytes(Paths.get(testdataRoot).resolve("nexus2JsonTest.json")));

    response = mapper.readValue(jsonResponse, Nexus2SearchResponse.class);

    // when
    List<URL> downloadLinks = response.retrieveTemplateSetXmlDownloadURLs();

    // then
    assertThat(downloadLinks).contains(new URL(
        "https://s01.oss.sonatype.org/service/local/repositories/releases/content/com/devonfw/cobigen/templates/crud-java-server-app/2021.08.001/crud-java-server-app-2021.08.001-template-set.xml"));
  }

  /**
   * Tests if nexus3 json response can properly be parsed and converted to a list of download URLs
   *
   * @throws IOException if an error occurred while reading the test json file
   */
  @Test
  public void testNexus3ParseDownloadLinks() throws IOException {

    // given
    ObjectMapper mapper = new ObjectMapper();
    Nexus3SearchResponse response = new Nexus3SearchResponse();

    String jsonResponse = new String(Files.readAllBytes(Paths.get(testdataRoot).resolve("nexus3JsonTest.json")));

    response = mapper.readValue(jsonResponse, Nexus3SearchResponse.class);

    // when
    List<URL> downloadLinks = response.retrieveTemplateSetXmlDownloadURLs();

    // then
    assertThat(downloadLinks).contains(new URL(
        "http://localhost:8081/repository/com/devonfw/cobigen/templates/crud-java-server-app/2021.08.001/crud-java-server-app-2021.08.001-template-set.xml"));
  }

  /**
   * Tests if jfrog json response can properly be parsed and converted to a list of download URLs
   *
   * @throws IOException if an error occurred while reading the test json file
   */
  @Test
  public void testJfrogParseDownloadLinks() throws IOException {

    // given
    ObjectMapper mapper = new ObjectMapper();
    JfrogSearchResponse response = new JfrogSearchResponse();

    String jsonResponse = new String(Files.readAllBytes(Paths.get(testdataRoot).resolve("jfrogJsonTest.json")));

    // when
    response = mapper.readValue(jsonResponse, JfrogSearchResponse.class);
    List<URL> downloadLinks = response.retrieveTemplateSetXmlDownloadURLs();

    // then
    assertThat(downloadLinks).contains(new URL(
        "http://localhost:8081/artifactory/api/storage/libs-release-local/com/devonfw/cobigen/templates/crud-java-server-app/2021.08.001/crud-java-server-app-2021.08.001-template-set.xml"));
  }

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
