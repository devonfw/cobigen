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
     * @param args
     *            list of arguments the user has passed
     */
	public static void main(String... args) {
		CommandsHandler CmdHandler = CommandsHandler.getInstance();
		CmdHandler.executeCommand(args);

		File inputFile = null;

		CreateJarFile createjarFile = new CreateJarFile();
		File jarPath = new File("templates_jar");

		if (!jarPath.exists()) {
			jarPath.mkdir();
		}

		// We get the templates that will be used for generation
		getTemplatesJar(false);

		if (createjarFile.validateFile(inputFile)) {
			createjarFile.createJarAndGenerateIncr(inputFile);
		} else {
			// TODO: ask user to prompt input file again
		}

	}

    /**
     * Tries to find the templates jar. If it was not found, it will download it and then return it.
     * @param isSource
     *            true if we want to get source jar file path
     * @return the jar file of the templates
     */
    private static File getTemplatesJar(boolean isSource) {
        File jarPath = new File("templates_jar");
        // URL resource = TestPicocli.class.getResource("/cobigen_jar");
        File jarFileDir = jarPath.getAbsoluteFile();

        if (!jarPath.exists()) {
            jarPath.mkdir();
        }

        // We first check if we already have the CobiGen_Templates jar downloaded
        if (TemplatesJarUtil.getJarFile(isSource, jarFileDir) == null) {
            try {
                TemplatesJarUtil.downloadLatestDevon4jTemplates(isSource, jarFileDir);
            } catch (MalformedURLException e) {
                // if a path of one of the class path entries is not a valid URL
                logger.error("Problem while downloading the templates, URL not valid. This is a bug", e);
            } catch (IOException e) {
                // IOException occurred
                logger.error(
                    "Problem while downloading the templates, most probably you are facing connection issues.\n\n"
                        + "Please try again later.",
                    e);
            }
        }
        return TemplatesJarUtil.getJarFile(isSource, jarFileDir);
    }

    

}
