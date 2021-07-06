package com.devonfw.cobigen.cli.commands;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.JaccardDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.exception.InputReaderException;
import com.devonfw.cobigen.api.to.GenerableArtifact;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.api.util.MavenUtil;
import com.devonfw.cobigen.api.util.Tuple;
import com.devonfw.cobigen.cli.CobiGenCLI;
import com.devonfw.cobigen.cli.constants.MessagesConstants;
import com.devonfw.cobigen.cli.utils.CobiGenUtils;
import com.devonfw.cobigen.cli.utils.ParsingUtils;
import com.devonfw.cobigen.cli.utils.ValidationUtils;
import com.google.googlejavaformat.java.FormatterException;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * This class handles the generation command
 */
@Command(description = MessagesConstants.GENERATE_DESCRIPTION, name = "generate", aliases = { "g" },
    mixinStandardHelpOptions = true)
public class GenerateCommand extends CommandCommons {

    /**
     * Selection threshold when user tries to find closest increments and templates
     */
    final double SELECTION_THRESHOLD = 0.1;

    /**
     * User input file
     */
    @Parameters(index = "0", arity = "1..*", split = ",", description = MessagesConstants.INPUT_FILE_DESCRIPTION)
    List<File> inputFiles = null;

    /**
     * User output project
     */
    @Option(names = { "--out", "-o" }, arity = "0..1", description = MessagesConstants.OUTPUT_ROOT_PATH_DESCRIPTION)
    File outputRootPath = null;

    /**
     * This option provides the use of multiple available increments
     */
    @Option(names = { "--increments", "-i" }, split = ",",
        description = MessagesConstants.INCREMENTS_OPTION_DESCRIPTION)
    List<String> increments = null;

    /**
     * This option provide specified list of template
     */
    @Option(names = { "--templates", "-t" }, split = ",", description = MessagesConstants.TEMPLATES_OPTION_DESCRIPTION)
    List<String> templates = null;

    /**
     * Logger to output useful information to the user
     */
    private static Logger LOG = LoggerFactory.getLogger(CobiGenCLI.class);

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
    public Integer doAction() throws Exception {

        if (!areArgumentsValid()) {
            return 1;
        }

        LOG.debug("Input files and output root path confirmed to be valid.");
        CobiGen cg = CobiGenUtils.initializeCobiGen(templatesProject);

        if (increments == null && templates != null) {
            Tuple<List<Object>, List<TemplateTo>> inputsAndArtifacts = preprocess(cg, TemplateTo.class);
            for (int i = 0; i < inputsAndArtifacts.getA().size(); i++) {
                generate(inputFiles.get(i).toPath(), inputsAndArtifacts.getA().get(i),
                    MavenUtil.getProjectRoot(inputFiles.get(i).toPath(), false), inputsAndArtifacts.getB(), cg,
                    TemplateTo.class);
            }
        } else {
            Tuple<List<Object>, List<IncrementTo>> inputsAndArtifacts = preprocess(cg, IncrementTo.class);
            for (int i = 0; i < inputsAndArtifacts.getA().size(); i++) {
                generate(inputFiles.get(i).toPath(), inputsAndArtifacts.getA().get(i),
                    MavenUtil.getProjectRoot(inputFiles.get(i).toPath(), false), inputsAndArtifacts.getB(), cg,
                    IncrementTo.class);
            }
        }
        return 0;
    }

