package com.capgemini.cobigen.eclipse.wizard.common;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.MDC;
import org.eclipse.core.runtime.IProgressMonitor;

import com.capgemini.cobigen.eclipse.common.AbstractCobiGenJob;
import com.capgemini.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.capgemini.cobigen.eclipse.generator.CobiGenWrapper;
import com.capgemini.cobigen.extension.to.IncrementTo;
import com.capgemini.cobigen.extension.to.TemplateTo;
import com.google.common.collect.Lists;

/**
 *
 * @author mbrunnli (Jan 10, 2016)
 */
public class DetermineTemplatesJob extends AbstractCobiGenJob {

    /** {@link CobiGenWrapper} instance */
    private CobiGenWrapper cobiGenWrapper;

    /** Paths to be generated */
    private Set<String> filePathsToBeGenerated;

    /** Selected increments of the generate wizard */
    private Set<IncrementTo> selectedIncrements;

    /** Result templates determined */
    private List<TemplateTo> templates = Lists.newArrayList();

    /**
     * Creates a new job for determining the templates for specific paths to be generated.
     * @param filePathsToBeGenerated
     *            Paths to be generated
     * @param selectedIncrements
     *            of the generate wizard
     * @param cobiGenWrapper
     *            {@link CobiGenWrapper} instance
     * @author mbrunnli (Jan 10, 2016)
     */
    public DetermineTemplatesJob(Set<String> filePathsToBeGenerated, Set<IncrementTo> selectedIncrements,
        CobiGenWrapper cobiGenWrapper) {
        this.filePathsToBeGenerated = filePathsToBeGenerated;
        this.selectedIncrements = selectedIncrements;
        this.cobiGenWrapper = cobiGenWrapper;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (Jan 10, 2016)
     */
    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());

        try {
            monitor.beginTask("Determine templates for selected paths...", filePathsToBeGenerated.size());
            for (String path : filePathsToBeGenerated) {
                monitor.subTask(path);
                monitor.worked(1);
                templates.addAll(cobiGenWrapper.getTemplatesForFilePath(path, selectedIncrements));
            }
        } catch (RuntimeException e) {
            occurredException = e;
        }

        MDC.remove(InfrastructureConstants.CORRELATION_ID);
    }

    /**
     * Returns the determined templates as a result.
     * @return the determined templates as a result.
     * @author mbrunnli (Jan 10, 2016)
     */
    public List<TemplateTo> getResultTemplates() {
        return templates;
    }
}
