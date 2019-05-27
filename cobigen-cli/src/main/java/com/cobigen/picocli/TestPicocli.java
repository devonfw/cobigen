package com.cobigen.picocli;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cobigen.picocli.utils.CreateJarFile;
import com.cobigen.picocli.utils.ValidateMavenProject;
import com.devonfw.cobigen.impl.util.TemplatesJarUtil;

import picocli.CommandLine.Command;

/**
 * Starting point of the CobiGen CLI. Contains the main method.
 */
@Command(name = "TestPicocli", header = "%n@|TestPicocli Hello world demo|@")
public class TestPicocli {
    private static Logger logger = LoggerFactory.getLogger(TestPicocli.class);

    /**
     * Main starting point of the CLI. Here we parse the arguments from the user.
     * @param args
     *            list of arguments the user has passed
     */
	public static void main(String... args) {
		String cwd = System.getProperty("user.dir");
		System.out.println("current path = " + System.getProperty("user.dir"));
		ValidateMavenProject validateMavenProject = new ValidateMavenProject();
		validateMavenProject.findPom(new File(cwd));
		System.out.println("cwd = > " + cwd);
		String userInput = "";
		File inputFile = null;

		if (args == null || args.length < 1) {

			logger.info("Your current directory is " + userInput);			
			Scanner inputReader = new Scanner(System.in);
			userInput = inputReader.nextLine();
		} else {

			logger.info("Your current directory is " + userInput);
		}
		// }
		if (args.length > 0) {
			for (int i = 0; i <= 1; i++)
				inputFile = new File(args[1]);
			System.out.println("input===> " + inputFile);

		}
		CreateJarFile createjarFile = new CreateJarFile();

		File jarPath = new File("templates_jar");
		// URL resource = TestPicocli.class.getResource("/cobigen_jar");
		File jarFileDir = jarPath.getAbsoluteFile();

		// EmployeeEntity life = cls.newInstance();
		if (!jarPath.exists()) {
			jarPath.mkdir();
		}

		// We get the templates that will be used for generation
		getTemplatesJar(false);

		createjarFile.validateFile(inputFile);
		System.out.println("Input file in testPicocli is =>" + inputFile);
		createjarFile.createJarAndGenerateIncr(inputFile);

		logger.info("successfully call cobigen create method");

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

    /**
     * Retrieves the input from the user
     * @param args
     *            list of arguments that were passed to this CLI
     * @return user input as String
     */
    private static String getUserInput(String... args) {
        String userInput = "";
        if (args == null || args.length == 0 || args[0].length() < 1) {
            logger.info("Please enter input from command prompt");
            try (Scanner inputReader = new Scanner(System.in)) {

                userInput = inputReader.nextLine();
            }
        } else {
            userInput = args[0];
        }
        return userInput;
    }

}
