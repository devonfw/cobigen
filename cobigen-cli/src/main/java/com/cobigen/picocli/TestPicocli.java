package com.cobigen.picocli;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cobigen.picocli.commands.GenerateCommand;
import com.cobigen.picocli.handlers.CommandsHandler;
import com.cobigen.picocli.utils.CreateJarFile;
import com.cobigen.picocli.utils.ValidateMavenProject;
import com.devonfw.cobigen.impl.util.TemplatesJarUtil;

import picocli.CommandLine.Command;

/**
 * Starting point of the CobiGen CLI. Contains the main method.
 */
@Command(name = "TestPicocli", header = "%n@|TestPicocli Hello world demo|@")
public class TestPicocli {

	/**
	 * Logger to output useful information to the user
	 */
	private static Logger logger = LoggerFactory.getLogger(TestPicocli.class);

	/**
	 * Main starting point of the CLI. Here we parse the arguments from the user.
	 * 
	 * @param args list of arguments the user has passed
	 */
	public static void main(String... args) {
		CommandsHandler CmdHandler = CommandsHandler.getInstance();
		CmdHandler.executeCommand(args);

		File jarPath = new File("templates_jar");

		if (!jarPath.exists()) {
			jarPath.mkdir();
		}

	}

}
