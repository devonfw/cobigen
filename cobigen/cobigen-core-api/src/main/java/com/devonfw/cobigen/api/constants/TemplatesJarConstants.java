package com.devonfw.cobigen.api.constants;

/**
 * Constants needed for handling the templates jar
 */
public class TemplatesJarConstants {
  /**
   * Latest Jar folder downloaded from Update Templates
   */
  public static final String DOWNLOADED_JAR_FOLDER = "/.metadata/cobigen_jars";

  /**
   * Regular expression to check the correct definition of maven coordinates to download a template-set with the
   * configuration key template-sets.installed in the properties.
   */
  public static final String MAVEN_COORDINATES_CHECK = "([a-zA-Z0-9_\\-\\.]+):([a-zA-Z0-9_-]+)(:([0-9]+(\\.[0-9]+)*(-SNAPSHOT)?|LATEST))?";

  /**
   * Jar regular expression name to be used in a file name filter, so that we can check whether the templates are
   * already downloaded. Checks "templates-anystring-anydigitbetweendots.jar"
   */
  public static final String JAR_FILE_REGEX_NAME = "templates-([^-]+)-(\\d+\\.?)+.jar$";

  /**
   * Source jar regular expression name to be used in a file name filter, so that we can check whether the templates are
   * already downloaded. Checks "templates-anystring-anydigitbetweendots-sources.jar"
   */
  public static final String SOURCES_FILE_REGEX_NAME = "templates-([^-]+)-(\\d+\\.?)+-sources.jar$";

  /**
   * Jar regular expression used to check the version of the jar in oder to find out whether we should update. Checks
   * "templates-anystring-capturedigitsbetweendots.jar"
   */
  public static final String JAR_VERSION_REGEX_CHECK = "templates-([^-]+)-(\\d+|\\d+(\\.\\d+)*)?\\.jar$";

  /**
   * Sources jar regular expression used to check the version of the sources jar in oder to find out whether we should
   * update. Checks "templates-anystring-capturedigitsbetweendots-sources.jar"
   */
  public static final String SOURCES_VERSION_REGEX_CHECK = "templates-([^-]+)-(\\d+|\\d+(\\.\\d+)*)?\\-sources.jar$";

  /**
   * GroupId of the latest devon4j templates jar on Maven Central
   */
  public static final String DEVON4J_TEMPLATES_GROUPID = "com.devonfw.cobigen";

  /**
   * ArtifactId of the latest devon4j templates jar on Maven Central
   */
  public static final String DEVON4J_TEMPLATES_ARTIFACTID = "templates-devon4j";
}