package com.capgemini.cobigen.api.to;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Generation report.
 */
public class GenerationReportTo {

    /** Error messages */
    private List<String> errorMessages = Lists.newArrayList();

    /** Warnings */
    private List<String> warnings = Lists.newArrayList();

    /**
     * Adds a new error message to the report.
     * @param message
     *            error message.
     */
    public void addErrorMessage(String message) {
        errorMessages.add(message);
    }

    /**
     * Adds a new warning to the report.
     * @param message
     *            warning message.
     */
    public void addWarning(String message) {
        warnings.add(message);
    }

    /**
     * Returns all error messages occurred.
     * @return {@link List} of error messsages.
     */
    public List<String> getErrorMessages() {
        return errorMessages;
    }

    /**
     * Returns all warnings created during generation.
     * @return {@link List} of warnings.
     */
    public List<String> getWarnings() {
        return warnings;
    }

    /**
     * Returns whether the generation could be performed successfully. Equivalent to
     * {@code getErrorMessages().isEmpty()}
     * @return <code>true</code> if no errors occurred, <code>false</code> otherwise
     */
    public boolean wasSuccessfull() {
        return errorMessages.isEmpty();
    }
}
