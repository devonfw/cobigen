package com.devonfw.cobigen.cli.logger;

import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.cli.CobiGenCLI;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;

/**
 * This class declare layout logger and logger level
 */
public class CLILogger {

  /**
   * Getting logger instance from LoggerFactory for reset logger layout
   */
  private static Logger rootLogger = (Logger) LoggerFactory.getLogger(CobiGenCLI.class);

  /**
   * This method is setting the custom layout of logger
   *
   * @param verbose whether verbose logging should be enabled
   */
  public static void layoutLogger(boolean verbose) {

    LoggerContext loggerContext = rootLogger.getLoggerContext();
    if (!verbose) {
      loggerContext.reset();
    }
    PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    encoder.setContext(loggerContext);
    encoder.setPattern("[%-5level] %message%n");
    encoder.start();

    ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
    appender.setContext(loggerContext);
    appender.setEncoder(encoder);
    appender.start();

    if (rootLogger.getLevel() == null) {
      rootLogger.setLevel(Level.INFO);
    }
    rootLogger.addAppender(appender);
  }

  /**
   * This method declare level of logger
   *
   * @param level of logging
   */
  public static void setLevel(Level level) {

    rootLogger.setLevel(level);
  }
}
