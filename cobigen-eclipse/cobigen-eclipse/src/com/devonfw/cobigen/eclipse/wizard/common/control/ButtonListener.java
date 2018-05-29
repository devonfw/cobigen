package com.devonfw.cobigen.eclipse.wizard.common.control;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.devonfw.cobigen.eclipse.wizard.common.SelectFilesPage;

/**
 * The {@link ButtonListener} provides the logic for buttons to be clicked in the wizard (currently only for
 * the {@link SelectFilesPage})
 * @author mbrunnli (12.03.2013)
 */
public class ButtonListener implements Listener {

    /**
     * {@link SelectFilesPage} reference
     */
    private SelectFilesPage page;

    /**
     * Current state of the resource tree viewer
     */
    private boolean isCustomizable;

    /**
     * Creates a new {@link ButtonListener} instance and sets the internal state to initiallyCustomizable
     * @param initiallyCustomizable
     *            initial state for the listener (should be consistent to the UI initial state)
     * @param page
     *            {@link SelectFilesPage} reference
     * @author mbrunnli (12.03.2013)
     */
    public ButtonListener(boolean initiallyCustomizable, SelectFilesPage page) {
        isCustomizable = initiallyCustomizable;
        this.page = page;
    }

    @Override
    public void handleEvent(Event event) {
        isCustomizable = !isCustomizable;
        page.buildResourceTreeViewer(isCustomizable);

        if (event.widget instanceof Button) {
            if (isCustomizable) {
                ((Button) event.widget).setText("Hide");
            } else {
                ((Button) event.widget).setText("Customize");
            }
        }
    }

}