    /**
     * For each input file it is going to get its matching templates or increments and then performs an
     * intersection between all of them, so that the user gets only the templates or increments that will work
     * @param <T>
     *            type of generable artifacts to be pre-processed
     *
     * @param cg
     *            CobiGen initialized instance
     * @param c
     *            class type, specifies whether Templates or Increments should be preprocessed
     * @return List of templates that the user will be able to use
     *
     */
    @SuppressWarnings("unchecked")
    private <T extends GenerableArtifact> Tuple<List<Object>, List<T>> preprocess(CobiGen cg, Class<T> c) {
        boolean isIncrements = c.getSimpleName().equals(IncrementTo.class.getSimpleName());
        boolean firstIteration = true;
        List<T> finalTos = new ArrayList<>();
        List<Object> generationInputs = new ArrayList<>();
        for (File inputFile : inputFiles) {

            String extension = inputFile.getName().toLowerCase();
            boolean isJavaInput = extension.endsWith(".java");
            boolean isOpenApiInput = extension.endsWith(".yaml") || extension.endsWith(".yml");

            try {
                Object input = cg.read(inputFile.toPath(), StandardCharsets.UTF_8);
                List<T> matching =
                    (List<T>) (isIncrements ? cg.getMatchingIncrements(input) : cg.getMatchingTemplates(input));

                if (matching.isEmpty()) {
                    ValidationUtils.printNoTriggersMatched(inputFile, isJavaInput, isOpenApiInput);
                }

                if (firstIteration) {
                    finalTos = matching;
                    firstIteration = false;
                } else {
                    // We do the intersection between the previous increments and the new ones
                    finalTos = (List<T>) (isIncrements
                        ? CobiGenUtils.retainAllIncrements(toIncrementTo(finalTos), toIncrementTo(matching))
                        : CobiGenUtils.retainAllTemplates(toTemplateTo(finalTos), toTemplateTo(matching)));
                }
                generationInputs.add(input);
            } catch (InputReaderException e) {
                LOG.error("Invalid input for CobiGen, please check your input file '{}'", inputFile.toString());
            }
        }

        if (finalTos.isEmpty()) {
            LOG.error(
                "There are no common Templates/Increments which could be generated from every of your inputs. Please think about executing generation one by one input file.");
            throw new InputMismatchException("No compatible input files.");
        }

        List<T> selectedGenerableArtifacts =
            (List<T>) (isIncrements ? generableArtifactSelection(increments, toIncrementTo(finalTos), IncrementTo.class)
                : generableArtifactSelection(templates, toTemplateTo(finalTos), TemplateTo.class));
        return new Tuple<>(generationInputs, selectedGenerableArtifacts);
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
    public boolean areArgumentsValid() {

        int index = 0;
        for (File inputFile : inputFiles) {
            inputFile = preprocessInputFile(inputFile);
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
            outputRootPath = preprocessInputFile(outputRootPath);
        }
        return ValidationUtils.isOutputRootPathValid(outputRootPath);

    }

    /**
     * Generates new templates or increments using the inputFile from the inputProject.
     * @param <T>
     *            type of generable artifacts to generate
     * @param inputFile
     *            input file to be selected by the user
     * @param input
     *            parsed by CobiGen read method
     * @param inputProject
     *            input project where the input file is located. We need this in order to build the classpath
     *            of the input file
     * @param generableArtifacts
     *            the list of increments or templates that the user is going to use for generation
     * @param cg
     *            Initialized CobiGen instance
     * @param c
     *            class type, specifies whether Templates or Increments should be preprocessed
     *
     */
    public <T extends GenerableArtifact> void generate(Path inputFile, Object input, Path inputProject,
        List<T> generableArtifacts, CobiGen cg, Class<T> c) {

        boolean isIncrements = c.getSimpleName().equals(IncrementTo.class.getSimpleName());
        if (outputRootPath == null) {
            // If user did not specify the output path of the generated files, we can use
            // the current project folder
            setOutputRootPath(inputProject);
        }

        GenerationReportTo report = null;
        LOG.info("Generating {} for input '{}, this can take a while...", isIncrements ? "increments" : "templates",
            inputFile);
        report = cg.generate(input, generableArtifacts, Paths.get(outputRootPath.getAbsolutePath()), false,
            (task, progress) -> {
            });
        ValidationUtils.checkGenerationReport(report);
        Set<Path> generatedJavaFiles = report.getGeneratedFiles().stream()
            .filter(e -> e.getFileName().endsWith(".java")).collect(Collectors.toSet());
        if (!generatedJavaFiles.isEmpty()) {
            try {
                ParsingUtils.formatJavaSources(generatedJavaFiles);
            } catch (FormatterException e) {
                LOG.warn(
                    "Generation was successful but we were not able to format your code. Maybe you will see strange formatting.");
            }
        }
    }

    /**
     * Sets the directory where the code will be generated to
     *
     * @param inputProject
     *            project where the code will be generated to
     */
    private void setOutputRootPath(Path inputProject) {
        LOG.info(
            "As you did not specify where the code will be generated, we will use the project of your current Input file.");
        LOG.debug("Generating to: {}", inputProject);

        outputRootPath = inputProject.toFile();
    }

    /**
     * Method that handles the increments selection and prints some messages to the console
     * @param <T>
     *            type of generable artifacts
     *
     * @param userInputIncrements
     *            user selected increments
     * @param matching
     *            all the increments that match the current input file
     * @param c
     *            class type, specifies whether Templates or Increments should be preprocessed
     * @return The final increments that will be used for generation
     */
    private <T extends GenerableArtifact> List<T> generableArtifactSelection(List<String> userInputIncrements,
        List<T> matching, Class<T> c) {

        boolean isIncrements = c.getSimpleName().equals(IncrementTo.class.getSimpleName());
        List<T> userSelection = new ArrayList<>();
        String artifactType = isIncrements ? "increment" : "template";
        if (userInputIncrements == null || userInputIncrements.size() < 1) {
            // Print all matching generable artifacts add new arg userInputIncrements
            printFoundArtifacts(matching, isIncrements, artifactType, userInputIncrements);

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
                        return matching;
                    }
                    userSelection.add(j, matching.get(index));

                    String artifactDescription = isIncrements ? ((IncrementTo) matching.get(index)).getDescription()
                        : ((TemplateTo) matching.get(index)).getId();
                    LOG.info("(" + selectedArtifactNumber + ") " + artifactDescription);
                } catch (IndexOutOfBoundsException e) {
                    LOG.error("The {} number you have specified is out of bounds!", artifactType);
                    throw (e);
                } catch (NumberFormatException e) {
                    LOG.error(
                        "Error parsing your input. You need to specify {}s using numbers separated by comma (2,5,6).",
                        artifactType);
                    throw (e);
                }
            }

