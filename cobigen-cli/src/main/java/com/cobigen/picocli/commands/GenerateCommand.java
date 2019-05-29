package com.cobigen.picocli.commands;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cobigen.picocli.handlers.CommandsHandler;
import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.maven.validation.InputPreProcessor;

import net.sf.mmm.code.impl.java.JavaContext;
import net.sf.mmm.code.impl.java.source.maven.JavaSourceProviderUsingMaven;

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
    
    private static GenerateCommand generateCommand;
    
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
     * */ 
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
        validateArguments(args);
    }

    /**
     * @param args
     */
    public void validateArguments(ArrayList<String> args) {
        if (args.size() == 1) {
            logger.error(
                "You need to provide two arguments: <path_of_input_file> <path_of_project> and your second parameter was not found.");
            System.exit(0);
        } else if (args.size() == 2) {
            inputFile = new File(args.get(1));
        } else {
            logger.error(
                "Too many arguments have been provided, you need to provide two: <path_of_input_file> <path_of_project>");
            System.exit(0);
        }
    }
    public void generateTemplate(File inputFile,Object input, CobiGen cg ,List<Class<?>> utilClasses) {
    	  try {

              JavaSourceProviderUsingMaven provider = new JavaSourceProviderUsingMaven();
              JavaContext context = provider.createFromLocalMavenProject(inputFile);

              System.out.println("input for getMatchingIncrements => " + input);
              // context.getOrCreateSource(null, null) ;

              try {
                  context.getClassLoader()
                      .loadClass("com.devonfw.poc.jwtsample.authormanagement.dataaccess.api.AuthorEntity");
              } catch (ClassNotFoundException e) {
                  e.printStackTrace();
              }

              File classFile =
                  inputFile.toPath().resolve("src/main/java/com/devonfw/poc/jwtsample/authormanagement/"
                      + "dataaccess/api/AuthorEntity.java").toFile();

              input = InputPreProcessor.process(cg, classFile, context.getClassLoader());
              System.out.println(
                  "input before getmatchingIncrement= " + input.toString() + "class= " + input.getClass());
              List<IncrementTo> matchingIncrements = cg.getMatchingIncrements(input);
              for (IncrementTo inc : matchingIncrements) {

                  System.out.println("Increments Available = " + inc.getDescription());
              }

              cg.generate(input, matchingIncrements, Paths.get(classFile.getParentFile().getAbsolutePath()),
                  false, utilClasses);
              System.out.println("Successfully generated templates.\n");
              logger.info(
                  "Do you want to generate more code in or out this folder enter these shortcuts or give the correct path with help of "
                      + "cg generate" + " ?  ");
          } catch (MojoFailureException e) {
              e.printStackTrace();
          }
    	
    }
}
