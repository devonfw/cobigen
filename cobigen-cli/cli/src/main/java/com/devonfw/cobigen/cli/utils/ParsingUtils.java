
package com.devonfw.cobigen.cli.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.cli.CobiGenCLI;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

/**
 * This class contains utilities for parsing user input. It also contains mmm logic to parse user's input file in order
 * to find needed information
 */
public class ParsingUtils {

  /**
   * Logger to output useful information to the user
   */
  private static Logger LOG = LoggerFactory.getLogger(CobiGenCLI.class);

  /**
   * Tries to parse a relative path with the current working directory
   *
   * @param inputFiles list of all input files from the user
   *
   * @param inputFile input file which we are going to parse to find out whether it is a valid file
   * @param index location of the input file in the ArrayList of inputs
   * @return true only if the parsed file exists, false otherwise
   *
   */
  public static boolean parseRelativePath(List<Path> inputFiles, Path inputFile, int index) {

    try {
      Path inputFilePath = Paths.get(System.getProperty("user.dir"), inputFile.toString());

      if (inputFilePath.toFile().exists()) {
        inputFiles.set(index, inputFilePath);
        return true;
      }
    } catch (InvalidPathException e) {
      LOG.debug("The path string {} {} cannot be converted to a path", System.getProperty("user.dir"),
          inputFile.toString());
    }
    return false;
  }

  /**
   * This method format the runtime generated code with the help of google API
   *
   * @param generatedFiles List of generation report files
   * @throws FormatterException if any error occurred while formatting the Java code
   */
  public static void formatJavaSources(Set<Path> generatedFiles) throws FormatterException {

    Set<Path> filesToFormat = generatedFiles;
    Formatter formatter = new Formatter();
    Iterator<Path> itr = filesToFormat.iterator();
    LOG.info("Formatting code...");
    while (itr.hasNext()) {
      Path generatedFilePath = itr.next();
      try {
        String unformattedCode = new String(Files.readAllBytes(generatedFilePath));
        String formattedCode = formatter.formatSource(unformattedCode);
        Files.write(generatedFilePath, formattedCode.getBytes());
      } catch (IOException e) {
        LOG.error("Unable to read or write the generated file {} when trying to format it",
            generatedFilePath.toString());
        return;
      }
    }
    LOG.info("Finished successfully");

  }

}
