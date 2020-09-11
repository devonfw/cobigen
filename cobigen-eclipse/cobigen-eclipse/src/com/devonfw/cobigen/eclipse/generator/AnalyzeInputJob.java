package com.devonfw.cobigen.eclipse.generator;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.eclipse.common.AbstractCobiGenJob;
import com.devonfw.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.google.common.collect.Lists;

/**
 * Job implementation for processing long running operations of CobiGen off the ui thread.
 */
public class AnalyzeInputJob extends AbstractCobiGenJob {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(AnalyzeInputJob.class);

    /**
     * CobiGen API
     */
    private CobiGen cobigen;

    /**
     * input objects to be analyzed
     */
    private List<Object> inputs;

    /**
     * Determined matching templates
     */
    private List<TemplateTo> resultMatchingTemplates = Lists.newArrayList();

    /**
     * States whether the generation input is a single non container input.
     */
    private boolean resultSingleNonContainerInput;

    /**
     * Creates a new job for analyzing the inputs regarding matching templates etc.
     * @param cobigen
     *            CobiGen instance
     * @param inputs
     *            input objects to be analyzed
     */
    public AnalyzeInputJob(CobiGen cobigen, List<Object> inputs) {
        this.cobigen = cobigen;
        this.inputs = inputs;
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());

        try {
            LOG.info("Determine matching templates...");
            SubMonitor subMonitor = SubMonitor.convert(monitor, inputs.size() + 1);
            subMonitor.beginTask("Determine matching templates...", inputs.size() + 1);

            for (Object input : inputs) {
                subMonitor.split(1);
                resultMatchingTemplates.addAll(cobigen.getMatchingTemplates(input));
            }
            LOG.info("Determine if input is container...");
            resultSingleNonContainerInput = inputs.size() == 1 && !cobigen.combinesMultipleInputs(inputs.get(0));
            monitor.done();
        } catch (RuntimeException e) {
            occurredException = e;
        }

        MDC.remove(InfrastructureConstants.CORRELATION_ID);
    }

    /**
     * Returns the field 'resultMatchingTemplates'
     * @return value of resultMatchingTemplates
     */
    public List<TemplateTo> getResultMatchingTemplates() {
        return resultMatchingTemplates;
    }

    /**
     * Returns the field 'resultSingleNonContainerInput'
     * @return value of resultSingleNonContainerInput
     */
    public boolean isResultSingleNonContainerInput() {
        return resultSingleNonContainerInput;
    }

}
