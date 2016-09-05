package com.capgemini.cobigen.common.assertj;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map.Entry;

import org.assertj.core.api.AbstractAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.api.to.GenerationReportTo;

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
                actual.getWarnings(), actual.getErrorMessages()).isTrue();
        } catch (AssertionError e) {
            for (Entry<String, Throwable> entry : actual.getErrors().entrySet()) {
                LOG.error(entry.getKey(), entry.getValue());
            }
            throw e;
        }
        return this;
    }
}
