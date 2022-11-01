package com.devonfw.cobigen.cli.commands;

import java.util.concurrent.TimeUnit;

import com.devonfw.cobigen.gui.AppLauncher;

import picocli.CommandLine.Command;

/**
 * This class handles the manage template sets command
 */
@Command(description = "Opens GUI for Template Set Management", name = "manage", aliases = {
"m" }, mixinStandardHelpOptions = true)
public class ManageCommand extends CommandCommons {

  public AppLauncher launcher;

  /**
   * Constructor needed for Picocli
   */
  public ManageCommand() {

    super();
  }

  @Override
  public synchronized Integer doAction() throws Exception {

    System.out.println("You just called the GUI");
    this.launcher = new AppLauncher();
    try {
      wait(1337);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    while (this.launcher.app.window.isShowing()) {
      TimeUnit.SECONDS.sleep(1);
    }

    return 0;
  }

}
