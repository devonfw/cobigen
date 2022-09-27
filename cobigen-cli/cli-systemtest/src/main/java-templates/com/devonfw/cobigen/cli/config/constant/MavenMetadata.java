package com.devonfw.cobigen.cli.config.constant;

/**
 * Constants extracted from the maven pom by templating-maven-plugin.
 *
 * @see "edit template in src/main/java-templates"
 */
public class MavenMetadata {

    /** Maven version */
    public static final String VERSION = "${project.version}";

    /** Jacoco Agent configuration */
    public static final String JACOCO_AGENT_ARGS = "${surefireArgLineIntegration}";

}
