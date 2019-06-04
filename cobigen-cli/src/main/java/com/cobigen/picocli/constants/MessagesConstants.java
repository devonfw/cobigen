package com.cobigen.picocli.constants;

/**
 * Messages constants printed to the user
 */
public class MessagesConstants {

    public static final String COMMAND_NOT_UNDERSTOOD = "Command not understood,  please try again";

    public static final String COMMAND_NOT_YET_SUPPORTED = "Command not yet supported,  sorry for the unconvenience";

    public static final String WELCOME_MESSAGE = "Welcome to CobiGen.\n"
        + "The Code-based incemental Generator for end to end code generation tasks, mostly used in Java projects.\n"
        + "Available Commands:\n" + "cg generate (g)\n" + "cg update\n" + "cg check\n" + "cg revert\n"
        + "with [-h] you can get more infos about the commands you want to use or the increment you want to generate";

}