            // If String representation is given
            else {
                // Select all increments
                if ("ALL".equals(currentSelectedArtifact.toUpperCase())) {
                    LOG.info("(0) All");
                    return matching;
                }

                List<T> possibleArtifacts = new ArrayList<>();
                if (isIncrements) {
                    possibleArtifacts = search(currentSelectedArtifact, matching, IncrementTo.class);
                } else {
                    possibleArtifacts = search(currentSelectedArtifact, matching, TemplateTo.class);
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
                    throw new InputMismatchException("Wrong increment name");
                }

                userSelection = artifactStringSelection(userSelection, possibleArtifacts, artifactType);
            }
        }
        return userSelection;

    }

    /**
     * Prints all the generable artifacts (increments or templates) that have matched the string search
     * @param <T>
     *            type of generable artifacts
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
    private <T extends GenerableArtifact> void printFoundArtifacts(List<T> possibleArtifacts, boolean isIncrements,
        String artifactType, List<String> userInputIncrements) {
        if (userInputIncrements != null) {
            LOG.info("Here are the {}s that may match your search.", artifactType);
        }
        LOG.info("(0) " + "All");
        for (T artifact : possibleArtifacts) {
            String artifactDescription =
                isIncrements ? ((IncrementTo) artifact).getDescription() : ((TemplateTo) artifact).getId();
            LOG.info("(" + (possibleArtifacts.indexOf(artifact) + 1) + ") " + artifactDescription);
        }
        LOG.info("Please enter the number(s) of {}(s) that you want to generate separated by comma.", artifactType);
    }

    /**
     * Handles the selection of generable artifacts (increments or templates) by String.
     * @param <T>
     *            type of generable artifact
     *
     * @param userSelection
     *            previous selected artifacts that user wants to generate
     * @param possibleArtifacts
     *            list of possible artifacts the user can select
     * @param artifactType
     *            type of artifact (increment or template)
     * @return final user selection including previous ones
     */
    private <T extends GenerableArtifact> List<T> artifactStringSelection(List<T> userSelection,
        List<T> possibleArtifacts, String artifactType) {
        for (String userArtifact : getUserInput().split(",")) {
            try {
                if ("0".equals(userArtifact)) {
                    userSelection = possibleArtifacts;
                    return userSelection;
                }
                T currentArtifact = possibleArtifacts.get(Integer.parseInt(userArtifact) - 1);
                if (!userSelection.contains(currentArtifact)) {
                    userSelection.add(currentArtifact);
                }
            } catch (NumberFormatException e) {
                LOG.error("Error parsing your input. You need to specify {}s using numbers separated by comma (2,5,6).",
                    artifactType);
                throw (e);

            } catch (ArrayIndexOutOfBoundsException e) {
                LOG.error("Error parsing your input. Please give a valid number from the list above.");
                throw (e);
            }
        }
        return userSelection;
    }

    /**
     * Search for generable artifacts (increments or templates) matching the user input. Generable artifacts
     * similar to the given search string or containing it are returned.
     * @param <T>
     *            The type of generable artifacts
     * @param userInput
     *            the user's wished increment or template
     * @param matching
     *            all increments or templates that are valid to the input file(s)
     * @param c
     *            class type, specifies whether Templates or Increments should be preprocessed
     * @return Increments or templates matching the search string
     */
    private <T extends GenerableArtifact> List<T> search(String userInput, List<T> matching, Class<?> c) {
        boolean isIncrements = c.getSimpleName().equals(IncrementTo.class.getSimpleName());
        Map<T, Double> scores = new HashMap<>();

        for (int i = 0; i < matching.size(); i++) {
            if (!isIncrements) {
                String description = ((TemplateTo) matching.get(i)).getId();
                JaccardDistance distance = new JaccardDistance();
                scores.put(matching.get(i), distance.apply(description.toUpperCase(), userInput.toUpperCase()));
            } else {
                String description = ((IncrementTo) matching.get(i)).getDescription();
                String id = ((IncrementTo) matching.get(i)).getId();
                JaccardDistance distance = new JaccardDistance();
                double descriptionDistance = distance.apply(description.toUpperCase(), userInput.toUpperCase());
                double idDistance = distance.apply(id.toUpperCase(), userInput.toUpperCase());
                scores.put(matching.get(i), Math.min(idDistance, descriptionDistance));
            }
        }

        Map<T, Double> sorted = scores.entrySet().stream().sorted(comparingByValue())
            .collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));

        List<T> chosen = new ArrayList<>();
        for (T artifact : sorted.keySet()) {
            T tmp = isIncrements ? artifact : artifact;
            if (!isIncrements) {
                String description = ((TemplateTo) artifact).getId();
                if (description.toUpperCase().contains(userInput.toUpperCase())
                    || sorted.get(artifact) <= SELECTION_THRESHOLD) {
                    chosen.add(tmp);
                }
            } else {
                String description = ((IncrementTo) artifact).getDescription();
                String id = ((IncrementTo) artifact).getId();
                if (description.equalsIgnoreCase(userInput) || id.equalsIgnoreCase(userInput)) {
                    chosen.add(tmp);
                    return chosen;
                }
                if ((description.toUpperCase().contains(userInput.toUpperCase())
                    || id.toUpperCase().contains(userInput.toUpperCase()))
                    || sorted.get(artifact) <= SELECTION_THRESHOLD) {
                    chosen.add(tmp);
                }
            }
        }
        return chosen;
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

    /**
     * Processes the input file's path. Strips the quotes from the file path if they are given.
     * @param inputFile
     *            the input file
     * @return input file with processed path
     */
    public static File preprocessInputFile(File inputFile) {
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

}
