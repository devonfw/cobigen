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

import org.junit.Rule;
import org.junit.Test;

import com.devonfw.cobigen.retriever.mavensearch.util.to.model.SearchResponseFactory;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.ServerCredentials;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * Test class for {@link SearchResponseFactory}
 */
public class SearchResponseFactoryTest {

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

}
