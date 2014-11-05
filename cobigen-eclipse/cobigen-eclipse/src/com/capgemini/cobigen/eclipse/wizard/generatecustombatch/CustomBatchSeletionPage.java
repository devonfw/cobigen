package com.capgemini.cobigen.eclipse.wizard.generatecustombatch;

import java.util.Set;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Selection page for custom batches in the custom batch generation wizard
 * @author mbrunnli (20.03.2014)
 */
public class CustomBatchSeletionPage extends WizardPage implements SelectionListener {

    /**
     * Current selected batch id
     */
    private String selectedBatchId;

    /**
     * All available custom batches
     */
    private Set<String> availableCustomBatches;

    /**
     * Available batch SWT list
     */
    private org.eclipse.swt.widgets.List availableBatchList;

    /**
     * Creates a new batch selection wizard page
     * @param pageName
     *            name of the wizard page
     * @param availableCustomBatches
     *            {@link Set} of available custom batches
     * @author mbrunnli (20.03.2014)
     */
    protected CustomBatchSeletionPage(String pageName, Set<String> availableCustomBatches) {
        super(pageName);
        this.availableCustomBatches = availableCustomBatches;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (20.03.2014)
     */
    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.FILL);
        container.setLayout(new GridLayout());

        availableBatchList = new org.eclipse.swt.widgets.List(container, SWT.BORDER | SWT.V_SCROLL);
        for (String item : availableCustomBatches) {
            availableBatchList.add(item);
        }

        availableBatchList.addSelectionListener(this);

        setControl(container);
    }

    /**
     * Returns the selected batch id
     * @return the selected batch id
     * @author mbrunnli (21.03.2014)
     */
    public String getSelectedBatchId() {
        return selectedBatchId;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (21.03.2014)
     */
    @Override
    public void widgetSelected(SelectionEvent e) {
        if (availableBatchList.getSelection().length > 0) {
            selectedBatchId = availableBatchList.getSelection()[0];
            setPageComplete(true);
        } else {
            selectedBatchId = null;
            setPageComplete(false);
        }
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (21.03.2014)
     */
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }

}
