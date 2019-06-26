package com.devonfw.cobigen.cli.commands;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;

import org.apache.commons.text.similarity.JaccardDistance;
import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.cli.CobiGenCLI;
import com.devonfw.cobigen.cli.constants.MessagesConstants;
import com.devonfw.cobigen.cli.logger.CLILogger;
import com.devonfw.cobigen.cli.utils.CobiGenUtils;
import com.devonfw.cobigen.cli.utils.ParsingUtils;
import com.devonfw.cobigen.cli.utils.ValidationUtils;

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

    final double SELECTION_THRESHOLD = 0.3;

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

    /**
     * This option provides the use of multiple available increments
     */
    @Option(names = { "--increments", "-i" }, split = ",",
        description = MessagesConstants.INCREMENTS_OPTION_DESCRIPTION)
    /**
     * Initialize increments variable
     */
    ArrayList<Integer> increments = null;

    /**
     * This option provide specified list of template
     */
    @Option(names = { "--templates", "-t" }, split = ",", description = MessagesConstants.TEMPLATES_OPTION_DESCRIPTION)
    /**
     * Initialize templates variable
     */
    ArrayList<String> templates = null;

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

            if (increments == null && templates != null) {
                // User specified only templates, not increments
                for (File inputFile : inputFiles) {
                    generateTemplates(inputFile, ParsingUtils.getProjectRoot(inputFile), templates, cg,
                        cobigenUtils.getUtilClasses());
                }
            } else {

                List<IncrementTo> finalIncrements = null;
                if (inputFiles.size() > 1) {
                    finalIncrements = preprocessIncrements(cg);
                }
                for (File inputFile : inputFiles) {
                    generateIncrements(inputFile, ParsingUtils.getProjectRoot(inputFile), finalIncrements, cg,
                        cobigenUtils.getUtilClasses());
                }
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
                input = CobiGenUtils.getValidCobiGenInput(cg, inputFile, isJavaInput);

                List<IncrementTo> matchingIncrements = cg.getMatchingIncrements(input);

                if (matchingIncrements.isEmpty()) {
                    ValidationUtils.printNoTriggersMatched(inputFile, isJavaInput, isOpenApiInput);
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
                if (ParsingUtils.parseRelativePath(inputFiles, inputFile, index) == false) {
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
     * Generates new templates using the inputFile from the inputProject.
     *
     * @param inputFile
     *            input file the user wants to generate code from
     * @param inputProject
     *            input project where the input file is located. We need this in order to build the classpath
     *            of the input file
     * @param templates
     *            user selected templates
     * @param cg
     *            Initialized CobiGen instance
     * @param utilClasses
     *            util classes loaded from the templates jar
     *
     */
    public void generateTemplates(File inputFile, File inputProject, ArrayList<String> templates, CobiGen cg,
        List<Class<?>> utilClasses) {
        try {
            Object input;
            String extension = inputFile.getName().toLowerCase();
            Boolean isJavaInput = extension.endsWith(".java");
            Boolean isOpenApiInput = extension.endsWith(".yaml") || extension.endsWith(".yml");

            input = CobiGenUtils.getValidCobiGenInput(cg, inputFile, isJavaInput);

            // If user did not specify the output path of the generated files, we can use the current project
            // folder
            if (outputRootPath == null) {
                logger.info(
                    "As you did not specify where the code will be generated, we will use the project of your current"
                        + " input file.");
                logger.debug("Generating to: " + inputProject.getAbsolutePath());

                outputRootPath = inputProject;
            }

            // TODO failing method -> check why
            // Object input = checkInput(inputProject, inputProject, cg, isJavaInput);

            List<TemplateTo> matchingTemplates = cg.getMatchingTemplates(input);
            if (matchingTemplates.isEmpty()) {
                ValidationUtils.printNoTriggersMatched(inputFile, isJavaInput, isOpenApiInput);
            }

            List<TemplateTo> userTemplates = templatesSelection(templates, matchingTemplates);

            logger.info("Generating templates, this can take a while...");
            GenerationReportTo report =
                cg.generate(input, userTemplates, Paths.get(outputRootPath.getAbsolutePath()), false, utilClasses);

            ValidationUtils.checkGenerationReport(report);
        } catch (MojoFailureException e) {
            logger.error("Invalid input for CobiGen, please check your input file.");

        }

    }

    /**
     * Generates new increments using the inputFile from the inputProject.
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
    public void generateIncrements(File inputFile, File inputProject, List<IncrementTo> finalIncrements, CobiGen cg,
        List<Class<?>> utilClasses) {
        try {
            Object input;
            String extension = inputFile.getName().toLowerCase();
            Boolean isJavaInput = extension.endsWith(".java");
            Boolean isOpenApiInput = extension.endsWith(".yaml") || extension.endsWith(".yml");

            input = CobiGenUtils.getValidCobiGenInput(cg, inputFile, isJavaInput);

            List<IncrementTo> matchingIncrements = cg.getMatchingIncrements(input);

            if (matchingIncrements.isEmpty()) {
                ValidationUtils.printNoTriggersMatched(inputFile, isJavaInput, isOpenApiInput);
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

            logger.info("Generating templates for input '" + inputFile.getName() + "', this can take a while...");
            GenerationReportTo report =
                cg.generate(input, finalIncrements, Paths.get(outputRootPath.getAbsolutePath()), false, utilClasses);

            ValidationUtils.checkGenerationReport(report);

        } catch (MojoFailureException e) {
            logger.error("Invalid input for CobiGen, please check your input file.");

        }
    }

    // /**
    // * Checks if the inputfile is valid and if the user specified an output path
    // * @param inputFile
    // * input file the user wants to generate code from
    // * @param inputProject
    // * input project where the input file is located. We need this in order to build the classpath
    // * of the input file
    // * @param cg
    // * Initialized CobiGen instance
    // * @param isJavaInput
    // * boolean value if file is java input
    // * @return input returns the valid input Object
    // *
    // */
    // private Object checkInput(File inputFile, File inputProject, CobiGen cg, Boolean isJavaInput) {
    // Object input = null;
    // try {
    // // If it is a Java file, we need the class loader
    // if (isJavaInput) {
    // JavaContext context = ParsingUtils.getJavaContext(inputFile, inputProject);
    // input = InputPreProcessor.process(cg, inputFile, context.getClassLoader());
    // } else {
    // input = InputPreProcessor.process(cg, inputFile, null);
    // }
    //
    // // If user did not specify the output path of the generated files, we can use the current project
    // // folder
    // if (outputRootPath == null) {
    // logger.info(
    // "As you did not specify where the code will be generated, we will use the project of your current"
    // + " input file.");
    // logger.debug("Generating to: " + inputProject.getAbsolutePath());
    //
    // outputRootPath = inputProject;
    // }
    // } catch (MojoFailureException e) {
    // logger.error("Invalid input for CobiGen, please check your input file.");
    // e.printStackTrace();
    // }
    // return input;
    // }

    /**
     * Set the output root path and also print a message warning this
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
     * Method that handles the templates selection and prints some messages to the console
     * @param templates
     *            user selected templates
     * @param matchingTemplates
     *            all the templates that match the current input file
     * @return The final templates that will be used for generation
     */
    private List<TemplateTo> templatesSelection(ArrayList<String> templates, List<TemplateTo> matchingTemplates) {
        // Print all matching increments
        int i = 0;
        List<TemplateTo> userTemplates = new ArrayList<>();
        logger.info("(0) All");
        for (TemplateTo temp : matchingTemplates) {
            logger.info("(" + ++i + ") " + temp.getId());
        }
        System.out.println("---------------------------------------------");

        if (templates == null || templates.size() < 1) {
            logger.info("Here are the options you have for your choice. Which templates do you want to generate?"
                + " Please list the templates number you want separated by comma:");

            templates = new ArrayList<>();
            for (String userInc : getUserInput().split(",")) {
                try {
                    templates.add(userInc);
                } catch (NumberFormatException e) {
                    logger.error(
                        "Error parsing your input. You need to specify templates using numbers separated by comma (2,5,6).");
                    System.exit(1);
                }
            }

        } else {
            logger.info("Those are all the templates that you can select with your input file, but you have chosen:");
        }

        // Print user selected templates
        String digitMatch = "\\d+";

        for (int selectedTempNum = 0; selectedTempNum < templates.size(); selectedTempNum++) {

            String currentTemplate = templates.get(selectedTempNum);

            // If given increment is Integer
            if (currentTemplate.matches(digitMatch)) {
                try {
                    int selectedIncrementNumber = Integer.parseInt(currentTemplate);

                    // We need to generate all
                    if (selectedIncrementNumber == 0) {
                        logger.info("(0) All");
                        userTemplates = matchingTemplates;
                        break;
                    }
                    userTemplates.add(selectedTempNum, matchingTemplates.get(selectedIncrementNumber - 1));
                    logger.info("(" + selectedIncrementNumber + ") " + userTemplates.get(selectedTempNum).getId());
                } catch (IndexOutOfBoundsException e) {
                    logger.error("The increment number you have specified is out of bounds!");
                    System.exit(1);
                }

            }
            // // We need to generate all
            // if (selectedTemplatesString == "0" || selectedTemplatesString.toLowerCase().contains("all")) {
            // logger.info("(0) All");
            // userTemplates = matchingTemplates;
            // break;
            // }

            // If String representation is given
            else {
                // Select all increments
                if ("all".toUpperCase().equals(currentTemplate.toUpperCase())) {
                    logger.info("(0) All");
                    userTemplates = matchingTemplates;
                    break;
                }

                List<TemplateTo> chosenTemplates = getClosestTemplates(currentTemplate, matchingTemplates);

                if (chosenTemplates.size() > 0) {
                    logger.info(
                        "Here are the increments that may match your search. Please list the increments number you want separated by comma.");
                    logger.info("(0) " + "All");
                    for (TemplateTo temp : chosenTemplates) {
                        logger.info("(" + (chosenTemplates.indexOf(temp) + 1) + ") " + temp.getId());
                    }

                }
                logger.info("Please enter the number(s) of increment(s) that you want to generate.");

                for (String userInc : getUserInput().split(",")) {
                    try {
                        if ("0".equals(userInc)) {
                            System.out.println("DEBUG: All added");
                            userTemplates = chosenTemplates;
                            break;
                        }
                        TemplateTo currentTemplateTo = chosenTemplates.get(Integer.parseInt(userInc) - 1);
                        if (!userTemplates.contains(currentTemplateTo)) {
                            System.out.println("DEBUG: " + currentTemplateTo.getId() + " added");
                            userTemplates.add(currentTemplateTo);
                        }
                    } catch (NumberFormatException e) {
                        logger.error(
                            "Error parsing your input. You need to specify increments using numbers separated by comma (2,5,6).");
                        System.exit(1);

                    } catch (ArrayIndexOutOfBoundsException e) {
                        logger.error("Error parsing your input. Please give a valid number from the list above.");
                        System.exit(1);
                    }
                }
            }
        }
        return userTemplates;

    }

    private ArrayList<TemplateTo> getClosestTemplates(String template, List<TemplateTo> matchingTemplates) {

        Map<TemplateTo, Double> scores = new HashMap<TemplateTo, Double>();
        for (TemplateTo temp : matchingTemplates) {
            String tempId = temp.getId();
            JaccardDistance distance = new JaccardDistance();
            // LevenshteinDistance distance = new LevenshteinDistance();
            scores.put(temp, distance.apply(tempId.toUpperCase(), template.toUpperCase()));
        }
        Map<TemplateTo, Double> sorted = scores.entrySet().stream().sorted(comparingByValue())
            .collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));

        ArrayList<TemplateTo> chosenIncrements = new ArrayList<>();
        for (TemplateTo temp : sorted.keySet()) {
            String incDescription = temp.getId();
            if (incDescription.toUpperCase().contains(template.toUpperCase())
                || sorted.get(temp) <= SELECTION_THRESHOLD) {
                chosenIncrements.add(temp);
            }
        }
        return chosenIncrements;
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
