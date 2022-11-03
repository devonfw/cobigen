package com.devonfw.cobigen.eclipse.workbenchcontrol.handler;

import java.util.concurrent.TimeUnit;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.eclipse.common.tools.ResourcesPluginUtil;
import com.devonfw.cobigen.gui.AppLauncher;

/**
 * TODO nneuhaus This type ...
 *
 */
public class ManageTemplateSetsHandler extends AbstractHandler {

  /**
   * Assigning logger to ManageTemplateSetsHandler
   */
  private static final Logger LOG = LoggerFactory.getLogger(ManageTemplateSetsHandler.class);

  public AppLauncher launcher;

  /**
   * Location of workspace root
   */
  IPath ws = ResourcesPluginUtil.getWorkspaceLocation();

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {

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

    return null;

  }

}
