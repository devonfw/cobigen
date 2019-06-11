package com.cobigen.picocli.commands;

import java.io.File;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cobigen.picocli.CobiGenCLI;
import com.cobigen.picocli.constants.MessagesConstants;

import picocli.CommandLine.Command;

/**
 * This class defines the main CobiGen command
 */
@Command(description = MessagesConstants.WELCOME_MESSAGE, name = "cobigen", aliases = { "cg" },
    mixinStandardHelpOptions = true, version = "CobiGen CLI 1.0", subcommands = { GenerateCommand.class })
public class CobiGenCommand implements Runnable {

    /**
     * Logger to output useful information to the user
     */
    private static Logger logger = LoggerFactory.getLogger(CobiGenCLI.class);

    private static final Scanner inputReader = new Scanner(System.in);

    @Override
    public void run() {

        File jarPath = new File("templates_jar");

        // Create a folder where the templates will be stored
        if (!jarPath.exists()) {
            jarPath.mkdir();
        }
    }

    /**
     * Asks the user for input and returns the value
     *
     * @return String containing the user input
     */
    public static String getUserInput() {
        String userInput = "";
        userInput = inputReader.nextLine();
        return userInput;
    }

}
