package com.devonfw.cobigen.cli.commands;

import com.devonfw.cobigen.cli.constants.MessagesConstants;
import com.devonfw.cobigen.cli.utils.CobiGenVersionProvider;

import picocli.CommandLine.Command;

/**
 * This class defines the main CobiGen command
 */

@Command(description = MessagesConstants.WELCOME_MESSAGE, name = "cobigen", aliases = {
"cg" }, mixinStandardHelpOptions = true, subcommands = { GenerateCommand.class, UpdateCommand.class,
AdaptTemplatesCommand.class, ManageCommand.class }, versionProvider = CobiGenVersionProvider.class)
public class CobiGenCommand implements Runnable {

  @Override
  public void run() {

    // Nothing to do here, this is the master command
  }

}
