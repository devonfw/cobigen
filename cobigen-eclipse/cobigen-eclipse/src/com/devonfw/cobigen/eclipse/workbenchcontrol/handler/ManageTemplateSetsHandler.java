package com.devonfw.cobigen.eclipse.workbenchcontrol.handler;

import java.util.concurrent.TimeUnit;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.gui.AppLauncher;

/**
 * Handler for the Package-Explorer Event
 *
 */
public class ManageTemplateSetsHandler extends AbstractHandler {

  /**
   * Assigning logger to ManageTemplateSetsHandler
   */
  private static final Logger LOG = LoggerFactory.getLogger(ManageTemplateSetsHandler.class);

  /**
   * Launcher for GUI
   */
  public AppLauncher launcher;

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {

    System.out.println("You just called the GUI");
    this.launcher = new AppLauncher();
    try {
      wait(1337);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    while (this.launcher.app.window.isShowing()) {
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    return null;

  }

}
