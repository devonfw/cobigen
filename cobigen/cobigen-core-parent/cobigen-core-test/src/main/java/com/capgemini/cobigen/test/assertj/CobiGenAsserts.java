package com.capgemini.cobigen.test.assertj;

import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;

import com.capgemini.cobigen.api.to.GenerationReportTo;

/**
 * AssertJ factory for any CobiGen custom assertions.
 */
public class CobiGenAsserts extends Assertions {

    /**
     * Creates a new {@link Assert} object for {@link GenerationReportTo} objects
     * @param target
     *            {@link GenerationReportTo} to be asserted
     * @return the {@link GenerationReportToAssert} instance
     */
    public static GenerationReportToAssert assertThat(GenerationReportTo target) {
        return new GenerationReportToAssert(target);
    }
}
