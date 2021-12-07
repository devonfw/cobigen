package com.devonfw.cobigen.eclipse.test.common.swtbot;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.waits.ICondition;

/**
 * Condition for {@link SWTBot#waitUntil(ICondition)} to wait until a Java project has been built.
 */
public class HasBeenBuilt extends DefaultCondition {

  /** Java project to wait for finished building. */
  private IJavaProject project;

  /**
   * Creates a new condition waiting until a job is started containing the given string it is job name.
   *
   * @param project Java project to wait for finished building
   */
  public HasBeenBuilt(IJavaProject project) {

    this.project = project;
  }

  @Override
  public boolean test() throws Exception {

    return this.project.hasBuildState();
  }

  @Override
  public String getFailureMessage() {

    return "Java project '" + this.project + "' has not been built in the given amount of time.";
  }

}
