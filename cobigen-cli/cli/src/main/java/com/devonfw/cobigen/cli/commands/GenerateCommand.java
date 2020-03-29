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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.exception.InputReaderException;
import com.devonfw.cobigen.api.to.GenerableArtifact;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.cli.CobiGenCLI;
import com.devonfw.cobigen.cli.constants.MessagesConstants;
import com.devonfw.cobigen.cli.logger.CLILogger;
import com.devonfw.cobigen.cli.utils.CobiGenUtils;
import com.devonfw.cobigen.cli.utils.ParsingUtils;
import com.devonfw.cobigen.cli.utils.ValidationUtils;
import com.google.googlejavaformat.java.FormatterException;

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
     * Selection threshold when user tries to find closest increments and templates
     */
    final double SELECTION_THRESHOLD = 0.1;

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
    ArrayList<String> increments = null;

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
            CobiGen cg = cobigenUtils.initializeCobiGen();

            if (increments == null && templates != null) {
                // User specified only templates, not increments
                List<TemplateTo> finalTemplates = null;
                if (inputFiles.size() > 1) {
                    finalTemplates = toTemplateTo(preprocess(cg, TemplateTo.class));
                }
                for (File inputFile : inputFiles) {
                    generate(inputFile, ParsingUtils.getProjectRoot(inputFile), finalTemplates, cg,
                        cobigenUtils.getUtilClasses(), TemplateTo.class);
                }
            } else {

                List<IncrementTo> finalIncrements = null;
                if (inputFiles.size() > 1) {
                    finalIncrements = toIncrementTo(preprocess(cg, IncrementTo.class));
                }
                for (File inputFile : inputFiles) {
                    generate(inputFile, ParsingUtils.getProjectRoot(inputFile), finalIncrements, cg,
                        cobigenUtils.getUtilClasses(), IncrementTo.class);
                }
            }
            return 0;
        }

        return 1;

    }

    /**
     * For each input file it is going to get its matching templates or increments and then performs an
     * intersection between all of them, so that the user gets only the templates or increments that will work
     *
     * @param cg
     *            CobiGen initialized instance
     * @param c
     *            class type, specifies whether Templates or Increments should be preprocessed
     * @return List of templates that the user will be able to use
     *
     */
    private List<? extends GenerableArtifact> preprocess(CobiGen cg, Class<?> c) {
        Boolean isIncrements = c.getSimpleName().equals(IncrementTo.class.getSimpleName());
        Boolean firstIteration = true;
        List<? extends GenerableArtifact> finalTos = new ArrayList<>();

        for (File inputFile : inputFiles) {

            Object input;
            String extension = inputFile.getName().toLowerCase();
            Boolean isJavaInput = extension.endsWith(".java");
            Boolean isOpenApiInput = extension.endsWith(".yaml") || extension.endsWith(".yml");

            try {
                input = CobiGenUtils.getValidCobiGenInput(cg, inputFile, isJavaInput);
                List<? extends GenerableArtifact> matching =
                    isIncrements ? cg.getMatchingIncrements(input) : cg.getMatchingTemplates(input);

                if (matching.isEmpty()) {
                    ValidationUtils.printNoTriggersMatched(inputFile, isJavaInput, isOpenApiInput);
                }

                if (firstIteration) {
                    finalTos = matching;
                    firstIteration = false;
                } else {
                    // We do the intersection between the previous increments and the new ones
                    finalTos = isIncrements
                        ? CobiGenUtils.retainAllIncrements(toIncrementTo(finalTos), toIncrementTo(matching))
                        : CobiGenUtils.retainAllTemplates(toTemplateTo(finalTos), toTemplateTo(matching));
                }

            } catch (InputReaderException e) {
                logger.error("Invalid input for CobiGen, please check your input file '" + inputFile.toString() + "'");

            }

        }
        return isIncrements ? generableArtifactSelection(increments, toIncrementTo(finalTos), IncrementTo.class)
            : generableArtifactSelection(templates, toIncrementTo(finalTos), TemplateTo.class);
    }

    /**
     * Casting class, from List<subclasses of GenerableArtifact> to List<IncrementTo>
     *
     * @param matching
     *            List containing instances of subclasses of GenerableArtifact
     * @return casted list containing instances of subclasses of IncrementTo
     */
    @SuppressWarnings("unchecked")
    private List<IncrementTo> toIncrementTo(List<? extends GenerableArtifact> matching) {
        return (List<IncrementTo>) matching;
    }

    /**
     * Casting class, from List<subclasses of GenerableArtifact> to List<TemplateTo>
     *
     * @param matching
     *            List containing instances of subclasses of GenerableArtifact
     * @return casted list containing instances of subclasses of TemplateTo
     */
    @SuppressWarnings("unchecked")
    private List<TemplateTo> toTemplateTo(List<? extends GenerableArtifact> matching) {
        return (List<TemplateTo>) matching;
    }

    /**
     * Processes the input file's path. Strips the quotes from the file path if they are given.
     * @param inputFile
     *            the input file
     * @return input file with processed path
     */
    private File preprocessInputFile(File inputFile) {
        String path = inputFile.getPath();
        String pattern = "[\\\"|\\'](.+)[\\\"|\\']";
        boolean matches = path.matches(pattern);
        if (matches) {
            path = path.replace("\"", "");
            path = path.replace("\'", "");
            return new File(path);
        }

        return inputFile;
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
            inputFile = preprocessInputFile(inputFile);
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

        if (outputRootPath != null) {
            outputRootPath = preprocessInputFile(outputRootPath);
        }
        return ValidationUtils.isOutputRootPathValid(outputRootPath);

    }

    /**
     * Generates new templates or increments using the inputFile from the inputProject.
     *
     * @param inputFile
     *            input file the user wants to generate code from
     * @param inputProject
     *            input project where the input file is located. We need this in order to build the classpath
     *            of the input file
     * @param finalTos
     *            the list of increments or templates that the user is going to use for generation
     * @param cg
     *            Initialized CobiGen instance
     * @param c
     *            class type, specifies whether Templates or Increments should be preprocessed
     * @param utilClasses
     *            util classes loaded from the templates jar
     *
     */
    public void generate(File inputFile, File inputProject, List<? extends GenerableArtifact> finalTos, CobiGen cg,
        List<Class<?>> utilClasses, Class<?> c) {

        Boolean isIncrements = c.getSimpleName().equals(IncrementTo.class.getSimpleName());
        inputFile = preprocessInputFile(inputFile);
        try {
            Object input;
            String extension = inputFile.getName().toLowerCase();
            Boolean isJavaInput = extension.endsWith(".java");
            Boolean isOpenApiInput = extension.endsWith(".yaml") || extension.endsWith(".yml");

            input = CobiGenUtils.getValidCobiGenInput(cg, inputFile, isJavaInput);

            List<? extends GenerableArtifact> matching =
                isIncrements ? cg.getMatchingIncrements(input) : cg.getMatchingTemplates(input);

            if (matching.isEmpty()) {
                ValidationUtils.printNoTriggersMatched(inputFile, isJavaInput, isOpenApiInput);
            }

            if (outputRootPath == null) {
                // If user did not specify the output path of the generated files, we can use
                // the current
                // project folder
                setOutputRootPath(inputProject);
            }

            if (finalTos != null) {
                // We need this to allow the use of multiple input files of different types
                finalTos =
                    isIncrements ? CobiGenUtils.retainAllIncrements(toIncrementTo(matching), toIncrementTo(finalTos))
                        : CobiGenUtils.retainAllTemplates(toTemplateTo(matching), toTemplateTo(finalTos));
            } else {
                finalTos =
                    isIncrements ? generableArtifactSelection(increments, toIncrementTo(matching), IncrementTo.class)
                        : generableArtifactSelection(templates, toIncrementTo(matching), TemplateTo.class);
            }

            GenerationReportTo report = null;

            if (!isIncrements) {
                logger.info("Generating templates for input '" + inputFile.getName() + "', this can take a while...");
                report = cg.generate(input, finalTos, Paths.get(outputRootPath.getAbsolutePath()), false, utilClasses);
            } else {
                logger.info("Generating increments for input '" + inputFile.getName() + "', this can take a while...");
                report = cg.generate(input, finalTos, Paths.get(outputRootPath.getAbsolutePath()), false, utilClasses);
            }
            if (ValidationUtils.checkGenerationReport(report) && isJavaInput) {
                try {
                    ParsingUtils.formatJavaSources(report.getGeneratedFiles());
                } catch (FormatterException e) {
                    logger.info(
                        "Generation was successful but we were not able to format your code. Maybe you will see strange formatting.");
                }
            }
        } catch (InputReaderException e) {
            logger.error("Invalid input for CobiGen, please check your input file.");

        }
    }

    /**
     * Sets the directory where the code will be generated to
     *
     * @param inputProject
     *            project where the code will be generated to
     */
    private void setOutputRootPath(File inputProject) {
        logger.info("As you did not specify where the code will be generated, we will use the project of your current"
            + " Input file.");

        logger.debug("Generating to: " + inputProject.getAbsolutePath());

        outputRootPath = inputProject;
    }

    /**
     * Method that handles the increments selection and prints some messages to the console
     *
     * @param userInputIncrements
     *            user selected increments
     * @param matching
     *            all the increments that match the current input file
     * @param c
     *            class type, specifies whether Templates or Increments should be preprocessed
     * @return The final increments that will be used for generation
     */
    @SuppressWarnings("unchecked")
    private List<? extends GenerableArtifact> generableArtifactSelection(ArrayList<String> userInputIncrements,
        List<? extends GenerableArtifact> matching, Class<?> c) {

        Boolean isIncrements = c.getSimpleName().equals(IncrementTo.class.getSimpleName());
        List<GenerableArtifact> userSelection = new ArrayList<>();
        String artifactType = isIncrements ? "increment" : "template";
        if (userInputIncrements == null || userInputIncrements.size() < 1) {
            // Print all matching generable artifacts add new arg userInputIncrements
            printFoundArtifacts(new ArrayList<GenerableArtifact>(matching), isIncrements, artifactType,
                userInputIncrements);

            userInputIncrements = new ArrayList<>();
            for (String userArtifact : getUserInput().split(",")) {
                userInputIncrements.add(userArtifact);
            }
        }

        // Print user selected increments
        for (int j = 0; j < userInputIncrements.size(); j++) {
            String currentSelectedArtifact = userInputIncrements.get(j);

            String digitMatch = "\\d+";
            // If given generable artifact is Integer
            if (currentSelectedArtifact.matches(digitMatch)) {
                try {
                    int selectedArtifactNumber = Integer.parseInt(currentSelectedArtifact);
                    int index = selectedArtifactNumber - 1;

                    // We need to generate all
                    if (selectedArtifactNumber == 0) {
                        logger.info("(0) All");
                        userSelection = (List<GenerableArtifact>) matching;
                        return userSelection;
                    }
                    userSelection.add(j, matching.get(index));

                    String artifactDescription = isIncrements ? ((IncrementTo) matching.get(index)).getDescription()
                        : ((TemplateTo) matching.get(index)).getId();
                    logger.info("(" + selectedArtifactNumber + ") " + artifactDescription);
                } catch (IndexOutOfBoundsException e) {
                    logger.error("The " + artifactType + " number you have specified is out of bounds!");
                    System.exit(1);
                } catch (NumberFormatException e) {
                    logger.error("Error parsing your input. You need to specify " + artifactType
                        + "s using numbers separated by comma (2,5,6).");
                    System.exit(1);
                }
            }

            // If String representation is given
            else {
                // Select all increments
                if ("all".toUpperCase().equals(currentSelectedArtifact.toUpperCase())) {
                    logger.info("(0) All");
                    userSelection = (List<GenerableArtifact>) matching;
                    return userSelection;
                }

                ArrayList<GenerableArtifact> possibleArtifacts = new ArrayList<>();
                if (isIncrements) {
                    possibleArtifacts =
                        (ArrayList<GenerableArtifact>) search(currentSelectedArtifact, matching, IncrementTo.class);
                } else {
                    possibleArtifacts =
                        (ArrayList<GenerableArtifact>) search(currentSelectedArtifact, matching, TemplateTo.class);
                }

                if (possibleArtifacts.size() > 1) {
                    printFoundArtifacts(possibleArtifacts, isIncrements, artifactType, userInputIncrements);
                } else if (possibleArtifacts.size() == 1) {
                    String artifactDescription =
                        isIncrements ? ((IncrementTo) possibleArtifacts.get(0)).getDescription()
                            : ((TemplateTo) possibleArtifacts.get(0)).getId();
                    logger.info("Exact match found: " + artifactDescription + ".");
                    userSelection.add(possibleArtifacts.get(0));
                    return userSelection;
                } else if (possibleArtifacts.size() < 1) {
                    logger.info(
                        "No increment with that name has been found, Please provide correct increment name and try again ! Thank you");

                    System.exit(1);
                }

                userSelection = artifactStringSelection(userSelection, possibleArtifacts, artifactType);
            }

        }

        return userSelection;

    }

    /**
     * Prints all the generable artifacts (increments or templates) that have matched the string search
     *
     * @param possibleArtifacts
     *            list of possible artifacts the user can select
     * @param isIncrements
     *            true if we want to generate increments
     * @param artifactType
     *            type of artifact (increment or template)
     * @param userInputIncrements
     *            user selected increments
     *
     */
    private void printFoundArtifacts(ArrayList<GenerableArtifact> possibleArtifacts, Boolean isIncrements,
        String artifactType, ArrayList<String> userInputIncrements) {
        if (userInputIncrements != null) {
            logger.info("Here are the " + artifactType + "s that may match your search.");
        }
        logger.info("(0) " + "All");
        for (GenerableArtifact artifact : possibleArtifacts) {
            String artifactDescription =
                isIncrements ? ((IncrementTo) artifact).getDescription() : ((TemplateTo) artifact).getId();
            logger.info("(" + (possibleArtifacts.indexOf(artifact) + 1) + ") " + artifactDescription);
        }
        logger.info(
            "Please enter the number(s) of " + artifactType + "(s) that you want to generate separated by comma.");
    }

    /**
     * Handles the selection of generable artifacts (increments or templates) by String.
     *
     * @param userSelection
     *            previous selected artifacts that user wants to generate
     * @param possibleArtifacts
     *            list of possible artifacts the user can select
     * @param artifactType
     *            type of artifact (increment or template)
     * @return final user selection including previous ones
     */
    private List<GenerableArtifact> artifactStringSelection(List<GenerableArtifact> userSelection,
        ArrayList<GenerableArtifact> possibleArtifacts, String artifactType) {
        for (String userArtifact : getUserInput().split(",")) {
            try {
                if ("0".equals(userArtifact)) {
                    userSelection = possibleArtifacts;
                    return userSelection;
                }
                GenerableArtifact currentArtifact = possibleArtifacts.get(Integer.parseInt(userArtifact) - 1);
                if (!userSelection.contains(currentArtifact)) {
                    userSelection.add(currentArtifact);
                }
            } catch (NumberFormatException e) {
                logger.error("Error parsing your input. You need to specify " + artifactType
                    + "s using numbers separated by comma (2,5,6).");
                System.exit(1);

            } catch (ArrayIndexOutOfBoundsException e) {
                logger.error("Error parsing your input. Please give a valid number from the list above.");
                System.exit(1);
            }
        }
        return userSelection;
    }

    /**
     * Search for generable artifacts (increments or templates) matching the user input. Generable artifacts
     * similar to the given search string or containing it are returned.
     *
     * @param userInput
     *            the user's wished increment or template
     * @param matching
     *            all increments or templates that are valid to the input file(s)
     * @param c
     *            class type, specifies whether Templates or Increments should be preprocessed
     * @return Increments or templates matching the search string
     */
    @SuppressWarnings("unchecked")
    private ArrayList<? extends GenerableArtifact> search(String userInput, List<? extends GenerableArtifact> matching,
        Class<?> c) {
        Boolean isIncrements = c.getSimpleName().equals(IncrementTo.class.getSimpleName());
        Map<? super GenerableArtifact, Double> scores = new HashMap<>();

        for (int i = 0; i < matching.size(); i++) {
            String description = isIncrements ? ((IncrementTo) matching.get(i)).getDescription()
                : ((TemplateTo) matching.get(i)).getId();
            JaccardDistance distance = new JaccardDistance();
            scores.put(matching.get(i), distance.apply(description.toUpperCase(), userInput.toUpperCase()));
        }

        Map<? super GenerableArtifact, Double> sorted = scores.entrySet().stream().sorted(comparingByValue())
            .collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));

        ArrayList<? super GenerableArtifact> chosen = new ArrayList<>();

        for (Object artifact : sorted.keySet()) {
            GenerableArtifact tmp;
            tmp = isIncrements ? (IncrementTo) artifact : (TemplateTo) artifact;
            String description =
                isIncrements ? ((IncrementTo) artifact).getDescription() : ((TemplateTo) artifact).getId();
            if (description.toUpperCase().contains(userInput.toUpperCase())
                || sorted.get(artifact) <= SELECTION_THRESHOLD) {
                chosen.add(tmp);
            }
        }

        return (ArrayList<? extends GenerableArtifact>) chosen;
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
