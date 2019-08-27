package com.devonfw.cobigen.cli.commands;

import com.devonfw.cobigen.cli.constants.MessagesConstants;

import picocli.CommandLine.Command;

/**
 * This class defines the main CobiGen command
 */
@Command(description = MessagesConstants.WELCOME_MESSAGE, name = "cobigen", aliases = { "cg" },
    mixinStandardHelpOptions = true, version = {"CobiGen CLI 1.0","Java plug-in 2.1.0","OpenAPI plug-in	2.3.0","HTML plug-in 2.0.1"}, subcommands = { GenerateCommand.class })
public class CobiGenCommand implements Runnable {

    @Override
    public void run() {

        // Nothing to do here, this is the master command
    }

}
