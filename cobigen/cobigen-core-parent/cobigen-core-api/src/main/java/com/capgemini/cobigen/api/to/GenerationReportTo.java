package com.capgemini.cobigen.api.to;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Generation report.
 */
public class GenerationReportTo {

    /** Error messages mapping from message to cause to avoid duplicates. */
    private Map<String, Throwable> errors = Maps.newHashMap();

    /** Warnings in a hash set to remove duplicates */
    private Set<String> warnings = Sets.newHashSet();

    /**
     * A temporary {@link Path} pointing to the incomplete generation result iff {@link #isSuccessful()
     * isSuccessful() == false}
     */
    private Path incompleteGenerationPath;

    /**
     * Adds a new error message to the report.
     * @param cause
     *            cause of the error.
     */
    public void addError(Throwable cause) {
        errors.put(cause.getMessage(), cause);
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
     * @param errors
     *            error messages.
     */
    public void addAllErrors(List<Throwable> errors) {
        for (Throwable t : errors) {
            this.errors.put(t.getMessage(), t);
        }
    }

    /**
     * Returns the {@link Path} to the temporary sources of the resulting incomplete generation contents iff
     * {@link #isSuccessful() isSuccessful() == false}.
     * @return the {@link Path} to the generation contents.
     */
    public Path getIncompleteGenerationPath() {
        return incompleteGenerationPath;
    }

    /**
     * Sets the {@link Path} to the temporary sources of the resulting incomplete generation contents iff
     * {@link #isSuccessful() isSuccessful() == false}.
     * @param incompleteGenerationPath
     *            the {@link Path} to the incomplete generation result.
     */
    public void setIncompleteGenerationPath(Path incompleteGenerationPath) {
        this.incompleteGenerationPath = incompleteGenerationPath;
    }

    /**
     * Aggregates all properties of the given report within {@code this} report. The
     * {@link #incompleteGenerationPath} will be overwritten on every aggregation by the passed instance's
     * value iff not <code>null</code>.
     * @param report
     *            {@link GenerationReportTo} to be aggregated
     */
    public void aggregate(GenerationReportTo report) {
        addAllErrors(report.getErrors());
        addAllWarnings(report.getWarnings());
        if (report.getIncompleteGenerationPath() != null) {
            incompleteGenerationPath = report.getIncompleteGenerationPath();
        }
    }

    /**
     * Returns the {@link List} of occurred errors.
     * @return the {@link List} of occurred errors.
     */
    public List<Throwable> getErrors() {
        return Lists.newArrayList(errors.values());
    }

    /**
     * Returns all warnings created during generation.
     * @return {@link List} of warnings.
     */
    public List<String> getWarnings() {
        return Lists.newArrayList(warnings);
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
