package com.devonfw.cobigen.cli;

import java.util.Arrays;

import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.cli.commands.CobiGenCommand;
import com.devonfw.cobigen.cli.logger.CLILogger;

import ch.qos.logback.classic.Logger;
import picocli.CommandLine;

/**
 * Starting point of the CobiGen CLI. Contains the main method.
 */
public class CobiGenCLI {

    /**
     * Logger to output useful information to the user
     */
    private static Logger LOG = (Logger) LoggerFactory.getLogger(CobiGenCLI.class);

    /**
     * Picocli command line object
     */
    private final static CommandLine commandLine = new CommandLine(new CobiGenCommand());	

    /**
     * @return the {@link CommandLine} object of this current execution
     */
    public static CommandLine getCLI() {
        return commandLine;
    }

    /**
     * Main starting point of the CLI. Here we parse the arguments from the user.
     *
     * @param args
     *            list of arguments the user has passed
     */
    public static void main(String... args) {
    	boolean verbose = Arrays.asList(args).contains("-v"); 
        CLILogger.layoutLogger(verbose);
        LOG.debug("Current working directory: {}", System.getProperty("user.dir"));
        if (commandLine.execute(args) == 0) {
            LOG.debug("Commands were executed correctly");
        }

    }

}
