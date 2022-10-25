package com.devonfw.cobigen.retriever.unittest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import com.devonfw.cobigen.retriever.mavensearch.util.to.model.jfrog.JfrogSearchResponse;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.maven.MavenSearchResponse;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.nexus2.Nexus2SearchResponse;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.nexus3.Nexus3SearchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class for proper parsing of maven repository search responses
 */
public class MavenSearchResponseTest {

  /** Testdata root path */
  private static final String testdataRoot = "src/test/resources/testdata/unittest/MavenSearchResponseTest";

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
}
