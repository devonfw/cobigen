package com.devonfw.cobigen.cli;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.cli.commands.CobiGenCommand;
import com.devonfw.cobigen.cli.logger.CLILogger;

import picocli.CommandLine;

/**
 * Starting point of the CobiGen CLI. Contains the main method.
 */
public class CobiGenCLI {

  /**
   * Logger to output useful information to the user
   */
  private static Logger LOG = LoggerFactory.getLogger(CobiGenCLI.class);

  /**
   * Main starting point of the CLI. Here we parse the arguments from the user.
   *
   * @param args list of arguments the user has passed
   */
  public static void main(String... args) {

    boolean verbose = Arrays.asList(args).contains("-v");
    CLILogger.layoutLogger(verbose);
    LOG.debug("Current working directory: {}", System.getProperty("user.dir"));
    CommandLine commandLine = new CommandLine(new CobiGenCommand());
    commandLine.registerConverter(Path.class, s -> Paths.get(s));
    int exitCode = commandLine.execute(args);
    if (exitCode == 0) {
      LOG.info("Success");
    } else {
      LOG.info("Failed");
    }
    System.exit(exitCode);
  }

}
