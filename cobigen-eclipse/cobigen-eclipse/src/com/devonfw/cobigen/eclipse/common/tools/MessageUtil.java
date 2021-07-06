package com.devonfw.cobigen.eclipse.common.tools;

import com.devonfw.cobigen.api.to.HealthCheckReport;

/**
 * Provides utility functions for creating dialog messages.
 */
public class MessageUtil {

    /**
     * @param s
     *            the String that contains the previous error message
     * @param report
     *            the {@link HealthCheckReport} to be addressed by this method.
     * @return a new enriched String if more than one error occurred while running the HealthCheck, otherwise
     *         just returns the input String.
     */
    public static String enrichMsgIfMultiError(String s, HealthCheckReport report) {
    	if (report != null) {
    		if (report.getNumberOfErrors() > 1) {
                s += "\n\n"
                    + "There was more than one error while running the Health Check. See the log file for further information.";
            }
    	}
        
        return s;
    }

}
