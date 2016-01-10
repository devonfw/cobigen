package com.capgemini.cobigen.eclipse.wizard.generate;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.eclipse.generator.CobiGenWrapper;
import com.capgemini.cobigen.eclipse.generator.java.JavaGeneratorWrapper;
import com.capgemini.cobigen.eclipse.wizard.common.SelectFilesPage;
import com.capgemini.cobigen.eclipse.wizard.generate.common.AbstractGenerateWizard;
import com.capgemini.cobigen.eclipse.wizard.generate.common.SelectAttributesPage;
import com.capgemini.cobigen.eclipse.wizard.generate.control.GenerateSelectionJob;

/**
 * The {@link SelectFilesPage} guides through the generation process
 *
 * @author mbrunnli (15.02.2013)
 */
public class GenerateWizard extends AbstractGenerateWizard {

    /**
     * The second page of the Wizard
     */
    private SelectAttributesPage page2;

    /**
     * Assigning logger to GenerateWizard
     */
    private static final Logger LOG = LoggerFactory.getLogger(GenerateWizard.class);

    /**
     * The {@link GenerateWizard} guides through the generation process
     *
     * @param generator
     *            {@link CobiGenWrapper} to be used for generation
     * @author mbrunnli (15.02.2013)
     */
    public GenerateWizard(CobiGenWrapper generator) {

        super(generator);
        setWindowTitle("CobiGen");
        initializeWizard();
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (18.02.2013)
     */
    @Override
    protected void initializeWizard() {

        super.initializeWizard();

        if (cobigenWrapper instanceof JavaGeneratorWrapper) {
            page2 =
                new SelectAttributesPage(
                    ((JavaGeneratorWrapper) cobigenWrapper).getAttributesToTypeMapOfFirstInput());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (15.02.2013)
     */
    @Override
    public void addPages() {

        addPage(page1);
        if (cobigenWrapper instanceof JavaGeneratorWrapper) {
            addPage(page2);
        }
    }

    /**
     * Generates the contents to be generated and reports the progress to the user
     *
     * @param dialog
     *            {@link ProgressMonitorDialog} which should be used for reporting the progress
     * @author mbrunnli (25.02.2013)
     */
    @Override
    protected void generateContents(ProgressMonitorDialog dialog) {

        if (cobigenWrapper instanceof JavaGeneratorWrapper) {
            for (String attr : page2.getUncheckedAttributes()) {
                ((JavaGeneratorWrapper) cobigenWrapper).removeFieldFromModel(attr);
            }
        }

        LOG.info("Start generation process job...");
        GenerateSelectionJob job =
            new GenerateSelectionJob(cobigenWrapper, page1.getTemplatesToBeGenerated());
        try {
            dialog.run(true, false, job);
        } catch (InvocationTargetException e) {
            LOG.error("An internal error occured while invoking the generation job.", e);
        } catch (InterruptedException e) {
            LOG.warn("The working thread doing the generation job has been interrupted.", e);
        }
    }

}
