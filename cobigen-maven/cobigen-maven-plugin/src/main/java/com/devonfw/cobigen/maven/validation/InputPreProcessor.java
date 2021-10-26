package com.devonfw.cobigen.maven.validation;

import java.io.File;
import java.nio.file.Paths;

import org.apache.maven.plugin.MojoFailureException;

import com.devonfw.cobigen.api.InputInterpreter;
import com.devonfw.cobigen.api.exception.InputReaderException;
import com.google.common.base.Charsets;

/**
 * Input pre-processor, which tries to identify valid file inputs for CobiGen
 */
public class InputPreProcessor {

  /**
   * Processes the given file to be converted into any CobiGen valid input format
   *
   * @param file {@link File} converted into any CobiGen valid input format
   * @param cl {@link ClassLoader} to be used, when considering Java-related inputs
   * @param inputInterpreter parse cobiGen compliant input from the file
   * @throws MojoFailureException if the input retrieval did not result in a valid CobiGen input
   * @return a CobiGen valid input
   */
  public static Object process(InputInterpreter inputInterpreter, File file, ClassLoader cl)
      throws MojoFailureException {

    if (!file.exists() || !file.canRead()) {
      throw new MojoFailureException("Could not read input file " + file.getAbsolutePath());
    }
    Object input = null;
    try {
      input = inputInterpreter.read(Paths.get(file.toURI()), Charsets.UTF_8, cl);
    } catch (InputReaderException e) {
      // nothing
    }
    if (input != null) {
      return input;
    }
    throw new MojoFailureException("The file " + file.getAbsolutePath() + " is not a valid input for CobiGen.");
  }
}
