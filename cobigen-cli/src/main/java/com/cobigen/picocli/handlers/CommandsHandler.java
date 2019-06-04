package com.cobigen.picocli.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cobigen.picocli.commands.GenerateCommand;
import com.cobigen.picocli.constants.MessagesConstants;
import com.cobigen.picocli.utils.ValidationUtils;

/**
 * This class handles the user commands passed to the CLI
 */
public class CommandsHandler {

    /**
     * Logger to output useful information to the user
     */
    private static Logger logger = LoggerFactory.getLogger(CommandsHandler.class);

    private static final Scanner inputReader = new Scanner(System.in);

    private ArrayList<String> argsList;

    private static CommandsHandler cmdHandler;
    /**
     * static block initialization for exception handling
     */
    static {
        try {
            cmdHandler = new CommandsHandler();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in creation of singleton instance");
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

            logger.info(MessagesConstants.WELCOME_MESSAGE);

            dispatchCommand(getUserInput());
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

        if (options.isEmpty()) {
            String inputProject = getWorkingDirectory();
            String inputFile = getInputFile();
            options.add(inputFile);
            options.add(inputProject);
        } else {
            // we remove the "g" or "generate", so that we only have options: <input_file>, <input_project>...
            options.remove(0);
        }

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
     * @return
     */
    private String getInputFile() {
        logger.info("Please provide the input file you want to use for generation");
        return getUserInput();
    }

    /**
     * @return
     *
     */
    private String getWorkingDirectory() {
        // current working directory where the CLI is getting executed
        String cwd = System.getProperty("user.dir");

        File pomFile = ValidationUtils.findPom(new File(cwd));
        if (pomFile == null) {
            logger.info(
                "You are not in valid maven project. Please provide two arguments: <path_of_input_file> <path_of_project>");
            String userInput = getUserInput();
            return userInput;
        }

        return ValidationUtils.chooseWorkingDirectory(getUserInput());
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
