/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.eclipse.wizard.generate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.common.tools.JavaModelUtil;
import com.capgemini.cobigen.eclipse.generator.java.JavaGeneratorWrapper;
import com.capgemini.cobigen.eclipse.wizard.generate.common.AbstractGenerateWizard;
import com.capgemini.cobigen.eclipse.wizard.generate.control.GenerateBatchSelectionProcess;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.exceptions.UnknownContextVariableException;
import com.capgemini.cobigen.exceptions.UnknownExpressionException;
import com.capgemini.cobigen.exceptions.UnknownTemplateException;
import com.capgemini.cobigen.extension.to.TemplateTo;
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
     * @param selection
     *            the selection that stores the types which should be the source of all information retrieved
     *            for the code generation
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
     * @author trippl (22.04.2013)
     */
    public GenerateBatchWizard(IStructuredSelection selection) throws CoreException,
        UnknownTemplateException, UnknownContextVariableException, IOException,
        InvalidConfigurationException, UnknownExpressionException, ClassNotFoundException,
        GeneratorProjectNotExistentException {

        super();
        javaGeneratorWrapper = new JavaGeneratorWrapper();
        extractInput(selection);
        initializeWizard();
        setWindowTitle("CobiGen (batch mode)");
    }

    /**
     * Initializes the {@link JavaGeneratorWrapper}
     *
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
     * @author trippl (22.04.2013)
     */
    private void initializeWizard() throws UnknownContextVariableException, UnknownExpressionException,
        InvalidConfigurationException, UnknownTemplateException, ClassNotFoundException, IOException,
        CoreException, GeneratorProjectNotExistentException {

        if (inputTypes != null) {
            super.initializeWizard(inputTypes);
        } else {
            super.initializeWizard(container);
        }

        page1
            .setMessage(
                "You are running a generation in batch mode!\n"
                    + "The shown target files are based on the first input of your selection. "
                    + "All target files selected will be created/merged/overwritten analogue for every input of your selection.",
                IMessageProvider.WARNING);
    }

    /**
     * Loads the {@link IType}s of the items within selection.
     *
     * @param selection
     *            to load the input types from
     * @throws CoreException
     *             if an internal eclipse exception occurs
     * @throws MalformedURLException
     *             might occur while IFile to file transformation or vice versa
     * @throws ClassNotFoundException
     *             might occur if
     * @author trippl (22.04.2013)
     */
    private void extractInput(IStructuredSelection selection) throws ClassNotFoundException,
        MalformedURLException, CoreException {
        Iterator<?> it = selection.iterator();

        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof ICompilationUnit) {
                IType type = JavaModelUtil.getJavaClassType((ICompilationUnit) next);
                if (inputTypes == null) {
                    inputTypes = new ArrayList<>();
                }
                inputTypes.add(type);
            } else if (next instanceof IPackageFragment) {
                if (container != null) {
                    throw new NotImplementedException(
                        "If you see this message please contact one of the developers of CobiGen.");
                }
                container = (IPackageFragment) next;
            }
        }
        if (inputTypes != null) {
            javaGeneratorWrapper.setInputTypes(inputTypes);
        } else if (container != null) {
            javaGeneratorWrapper.setInputPackage(container);
        } else if (container != null && inputTypes != null || container == null && inputTypes == null) {
            throw new NotImplementedException(
                "If you see this message please contact one of the developers of CobiGen.");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author trippl (22.04.2013)
     */
    @Override
    public void addPages() {

        addPage(page1);
    }

    /**
     * Generates the contents to be generated and reports the progress to the user
     *
     * @param dialog
     *            {@link ProgressMonitorDialog} which should be used for reporting the progress
     * @author trippl (22.04.2013)
     */
    @Override
    protected void generateContents(ProgressMonitorDialog dialog) {

        List<TemplateTo> templatesToBeGenerated = page1.getTemplatesToBeGenerated();
        List<String> templateIds = Lists.newLinkedList();
        for (TemplateTo template : templatesToBeGenerated) {
            templateIds.add(template.getId());
        }

        GenerateBatchSelectionProcess job;
        if (container == null) {
            job =
                new GenerateBatchSelectionProcess(getShell(), javaGeneratorWrapper,
                    javaGeneratorWrapper.getTemplates(templateIds), inputTypes);
        } else {
            job =
                new GenerateBatchSelectionProcess(getShell(), javaGeneratorWrapper,
                    javaGeneratorWrapper.getTemplates(templateIds), container);
        }
        try {
            dialog.run(false, false, job);
        } catch (InvocationTargetException e) {
            LOG.error("An internal error occured while invoking the generation batch job.", e);
        } catch (InterruptedException e) {
            LOG.warn("The working thread doing the generation job has been interrupted.", e);
        }
    }
}
