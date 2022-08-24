package com.devonfw.cobigen.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.devonfw.cobigen.api.constants.MavenSearchRepositoryConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.RestSearchResponseException;
import com.devonfw.cobigen.api.util.MavenUtil;
import com.devonfw.cobigen.api.util.to.AbstractSearchResponse;
import com.devonfw.cobigen.api.util.to.jfrog.JfrogSearchResponse;
import com.devonfw.cobigen.api.util.to.maven.MavenSearchResponse;
import com.devonfw.cobigen.api.util.to.nexus2.Nexus2SearchResponse;
import com.devonfw.cobigen.api.util.to.nexus3.Nexus3SearchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.ProcessingException;

/**
 * Test class for maven utilities
 */
public class MavenUtilTest {

  /** Testdata root path */
  private static final String testdataRoot = "src/test/resources/testdata/unittest/MavenUtilTest";

  /**
   * Tests if a wrong repository type throws {@link CobiGenRuntimeException}
   */
  @Test(expected = CobiGenRuntimeException.class)
  public void testWrongRepositoryTypeThrowsException() {

    assertThat(MavenUtil.retrieveMavenArtifactsByGroupId("this/is/not/a/link", "test", null));
  }

  /**
   * Tests if an exception gets thrown when a faulty target link without a token was used
   */
  @Test(expected = ProcessingException.class)
  public void testWrongTargetLinkThrowsException() {

    AbstractSearchResponse.getJsonResponseStringByTargetLink("this/is/not/a/link", null);
  }

  /**
   * Tests if an exception gets thrown when a faulty target link and token was used
   */
  @Test(expected = ProcessingException.class)
  public void testWrongTargetLinkAndTokenThrowsException() {

    AbstractSearchResponse.getJsonResponseStringByTargetLink("this/is/not/a/link", "thisisabadtoken");
  }

  /**
   * Tests if an exception gets thrown when a status code was not 200
   */
  @Test(expected = RestSearchResponseException.class)
  public void testWrongResponseStatusCodeThrowsException() {

    AbstractSearchResponse.getJsonResponseStringByTargetLink("https://search.maven.org/solrsearch/select?test", null);
  }

  /**
   * Tests if maven json response can properly be parsed and converted to a list of download URLs
   *
   * @throws IOException
   */
  @Test
  public void testMavenParseDownloadLinks() throws IOException {

    ObjectMapper mapper = new ObjectMapper();
    MavenSearchResponse response = new MavenSearchResponse();

    String jsonResponse = new String(Files.readAllBytes(Paths.get(testdataRoot).resolve("mavenJsonTest.json")));

    response = mapper.readValue(jsonResponse, MavenSearchResponse.class);
    List<URL> downloadLinks = response.retrieveDownloadURLs();
    assertThat(downloadLinks).contains(
        new URL("https://repo1.maven.org/maven2/com/google/inject/guice/5.1.0/guice-5.1.0.jar"),
        new URL("https://repo1.maven.org/maven2/com/google/inject/guice-bom/5.1.0/guice-bom-5.1.0.pom"),
        new URL("https://repo1.maven.org/maven2/com/google/inject/guice-parent/5.1.0/guice-parent-5.1.0.pom"),
        new URL("https://repo1.maven.org/maven2/com/google/inject/jdk8-tests/5.0.1/jdk8-tests-5.0.1.jar"));
  }

  /**
   * Tests if nexus2 json response can properly be parsed and converted to a list of download URLs
   *
   * @throws IOException
   */
  @Test
  public void testNexus2ParseDownloadLinks() throws IOException {

    ObjectMapper mapper = new ObjectMapper();
    Nexus2SearchResponse response = new Nexus2SearchResponse();

    String jsonResponse = new String(Files.readAllBytes(Paths.get(testdataRoot).resolve("nexus2JsonTest.json")));

    response = mapper.readValue(jsonResponse, Nexus2SearchResponse.class);
    List<URL> downloadLinks = response.retrieveDownloadURLs();
    assertThat(downloadLinks).contains(new URL(
        "https://s01.oss.sonatype.org/service/local/repositories/releases/content/com/devonfw/cobigen/openapiplugin/2021.12.006/openapiplugin-2021.12.006.pom"),
        new URL(
            "https://s01.oss.sonatype.org/service/local/repositories/releases/content/com/devonfw/cobigen/openapiplugin/2021.12.006/openapiplugin-2021.12.006.jar"),
        new URL(
            "https://s01.oss.sonatype.org/service/local/repositories/releases/content/com/devonfw/cobigen/openapiplugin/2021.12.005/openapiplugin-2021.12.005.pom"),
        new URL(
            "https://s01.oss.sonatype.org/service/local/repositories/releases/content/com/devonfw/cobigen/openapiplugin/2021.12.005/openapiplugin-2021.12.005.jar"),
        new URL(
            "https://s01.oss.sonatype.org/service/local/repositories/releases/content/com/devonfw/cobigen/jsonplugin/2021.12.006/jsonplugin-2021.12.006.pom"),
        new URL(
            "https://s01.oss.sonatype.org/service/local/repositories/releases/content/com/devonfw/cobigen/jsonplugin/2021.12.006/jsonplugin-2021.12.006.jar"),
        new URL(
            "https://s01.oss.sonatype.org/service/local/repositories/releases/content/com/devonfw/cobigen/jsonplugin/2021.12.005/jsonplugin-2021.12.005.pom"),
        new URL(
            "https://s01.oss.sonatype.org/service/local/repositories/releases/content/com/devonfw/cobigen/jsonplugin/2021.12.005/jsonplugin-2021.12.005.jar"));
  }

