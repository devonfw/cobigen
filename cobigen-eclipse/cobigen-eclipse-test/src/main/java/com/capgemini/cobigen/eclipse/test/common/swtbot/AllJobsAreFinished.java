package com.devonfw.cobigen.eclipse.test.common.swtbot;

import java.util.Calendar;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.waits.ICondition;

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
        bot.captureScreenshot(Calendar.getInstance().getTime() + "Jobs_not_finished.jpeg");
        return "Could not finish all Jobs in the given amount of time.";
    }

}
