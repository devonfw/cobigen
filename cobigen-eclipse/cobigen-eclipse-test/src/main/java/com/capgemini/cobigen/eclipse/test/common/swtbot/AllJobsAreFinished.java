package com.capgemini.cobigen.eclipse.test.common.swtbot;

import java.util.Calendar;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;

/**
 * Condition for {@link SWTBot#waitUntil(ICondition)} to wait until all Jobs are finished.
 */
public class AllJobsAreFinished implements ICondition {

    /** Current {@link SWTBot}. Should not be used for navigation to not break test invariants */
    private SWTBot bot;

    @Override
    public boolean test() throws Exception {
        return Job.getJobManager().isIdle();
    }

    @Override
    public void init(SWTBot bot) {
        this.bot = bot;
    }

    @Override
    public String getFailureMessage() {
        bot.captureScreenshot(Calendar.getInstance().getTime() + "Jobs_not_finished.jpeg");
        return "Could not finish all Jobs in the given amount of time.";
    }

}
