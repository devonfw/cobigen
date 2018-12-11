package com.devonfw.cobigen.eclipse.common.constants.external;

/**
 * This class encloses all relevant resource functionality for the generator configuration
 */
public class ResourceConstants {

    /**
     * Generator Configuration Project Name
     */
    public static final String CONFIG_PROJECT_NAME = "CobiGen_Templates";

    /**
     * Latest Jar folder downloaded from Update Templates
     */
    public static final String DOWNLOADED_JAR_FOLDER = "/.metadata/cobigen_jars";

    /**
     * Jar regular expression name to be used in a file name filter, so that we can check whether the
     * templates are already downloaded. Checks "templates-anystring-anydigitbetweendots.jar"
     */
    public static final String JAR_FILE_REGEX_NAME = "templates-([^-]+)-(\\d+\\.?)+.jar";

    /**
     * Source kar regular expression name to be used in a file name filter, so that we can check whether the
     * templates are already downloaded. Checks "templates-anystring-anydigitbetweendots-sources.jar"
     */
    public static final String SOURCES_FILE_REGEX_NAME = "templates-([^-]+)-(\\d+\\.?)+-sources.jar";

}
