package com.devonfw.cobigen.maven.config.constant;

/**
 * Constants extracted from the maven pom by templating-maven-plugin.
 *
 * @see "edit template in src/main/java-templates"
 */
public class MavenMetadata {

    /** Maven version */
    public static final String VERSION = "${project.version}";

    /** Maven group ID */
    public static final String GROUP_ID = "${project.groupId}";

    /** Maven artifact ID */
    public static final String ARTIFACT_ID = "${project.artifactId}";

    /** Jacoco Agent configuration */
    public static final String JACOCO_AGENT_ARGS = "${surefireArgLineIntegration}";
}
