package com.devonfw.cobigen.api.constants;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * External accessible configuration constants.
 */
public class ConfigurationConstants {

  /** Merge strategy to override complete file rather than merging at all */
  public static final String MERGE_STRATEGY_OVERRIDE = "override";

  // configuration constants of templates folder contents

  /** Context configuration file name */
  public static final String CONTEXT_CONFIG_FILENAME = "context.xml";

  /** Templates configuration file name */
  public static final String TEMPLATES_CONFIG_FILENAME = "templates.xml";

  /** Filename of the {@link Properties} used to customize cobigen properties and template relocation. */
  public static final String COBIGEN_PROPERTIES = "cobigen.properties";

  /** Resource folder containing templates */
  public static final String TEMPLATE_RESOURCE_FOLDER = "src/main/templates";

  /** Resource folder containing merge schemas */
  public static final String MERGE_SCHEMA_RESOURCE_FOLDER = "src/main/resources/mergeSchemas";

  /** Delimiter splitting the template folder and value of references in templates.xml files */
  public static final String REFERENCE_DELIMITER = "::";

  // configuration resource constants of cobigen home

  /** Default directory name in the home folder in case of {@link #DEFAULT_HOME} */
  public static final String DEFAULT_HOME_DIR_NAME = ".cobigen";

  /** Default cobigen home for storing configuration and downloads */
  public static final Path DEFAULT_HOME = Paths.get(System.getProperty("user.home")).resolve(DEFAULT_HOME_DIR_NAME);

  /** Name of the templates folder */
  public static final String TEMPLATES_FOLDER = "templates";

  /** Name of the template sets downloaded folder */
  public static final String DOWNLOADED_FOLDER = "downloaded";

  /** Name of the template sets adapted folder */
  public static final String ADAPTED_FOLDER = "adapted";

  /** Name of the template sets folder */
  public static final String TEMPLATE_SETS_FOLDER = "template-sets";

  /** Name of the extracted templates project */
  public static final String COBIGEN_TEMPLATES = "CobiGen_Templates";

  /** Name of cobigen configuration file */
  public static final String COBIGEN_CONFIG_FILE = ".cobigen";

  // cobigen configuration constants

  /**
   * Name of configuration key for location of templates. It could be Cobigen_Templates folder, an artifact or a maven
   * dependency
   */
  public static final String CONFIG_PROPERTY_TEMPLATES_PATH = "templates";

  /**
   * Name of configuration key for location of template sets.
   */
  public static final String CONFIG_PROPERTY_TEMPLATE_SETS_PATH = "template-sets";

  // cobigen configuration environment variables

  /** Name of the environment variable pointing to cobigen configuration file */
  public static final String CONFIG_ENV_HOME = "COBIGEN_HOME";

}
