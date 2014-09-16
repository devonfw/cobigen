/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.eclipse.wizard.generate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.common.tools.JavaModelUtil;
import com.capgemini.cobigen.eclipse.generator.java.JavaGeneratorWrapper;
import com.capgemini.cobigen.eclipse.wizard.common.SelectFilesPage;
import com.capgemini.cobigen.eclipse.wizard.common.model.HierarchicalTreeOperator;
import com.capgemini.cobigen.eclipse.wizard.generate.common.AbstractGenerateWizard;
import com.capgemini.cobigen.eclipse.wizard.generate.control.GenerateBatchSelectionProcess;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.exceptions.UnknownContextVariableException;
import com.capgemini.cobigen.exceptions.UnknownExpressionException;
import com.capgemini.cobigen.exceptions.UnknownTemplateException;

/**
 * Wizard for running the generation in batch mode
 * 
 * @author trippl (22.04.2013)
 */
public class GenerateBatchWizard extends AbstractGenerateWizard {

    /**
     * The selected {@link IType}s
     */
    private List<IType> inputTypes = new ArrayList<IType>();

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
        g = new JavaGeneratorWrapper();
        loadInputTypes(selection);
        setWindowTitle("CobiGen (batch mode)");
        initializeWizard(inputTypes.get(0));
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
     * @author trippl (22.04.2013)
     */
    @Override
    protected void initializeWizard(IType inputType) throws IOException, InvalidConfigurationException,
        UnknownTemplateException, UnknownContextVariableException, UnknownExpressionException, CoreException,
        ClassNotFoundException, GeneratorProjectNotExistentException {

        super.initializeWizard(inputType);

        page1 = new SelectFilesPage(g, true);
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
     * @throws JavaModelException
     *             if an internal eclipse exception occurs
     * @author trippl (22.04.2013)
     */
    private void loadInputTypes(IStructuredSelection selection) throws JavaModelException {

        Iterator<?> it = selection.iterator();

        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof ICompilationUnit) {
                IType type = JavaModelUtil.getJavaClassType((ICompilationUnit) next);
                inputTypes.add(type);
            } else if (next instanceof IPackageFragment) {
                container = (IPackageFragment) next;
                g.setInputPackage((IPackageFragment) next);
                List<String> triggerIds = g.getMatchingTriggerIds();
                getInputTypesRecursively((IPackageFragment) next, triggerIds);
            }
        }
    }

    /**
     * Returns retrieves all {@link IType}s under the given {@link IPackageFragment} recursively and saves
     * them to the {@link #inputTypes} list
     * 
     * @param next
     *            {@link IPackageFragment} to search in
     * @param triggerIds
     *            which have been seen in former selected items
     * @throws JavaModelException
     * @author mbrunnli (03.06.2014)
     */
    private void getInputTypesRecursively(IPackageFragment next, List<String> triggerIds)
        throws JavaModelException {

        List<IPackageFragment> children = HierarchicalTreeOperator.getPackageChildren(next);

        for (ICompilationUnit cu : next.getCompilationUnits()) {
            if (inputTypes.size() > 0) break;
            IType type = JavaModelUtil.getJavaClassType(cu);
            try {
                g.setInputType(type);
                List<String> matchingTriggerIds = g.getMatchingTriggerIds();
                if (triggerIds.containsAll(matchingTriggerIds) && matchingTriggerIds.containsAll(triggerIds)) {
                    inputTypes.add(type);
                }
            } catch (CoreException e) {
                LOG.error("An internal exception occured", e);
            } catch (ClassNotFoundException e) {
                LOG.error("An internal exception occured", e);
            } catch (IOException e) {
                LOG.error("An internal exception occured", e);
            }
        }
        // only fetch first one for visualization due to performance issues
        if (inputTypes.size() == 0) for (IPackageFragment frag : children) {
            getInputTypesRecursively(frag, triggerIds);
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

        GenerateBatchSelectionProcess job;
        if (container == null) {
            job =
                new GenerateBatchSelectionProcess(getShell(), g, page1.getTemplatesToBeGenerated(),
                    inputTypes);
        } else {
            job =
                new GenerateBatchSelectionProcess(getShell(), g, page1.getTemplatesToBeGenerated(), container);
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
