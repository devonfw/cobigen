package com.capgemini.cobigen.api.to;

import java.util.Collection;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Generation report.
 */
public class GenerationReportTo {

    /** Error messages */
    private SortedMap<String, Throwable> errors = Maps.newTreeMap();

    /** Warnings */
    private Set<String> warnings = Sets.newTreeSet();

    /**
     * Adds a new error message to the report.
     * @param message
     *            error message.
     * @param cause
     *            cause of the error.
     */
    public void addError(String message, Throwable cause) {
        errors.put(message, cause);
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
     * Adds the new warning messages to the report.
     * @param messages
     *            warning messages.
     */
    public void addAllWarnings(Collection<String> messages) {
        warnings.addAll(messages);
    }

    /**
     * Adds the new error messages to the report.
     * @param messages
     *            error messages.
     */
    public void addAllErrors(Collection<String> messages) {
        warnings.addAll(messages);
    }

    /**
     * Aggregates all properties of the given report within {@code this} report.
     * @param report
     *            {@link GenerationReportTo} to be aggregated
     */
    public void aggregate(GenerationReportTo report) {
        addAllErrors(report.getErrorMessages());
        addAllWarnings(report.getWarnings());
    }

    /**
     * Returns all error messages occurred.
     * @return {@link TreeSet} of error messages.
     */
    public SortedSet<String> getErrorMessages() {
        return Sets.newTreeSet(errors.keySet());
    }

    /**
     * Returns the cause of the error with the given error message
     * @param message
     *            error message to the the cause for
     * @return the cause of the error with the given error message
     */
    public Throwable getErrorCause(String message) {
        return errors.get(message);
    }

    /**
     * Returns a sorted error message to cause mapping.
     * @return a {@link TreeSet} error message to cause mapping.
     */
    public SortedMap<String, Throwable> getErrors() {
        return Maps.newTreeMap(errors);
    }

    /**
     * Returns all warnings created during generation.
     * @return {@link TreeSet} of warnings.
     */
    public SortedSet<String> getWarnings() {
        return Sets.newTreeSet(warnings);
    }

    /**
     * Returns whether the report contains warnings.
     * @return {@code true} if there is at least one warning, {@code false} otherwise.
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    /**
     * Returns whether the report contains errors.
     * @return {@code true} if there is at least one error, {@code false} otherwise.
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Returns whether the generation could be performed successfully. Equivalent to !{@link #hasErrors()}
     * @return <code>true</code> if no errors occurred, <code>false</code> otherwise
     */
    public boolean isSuccessful() {
        return errors.isEmpty();
    }
}
