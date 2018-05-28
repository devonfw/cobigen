package com.devonfw.cobigen.test.assertj;

import static org.assertj.core.api.Assertions.assertThat;

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
     * @param actual
     *            {@link GenerationReportTo} to be asserted
     */
    public GenerationReportToAssert(GenerationReportTo actual) {
        super(actual, GenerationReportToAssert.class);
    }

    /**
     * Checks whether the {@link GenerationReportTo} reports a successful generation. In case of a
     * non-successful generation report, warnings and errors will be reported in the assertion error message.
     * @return the {@link GenerationReportToAssert}
     */
    public GenerationReportToAssert isSuccessful() {
        try {
            assertThat(actual.isSuccessful()).overridingErrorMessage(
                "Generation not successfull. Warnings: %s // Errors: %s. Please see the printed stack traces in the LOG.",
                actual.getWarnings(), actual.getErrors()).isTrue();
        } catch (AssertionError e) {
            for (Throwable entry : actual.getErrors()) {
                LOG.error(entry.getMessage(), entry);
            }
            throw e;
        }
        return this;
    }
}
