package com.devonfw.cobigen.eclipse.wizard.generate;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.eclipse.common.constants.external.CobiGenDialogConstants;
import com.devonfw.cobigen.eclipse.generator.CobiGenWrapper;
import com.devonfw.cobigen.eclipse.wizard.common.SelectFilesPage;
import com.devonfw.cobigen.eclipse.wizard.generate.common.AbstractGenerateWizard;
import com.devonfw.cobigen.eclipse.wizard.generate.control.GenerateSelectionJob;

/**
 * Wizard for running the generation in batch mode
 */
public class GenerateBatchWizard extends AbstractGenerateWizard {

    /** Assigning logger to GenerateBatchWizard */
    private static final Logger LOG = LoggerFactory.getLogger(GenerateBatchWizard.class);

    /**
     * The {@link GenerateWizard} guides through the generation process
     *
     * @param generator
     *            to be used for generation
     * @param monitor
     *            to track progress
     */
    public GenerateBatchWizard(CobiGenWrapper generator, IProgressMonitor monitor) {
        super(generator, monitor);
        initializeWizard();
        setWindowTitle(CobiGenDialogConstants.GenerateWizard.DIALOG_TITLE_BATCH);
    }

    /**
     * Initializes the wizard pages
     */
    private void initializeWizard() {
        page1 = new SelectFilesPage(cobigenWrapper, true);
        page1.setMessage("You are running a generation in batch mode!\n"
            + "The shown target files are based on the first input of your selection. "
            + "All target files selected will be created/merged/overwritten analogue for every input of your selection.",
            IMessageProvider.WARNING);
    }

    @Override
    public void addPages() {
        addPage(page1);
    }

    /**
     * Generates the contents to be generated and reports the progress to the user
     * @param dialog
     *            {@link ProgressMonitorDialog} which should be used for reporting the progress
     */
    @Override
    protected void generateContents(ProgressMonitorDialog dialog) {

        LOG.info("Start generation process job...");
        GenerateSelectionJob job = new GenerateSelectionJob(cobigenWrapper, page1.getTemplatesToBeGenerated());
        try {
            dialog.run(true, false, job);
        } catch (InvocationTargetException e) {
            LOG.error("An internal error occured while invoking the generation job.", e);
        } catch (InterruptedException e) {
            LOG.warn("The working thread doing the generation job has been interrupted.", e);
        }
    }
}
