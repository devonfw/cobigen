package com.cobigen.picocli.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cobigen.picocli.CobiGenCLI;
import com.cobigen.picocli.constants.MessagesConstants;

import picocli.CommandLine.Command;

/**
 * This class defines the main CobiGen command
 */
@Command(description = MessagesConstants.WELCOME_MESSAGE, name = "cobigen", aliases = { "cg" },
    mixinStandardHelpOptions = true, version = "CobiGen CLI 1.0", subcommands = { GenerateCommand.class })
public class CobiGenCommand implements Runnable {

    /**
     * Logger to output useful information to the user
     */
    private static Logger logger = LoggerFactory.getLogger(CobiGenCLI.class);

    @Override
    public void run() {

        // Nothing to do here, this is the master command
    }

}
