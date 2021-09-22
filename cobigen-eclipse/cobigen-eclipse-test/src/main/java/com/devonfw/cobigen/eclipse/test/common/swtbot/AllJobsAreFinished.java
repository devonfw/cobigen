package com.devonfw.cobigen.eclipse.test.common.swtbot;

import org.eclipse.core.runtime.jobs.Job;
import com.devonfw.cobigen.eclipse.test.common.utils.EclipseCobiGenUtils;

/**
 * Condition for {@link SWTBot#waitUntil(ICondition)} to wait until all Jobs are finished.
 */
public class AllJobsAreFinished extends DefaultCondition {

  @Override
  public boolean test() throws Exception {

    return Job.getJobManager().isIdle();
  }

  @Override
  public String getFailureMessage() {

    EclipseCobiGenUtils.takeScreenshot(bot, "jobs_not_finished");
    return "Could not finish all Jobs in the given amount of time.";
  }

}
