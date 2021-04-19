package com.devonfw.cobigen.tsplugin.config.constant;

/**
 * Constants extracted from the maven pom by templating-maven-plugin.
 * 
 * @see "edit template in src/main/java-templates"
 */
@SuppressWarnings("javadoc")
public class MavenMetadata {

    /** External server version */
    public static final String SERVER_VERSION = "${server.version}";
    
    /** Project artifact ID */
    public static final String ARTIFACT_ID = "${project.artifactId}";
    
    /** Windows download URL of the external server */
    public static final String DOWNLOAD_URL_WIN = "${download.url.win}";
    
    /** Windows download URL of the external server */
    public static final String DOWNLOAD_URL_LINUX = "${download.url.linux}";
    
    /** Windows download URL of the external server */
    public static final String DOWNLOAD_URL_MACOS = "${download.url.macos}";
}
