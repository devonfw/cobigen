package com.devonfw.cobigen.cli.constants;

import com.devonfw.cobigen.impl.config.constant.WikiConstants;

/**
 * Messages constants printed to the user
 */
public class MessagesConstants {

  /**
   * Message constant: when a command has not been understood
   */
  public static final String COMMAND_NOT_UNDERSTOOD = "Command not understood,  please try again";

  /**
   * Message constant: when a command is not yet supported
   */
  public static final String COMMAND_NOT_YET_SUPPORTED = "Command not yet supported,  sorry for the unconvenience";

  /**
   * Message constant: welcome message when user executes CobiGen help
   */
  public static final String WELCOME_MESSAGE = "Welcome to CobiGen.\n"
      + "The Code-based incemental Generator for end to end code generation tasks, mostly used in Java projects.\n";

  /**
   * Message constant: description of the generate command
   */
  public static final String GENERATE_DESCRIPTION = "Using an input file (Java entity or ETO, OpenAPI definition, XML...) can generate code to a location on your computer";

  /**
   * Message constant: description of the input files that can be used
   */
  public static final String INPUT_FILE_DESCRIPTION = "Input files (Java entity or ETO, OpenAPI definition, XML...) that will be parsed by CobiGen and generate code from them."
      + " You can use glob patterns on the path, for using multiple input files. "
      + "Also you can specify input files one by one separated by comma.";

  /**
   * Message constant: description of the output root path option
   */
  public static final String OUTPUT_ROOT_PATH_DESCRIPTION = "Location where the generated code will be stored.";

  /**
   * Message constant: description of the verbose option
   */
  public static final String VERBOSE_OPTION_DESCRIPTION = "If this options is enabled, we will print also debug messages";

  /** Message constant: description of the templates path configuration */
  public static final String TEMPLATE_PATH_DESCRIPTION = "Location of the templates project. Can be either a project "
      + "containing the templates and utils like CobiGen_Templates or a jar file with the compiled templates and utils.";

  /**
   * Message constant: description of the increments option
   */
  public static final String INCREMENTS_OPTION_DESCRIPTION = "List of increments that will be generated. They need to be specified with numbers separated by comma.";

  /**
   * Message constant: description of the templates option
   */
  public static final String TEMPLATES_OPTION_DESCRIPTION = "List of templates that will be generated. They need to be specified with numbers separated by comma.";

  /**
   * Message constant: description of the plug-in update
   */
  public static final String UPDATE_OPTION_DESCRIPTION = "Use update command to find out which plug-ins are outdated and choose which ones do you want to update .";

  /**
   * Message constant: description of update all option
   */
  public static final String UPDATE_ALL_DESCRIPTION = "If this option is enabled, all plugins will get updated.";

  /**
   * Message constant: description of the adapt-templates command
   */
  public static final String ADAPT_TEMPLATES_DESCRIPTION = "Generates a new templates folder next to the cobigen cli";

  /**
   * Message constant: description of the adapt-templates --all command
   */
  public static final String ADAPT_ALL_DESCRIPTION = "If this option is enabled, all templates will get adapted.";

  /**
   * Message constant: description of the custom-location option
   */
  public static final String CUSTOM_LOCATION_OPTION_DESCRIPTION = "Custom location where the unpacked templates will be stored.";

  /**
   * Message constant: description of the --upgrade option
   */
  public static final String UPGRADE_CONFIGURATION_OPTION = "Will upgrade the monolithc templates automatically, if enabled.";

  /**
   * Message constant: description of the generate command with --force-monolithic-templates option
   */
  public static final String FORCE_MONOLITHIC_CONFIGURATION = "If this option is enabled, the old monolithic template structure will be used instead of the new template sets structure..Further Information can be found at: "
      + WikiConstants.WIKI_UPGRADE_MONOLITHIC_CONFIGURATION;

  /**
   * Message constant: description of the CLI Yes or No answer
   */
  public static final String YES_NO_ANSWER_DESCRIPTION = "Type yes/y to continue or no/n to cancel (or hit return for yes). ";

  /**
   * Message constant: description of the CLI Invalid Yes or No answer
   */
  public static final String INVALID_YES_NO_ANSWER_DESCRIPTION = "Invalid input. Please answer yes/n or no/n (or hit return for yes).";

}
