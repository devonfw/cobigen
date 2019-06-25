package com.devonfw.cobigen.cli.commands;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;

import net.sf.mmm.code.impl.java.JavaContext;
import net.sf.mmm.code.impl.java.source.maven.JavaSourceProviderUsingMaven;

import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.cli.CobiGenCLI;
import com.devonfw.cobigen.cli.constants.MessagesConstants;
import com.devonfw.cobigen.cli.logger.CLILogger;
import com.devonfw.cobigen.cli.utils.CobiGenUtils;
import com.devonfw.cobigen.cli.utils.ParsingUtils;
import com.devonfw.cobigen.cli.utils.ValidationUtils;
import com.devonfw.cobigen.maven.validation.InputPreProcessor;

import ch.qos.logback.classic.Level;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * This class handles the generation command
 */
@Command(description = MessagesConstants.GENERATE_DESCRIPTION, name = "generate", aliases = { "g" },
    mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable<Integer> {

    /**
     * User input file
     */
    @Parameters(index = "0", arity = "1..*", split = ",", description = MessagesConstants.INPUT_FILE_DESCRIPTION)
    ArrayList<File> inputFiles = null;

    /**
     * User output project
     */
    @Option(names = { "--out", "-o" }, arity = "0..1", description = MessagesConstants.OUTPUT_ROOT_PATH_DESCRIPTION)
    File outputRootPath = null;

    /**
     * If this options is enabled, we will print also debug messages
     */
    @Option(names = { "--verbose", "-v" }, negatable = true, description = MessagesConstants.VERBOSE_OPTION_DESCRIPTION)
    boolean verbose;

    @Option(names = { "--increments", "-i" }, split = ",",
        description = MessagesConstants.INCREMENTS_OPTION_DESCRIPTION)
    ArrayList<Integer> increments = null;

    /**
     * Logger to output useful information to the user
     */
    private static Logger logger = LoggerFactory.getLogger(CobiGenCLI.class);

    /**
     * Utils class for CobiGen related operations
     */
    private CobiGenUtils cobigenUtils = new CobiGenUtils();

    /**
     * Used for getting users input
     */
    private static final Scanner inputReader = new Scanner(System.in);

    /**
     * Constructor needed for Picocli
     */
    public GenerateCommand() {
        super();
    }

    @Override
    public Integer call() throws Exception {

        if (verbose) {
            CLILogger.setLevel(Level.DEBUG);
        }

        if (areArgumentsValid()) {
            logger.debug("Input files and output root path confirmed to be valid.");
            cobigenUtils.getTemplatesJar(false);
            CobiGen cg = cobigenUtils.initializeCobiGen();

            List<IncrementTo> finalIncrements = null;
            if (inputFiles.size() > 1) {
                finalIncrements = preprocessIncrements(cg);
            }

            for (File inputFile : inputFiles) {
                generateTemplates(inputFile, getProjectRoot(inputFile), finalIncrements, cg,
                    cobigenUtils.getUtilClasses());
            }
            return 0;
        }

        return 1;
    }

    /**
     * For each input file it is going to get its matching increments and then performs an intersection
     * between all of them, so that the user gets only the increments that will work
     * @param cg
     *            CobiGen initialized instance
     * @return List of Increments that the user will be able to use
     *
     */
    private List<IncrementTo> preprocessIncrements(CobiGen cg) {

        Boolean firstIteration = true;
        List<IncrementTo> finalIncrements = new ArrayList<>();

        for (File inputFile : inputFiles) {

            Object input;
            String extension = inputFile.getName().toLowerCase();
            Boolean isJavaInput = extension.endsWith(".java");
            Boolean isOpenApiInput = extension.endsWith(".yaml") || extension.endsWith(".yml");

            try {
                // If it is a Java file, we need the class loader
                if (isJavaInput) {
                    JavaContext context = getJavaContext(inputFile, getProjectRoot(inputFile));
                    input = InputPreProcessor.process(cg, inputFile, context.getClassLoader());
                } else {
                    input = InputPreProcessor.process(cg, inputFile, null);
                }

                List<IncrementTo> matchingIncrements = cg.getMatchingIncrements(input);

                if (matchingIncrements.isEmpty()) {
                    printNoTriggersMatched(inputFile, isJavaInput, isOpenApiInput);
                }

                if (firstIteration) {
                    finalIncrements.addAll(matchingIncrements);
                    firstIteration = false;
                } else {
                    // We do the intersection between the previous increments and the new ones
                    finalIncrements = CobiGenUtils.retainAll(finalIncrements, matchingIncrements);
                }
            } catch (MojoFailureException e) {
                logger.error("Invalid input for CobiGen, please check your input file '" + inputFile.toString() + "'");

            }
        }

        return incrementsSelection(increments, finalIncrements);
    }

    /**
     * Validates the user arguments in the context of the generate command. Tries to check whether all the
     * input files and the output root path are valid.
     *
     * @return true when these arguments are correct
     */
    public Boolean areArgumentsValid() {

        int index = 0;
        for (File inputFile : inputFiles) {
            // Input file can be: C:\folder\input.java
            if (inputFile.exists() == false) {
                logger.debug("We could not find input file: " + inputFile.getAbsolutePath()
                    + " . But we will keep trying, maybe you are using relative paths");

                // Input file can be: folder\input.java. We should use current working directory
                if (parseRelativePath(inputFile, index) == false) {
                    logger.error("Your <inputFile> '" + inputFile.toString() + "' has not been found");
                    return false;
                }
            }
            if (inputFile.isDirectory()) {
                logger.error("Your input file: " + inputFile.getAbsolutePath()
                    + " is a directory. CobiGen cannot understand that. Please use files.");
                return false;
            }
        }
        return ValidationUtils.isOutputRootPathValid(outputRootPath);

    }

    /**
     * Tries to parse a relative path with the current working directory
     *
     * @param inputFile
     *            input file which we are going to parse to find out whether it is a valid file
     * @param index
     *            location of the input file in the ArrayList of inputs
     * @return true only if the parsed file exists, false otherwise
     *
     */
    private Boolean parseRelativePath(File inputFile, int index) {
        try {
            Path inputFilePath = Paths.get(System.getProperty("user.dir"), inputFile.toString());

            if (inputFilePath.toFile().exists()) {
                inputFiles.set(index, inputFilePath.toFile());
                return true;
            }
        } catch (InvalidPathException e) {
            logger.debug("The path string " + System.getProperty("user.dir") + " + " + inputFile.toString()
                + " cannot be converted to a path");
        }
        return false;
    }

    /**
     * Tries to find the root folder of the project in order to build the classpath. This method is trying to
     * find the first pom.xml file and then getting the folder where is located
     *
     * @param inputFile
     *            passed by the user
     * @return the project folder
     *
     */
    private File getProjectRoot(File inputFile) {

        File pomFile = ValidationUtils.findPom(inputFile);
        if (pomFile != null) {
            return pomFile.getParentFile();
        }
        logger.debug("Projec root could not be found, therefore we use your current input file location.");
        logger.debug("Using '" + inputFile.getParent() + "' as location where code will be generated");
        return inputFile.getAbsoluteFile().getParentFile();
    }

    /**
     * Generates new templates using the inputFile from the inputProject.
     *
     * @param inputFile
     *            input file the user wants to generate code from
     * @param inputProject
     *            input project where the input file is located. We need this in order to build the classpath
     *            of the input file
     * @param finalIncrements
     *            the list of increments that the user is going to use for generation
     * @param cg
     *            Initialized CobiGen instance
     * @param utilClasses
     *            util classes loaded from the templates jar
     *
     */
    public void generateTemplates(File inputFile, File inputProject, List<IncrementTo> finalIncrements, CobiGen cg,
        List<Class<?>> utilClasses) {

        try {
            Object input;
            String extension = inputFile.getName().toLowerCase();
            Boolean isJavaInput = extension.endsWith(".java");
            Boolean isOpenApiInput = extension.endsWith(".yaml") || extension.endsWith(".yml");

            // If it is a Java file, we need the class loader
            if (isJavaInput) {
                JavaContext context = getJavaContext(inputFile, inputProject);
                input = InputPreProcessor.process(cg, inputFile, context.getClassLoader());
            } else {
                input = InputPreProcessor.process(cg, inputFile, null);
            }

            List<IncrementTo> matchingIncrements = cg.getMatchingIncrements(input);

            if (matchingIncrements.isEmpty()) {
                printNoTriggersMatched(inputFile, isJavaInput, isOpenApiInput);
            }

            if (outputRootPath == null) {
                // If user did not specify the output path of the generated files, we can use the current
                // project folder
                setOutputRootPath(inputProject);
            }

            if (finalIncrements != null) {
                // We need this to allow the use of multiple input files of different types
                finalIncrements = CobiGenUtils.retainAll(matchingIncrements, finalIncrements);
            } else {
                finalIncrements = incrementsSelection(increments, matchingIncrements);
            }

            logger.info("Generating templates, this can take a while...");
            GenerationReportTo report =
                cg.generate(input, finalIncrements, Paths.get(outputRootPath.getAbsolutePath()), false, utilClasses);

            checkGenerationReport(report);

        } catch (MojoFailureException e) {
            logger.error("Invalid input for CobiGen, please check your input file.");

        }
    }

    /**
     * Set the output root path and also print a message warnign this
     * @param inputProject
     *            value to set the output root path
     */
    private void setOutputRootPath(File inputProject) {
        logger.info("As you did not specify where the code will be generated, we will use the project of your current"
            + " input file.");
        logger.debug("Generating to: " + inputProject.getAbsolutePath());

        outputRootPath = inputProject;
    }

    /**
     * Prints an error message to the user for informing that no triggers have been matched. Depending on the
     * type of the input file will print different messages.
     * @param inputFile
     *            User input file
     * @param isJavaInput
     *            true when input file is Java
     * @param isOpenApiInput
     *            true when input file is OpenAPI
     */
    private void printNoTriggersMatched(File inputFile, Boolean isJavaInput, Boolean isOpenApiInput) {
        logger.error("Your input file '" + inputFile.getName()
            + "' is not valid as input for any generation purpose. It does not match any trigger.");
        if (isJavaInput) {
            logger.error("Check that your Java input file is following devon4j naming convention. "
                + "Explained on https://github.com/devonfw/devon4j/wiki/coding-conventions");
        } else if (isOpenApiInput) {
            logger.error("Validate your OpenAPI specification, check that is following 3.0 standard. "
                + "More info here https://github.com/devonfw/tools-cobigen/wiki/cobigen-openapiplugin#usage");
        }
        System.exit(1);
    }

    /**
     * Method that handles the increments selection and prints some messages to the console
     * @param increments
     *            user selected increments
     * @param matchingIncrements
     *            all the increments that match the current input file
     * @return The final increments that will be used for generation
     */
    private List<IncrementTo> incrementsSelection(ArrayList<Integer> increments, List<IncrementTo> matchingIncrements) {

        // Print all matching increments
        int i = 0;
        List<IncrementTo> userIncrements = new ArrayList<>();
        logger.info("(0) All");
        for (IncrementTo inc : matchingIncrements) {
            logger.info("(" + ++i + ") " + inc.getDescription());
        }
        System.out.println("---------------------------------------------");

        if (increments == null || increments.size() < 1) {
            logger.info("Here are the options you have for your choice. Which increments do you want to generate?"
                + " Please list the increments number you want separated by comma:");

            increments = new ArrayList<>();
            for (String userInc : getUserInput().split(",")) {
                try {
                    increments.add(Integer.parseInt(userInc));
                } catch (NumberFormatException e) {
                    logger.error(
                        "Error parsing your input. You need to specify increments using numbers separated by comma (2,5,6).");
                    System.exit(1);
                }
            }

        } else {
            logger.info("Those are all the increments that you can select with your input file, but you have chosen:");
        }

        // Print user selected increments
        for (int j = 0; j < increments.size(); j++) {
            try {
                int selectedIncrementNumber = increments.get(j);

                // We need to generate all
                if (selectedIncrementNumber == 0) {
                    logger.info("(0) All");
                    userIncrements = matchingIncrements;
                    break;
                }
                userIncrements.add(j, matchingIncrements.get(selectedIncrementNumber - 1));
                logger.info("(" + selectedIncrementNumber + ") " + userIncrements.get(j).getDescription());
            } catch (IndexOutOfBoundsException e) {
                logger.error("The increment number you have specified is out of bounds!");
                System.exit(1);
            }
        }
        return userIncrements;
    }

    /**
     * Checks the generation report in order to find possible errors and warnings
     * @param report
     *            the generation report returned by the CobiGen.generate method
     */
    private void checkGenerationReport(GenerationReportTo report) {
        if (report.getErrors() == null || report.getErrors().isEmpty()) {
            logger.info("Successfully generated templates.\n");
        } else {
            logger.error("Generation failed due to the following problems:");
            for (Throwable throwable : report.getErrors()) {
                logger.error(throwable.getMessage());
            }
        }

        for (String warning : report.getWarnings()) {
            logger.debug("Warning: " + warning);
        }
    }

    /**
     * Tries to get the Java context by creating a new class loader of the input project that is able to load
     * the input file. We need this in order to perform reflection on the templates.
     *
     * @param inputFile
     *            input file the user wants to generate code from
     * @param inputProject
     *            input project where the input file is located. We need this in order to build the classpath
     *            of the input file
     * @return the Java context created from the input project
     */
    private JavaContext getJavaContext(File inputFile, File inputProject) {
        JavaContext context = JavaSourceProviderUsingMaven.createFromLocalMavenProject(inputProject);

        String qualifiedName = ParsingUtils.getQualifiedName(inputFile, context);

        try {
            context.getClassLoader().loadClass(qualifiedName);
        } catch (NoClassDefFoundError | ClassNotFoundException e) {
            logger.error("Compiled class " + e.getMessage()
                + " has not been found. Most probably you need to build project " + inputProject.toString() + " .");
            System.exit(1);
        }
        return context;
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
