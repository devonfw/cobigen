package com.capgemini.cobigen.api.to;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Generation report.
 */
public class GenerationReportTo {

    /** Error messages */
    private Set<String> errorMessages = Sets.newTreeSet();

    /** Warnings */
    private Set<String> warnings = Sets.newTreeSet();

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
    public Set<String> getErrorMessages() {
        return errorMessages;
    }

    /**
     * Returns all warnings created during generation.
     * @return {@link List} of warnings.
     */
    public Set<String> getWarnings() {
        return warnings;
    }

    /**
     * Returns whether the generation could be performed successfully. Equivalent to
     * {@code getErrorMessages().isEmpty()}
     * @return <code>true</code> if no errors occurred, <code>false</code> otherwise
     */
    public boolean isSuccessfull() {
        return errorMessages.isEmpty();
    }
}
