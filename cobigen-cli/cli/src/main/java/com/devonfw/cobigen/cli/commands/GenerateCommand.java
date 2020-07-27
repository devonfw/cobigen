package com.devonfw.cobigen.cli.commands;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.JaccardDistance;
import org.apache.maven.shared.utils.io.FileUtils;
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
import com.devonfw.cobigen.cli.utils.ConfigurationUtils;
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
    private static Logger LOG = LoggerFactory.getLogger(CobiGenCLI.class);

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
            LOG.debug("Input files and output root path confirmed to be valid.");
            CobiGen cg = cobigenUtils.initializeCobiGen();

            Path templateFolder = ConfigurationUtils.getCobigenTemplatesFolderPath();

            ClassLoader inputClassLoader;

            if (templateFolder != null) {
                inputClassLoader = URLClassLoader.newInstance(new URL[] { templateFolder.toUri().toURL() },
                    getClass().getClassLoader());
            } else {
                inputClassLoader = getClass().getClassLoader();
            }

            if (increments == null && templates != null) {
                // User specified only templates, not increments
                List<TemplateTo> finalTemplates = null;
                if (inputFiles.size() > 1) {
                    finalTemplates = toTemplateTo(preprocess(cg, TemplateTo.class));
                }

                for (File inputFile : inputFiles) {
                    generate(inputFile, ParsingUtils.getProjectRoot(inputFile), finalTemplates, cg, inputClassLoader,
                        TemplateTo.class, templateFolder);
                }
            } else {

                List<IncrementTo> finalIncrements = null;
                if (inputFiles.size() > 1) {
                    finalIncrements = toIncrementTo(preprocess(cg, IncrementTo.class));
                }

                for (File inputFile : inputFiles) {
                    generate(inputFile, ParsingUtils.getProjectRoot(inputFile), finalIncrements, cg, inputClassLoader,
                        IncrementTo.class, templateFolder);
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
                LOG.error("Invalid input for CobiGen, please check your input file '{}'", inputFile.toString());

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
     * Validates the user arguments in the context of the generate command. Tries to check whether all the
     * input files and the output root path are valid.
     *
     * @return true when these arguments are correct
     */
    public Boolean areArgumentsValid() {

        int index = 0;
        for (File inputFile : inputFiles) {
            inputFile = ConfigurationUtils.preprocessInputFile(inputFile);
            // Input file can be: C:\folder\input.java
            if (inputFile.exists() == false) {
                LOG.debug(
                    "We could not find input file: {}. But we will keep trying, maybe you are using relative paths",
                    inputFile.getAbsolutePath());

                // Input file can be: folder\input.java. We should use current working directory
                if (ParsingUtils.parseRelativePath(inputFiles, inputFile, index) == false) {
                    LOG.error("Your <inputFile> '{}' has not been found", inputFile.toString());
                    return false;
                }
            }
            if (inputFile.isDirectory()) {
                LOG.error("Your input file: {} is a directory. CobiGen cannot understand that. Please use files.",
                    inputFile.getAbsolutePath());
                return false;
            }
        }

        if (outputRootPath != null) {
            outputRootPath = ConfigurationUtils.preprocessInputFile(outputRootPath);
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
     * @param classLoader
     *            a {@link ClassLoader}, containing the archive to load template utility classes from
     * @param templateFolder
     *            Path to load template utility classes from (root path of CobiGen templates)
     *
     */
    public void generate(File inputFile, File inputProject, List<? extends GenerableArtifact> finalTos, CobiGen cg,
        ClassLoader classLoader, Class<?> c, Path templateFolder) {

        Boolean isIncrements = c.getSimpleName().equals(IncrementTo.class.getSimpleName());
        inputFile = ConfigurationUtils.preprocessInputFile(inputFile);
        try {
            Object input;
            String extension = FileUtils.getExtension(inputFile.getName());
            Boolean isJavaInput = extension.equals("java");
            Boolean isOpenApiInput = extension.equals("yaml") || extension.equals("yml");

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
                LOG.info("Generating templates for input '{}', this can take a while...", inputFile.getName());
                report = cg.generate(input, finalTos, Paths.get(outputRootPath.getAbsolutePath()), false, classLoader,
                    templateFolder);
            } else {
                LOG.info("Generating increments for input '{}, this can take a while...", inputFile.getName());
                report = cg.generate(input, finalTos, Paths.get(outputRootPath.getAbsolutePath()), false, classLoader,
                    templateFolder);
            }
            if (ValidationUtils.checkGenerationReport(report)) {
                Set<Path> generatedJavaFiles = report.getGeneratedFiles().stream()
                    .filter(e -> FileUtils.getExtension(e.toAbsolutePath().toString()).equals("java"))
                    .collect(Collectors.toSet());
                if (!generatedJavaFiles.isEmpty()) {
                    try {
                        ParsingUtils.formatJavaSources(generatedJavaFiles);
                    } catch (FormatterException e) {
                        LOG.info(
                            "Generation was successful but we were not able to format your code. Maybe you will see strange formatting.",
                            LOG.isDebugEnabled() ? e : null);
                    }
                }
            }
        } catch (InputReaderException e) {
            LOG.error("Invalid input for CobiGen, please check your input file.", e);

        }
    }

    /**
     * Sets the directory where the code will be generated to
     *
     * @param inputProject
     *            project where the code will be generated to
     */
    private void setOutputRootPath(File inputProject) {
        LOG.info(
            "As you did not specify where the code will be generated, we will use the project of your current Input file.");

        LOG.debug("Generating to: {}", inputProject.getAbsolutePath());

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
                        LOG.info("(0) All");
                        userSelection = (List<GenerableArtifact>) matching;
                        return userSelection;
                    }
                    userSelection.add(j, matching.get(index));

                    String artifactDescription = isIncrements ? ((IncrementTo) matching.get(index)).getDescription()
                        : ((TemplateTo) matching.get(index)).getId();
                    LOG.info("(" + selectedArtifactNumber + ") " + artifactDescription);
                } catch (IndexOutOfBoundsException e) {
                    LOG.error("The {} number you have specified is out of bounds!", artifactType);
                    System.exit(1);
                } catch (NumberFormatException e) {
                    LOG.error(
                        "Error parsing your input. You need to specify {}s using numbers separated by comma (2,5,6).",
                        artifactType);
                    System.exit(1);
                }
            }

            // If String representation is given
            else {
                // Select all increments
                if ("all".toUpperCase().equals(currentSelectedArtifact.toUpperCase())) {
                    LOG.info("(0) All");
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
                    LOG.info("Exact match found: {}.", artifactDescription);
                    userSelection.add(possibleArtifacts.get(0));
                    return userSelection;
                } else if (possibleArtifacts.size() < 1) {
                    LOG.info(
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
            LOG.info("Here are the {}s that may match your search.", artifactType);
        }
        LOG.info("(0) " + "All");
        for (GenerableArtifact artifact : possibleArtifacts) {
            String artifactDescription =
                isIncrements ? ((IncrementTo) artifact).getDescription() : ((TemplateTo) artifact).getId();
            LOG.info("(" + (possibleArtifacts.indexOf(artifact) + 1) + ") " + artifactDescription);
        }
        LOG.info("Please enter the number(s) of {}(s) that you want to generate separated by comma.", artifactType);
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
                LOG.error("Error parsing your input. You need to specify {}s using numbers separated by comma (2,5,6).",
                    artifactType);
                System.exit(1);

            } catch (ArrayIndexOutOfBoundsException e) {
                LOG.error("Error parsing your input. Please give a valid number from the list above.");
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
            if (!isIncrements) {
                String description = ((TemplateTo) matching.get(i)).getId();
                JaccardDistance distance = new JaccardDistance();
                scores.put(matching.get(i), distance.apply(
                        description.toUpperCase(), userInput.toUpperCase()));
            } else {
                String description =
                        ((IncrementTo) matching.get(i)).getDescription();
                String id = ((IncrementTo) matching.get(i)).getId();
                JaccardDistance distance = new JaccardDistance();
                Double descriptionDistance = distance.apply(
                        description.toUpperCase(), userInput.toUpperCase());
                Double idDistance = distance.apply(id.toUpperCase(),
                        userInput.toUpperCase());
                scores.put(matching.get(i),
                        Math.min(idDistance, descriptionDistance));
            }
        }

        Map<? super GenerableArtifact, Double> sorted =
                scores.entrySet().stream().sorted(comparingByValue())
                        .collect(toMap(e -> e.getKey(), e -> e.getValue(),
                                (e1, e2) -> e2, LinkedHashMap::new));

        ArrayList<? super GenerableArtifact> chosen = new ArrayList<>();

        for (Object artifact : sorted.keySet()) {
            GenerableArtifact tmp;
            tmp = isIncrements ? (IncrementTo) artifact : (TemplateTo) artifact;
            if (!isIncrements) {
                String description = ((TemplateTo) artifact).getId();
                if (description.toUpperCase().contains(userInput.toUpperCase())
                        || sorted.get(artifact) <= SELECTION_THRESHOLD) {
                    chosen.add(tmp);
                }
            } else {
                String description = ((IncrementTo) artifact).getDescription();
                String id = ((IncrementTo) artifact).getId();
                if (description.equalsIgnoreCase(userInput)
                        || id.equalsIgnoreCase(userInput)) {
                    chosen.add(tmp);
                    return (ArrayList<? extends GenerableArtifact>) chosen;
                }
                if ((description.toUpperCase().contains(userInput.toUpperCase())
                        || id.toUpperCase().contains(userInput.toUpperCase()))
                        || sorted.get(artifact) <= SELECTION_THRESHOLD) {
                    chosen.add(tmp);
                }
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
