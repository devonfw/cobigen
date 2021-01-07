package com.devonfw.cobigen.api.assertj;

import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;

import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.HealthCheckReport;

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

    /**
     * Creates a new {@link Assert} object for {@link HealthCheckReport} objects
     * @param target
     *            {@link HealthCheckReport} to be asserted
     * @return the {@link HealthCheckReportAssert} instance
     */
    public static HealthCheckReportAssert assertThat(HealthCheckReport target) {
        return new HealthCheckReportAssert(target);
    }
}
