package com.devonfw.cobigen.api.constants;

/**
 * Constants needed for handling the maven search REST APIs
 */
public class MavenSearchRepositoryConstants {

  /**
   * Maven repository URL
   */
  public static String MAVEN_REPOSITORY_URL = "https://search.maven.org";

  /**
   * Maven repository download link
   */
  public static String MAVEN_REPOSITORY_DOWNLOAD_LINK = "https://repo1.maven.org/maven2";

  /**
   * Maven REST search API path
   */
  public static String MAVEN_REST_SEARCH_API_PATH = "solrsearch/select";

  /**
   * Nexus2 repository URL
   */
  public static String NEXUS2_REPOSITORY_URL = "https://s01.oss.sonatype.org";

  /**
   * Nexus2 repository link
   */
  public static String NEXUS2_REPOSITORY_LINK = "service/local/repositories/releases/content";

  /**
   * Nexus2 REST search API path
   */
  public static String NEXUS2_REST_SEARCH_API_PATH = "service/local/lucene/search";

  /**
   * Nexus3 REST search API path
   */
  public static String NEXUS3_REST_SEARCH_API_PATH = "service/rest/v1/search";

  /**
   * Nexus3 repository URL
   */
  public static String NEXUS3_REPOSITORY_URL = "";

  /**
   * Jfrog repository URL
   */
  public static String JFROG_REPOSITORY_URL = "http://localhost:8082/artifactory";

  /**
   * Jfrog REST search API path
   */
  public static String JFROG_REST_SEARCH_API_PATH = "artifactory/api/search/gavc";

}
