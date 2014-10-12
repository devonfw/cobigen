/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.eclipse.wizard.generate.common;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.CobiGen;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.generator.java.JavaGeneratorWrapper;
import com.capgemini.cobigen.eclipse.wizard.common.SelectFilesPage;
import com.capgemini.cobigen.eclipse.wizard.common.model.stubs.IJavaElementStub;
import com.capgemini.cobigen.eclipse.wizard.common.model.stubs.IResourceStub;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.exceptions.UnknownContextVariableException;
import com.capgemini.cobigen.exceptions.UnknownExpressionException;
import com.capgemini.cobigen.exceptions.UnknownTemplateException;
import com.capgemini.cobigen.extension.to.TemplateTo;

/**
 * The {@link SelectFilesPage} guides through the generation process
 *
 * @author mbrunnli (15.02.2013)
 */
public abstract class AbstractGenerateWizard extends Wizard {

    /**
     * The first page of the Wizard
     */
    protected SelectFilesPage page1;

    /**
     * Wrapper for the {@link CobiGen}
     */
    protected JavaGeneratorWrapper javaGeneratorWrapper;

    /**
     * Assigning logger to AbstractGenerateWizard
     */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractGenerateWizard.class);

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
    protected void initializeWizard(IType inputType) throws IOException, InvalidConfigurationException,
        UnknownTemplateException, UnknownContextVariableException, UnknownExpressionException, CoreException,
        ClassNotFoundException, GeneratorProjectNotExistentException {

        this.javaGeneratorWrapper = new JavaGeneratorWrapper(inputType);
        this.javaGeneratorWrapper.setGenerationTargetProject(inputType.getJavaProject().getProject());
    }

    /**
     * Returns the set of all destination paths for the templates
     *
     * @return the set of all destination paths for the templates
     * @author mbrunnli (11.03.2013)
     */
    public Set<String> getAllGenerationPaths() {

        Set<String> paths = new HashSet<>();
        for (TemplateTo tmp : this.javaGeneratorWrapper.getAllTemplates()) {
            paths.add(tmp.getDestinationPath());
        }
        return paths;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (15.02.2013)
     */
    @Override
    public boolean performFinish() {

        ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());

        if (!userConfirmed()) {
            return false;
        }

        this.page1.saveSelection();
        generateContents(dialog);

        return true;
    }

    /**
     * Generates the contents to be generated and reports the progress to the user
     *
     * @param dialog
     *            {@link ProgressMonitorDialog} which should be used for reporting the progress
     * @author mbrunnli (11.04.2014)
     */
    protected abstract void generateContents(ProgressMonitorDialog dialog);

    /**
     * Checks whether files will be overwritten by the generation process and whether the user is aware of
     * this behavior and confirms it
     *
     * @return true, if the user confirms the changes beeing made or no files will be overwritten<br>
     *         false, otherwise
     * @author mbrunnli (18.02.2013)
     */
    private boolean userConfirmed() {

        List<Object> diff = this.page1.getSelectedResources();

        // Delete simulated resources
        Iterator<Object> it = diff.iterator();
        while (it.hasNext()) {
            Object r = it.next();
            if (r instanceof IResourceStub || r instanceof IJavaElementStub) {
                it.remove();
            }
        }
        // Delete mergable files
        Set<IFile> mergableFiles = this.javaGeneratorWrapper.getMergeableFiles();
        it = diff.iterator();
        while (it.hasNext()) {
            Object r = it.next();
            Object iResource = null;
            if (r instanceof IJavaElement) {
                try {
                    iResource = ((IJavaElement) r).getCorrespondingResource();
                } catch (JavaModelException e) {
                    LOG.error(
                        "An internal java model exception occured while retrieving the java elements '{}' corresponding resource.",
                        e);
                }
            } else {
                iResource = r;
            }
            if (iResource != null && mergableFiles.contains(iResource)) {
                it.remove();
            }
        }

        if (!diff.isEmpty()) {
            MessageDialog dialog =
                new MessageDialog(
                    getShell(),
                    "Warning!",
                    null,
                    "You have selected resources that are already existent and will be overwritten when proceeding.\nDo you really want to replace the existing files by newly generated ones?",
                    MessageDialog.WARNING, new String[] { "Yes", "No" }, 1);
            int result = dialog.open();
            if (result == 1 || result == SWT.DEFAULT) {
                return false;
            }
        }
        return true;
    }
}
