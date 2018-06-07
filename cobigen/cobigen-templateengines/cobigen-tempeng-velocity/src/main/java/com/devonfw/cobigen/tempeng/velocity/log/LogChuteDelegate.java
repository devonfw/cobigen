package com.devonfw.cobigen.tempeng.velocity.log;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.slf4j.Logger;

/**
 * A wrapper for the {@link org.slf4j.Logger} implementing {@link LogChute}. Provides log output during the
 * {@link org.apache.velocity.app.VelocityEngine} execution
 */
public class LogChuteDelegate implements LogChute {

    /**
     * The Logger instance
     */
    private final Logger log;

    /**
     * @param log
     *            an already instantiated {@link Logger}
     */
    public LogChuteDelegate(Logger log) {
        this.log = log;
    }

    @Override
    public void init(RuntimeServices runtimeServices) throws Exception {
        log.debug("Logger initialized with ignored RuntimeServices: {}", runtimeServices.getConfiguration().toString());
    }

    @Override
    public boolean isLevelEnabled(int logLevel) {
        switch (logLevel) {
        case LogChute.DEBUG_ID:
            return log.isDebugEnabled();
        case LogChute.ERROR_ID:
            return log.isErrorEnabled();
        case LogChute.INFO_ID:
            return log.isInfoEnabled();
        case LogChute.TRACE_ID:
            return log.isTraceEnabled();
        case LogChute.WARN_ID:
            return log.isWarnEnabled();
        }
        return false;
    }

    @Override
    public void log(int logLevel, String message) {
        switch (logLevel) {
        case LogChute.DEBUG_ID:
            log.debug(message);
            return;
        case LogChute.ERROR_ID:
            log.error(message);
            return;
        case LogChute.INFO_ID:
            log.info(message);
            return;
        case LogChute.TRACE_ID:
            log.trace(message);
            return;
        case LogChute.WARN_ID:
            log.warn(message);
            return;
        default:
            log.warn("Unsupported log level {}:{}", logLevel, message);
        }

    }

    @Override
    public void log(int logLevel, String message, Throwable throwable) {
        switch (logLevel) {
        case LogChute.DEBUG_ID:
            log.debug(message, throwable);
            return;
        case LogChute.ERROR_ID:
            log.error(message, throwable);
            return;
        case LogChute.INFO_ID:
            log.info(message, throwable);
            return;
        case LogChute.TRACE_ID:
            log.trace(message, throwable);
            return;
        case LogChute.WARN_ID:
            log.warn(message, throwable);
            return;
        default:
            log.warn("Unsupported log level {}\n{}\nthrows\n{}", logLevel, message, throwable.toString());
        }

    }

}
