package com.devonfw.cobigen.api.to;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/** Generation report. */
public class GenerationReportTo {

  /** Error messages mapping from message to cause to avoid duplicates. */
  private Map<String, RuntimeException> errors = Maps.newTreeMap();

  /** Warnings in a hash set to remove duplicates */
  private Set<String> warnings = Sets.newTreeSet();

  /** @see #getTemporaryWorkingDirectory() */
  private Path temporaryWorkingDirectory = null;

  /** @see #getGeneratedFiles() */
  private Set<Path> generatedFiles = new TreeSet<>();

  /**
   * @see #isCancelled()
   */
  private boolean cancelled = false;

  /**
   * @see #getGeneratedFiles()
   * @param file generated/touched file
   */
  public void addGeneratedFile(Path file) {

    this.generatedFiles.add(file);
  }

  /**
   * @see #getGeneratedFiles()
   * @param files a collection of generated/touched files
   */
  private void addAllGeneratedFiles(Collection<Path> files) {

    this.generatedFiles.addAll(files);
  }

  /**
   * The sorted set of generated files.
   *
   * @return a {@link TreeSet} of {@link Path}s
   */
  public Set<Path> getGeneratedFiles() {

    return this.generatedFiles;
  }

  /**
   * Adds a new error message to the report.
   *
   * @param cause cause of the error.
   */
  public void addError(RuntimeException cause) {

    this.errors.put(cause.getMessage(), cause);
  }

  /**
   * Adds a new warning to the report.
   *
   * @param message warning message.
   */
  public void addWarning(String message) {

    this.warnings.add(message);
  }

  /**
   * Adds the new warning messages to the report.
   *
   * @param messages warning messages.
   */
  private void addAllWarnings(Collection<String> messages) {

    this.warnings.addAll(messages);
  }

  /**
   * Adds the new error messages to the report.
   *
   * @param errors error messages.
   */
  private void addAllErrors(List<RuntimeException> errors) {

    for (RuntimeException t : errors) {
      this.errors.put(t.getMessage(), t);
    }
  }

  /**
   * Returns the temporary {@link Path} pointing to the incomplete generation result iff {@link #isSuccessful()
   * isSuccessful() == false}. Otherwise, the directory will have been deleted after generation.
   *
   * @return the {@link Path} to the generation contents.
   */
  public Path getTemporaryWorkingDirectory() {

    return this.temporaryWorkingDirectory;
  }

  /**
   * @see #getTemporaryWorkingDirectory()
   * @param temporaryWorkingDirectory the {@link Path} to the incomplete generation result.
   */
  public void setTemporaryWorkingDirectory(Path temporaryWorkingDirectory) {

    this.temporaryWorkingDirectory = temporaryWorkingDirectory;
  }

  /**
   * Aggregates all properties of the given report within {@code this} report. The {@link #temporaryWorkingDirectory}
   * will be overwritten on every aggregation by the passed instance's value iff not <code>null</code>.
   * {@link #getTemporaryWorkingDirectory()} will not be aggregated, just the first non null value will be preserved.
   *
   * @param report {@link GenerationReportTo} to be aggregated
   */
  public void aggregate(GenerationReportTo report) {

    addAllErrors(report.getErrors());
    addAllWarnings(report.getWarnings());
    addAllGeneratedFiles(report.getGeneratedFiles());
    if (report.getTemporaryWorkingDirectory() != null) {
      this.temporaryWorkingDirectory = report.getTemporaryWorkingDirectory();
    }
  }

  /**
   * Returns the {@link List} of occurred errors.
   *
   * @return the {@link List} of occurred errors.
   */
  public List<RuntimeException> getErrors() {

    return Lists.newArrayList(this.errors.values());
  }

  /**
   * Returns all warnings created during generation.
   *
   * @return {@link List} of warnings.
   */
  public List<String> getWarnings() {

    return Lists.newArrayList(this.warnings);
  }

  /**
   * Returns whether the report contains warnings.
   *
   * @return {@code true} if there is at least one warning, {@code false} otherwise.
   */
  public boolean hasWarnings() {

    return !this.warnings.isEmpty();
  }

  /**
   * Returns whether the report contains errors.
   *
   * @return {@code true} if there is at least one error, {@code false} otherwise.
   */
  public boolean hasErrors() {

    return !this.errors.isEmpty();
  }

  /**
   * Returns whether the generation could be performed successfully. Equivalent to !{@link #hasErrors()}
   *
   * @return <code>true</code> if no errors occurred, <code>false</code> otherwise
   */
  public boolean isSuccessful() {

    return this.errors.isEmpty();
  }

  /**
   * Return whether the generation got cancelled by the user
   *
   * @return <code>true</code> if it got cancelled, <code>false</code> otherwise
   */
  public boolean isCancelled() {

    return this.cancelled;
  }

  /**
   * set's whether the generation got cancelled or not
   *
   * @param cancelled if generation got cancelled
   */
  public void setCancelled(boolean cancelled) {

    this.cancelled = cancelled;
  }

}
