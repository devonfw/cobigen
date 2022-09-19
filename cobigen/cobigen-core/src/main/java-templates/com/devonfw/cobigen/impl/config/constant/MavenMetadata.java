package com.devonfw.cobigen.impl.config.constant;

/**
 * Constants extracted from the maven pom by templating-maven-plugin.
 *
 * @see "edit template in src/main/java-templates"
 */
public class MavenMetadata {

    /** Maven version */
    public static final String VERSION = "${project.version}";

    /** Maven GroupID */
    public static final String GROUPID = "${project.groupId}";
}
