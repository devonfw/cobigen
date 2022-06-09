package com.devonfw.cobigen.eclipse.test.common.swtbot;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.BoolResult;
import org.eclipse.swtbot.swt.finder.utils.internal.Assert;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;

/**
 * Checks whether any shell with one of the given dialog titles is displayed and having focus.
 */
public class AnyShellIsActive extends DefaultCondition {

  /** Dialog titles to search for */
  private String[] titles;

  /**
   * Creates a new condition with the given dialog titles to search for
   *
   * @param titles dialog titles to search for
   */
  public AnyShellIsActive(String... titles) {

    Assert.isNotNull(titles, "The shell text was null");
    Assert.isLegal(titles.length > 0, "The shell text was empty");
    this.titles = titles;
  }

  @Override
  public String getFailureMessage() {

    return "No shell with one of these titles has been activated: '" + this.titles + "'";
  }

  @Override
  public boolean test() throws Exception, CobiGenRuntimeException {

    for (String title : this.titles) {
      try {
        final SWTBotShell shell = this.bot.shell(title);
        return UIThreadRunnable.syncExec(new BoolResult() {
          @Override
          public Boolean run() {

            return shell.widget.isVisible() || shell.widget.isFocusControl();
          }
        });
      } catch (WidgetNotFoundException e) {
      }
    }
    return false;
  }
}
