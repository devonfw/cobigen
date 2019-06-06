package com.cobigen.picocli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cobigen.picocli.commands.CobiGenCommand;

import picocli.CommandLine;

/**
 * Starting point of the CobiGen CLI. Contains the main method.
 */
public class CobiGenCLI {

    /**
     * Logger to output useful information to the user
     */
    private static Logger logger = LoggerFactory.getLogger(CobiGenCLI.class);

    /**
     * Main starting point of the CLI. Here we parse the arguments from the user.
     *
     * @param args
     *            list of arguments the user has passed
     */
    public static void main(String... args) {

        CommandLine commandLine = new CommandLine(new CobiGenCommand());
        commandLine.parseArgs(args);
        if (commandLine.execute(args) == 0) {
            logger.debug("Commands were executed correctly");
        }

    }

}
