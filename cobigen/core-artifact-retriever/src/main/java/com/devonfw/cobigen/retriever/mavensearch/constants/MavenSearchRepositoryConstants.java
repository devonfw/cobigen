package com.devonfw.cobigen.retriever.mavensearch.constants;

/**
 * Constants needed for handling the maven search REST APIs
 */
public class MavenSearchRepositoryConstants {

  /**
   * Message for an exception when an unexpected status code was returned by the API
   */
  public static final String MAVEN_SEARCH_API_EXCEPTION_UNEXPECTED_STATUS_CODE = "The search REST API returned the unexpected status code";

  /**
   * Message for an exception when no response was returned by the API
   */
  public static final String MAVEN_SEARCH_API_EXCEPTION_NO_RESPONSE = "It was not possible to get a response from";

  /**
   * Message for an exception when the request to the API failed
   */
  public static final String MAVEN_SEARCH_API_EXCEPTION_REQUEST_FAILED = "The search REST API was unable to send or receive the message from the service using the URL";

  /**
   * Message for an exception when a faulty target URL was provided
   */
  public static final String MAVEN_SEARCH_API_EXCEPTION_FAULTY_TARGET_URL = "CobiGen was unable to retrieve valid URLs from the REST Search API of the";

  /**
   * Message for an exception when an empty response was returned by the API
   */
  public static final String MAVEN_SEARCH_API_EXCEPTION_EMPTY_JSON_RESPONSE = "The search REST API recieved an empty json response from the target URL";

  /**
   * Message for an exception when an empty artifact list was returned by the API
   */
  public static final String MAVEN_SEARCH_API_EXCEPTION_ARTIFACT_LIST_EMPTY = "CobiGen did not find any artifacts using the REST Search API of the";

  /**
   * Message part one for an exception when the authentication for an API failed
   */
  public static final String MAVEN_SEARCH_API_EXCEPTION_AUTH_FAILED_ONE = "CobiGen was unable to access the REST Search API of the";

  /**
   * Message part two for an exception when the authentication for an API failed
   */
  public static final String MAVEN_SEARCH_API_EXCEPTION_AUTH_FAILED_TWO = "repository with authentication method: Basic Authentication using the URL";

  /**
   * Message for an exception when the API response json could not be parsed
   */
  public static final String MAVEN_SEARCH_API_EXCEPTION_UNABLE_TO_PARSE_JSON = "CobiGen was unable to parse the json response from the REST Search API of the";

  /**
   * Fallback repository URL
   */
  public static final String FALLBACK_REPOSITORY_URL = "https://s01.oss.sonatype.org";

  /**
   * Maven repository URL
   */
  public static final String MAVEN_REPOSITORY_URL = "https://search.maven.org";

  /**
   * Maven repository download link
   */
  public static final String MAVEN_REPOSITORY_DOWNLOAD_LINK = "https://repo1.maven.org/maven2";

  /**
   * Maven REST search API path
   */
  public static final String MAVEN_REST_SEARCH_API_PATH = "solrsearch/select";

  /**
   * Nexus2 repository URL
   */
  public static final String NEXUS2_REPOSITORY_URL = "https://s01.oss.sonatype.org";

  /**
   * Nexus2 repository link
   */
  public static final String NEXUS2_REPOSITORY_LINK = "service/local/repositories/releases/content";

  /**
   * Nexus2 SNAPSHOT repository link
   */
  public static final String NEXUS2_SNAPSHOT_REPOSITORY_LINK = "service/local/repositories/snapshots/content";

  /**
   * Nexus2 REST search API path
   */
  public static final String NEXUS2_REST_SEARCH_API_PATH = "service/local/lucene/search";

  /**
   * Nexus3 REST search API path
   */
  public static final String NEXUS3_REST_SEARCH_API_PATH = "service/rest/v1/search";

  /**
   * Nexus3 repository URL
   */
  public static final String NEXUS3_REPOSITORY_URL = "";

  /**
   * Jfrog repository URL
   */
  public static final String JFROG_REPOSITORY_URL = "http://localhost:8082/artifactory";

  /**
   * Jfrog REST search API path
   */
  public static final String JFROG_REST_SEARCH_API_PATH = "artifactory/api/search/gavc";

}
