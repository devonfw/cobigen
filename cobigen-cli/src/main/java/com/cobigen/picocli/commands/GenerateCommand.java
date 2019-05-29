package com.cobigen.picocli.commands;

import java.io.File;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cobigen.picocli.handlers.CommandsHandler;

/**
 * This class handles the generation command
 */
public class GenerateCommand {

    /**
     * Logger to output useful information to the user
     */
    private static Logger logger = LoggerFactory.getLogger(GenerateCommand.class);

    /**
     * User input file
     */
    File inputFile = null;
    
    private static GenerateCommand generateCommand;
    
 /**
  * static block initialization for exception handling
  */
 	static {
 		try {
 			generateCommand = new GenerateCommand();
 		} catch (Exception e) {
 			throw new RuntimeException("Exception occure in creation of singlton instance");
 		}
 	}

    /**
     * private constructor restricted to this class itself 
     * */ 
 	private GenerateCommand() {
 	};

 	public static GenerateCommand getInstance() {
 		return generateCommand;
 	}
    /**
     * Constructor for {@link GenerateCommand}
     * @param args
     *            String array with all the user arguments
     */
    public GenerateCommand(ArrayList<String> args) {
        validateArguments(args);
    }

    /**
     * @param args
     */
    public void validateArguments(ArrayList<String> args) {
        if (args.size() == 1) {
            logger.error(
                "You need to provide two arguments: <path_of_input_file> <path_of_project> and your second parameter was not found.");
            System.exit(0);
        } else if (args.size() == 2) {
            inputFile = new File(args.get(1));
        } else {
            logger.error(
                "Too many arguments have been provided, you need to provide two: <path_of_input_file> <path_of_project>");
            System.exit(0);
        }
    }
}
