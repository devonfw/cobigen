package com.cobigen.picocli.logger;

import org.slf4j.LoggerFactory;

import com.cobigen.picocli.CobiGenCLI;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;

public class CLILogger {

    private static Logger rootLogger = (Logger) LoggerFactory.getLogger(CobiGenCLI.class);

    public static void layoutLogger() {
        LoggerContext loggerContext = rootLogger.getLoggerContext();
        // we are not interested in auto-configuration
        loggerContext.reset();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("[%-5level] %message%n");
        encoder.start();

        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<ILoggingEvent>();
        appender.setContext(loggerContext);
        appender.setEncoder(encoder);
        appender.start();

        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(appender);
    }

    public static void setLevel(Level level) {
        rootLogger.setLevel(level);
    }
}
