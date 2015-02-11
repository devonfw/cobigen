package com.capgemini.cobigen.maven.validation;

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

import com.capgemini.cobigen.extension.to.IncrementTo;
import com.capgemini.cobigen.extension.to.TemplateTo;

/**
 *
 * @author mbrunnli (09.02.2015)
 */
public class InputValidator {

    /**
     * Maven {@link Log}
     */
    private Log LOG;

    /**
     *
     * @param log
     * @author mbrunnli (11.02.2015)
     */
    public InputValidator(Log log) {
        LOG = log;
    }

    /**
     *
     * @param templates
     * @param templateIdsToBeGenerated
     * @author mbrunnli (11.02.2015)
     */
    public void validateTemplateInputs(List<TemplateTo> templates, List<String> templateIdsToBeGenerated) {
        List<String> templateIds = new LinkedList<>(templateIdsToBeGenerated);
        for (TemplateTo template : templates) {
            if (templateIds.contains(template.getId())) {
                templateIds.remove(template.getId());
            }
        }
        if (!templateIds.isEmpty()) {
            LOG.error("No template(s) with the given id(s) '" + templateIds + "' found.");
        }
    }

    /**
     *
     * @param increments
     * @param templateIdsToBeGenerated
     * @author mbrunnli (11.02.2015)
     */
    public void validateIncrementInputs(List<IncrementTo> increments, List<String> templateIdsToBeGenerated) {
        List<String> incrementIds = new LinkedList<>(templateIdsToBeGenerated);
        for (IncrementTo increment : increments) {
            if (incrementIds.contains(increment.getId())) {
                incrementIds.remove(increment.getId());
            }
        }
        if (!incrementIds.isEmpty()) {
            LOG.error("No increment(s) with the given id(s) '" + incrementIds + "' found.");
        }
    }
}
