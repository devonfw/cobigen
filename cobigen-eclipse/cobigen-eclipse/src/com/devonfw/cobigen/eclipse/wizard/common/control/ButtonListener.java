package com.devonfw.cobigen.eclipse.wizard.common.control;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.devonfw.cobigen.eclipse.wizard.common.SelectFilesPage;

/**
 * The {@link ButtonListener} provides the logic for buttons to be clicked in the wizard (currently only for the
 * {@link SelectFilesPage})
 */
public class ButtonListener implements Listener {

  /** {@link SelectFilesPage} reference */
  private SelectFilesPage page;

  /** Current state of the resource tree viewer */
  private boolean isCustomizable;

  /**
   * Creates a new {@link ButtonListener} instance and sets the internal state to initiallyCustomizable
   *
   * @param initiallyCustomizable initial state for the listener (should be consistent to the UI initial state)
   * @param page {@link SelectFilesPage} reference
   */
  public ButtonListener(boolean initiallyCustomizable, SelectFilesPage page) {

    this.isCustomizable = initiallyCustomizable;
    this.page = page;
  }

  @Override
  public void handleEvent(Event event) {

    this.isCustomizable = !this.isCustomizable;
    this.page.buildResourceTreeViewer(this.isCustomizable);

    if (event.widget instanceof Button) {
      if (this.isCustomizable) {
        ((Button) event.widget).setText("Hide");
      } else {
        ((Button) event.widget).setText("Customize");
      }
    }
  }

}