  /**
   * Tests if nexus3 json response can properly be parsed and converted to a list of download URLs
   *
   * @throws IOException
   */
  @Test
  public void testNexus3ParseDownloadLinks() throws IOException {

    ObjectMapper mapper = new ObjectMapper();
    Nexus3SearchResponse response = new Nexus3SearchResponse();

    String jsonResponse = new String(Files.readAllBytes(Paths.get(testdataRoot).resolve("nexus3JsonTest.json")));

    response = mapper.readValue(jsonResponse, Nexus3SearchResponse.class);
    List<URL> downloadLinks = response.retrieveDownloadURLs();
    assertThat(downloadLinks).contains(new URL(
        "http://localhost:8081/repository/maven-central/org/osgi/org.osgi.core/4.3.1/org.osgi.core-4.3.1-sources.jar"),
        new URL("http://localhost:8081/repository/maven-central/org/osgi/org.osgi.core/4.3.1/org.osgi.core-4.3.1.jar"),
        new URL("http://localhost:8081/repository/maven-central/org/osgi/org.osgi.core/4.3.1/org.osgi.core-4.3.1.pom"));
  }

  /**
   * Tests if jfrog json response can properly be parsed and converted to a list of download URLs
   *
   * @throws IOException
   */
  @Test
  public void testJfrogParseDownloadLinks() throws IOException {

    ObjectMapper mapper = new ObjectMapper();
    JfrogSearchResponse response = new JfrogSearchResponse();

    String jsonResponse = new String(Files.readAllBytes(Paths.get(testdataRoot).resolve("jfrogJsonTest.json")));

    response = mapper.readValue(jsonResponse, JfrogSearchResponse.class);
    List<URL> downloadLinks = response.retrieveDownloadURLs();
    assertThat(downloadLinks).contains(new URL(
        "http://localhost:8081/artifactory/api/storage/libs-release-local/org/acme/artifact/1.0/artifact-1.0-sources.jar"),
        new URL(
            "http://localhost:8081/artifactory/api/storage/libs-release-local/org/acme/artifactB/1.0/artifactB-1.0-sources.jar"));
  }

  /**
   * Tests if a request to maven search REST API returns a list of download URLs
   *
   * @throws IOException
   */
  @Test
  public void testMavenSearchRequestGetsValidDownloadLinks() throws IOException {

    List<URL> downloadList;

    downloadList = MavenUtil.retrieveMavenArtifactsByGroupId(MavenSearchRepositoryConstants.MAVEN_REPOSITORY_URL,
        "com.google.inject", null);

    assertThat(downloadList).contains(
        new URL("https://repo1.maven.org/maven2/com/google/inject/guice/5.1.0/guice-5.1.0.jar"),
        new URL("https://repo1.maven.org/maven2/com/google/inject/guice-bom/5.1.0/guice-bom-5.1.0.pom"),
        new URL("https://repo1.maven.org/maven2/com/google/inject/guice-parent/5.1.0/guice-parent-5.1.0.pom"),
        new URL("https://repo1.maven.org/maven2/com/google/inject/jdk8-tests/5.0.1/jdk8-tests-5.0.1.jar"));
  }

  /**
   * Tests if a request to nexus2 search REST API returns a list of download URLs
   *
   * @throws IOException
   */
  @Test
  public void testNexus2SearchRequestGetsValidDownloadLinks() throws IOException {

    List<URL> downloadList;

    downloadList = MavenUtil.retrieveMavenArtifactsByGroupId(MavenSearchRepositoryConstants.NEXUS2_REPOSITORY_URL,
        "com.devonfw.cobigen", null);

    assertThat(downloadList).contains(new URL(
        "https://s01.oss.sonatype.org/service/local/repositories/releases/content/com/devonfw/cobigen/openapiplugin/2021.12.006/openapiplugin-2021.12.006.jar"));
  }

  /**
   * Tests if a request to nexus3 search REST API returns a list of download URLs
   *
   * @throws IOException
   */
  @Test
  @Ignore // TODO: remove when nexus3 URLs are testable
  public void testNexus3SearchRequestGetsValidDownloadLinks() throws IOException {

    List<URL> downloadList;

    downloadList = MavenUtil.retrieveMavenArtifactsByGroupId(MavenSearchRepositoryConstants.NEXUS3_REPOSITORY_URL,
        "com.devonfw.cobigen", null);

    assertThat(downloadList).contains(new URL(
        "http://localhost:8081/repository/maven-central/org/osgi/org.osgi.core/4.3.1/org.osgi.core-4.3.1-sources.jar"),
        new URL("http://localhost:8081/repository/maven-central/org/osgi/org.osgi.core/4.3.1/org.osgi.core-4.3.1.jar"),
        new URL("http://localhost:8081/repository/maven-central/org/osgi/org.osgi.core/4.3.1/org.osgi.core-4.3.1.pom"));
  }

  /**
   * Tests if a request to jfrog search REST API returns a list of download URLs
   *
   * @throws IOException
   */
  @Test
  @Ignore // TODO: remove when jfrog URLs are testable
  public void testJfrogSearchRequestGetsValidDownloadLinks() throws IOException {

    List<URL> downloadList;

    downloadList = MavenUtil.retrieveMavenArtifactsByGroupId(MavenSearchRepositoryConstants.JFROG_REPOSITORY_URL,
        "com.devonfw.cobigen", null);
    assertThat(downloadList).contains(new URL(
        "https://localjfrog.com/artifactory/api/storage/maven-remote-cache/com/devonfw/cobigen/cli-parent/2021.04.001-SNAPSHOT/cli-parent-2021.04.001-SNAPSHOT.pom"),
        new URL(
            "https://localjfrog.com/artifactory/api/storage/maven-remote-cache/com/devonfw/cobigen/cli-parent/2021.08.002-SNAPSHOT/cli-parent-2021.08.002-SNAPSHOT.pom"));
  }

}
