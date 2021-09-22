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
   * @param log an already instantiated {@link Logger}
   */
  public LogChuteDelegate(Logger log) {

    this.log = log;
  }

  @Override
  public void init(RuntimeServices runtimeServices) throws Exception {

    this.log.debug("Logger initialized with ignored RuntimeServices: {}",
        runtimeServices.getConfiguration().toString());
  }

  @Override
  public boolean isLevelEnabled(int logLevel) {

    switch (logLevel) {
      case LogChute.DEBUG_ID:
        return this.log.isDebugEnabled();
      case LogChute.ERROR_ID:
        return this.log.isErrorEnabled();
      case LogChute.INFO_ID:
        return this.log.isInfoEnabled();
      case LogChute.TRACE_ID:
        return this.log.isTraceEnabled();
      case LogChute.WARN_ID:
        return this.log.isWarnEnabled();
    }
    return false;
  }

  @Override
  public void log(int logLevel, String message) {

    switch (logLevel) {
      case LogChute.DEBUG_ID:
        this.log.debug(message);
        return;
      case LogChute.ERROR_ID:
        this.log.error(message);
        return;
      case LogChute.INFO_ID:
        this.log.info(message);
        return;
      case LogChute.TRACE_ID:
        this.log.trace(message);
        return;
      case LogChute.WARN_ID:
        this.log.warn(message);
        return;
      default:
        this.log.warn("Unsupported log level {}:{}", logLevel, message);
    }

  }

  @Override
  public void log(int logLevel, String message, Throwable throwable) {

    switch (logLevel) {
      case LogChute.DEBUG_ID:
        this.log.debug(message, throwable);
        return;
      case LogChute.ERROR_ID:
        this.log.error(message, throwable);
        return;
      case LogChute.INFO_ID:
        this.log.info(message, throwable);
        return;
      case LogChute.TRACE_ID:
        this.log.trace(message, throwable);
        return;
      case LogChute.WARN_ID:
        this.log.warn(message, throwable);
        return;
      default:
        this.log.warn("Unsupported log level {}\n{}\nthrows\n{}", logLevel, message, throwable.toString());
    }

  }

}
