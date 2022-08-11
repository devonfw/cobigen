package com.devonfw.cobigen.api.constants;

/**
 * Maven search repository URLs
 *
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
   * Maven target link
   */
  public static String MAVEN_TARGET_LINK = "solrsearch/select";

  /**
   * Maven maximum response rows
   */
  public static int MAVEN_MAX_RESPONSE_ROWS = 20;

  /**
   * Nexus2 repository URL
   */
  public static String NEXUS2_REPOSITORY_URL = "https://s01.oss.sonatype.org";

  /**
   * Nexus2 repository link
   */
  public static String NEXUS2_REPOSITORY_LINK = "service/local/repositories/releases/content";

  /**
   * Nexus2 target link
   */
  public static String NEXUS2_TARGET_LINK = "service/local/lucene/search";

  /**
   * Nexus2 connect ID
   */
  public static String NEXUS2_DC_ID = "1660043999867";

  /**
   * Nexus3 target link
   */
  public static String NEXUS3_TARGET_LINK = "service/rest/v1/search";

  /**
   * Nexus3 repository URL
   */
  public static String NEXUS3_REPOSITORY_URL = "";

  /**
   * Jfrog repository URL
   */
  public static String JFROG_REPOSITORY_URL = "http://localhost:8082/artifactory";

  /**
   * Jfrog target link
   */
  public static String JFROG_TARGET_LINK = "api/search/gavc";

}
