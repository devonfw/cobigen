package com.capgemini.cobigen.eclipse.wizard.generate.control;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.capgemini.cobigen.eclipse.generator.CobiGenWrapper;
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
     * @param cobigenWrapper
     *            with which to generate the contents
     * @param templatesToBeGenerated
     *            {@link Set} of templates to be generated
     * @author mbrunnli (12.03.2013)
     */
    public GenerateSelectionProcess(CobiGenWrapper cobigenWrapper, List<TemplateTo> templatesToBeGenerated) {

        super(cobigenWrapper, templatesToBeGenerated);
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (12.03.2013)
     */
    @Override
    protected boolean performGeneration(IProgressMonitor monitor) throws Exception {

        final IProject proj = cobigenWrapper.getGenerationTargetProject();
        if (proj != null) {
            monitor.beginTask("GenerateHandler files...", templatesToBeGenerated.size());
            for (TemplateTo template : templatesToBeGenerated) {
                if (template.getMergeStrategy() == null) {
                    cobigenWrapper.generate(template, true);
                } else {
                    cobigenWrapper.generate(template, false);
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
