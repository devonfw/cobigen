package com.cobigen.picocli;


import org.slf4j.LoggerFactory;

import com.cobigen.picocli.commands.CobiGenCommand;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import picocli.CommandLine;
import ch.qos.logback.classic.Logger;

/**
 * Starting point of the CobiGen CLI. Contains the main method.
 */
public class CobiGenCLI {

    /**
     * Logger to output useful information to the user
     */
    private static Logger logger = (Logger) LoggerFactory.getLogger(CobiGenCLI.class);

    /**
     * Picocli command line object
     */
    private final static CommandLine commandLine = new CommandLine(new CobiGenCommand());

    /**
     * @return the {@link CommandLine} object of this current execution
     */
    public static CommandLine getCLI() {
        return commandLine;
    }

    /**
     * Main starting point of the CLI. Here we parse the arguments from the user.
     *
     * @param args
     *            list of arguments the user has passed
     */
    public static void main(String... args) {
    	/**
    	 * Customize logger  
    	 * */
    	Logger rootLogger = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        LoggerContext loggerContext = rootLogger.getLoggerContext();
        // we are not interested in auto-configuration
        { loggerContext.reset() ;}

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        {encoder.setContext(loggerContext);}
        { encoder.setPattern("%message%n");}
        {encoder.start();}

        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<ILoggingEvent>();
        { appender.setContext(loggerContext);}
        {appender.setEncoder(encoder); }
        {appender.start();}

        {rootLogger.addAppender(appender);}
        /**
         * Customization of  logger end 
         * */
        if (commandLine.execute(args) == 0) {
            logger.debug("Commands were executed correctly");
        }
        	
    }

}
