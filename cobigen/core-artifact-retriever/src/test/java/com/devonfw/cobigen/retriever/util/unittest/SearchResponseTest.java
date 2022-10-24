package com.devonfw.cobigen.retriever.util.unittest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;

import com.devonfw.cobigen.retriever.mavensearch.constants.MavenSearchRepositoryType;
import com.devonfw.cobigen.retriever.mavensearch.exception.RestSearchResponseException;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.AbstractSearchResponse;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.ServerCredentials;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * Test class for {@link AbstractSearchResponse}
 */
public class SearchResponseTest {

  /**
   * WireMock rule to initialize
   */
  @Rule
  public WireMockRule wireMockRule = new WireMockRule(options().disableRequestJournal());

  /** Testdata root path */
  private static final String testdataRoot = "src/test/resources/testdata/unittest/MavenSearchTest";

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

}
