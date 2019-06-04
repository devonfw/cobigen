package com.cobigen.picocli.commands;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.mmm.code.impl.java.JavaContext;
import net.sf.mmm.code.impl.java.source.maven.JavaSourceProviderUsingMaven;

import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cobigen.picocli.handlers.CommandsHandler;
import com.cobigen.picocli.utils.CreateJarFile;
import com.cobigen.picocli.utils.ParsingUtils;
import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.maven.validation.InputPreProcessor;

/**
 * This class handles the generation command
 */
public class GenerateCommand {

    /**
     * Logger to output useful information to the user
     */
    private static Logger logger = LoggerFactory.getLogger(GenerateCommand.class);

    /**
     * User input file
     */
    File inputFile = null;

    File inputProject = null;

    private static GenerateCommand generateCommand;

    CreateJarFile createJarFile = new CreateJarFile();

    /**
     * static block initialization for exception handling
     */
    static {
        try {
            generateCommand = new GenerateCommand();
        } catch (Exception e) {
            throw new RuntimeException("Exception occure in creation of singlton instance");
        }
    }

    /**
     * private constructor restricted to this class itself
     */
    private GenerateCommand() {
    };

    public static GenerateCommand getInstance() {
        return generateCommand;
    }

    /**
     * Constructor for {@link GenerateCommand}
     * @param args
     *            String array with all the user arguments
     */
    public GenerateCommand(ArrayList<String> args) {
        while (validateArguments(args) == false) {
            // Arguments are not valid, let's ask again for them
            String[] userArgs = CommandsHandler.getUserInput().split(" ");
            args = (ArrayList<String>) Arrays.asList(userArgs);

        }
        createJarFile.getTemplatesJar(false);
        CobiGen cg = createJarFile.initializeCobiGen();

        generateTemplate(inputFile, inputProject, cg, createJarFile.getUtilClasses());
    }

    /**
     * Validates the user arguments in the context of the generate command. Therefore, we suppose that the
     * arguments passed here are only related to generating.
     * @param args
     *            arguments related to generation
     * @return true when these arguments are correct
     */
    public Boolean validateArguments(ArrayList<String> args) {
        System.out.println("args size= " + args.size());
        int argSize = args.size();
        switch (argSize) {
        case 0:
            logger.error("Please provide two arguments: <path_of_input_file> <path_of_project>");
            return false;
        case 1:
            logger.error(
                "You need to provide two arguments: <path_of_input_file> <path_of_project> and your second parameter was not found.");
            return false;
        case 2:
            inputFile = new File(args.get(0));
            inputProject = new File(args.get(1));
            return true;
        default:
            logger.error(
                "Too many arguments have been provided, you need to provide two: <path_of_input_file> <path_of_project>");
            // Arguments are not valid, we should stop here
            System.exit(0);
            return false;

        }

    }

    /**
     * Generates new templates using the inputFile from the inputProject.
     * @param inputFile
     *            input file the user wants to generate code from
     * @param inputProject
     *            input project where the input file is located. We need this in order to build the classpath
     *            of the input file
     * @param cg
     *            Initialized CobiGen instance
     * @param utilClasses
     *            util classes loaded from the templates jar
     */
    public void generateTemplate(File inputFile, File inputProject, CobiGen cg, List<Class<?>> utilClasses) {
        try {

            JavaSourceProviderUsingMaven provider = new JavaSourceProviderUsingMaven();
            JavaContext context = provider.createFromLocalMavenProject(inputProject);

            String qualifiedName = ParsingUtils.getQualifiedName(inputFile, context);

            context.getClassLoader().loadClass(qualifiedName);

            Object input = InputPreProcessor.process(cg, inputFile, context.getClassLoader());
            System.out.println("input before getmatchingIncrement= " + input.toString() + "class= " + input.getClass());
            List<IncrementTo> matchingIncrements = cg.getMatchingIncrements(input);
            for (IncrementTo inc : matchingIncrements) {

                System.out.println("Increments Available = " + inc.getDescription());
            }

            cg.generate(input, matchingIncrements, Paths.get(inputFile.getParentFile().getAbsolutePath()), false,
                utilClasses);
            System.out.println("Successfully generated templates.\n");
            logger.info(
                "Do you want to generate more code in or out this folder enter these shortcuts or give the correct path with help of "
                    + "cg generate" + " ?  ");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (MojoFailureException e) {
            e.printStackTrace();
        }

    }
}
