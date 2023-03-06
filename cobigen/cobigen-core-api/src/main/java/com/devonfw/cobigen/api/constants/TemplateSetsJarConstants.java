package com.devonfw.cobigen.api.constants;

/**
 * Constants needed for handling the template set jars
 */
public class TemplateSetsJarConstants {

  public static final int ARTIFACT_ID_REGEX_GROUP = 0;

  public static final int VERSION_REGEX_GROUP = 1;

  //@formatter:off
  /**
   * Pattern to match artifact id and versions for template set jars:
   * Group 1: artifact id
   * Group 2: version
   * Group 3: snapshot or latest
   */
  //@formatter:on
  public static final String MAVEN_COORDINATE_JAR_PATTERN = "^([a-zA-Z-]+)-([\\d.]+)-(SNAPSHOT|LATEST)\\.jar$";

  //@formatter:off
  /**
   * Pattern to match artifact id and versions for template set source jars:
   * Group 1: artifact id
   * Group 2: version
   * Group 3: snapshot or latest
   */
  //@formatter:on
  public static final String MAVEN_COORDINATE_SOURCES_JAR_PATTERN = "^([a-zA-Z-]+)-([\\d.]+)-(Sources)\\.jar$";

}
