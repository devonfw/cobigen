package com.capgemini.cobigen.eclipse.wizard.generate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.eclipse.common.constants.external.CobiGenDialogConstants;
import com.capgemini.cobigen.eclipse.generator.CobiGenWrapper;
import com.capgemini.cobigen.eclipse.generator.java.JavaGeneratorWrapper;
import com.capgemini.cobigen.eclipse.wizard.generate.common.AbstractGenerateWizard;
import com.capgemini.cobigen.eclipse.wizard.generate.control.GenerateBatchSelectionJob;
import com.google.common.collect.Lists;

/**
 * Wizard for running the generation in batch mode
 *
 * @author trippl (22.04.2013)
 */
public class GenerateBatchWizard extends AbstractGenerateWizard {

    /**
     * The selected {@link IType}s
     */
    private List<IType> inputTypes;

    /**
     * {@link IPackageFragment}, which should be the input for the generation process
     */
    private IPackageFragment container;

    /**
     * Assigning logger to GenerateBatchWizard
     */
    private static final Logger LOG = LoggerFactory.getLogger(GenerateBatchWizard.class);

    /**
     * The {@link GenerateWizard} guides through the generation process
     *
     * @param generator
     *            to be used for generation
     */
    public GenerateBatchWizard(CobiGenWrapper generator) {

        super(generator);
        initializeWizard();
        setWindowTitle(CobiGenDialogConstants.GenerateWizard.DIALOG_TITLE_BATCH);
    }

    /**
     * Initializes the {@link JavaGeneratorWrapper}
     * @author trippl (22.04.2013)
     */
    @Override
    protected void initializeWizard() {
        super.initializeWizard();
        page1.setMessage(
            "You are running a generation in batch mode!\n"
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
     *
     * @param dialog
     *            {@link ProgressMonitorDialog} which should be used for reporting the progress
     */
    @Override
    protected void generateContents(ProgressMonitorDialog dialog) {

        List<TemplateTo> templatesToBeGenerated = page1.getTemplatesToBeGenerated();
        List<String> templateIds = Lists.newLinkedList();
        for (TemplateTo template : templatesToBeGenerated) {
            templateIds.add(template.getId());
        }

        GenerateBatchSelectionJob job;
        if (container == null) {
            job = new GenerateBatchSelectionJob(cobigenWrapper, cobigenWrapper.getTemplates(templateIds), inputTypes);
        } else {
            job = new GenerateBatchSelectionJob(cobigenWrapper, cobigenWrapper.getTemplates(templateIds), container);
        }
        try {
            dialog.run(true, false, job);
        } catch (InvocationTargetException e) {
            LOG.error("An internal error occured while invoking the generation batch job.", e);
        } catch (InterruptedException e) {
            LOG.warn("The working thread doing the generation job has been interrupted.", e);
        }
    }
}
