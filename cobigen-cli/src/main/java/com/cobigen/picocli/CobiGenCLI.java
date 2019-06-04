package com.cobigen.picocli;

import java.io.File;

import com.cobigen.picocli.handlers.CommandsHandler;

import picocli.CommandLine.Command;

/**
 * Starting point of the CobiGen CLI. Contains the main method.
 */
@Command(name = "TestPicocli", header = "%n@|TestPicocli Hello world demo|@")
public class CobiGenCLI {

    /**
     * Main starting point of the CLI. Here we parse the arguments from the user.
     *
     * @param args
     *            list of arguments the user has passed
     */
    public static void main(String... args) {
        CommandsHandler CmdHandler = CommandsHandler.getInstance();
        File jarPath = new File("templates_jar");

        // Create a folder where the templates will be stored
        if (!jarPath.exists()) {
            jarPath.mkdir();
        }

        CmdHandler.executeCommand(args);

    }

}
