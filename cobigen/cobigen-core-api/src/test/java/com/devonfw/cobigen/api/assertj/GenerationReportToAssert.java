package com.devonfw.cobigen.api.assertj;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.assertj.core.api.AbstractAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.to.GenerationReportTo;

/**
 * AssertJ assertion for {@link GenerationReportTo}.
 */
public class GenerationReportToAssert extends AbstractAssert<GenerationReportToAssert, GenerationReportTo> {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(GenerationReportToAssert.class);

  /**
   * The constructor.
   *
   * @param actual {@link GenerationReportTo} to be asserted
   */
  public GenerationReportToAssert(GenerationReportTo actual) {

    super(actual, GenerationReportToAssert.class);
  }

  /**
   * Checks whether the {@link GenerationReportTo} reports a successful generation. In case of a non-successful
   * generation report, warnings and errors will be reported in the assertion error message.
   *
   * @return the {@link GenerationReportToAssert}
   */
  public GenerationReportToAssert isSuccessful() {

    try {
      assertThat(this.actual.isSuccessful()).overridingErrorMessage(
          "Generation not successfull. Warnings: %s // Errors: %s. Please see the printed stack traces in the LOG.",
          this.actual.getWarnings(), this.actual.getErrors().stream().map(e -> "\n" + ExceptionUtils.getStackTrace(e))
              .collect(Collectors.joining()))
          .isTrue();
    } catch (AssertionError e) {
      for (Throwable entry : this.actual.getErrors()) {
        LOG.error(entry.getMessage(), entry);
      }
      throw e;
    }
    return this;
  }

  /**
   * Checks if an Exception of the given class was found in the {@link GenerationReportTo} error reports.
   *
   * @param exception Class of Exception to check for
   * @return {@link GenerationReportToAssert}
   * @throws AssertionError if another or no Exception was found in the report
   */
  public GenerationReportToAssert containsException(Class<? extends Throwable> exception) throws AssertionError {

    for (Throwable error : this.actual.getErrors()) {
      if (error.getClass().equals(exception)) {
        return this;
      }
    }
    throw new AssertionError("Expected an exception of class " + exception + ", which could not be found in the list "
        + this.actual.getErrors());
  }

  /**
   * Checks if an Exception of the given class was NOT found in the {@link GenerationReportTo} error reports. errors.
   *
   * @param exception Class of Exception to check for
   * @return boolean
   * @throws AssertionError if the Exception was found in the report
   */
  public GenerationReportToAssert notContainsException(Class<? extends Throwable> exception) throws AssertionError {

    for (Throwable error : this.actual.getErrors()) {
      if (error.getClass().equals(exception)) {
        throw new AssertionError("Expected no Exception of class: " + exception + " in error reports.");
      }
    }
    return this;
  }
}
