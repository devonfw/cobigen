package com.capgemini.cobigen.eclipse.wizard.generate.control;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.capgemini.cobigen.api.to.GenerationReportTo;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.eclipse.generator.CobiGenWrapper;

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
        return cobigenWrapper.generate(templatesToBeGenerated, monitor);
    }

}
