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
            cobigenUtils.getTemplatesJar(false);
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

            } catch (MojoFailureException e) {
                logger.error("Invalid input for CobiGen, please check your input file '" + inputFile.toString() + "'");

            }

        }
        return isIncrements ? incrementsSelection(increments, toIncrementTo(finalTos))
            : templatesSelection(templates, toTemplateTo(finalTos));
    }

    /**
     * Casting class, from List<subclasses of GenerableArtifact> to List<IncrementTo>
     * @param matching
     *            List containing instances of subclasses of GenerableArtifact
     * @return casted list containing instances of subclasses of IncrementTo
     */
    private List<IncrementTo> toIncrementTo(List<? extends GenerableArtifact> matching) {
        return (List<IncrementTo>) matching;
    }

    /**
     * Casting class, from List<subclasses of GenerableArtifact> to List<TemplateTo>
     * @param matching
     *            List containing instances of subclasses of GenerableArtifact
     * @return casted list containing instances of subclasses of TemplateTo
     */
    private List<TemplateTo> toTemplateTo(List<? extends GenerableArtifact> matching) {
        return (List<TemplateTo>) matching;
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
                // If user did not specify the output path of the generated files, we can use the current
                // project folder
                setOutputRootPath(inputProject);
            }

            if (finalTos != null) {
                // We need this to allow the use of multiple input files of different types
                finalTos =
                    isIncrements ? CobiGenUtils.retainAllIncrements(toIncrementTo(finalTos), toIncrementTo(matching))
                        : CobiGenUtils.retainAllTemplates(toTemplateTo(finalTos), toTemplateTo(matching));
            } else {
                finalTos = isIncrements ? incrementsSelection(increments, toIncrementTo(matching))
                    : templatesSelection(templates, toTemplateTo(matching));
            }

            GenerationReportTo report = null;

            if (!isIncrements) {
                logger.info("Generating templates for input '" + inputFile.getName() + "', this can take a while...");
                report = cg.generate(input, finalTos, Paths.get(outputRootPath.getAbsolutePath()), false, utilClasses);
            } else {
                logger.info("Generating increments for input '" + inputFile.getName() + "', this can take a while...");
                report = cg.generate(input, finalTos, Paths.get(outputRootPath.getAbsolutePath()), false, utilClasses);
            }
            ValidationUtils.checkGenerationReport(report);
        } catch (MojoFailureException e) {
            logger.error("Invalid input for CobiGen, please check your input file.");

        }
    }

    /**
     * Sets the directory where the code will be generated to
     * @param inputProject
     *            project where the code will be generated to
     */
    private void setOutputRootPath(File inputProject) {
        logger.info("As you did not specify where the code will be generated, we will use the project of your current"
            + " input file.");
        logger.debug("Generating to: " + inputProject.getAbsolutePath());

        outputRootPath = inputProject;
    }

    /**
     * Method that handles the increments selection and prints some messages to the console
     *
     * @param increments
     *            user selected increments
     * @param matchingIncrements
     *            all the increments that match the current input file
     * @return The final increments that will be used for generation
     */
    private List<IncrementTo> incrementsSelection(ArrayList<String> increments, List<IncrementTo> matchingIncrements) {

        // Print all matching increments
        int i = 0;
        List<IncrementTo> userIncrements = new ArrayList<>();

        if (increments == null || increments.size() < 1) {
            logger.info("(0) All");
            for (IncrementTo inc : matchingIncrements) {
                String incDescription = inc.getDescription();

                logger.info("(" + ++i + ") " + incDescription);

            }

            logger.info("Here are the options you have for your choice. Which increments do you want to generate?"
                + " Please list the increments number you want separated by comma:");

            increments = new ArrayList<>();
            for (String userInc : getUserInput().split(",")) {
                try {
                    increments.add(userInc);
                } catch (NumberFormatException e) {
                    logger.error(
                        "Error parsing your input. You need to specify increments using numbers separated by comma (2,5,6).");
                    System.exit(1);
                }
            }
        } else {
            logger.info("The increments that you have chosen are:");
        }

        // Print user selected increments
        String digitMatch = "\\d+";
        for (int j = 0; j < increments.size(); j++) {
            String currentIncrement = increments.get(j);

            // If given increment is Integer
            if (currentIncrement.matches(digitMatch)) {
                try {
                    int selectedIncrementNumber = Integer.parseInt(currentIncrement);

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

            // If String representation is given
            else {
                // Select all increments
                if ("all".toUpperCase().equals(currentIncrement.toUpperCase())) {
                    logger.info("(0) All");
                    userIncrements = matchingIncrements;
                    break;
                }

                ArrayList<IncrementTo> chosenIncrements =
                    (ArrayList<IncrementTo>) search(currentIncrement, matchingIncrements, IncrementTo.class);

                if (chosenIncrements.size() > 0) {
                    logger.info("Here are the increments that may match your search.");
                    logger.info("(0) " + "All");
                    for (IncrementTo inc : chosenIncrements) {
                        logger.info("(" + (chosenIncrements.indexOf(inc) + 1) + ") " + inc.getDescription());
                    }

                }
                logger.info("Please enter the number(s) of increment(s) that you want to generate separated by comma.");

                for (String userInc : getUserInput().split(",")) {
                    try {
                        if ("0".equals(userInc)) {
                            userIncrements = chosenIncrements;
                            break;
                        }
                        IncrementTo currentIncrementTo = chosenIncrements.get(Integer.parseInt(userInc) - 1);
                        if (!userIncrements.contains(currentIncrementTo)) {
                            userIncrements.add(currentIncrementTo);
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
        return userIncrements;

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
        List<TemplateTo> userTemplates = new ArrayList<>();

        // Print user selected templates
        String digitMatch = "\\d+";

        for (int selectedTempNum = 0; selectedTempNum < templates.size(); selectedTempNum++) {

            String currentTemplate = templates.get(selectedTempNum);

            // If given template is Integer
            if (currentTemplate.matches(digitMatch)) {
                try {
                    int selectedTemplateNumber = Integer.parseInt(currentTemplate);

                    // We need to generate all
                    if (selectedTemplateNumber == 0) {
                        logger.info("(0) All");
                        userTemplates = matchingTemplates;
                        break;
                    }

                    if (userTemplates.size() == 0) {
                        logger.info("The templates that you have chosen are:");
                    }

                    userTemplates.add(selectedTempNum, matchingTemplates.get(selectedTemplateNumber - 1));
                    logger.info("(" + selectedTemplateNumber + ") " + userTemplates.get(selectedTempNum).getId());
                } catch (IndexOutOfBoundsException e) {
                    logger.error("The template number you have specified is out of bounds!");
                    System.exit(1);
                }

            }

            // If String representation is given
            else {
                // Select all templates
                if ("all".toUpperCase().equals(currentTemplate.toUpperCase())) {
                    logger.info("(0) All");
                    userTemplates = matchingTemplates;
                    break;
                }

                // List<TemplateTo> chosenTemplates = getClosestTemplates(currentTemplate, matchingTemplates);
                ArrayList<TemplateTo> chosenTemplates =
                    (ArrayList<TemplateTo>) search(currentTemplate, matchingTemplates, TemplateTo.class);

                if (chosenTemplates.size() > 0) {
                    logger.info(
                        "Here are the templates that may match your search. Please list the templates number you want separated by comma.");
                    logger.info("(0) " + "All");
                    for (TemplateTo temp : chosenTemplates) {
                        logger.info("(" + (chosenTemplates.indexOf(temp) + 1) + ") " + temp.getId());
                    }

                }
                logger.info("Please enter the number(s) of template(s) that you want to generate.");

                for (String userInc : getUserInput().split(",")) {
                    try {
                        if ("0".equals(userInc)) {
                            userTemplates = chosenTemplates;
                            break;
                        }
                        TemplateTo currentTemplateTo = chosenTemplates.get(Integer.parseInt(userInc) - 1);

                        if (userTemplates.size() == 0) {
                            logger.info("The templates that you have chosen are:");
                        }

                        if (!userTemplates.contains(currentTemplateTo)) {
                            userTemplates.add(currentTemplateTo);
                            logger.info("(" + userInc + ") " + currentTemplateTo.getId());
                        }
                    } catch (NumberFormatException e) {
                        logger.error(
                            "Error parsing your input. You need to specify templates using numbers separated by comma (2,5,6).");
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

    /**
     * Search for increments matching the user input. Increments similar to the given search string or
     * containing it are returned.
     * @param increment
     *            the user's wished increment
     * @param matchingIncrements
     *            all increments that are valid to the input file(s)
     * @param c
     *            class type, specifies whether Templates or Increments should be preprocessed
     * @return Increments matching the search string
     */
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
