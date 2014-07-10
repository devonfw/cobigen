/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.eclipse.wizard.generate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.generator.java.JavaGeneratorWrapper;
import com.capgemini.cobigen.eclipse.wizard.common.SelectFilesPage;
import com.capgemini.cobigen.eclipse.wizard.generate.common.AbstractGenerateWizard;
import com.capgemini.cobigen.eclipse.wizard.generate.common.SelectAttributesPage;
import com.capgemini.cobigen.eclipse.wizard.generate.control.GenerateSelectionProcess;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.exceptions.UnknownContextVariableException;
import com.capgemini.cobigen.exceptions.UnknownExpressionException;
import com.capgemini.cobigen.exceptions.UnknownTemplateException;

/**
 * The {@link SelectFilesPage} guides through the generation process
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
     * @param inputType
     *            type which should be the source of all information retrieved for the code generation
     * @throws InvalidConfigurationException
     *             if the given configuration does not match the templates.xsd
     * @throws IOException
     *             if the generator project "RF-Generation" could not be accessed
     * @throws UnknownTemplateException
     *             if there is no template with the given name
     * @throws UnknownContextVariableException
     *             if the destination path contains an undefined context variable
     * @throws UnknownExpressionException
     *             if there is an unknown variable modifier
     * @throws CoreException
     *             if any internal eclipse exception occurs while creating the temporary simulated resources
     *             or the generation configuration project could not be opened
     * @throws ClassNotFoundException
     *             if the given type could not be found by the project {@link ClassLoader}
     * @throws GeneratorProjectNotExistentException
     * @author mbrunnli (15.02.2013)
     */
    public GenerateWizard(IType inputType) throws CoreException, UnknownTemplateException,
        UnknownContextVariableException, IOException, InvalidConfigurationException,
        UnknownExpressionException, ClassNotFoundException, GeneratorProjectNotExistentException {
        super();
        setWindowTitle("CobiGen");
        initializeWizard(inputType);
    }

    /**
     * Initializes the {@link JavaGeneratorWrapper}
     * 
     * @param inputType
     *            type which should be the source of all information retrieved for the code generation
     * @throws InvalidConfigurationException
     *             if the given configuration does not match the templates.xsd
     * @throws IOException
     *             if the generator project "RF-Generation" could not be accessed
     * @throws UnknownTemplateException
     *             if there is no template with the given name
     * @throws UnknownContextVariableException
     *             if the destination path contains an undefined context variable
     * @throws UnknownExpressionException
     *             if there is an unknown variable modifier
     * @throws CoreException
     *             if any internal eclipse exception occurs while creating the temporary simulated resources
     *             or the generation configuration project could not be opened
     * @throws ClassNotFoundException
     *             if the given type could not be found by the project {@link ClassLoader}
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration project "RF-Generation" is not existent
     * @author mbrunnli (18.02.2013)
     */
    @Override
    protected void initializeWizard(IType inputType) throws IOException, InvalidConfigurationException,
        UnknownTemplateException, UnknownContextVariableException, UnknownExpressionException, CoreException,
        ClassNotFoundException, GeneratorProjectNotExistentException {
        super.initializeWizard(inputType);

        page1 = new SelectFilesPage(g, false);
        page2 = new SelectAttributesPage(g.getAttributesToTypeMap());
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (15.02.2013)
     */
    @Override
    public void addPages() {
        addPage(page1);
        addPage(page2);
    }

    /**
     * Generates the contents to be generated and reports the progress to the user
     * @param dialog
     *            {@link ProgressMonitorDialog} which should be used for reporting the progress
     * @author mbrunnli (25.02.2013)
     */
    @Override
    protected void generateContents(ProgressMonitorDialog dialog) {
        final Set<String> pathsToBeGenerated = page1.getFilePathsToBeGenerated();

        for (String attr : page2.getUncheckedAttributes()) {
            g.removeFieldFromModel(attr);
        }

        GenerateSelectionProcess job = new GenerateSelectionProcess(getShell(), g, pathsToBeGenerated);
        try {
            dialog.run(false, false, job);
        } catch (InvocationTargetException e) {
            LOG.error("An internal error occured while invoking the generation job.", e);
        } catch (InterruptedException e) {
            LOG.warn("The working thread doing the generation job has been interrupted.", e);
        }
    }

}
