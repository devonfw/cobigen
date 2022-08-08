package com.devonfw.cobigen.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import com.devonfw.cobigen.api.constants.MavenSearchRepositoryType;
import com.devonfw.cobigen.api.util.MavenUtil;
import com.devonfw.cobigen.api.util.to.JfrogSearchResponse;
import com.devonfw.cobigen.api.util.to.MavenSearchResponse;
import com.devonfw.cobigen.api.util.to.NexusSearchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class for maven utilities
 */
public class MavenUtilTest {

  /** Testdata root path */
  private static final String testdataRoot = "src/test/resources/testdata/unittest/MavenUtilTest";

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
    List<URL> downloadLinks = response.getDownloadURLs();
    assertThat(downloadLinks).contains(
        new URL("https://repo1.maven.org/maven2/com/google/inject/guice/5.1.0/guice-5.1.0.jar"),
        new URL("https://repo1.maven.org/maven2/com/google/inject/guice-bom/5.1.0/guice-bom-5.1.0.jar"),
        new URL("https://repo1.maven.org/maven2/com/google/inject/guice-parent/5.1.0/guice-parent-5.1.0.jar"),
        new URL("https://repo1.maven.org/maven2/com/google/inject/jdk8-tests/5.0.1/jdk8-tests-5.0.1.jar"));
  }

  /**
   * Tests if nexus json response can properly be parsed and converted to a list of download URLs
   *
   * @throws IOException
   */
  @Test
  public void testNexusParseDownloadLinks() throws IOException {

    ObjectMapper mapper = new ObjectMapper();
    NexusSearchResponse response = new NexusSearchResponse();

    String jsonResponse = new String(Files.readAllBytes(Paths.get(testdataRoot).resolve("nexusJsonTest.json")));

    response = mapper.readValue(jsonResponse, NexusSearchResponse.class);
    List<URL> downloadLinks = response.getDownloadURLs();
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
    List<URL> downloadLinks = response.getDownloadURLs();
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

    downloadList = MavenUtil.getMavenArtifactsByGroupId(MavenSearchRepositoryType.maven, "com.google.inject");

    assertThat(downloadList).contains(
        new URL("https://repo1.maven.org/maven2/com/google/inject/guice/5.1.0/guice-5.1.0.jar"),
        new URL("https://repo1.maven.org/maven2/com/google/inject/guice-bom/5.1.0/guice-bom-5.1.0.jar"),
        new URL("https://repo1.maven.org/maven2/com/google/inject/guice-parent/5.1.0/guice-parent-5.1.0.jar"),
        new URL("https://repo1.maven.org/maven2/com/google/inject/jdk8-tests/5.0.1/jdk8-tests-5.0.1.jar"));
  }

  /**
   * Tests if a request to nexus search REST API returns a list of download URLs
   *
   * @throws IOException
   */
  @Test
  public void testNexusSearchRequestGetsValidDownloadLinks() throws IOException {

    List<URL> downloadList;

    downloadList = MavenUtil.getMavenArtifactsByGroupId(MavenSearchRepositoryType.nexus, "com.google.inject");

    assertThat(downloadList).contains(
        new URL("https://repo1.maven.org/maven2/com/google/inject/guice/5.1.0/guice-5.1.0.jar"),
        new URL("https://repo1.maven.org/maven2/com/google/inject/guice-bom/5.1.0/guice-bom-5.1.0.jar"),
        new URL("https://repo1.maven.org/maven2/com/google/inject/guice-parent/5.1.0/guice-parent-5.1.0.jar"),
        new URL("https://repo1.maven.org/maven2/com/google/inject/jdk8-tests/5.0.1/jdk8-tests-5.0.1.jar"));
  }

  /**
   * Tests if a request to nexus search REST API returns a list of download URLs
   *
   * @throws IOException
   */
  @Test
  public void testJfrogSearchRequestGetsValidDownloadLinks() throws IOException {

    List<URL> downloadList;

    downloadList = MavenUtil.getMavenArtifactsByGroupId(MavenSearchRepositoryType.jfrog, "com.google.inject");

    assertThat(downloadList).contains(
        new URL("https://repo1.maven.org/maven2/com/google/inject/guice/5.1.0/guice-5.1.0.jar"),
        new URL("https://repo1.maven.org/maven2/com/google/inject/guice-bom/5.1.0/guice-bom-5.1.0.jar"),
        new URL("https://repo1.maven.org/maven2/com/google/inject/guice-parent/5.1.0/guice-parent-5.1.0.jar"),
        new URL("https://repo1.maven.org/maven2/com/google/inject/jdk8-tests/5.0.1/jdk8-tests-5.0.1.jar"));
  }

}
