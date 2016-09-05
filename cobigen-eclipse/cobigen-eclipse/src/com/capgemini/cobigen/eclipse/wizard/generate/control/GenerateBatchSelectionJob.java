package com.capgemini.cobigen.eclipse.wizard.generate.control;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.capgemini.cobigen.api.to.GenerationReportTo;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.eclipse.generator.CobiGenWrapper;
import com.capgemini.cobigen.impl.exceptions.CobiGenRuntimeException;

/**
 * Running this process as issued in {@link IRunnableWithProgress} performs the generation tasks of the
 * generation wizard for each selected pojo.
 *
 * @author trippl (22.04.2013)
 */
public class GenerateBatchSelectionJob extends AbstractGenerateSelectionJob {

    /**
     * {@link List} containing the types of the selected inputs
     */
    private List<IType> inputTypes;

    /**
     * {@link IPackageFragment}, which should be the input for the generation process
     */
    private IPackageFragment container;

    /**
     * Creates a new process ({@link IRunnableWithProgress}) for performing the generation tasks
     *
     * @param javaGeneratorWrapper
     *            with which to generate the contents
     * @param templatesToBeGenerated
     *            {@link Set} of template ids to be generated
     * @param inputTypes
     *            {@link List} containing the types of the selected pojos
     *
     * @author trippl (22.04.2013)
     */
    public GenerateBatchSelectionJob(CobiGenWrapper javaGeneratorWrapper,
        List<TemplateTo> templatesToBeGenerated, List<IType> inputTypes) {

        super(javaGeneratorWrapper, templatesToBeGenerated);
        this.inputTypes = inputTypes;
    }

    /**
     * Creates a new process ({@link IRunnableWithProgress}) for performing the generation tasks
     *
     * @param javaGeneratorWrapper
     *            with which to generate the contents
     * @param templatesToBeGenerated
     *            {@link Set} of template ids to be generated
     * @param container
     *            selected {@link IPackageFragment} for the generation
     * @author mbrunnli (04.06.2014)
     */
    public GenerateBatchSelectionJob(CobiGenWrapper javaGeneratorWrapper,
        List<TemplateTo> templatesToBeGenerated, IPackageFragment container) {

        super(javaGeneratorWrapper, templatesToBeGenerated);
        this.container = container;
    }

    @Override
    protected GenerationReportTo performGeneration(IProgressMonitor monitor) throws Exception {
        LOG.info("Perform generation of contents in batch mode...");

        GenerationReportTo result;
        if (inputTypes != null && inputTypes.size() == 0 && container == null) {
            LOG.warn("Generation finished: No inputs provided!");
            result = new GenerationReportTo();
            result.addWarning("No input provided!");
            return result;
        }

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
