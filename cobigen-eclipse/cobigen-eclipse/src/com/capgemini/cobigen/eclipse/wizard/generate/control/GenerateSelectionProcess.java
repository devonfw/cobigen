/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.eclipse.wizard.generate.control;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import com.capgemini.cobigen.eclipse.generator.java.JavaGeneratorWrapper;
import com.capgemini.cobigen.extension.to.TemplateTo;

/**
 * Running this this process as issued in {@link IRunnableWithProgress} performs the generation tasks of the
 * generation wizard
 *
 * @author mbrunnli (12.03.2013)
 */
public class GenerateSelectionProcess extends AbstractGenerateSelectionProcess {

    /**
     * Creates a new process ({@link IRunnableWithProgress}) for performing the generation tasks
     *
     * @param shell
     *            on which to display error messages
     * @param javaGeneratorWrapper
     *            with which to generate the contents
     * @param templatesToBeGenerated
     *            {@link Set} of templates to be generated
     * @author mbrunnli (12.03.2013)
     */
    public GenerateSelectionProcess(Shell shell, JavaGeneratorWrapper javaGeneratorWrapper,
        List<TemplateTo> templatesToBeGenerated) {

        super(shell, javaGeneratorWrapper, templatesToBeGenerated);
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (12.03.2013)
     */
    @Override
    protected boolean performGeneration(IProgressMonitor monitor) throws Exception {

        final IProject proj = this.javaGeneratorWrapper.getGenerationTargetProject();
        if (proj != null) {
            monitor.beginTask("Generate files...", this.templatesToBeGenerated.size());
            for (TemplateTo template : this.templatesToBeGenerated) {
                if (template.getMergeStrategy() == null) {
                    this.javaGeneratorWrapper.generate(template, true);
                } else {
                    this.javaGeneratorWrapper.generate(template, false);
                }
                monitor.worked(1);
            }
            proj.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
            return true;
        } else {
            return false;
        }
    }

}
