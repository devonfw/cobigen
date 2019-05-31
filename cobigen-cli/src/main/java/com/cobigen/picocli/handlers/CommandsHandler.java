package com.cobigen.picocli.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cobigen.picocli.commands.GenerateCommand;
import com.cobigen.picocli.utils.ValidationUtils;

/**
 * This class handles the user commands passed to the CLI
 */
public class CommandsHandler {

    /**
     * Logger to output useful information to the user
     */
    private static Logger logger = LoggerFactory.getLogger(CommandsHandler.class);

    private ArrayList<String> argsList;

    private static CommandsHandler cmdHandler;
    /**
     * static block initialization for exception handling
     */
    static {
        try {
            cmdHandler = new CommandsHandler();
        } catch (Exception e) {
            throw new RuntimeException("Exception occure in creation of singlton instance");
        }
    }

    /**
     * private constructor restricted to this class itself
     */
    private CommandsHandler() {
    };

    public static CommandsHandler getInstance() {
        return cmdHandler;
    }

    /**
     * 
     * @param args
     *            String array with all the user arguments
     */
    public void executeCommand(String... args) {
        argsList = new ArrayList<>(Arrays.asList(args));
        if (args == null || args.length < 1) {

            logger.info("Welcome to CobiGen.\n"
                + "The Code-based incemental Generator for end to end code generation tasks, mostly used in Java projects.\n"
                + "Available Commands:\n" + "cg generate (g)\n" + "cg update\n" + "cg check\n" + "cg revert\n"
                + "with [-h] you can get more infos about the commands you want to use or the increment you want to generate");

            useCurrentWorkingDirectory();
        } else {
            dispatchCommand(args[0]);
        }
    }

    /**
     * Dispatches the command to the correct class. If the command is not valid, program gets terminated
     * 
     * @param command
     *            command to dispatch
     */
    private void dispatchCommand(String command) {
        System.out.println("command->" + command);
        ArrayList<String> options = argsList;

        options.remove(0);

        switch (command) {
        case "g":
            new GenerateCommand(options);
            break;
        case "generate":
            new GenerateCommand(options);
            break;

        default:
            logger.info("in default");
            logger.error("Command not understood, please try again");
            System.exit(0);
            break;
        }

    }

    /**
    *
    */
    private void useCurrentWorkingDirectory() {
        // current working directory where the CLI is getting executed
        String cwd = System.getProperty("user.dir");

        ValidationUtils.findPom(new File(cwd));

        String userInput = getUserInput();
    }

    /**
     * Asks the user for input and returns the value
     * 
     * @return String containing the user input
     */
    public String getUserInput() {
        try (Scanner inputReader = new Scanner(System.in)) {
            String userInput = inputReader.nextLine();
            return userInput;
        }
    }

}
