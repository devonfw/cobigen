package com.devonfw.cobigen.cli.utils;

import java.io.File;
import java.util.InputMismatchException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.cli.CobiGenCLI;

/**
 * Utilities class for validating user's input and generation
 */
public final class ValidationUtils {

  /**
   * Logger useful for printing information
   */
  private static Logger LOG = LoggerFactory.getLogger(CobiGenCLI.class);

  /**
   * Validating user input file is correct or not. We check if file exists and it can be read
   *
   * @param inputFile user input file
   * @return true when file is valid
   */
  public boolean validateFile(File inputFile) {

    if (inputFile == null) {
      return false;
    }

    if (!inputFile.exists()) {
      LOG.error("The input file " + inputFile.getAbsolutePath() + " has not been found on your system.");
      return false;
    }

    if (!inputFile.canRead()) {
      LOG.error("The input file '{}' cannot be read. Please check file permissions on the file",
          inputFile.getAbsolutePath());
      return false;
    }
    return true;
  }

  /**
   * Checks whether the current output root path is valid. It can be either null because it is an optional parameter or
   * either a folder that exists.
   *
   * @param outputRootPath where the user wants to generate the code
   *
   * @return true if it is a valid output root path
   */
  public static boolean isOutputRootPathValid(File outputRootPath) {

    // As outputRootPath is an optional parameter, it means that it can be null
    if (outputRootPath == null || outputRootPath.exists()) {
      return true;
    } else {
      LOG.error("Your <outputRootPath> '{}' does not exist, please use a valid path.", outputRootPath);
      return false;
    }
  }

  /**
   * Checks the generation report in order to find possible errors and warnings
   *
   * @param report the generation report returned by the CobiGen.generate method
   */
  public static void checkGenerationReport(GenerationReportTo report) {

    for (String warning : report.getWarnings()) {
      LOG.debug("Warning: {}", warning);
    }

    if (report.getErrors() == null || report.getErrors().isEmpty()) {
      LOG.info("Successful generation.");
    } else {
      if (LOG.isDebugEnabled() && report.getErrors().size() > 1) {
        for (int i = 1; i < report.getErrors().size(); i++) {
          LOG.error("Further reported error:", report.getErrors().get(i));
        }
      }
      if (report.getErrors().get(0) instanceof CobiGenRuntimeException) {
        throw report.getErrors().get(0);
      } else {
        throw new CobiGenRuntimeException("Generation failed. Enable debug mode to see the exceptions occurred.",
            report.getErrors().get(0));
      }
    }
  }

  /**
   * Prints an error message to the user informing that no triggers have been matched. Depending on the type of the
   * input file will print different messages.
   *
   * @param inputFile User input file
   * @param isJavaInput true when input file is Java
   * @param isOpenApiInput true when input file is OpenAPI
   */
  public static void throwNoTriggersMatched(File inputFile, boolean isJavaInput, boolean isOpenApiInput) {

    LOG.error("Your input file '{}' is not valid as input for any generation purpose. It does not match any trigger.",
        inputFile.getName());
    if (isJavaInput) {
      LOG.error("Check that your Java input file is following devon4j naming convention. "
          + "Explained on https://devonfw.com/website/pages/docs/devon4j.asciidoc_coding-conventions.html");
    } else if (isOpenApiInput) {
      LOG.error("Validate your OpenAPI specification, check that is following 3.0 standard. "
          + "More info here https://github.com/devonfw/cobigen/wiki/cobigen-openapiplugin#usage");
    }
    throw new InputMismatchException("Your input file is invalid.");
  }

}
