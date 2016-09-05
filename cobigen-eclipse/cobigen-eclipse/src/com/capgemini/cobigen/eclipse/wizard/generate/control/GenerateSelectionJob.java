package com.capgemini.cobigen.eclipse.wizard.generate.control;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.capgemini.cobigen.api.to.GenerationReportTo;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.eclipse.generator.CobiGenWrapper;
import com.capgemini.cobigen.impl.exceptions.CobiGenRuntimeException;

/**
 * Running this this process as issued in {@link IRunnableWithProgress} performs the generation tasks of the
 * generation wizard
 *
 * @author mbrunnli (12.03.2013)
 */
public class GenerateSelectionJob extends AbstractGenerateSelectionJob {

    /**
     * Creates a new process ({@link IRunnableWithProgress}) for performing the generation tasks
     *
     * @param cobigenWrapper
     *            with which to generate the contents
     * @param templatesToBeGenerated
     *            {@link Set} of templates to be generated
     * @author mbrunnli (12.03.2013)
     */
    public GenerateSelectionJob(CobiGenWrapper cobigenWrapper, List<TemplateTo> templatesToBeGenerated) {

        super(cobigenWrapper, templatesToBeGenerated);
    }

    @Override
    protected GenerationReportTo performGeneration(IProgressMonitor monitor) throws Exception {
        LOG.info("Perform Generation...");

        final IProject proj = cobigenWrapper.getGenerationTargetProject();
        if (proj != null) {
            monitor.beginTask("Generating files...", templatesToBeGenerated.size());

            GenerationReportTo reportSummary = new GenerationReportTo();
            for (TemplateTo template : templatesToBeGenerated) {
                monitor.subTask(template.getId());
                GenerationReportTo report;
                if (template.getMergeStrategy() == null) {
                    report = cobigenWrapper.generate(template, true);
                } else {
                    report = cobigenWrapper.generate(template, false);
                }
                reportSummary.aggregate(report);
                monitor.worked(1);
            }
            proj.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
            monitor.done();
            LOG.info("Generation finished successfully.");
            return reportSummary;
        } else {
            throw new CobiGenRuntimeException("No generation target project configured! This is a Bug!");
        }
    }

}
