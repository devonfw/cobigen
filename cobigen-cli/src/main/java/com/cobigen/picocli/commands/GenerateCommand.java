package com.cobigen.picocli.commands;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cobigen.picocli.utils.CreateJarFile;
import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.maven.validation.InputPreProcessor;

import net.sf.mmm.code.base.source.BaseSource;
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
        if(validateArguments(args))
        	// We get the templates that will be used for generation
        	createJarFile.getTemplatesJar(false);
        	CobiGen cg = createJarFile.initializeCobiGen();         	
        	// call mmm library to get the classloader  
        	
        	generateTemplate(inputFile, inputProject, cg,createJarFile.getUtilClasses());
    }

    /**
     * @param args
     */
	public Boolean validateArguments(ArrayList<String> args) {
		System.out.println("args size= " + args.size());
		int argSize = args.size();
		switch (argSize) {
		case 1:
			logger.error(
					"You need to provide two arguments: <path_of_input_file> <path_of_project> and your second parameter was not found.");
			System.exit(0);
			return false;
		case 2:
			inputFile = new File(args.get(0));
			inputProject = new File(args.get(1));
			return true;
		case 0:
			logger.error("Please provide input parameter");
			return false;
		default:
			logger.error(
					"Too many arguments have been provided, you need to provide two: <path_of_input_file> <path_of_project>");
			System.exit(0);
			return false;

		}

	}
    public void generateTemplate(File inputFile, File inputProject, CobiGen cg ,List<Class<?>> utilClasses) {
    	  try {

              JavaSourceProviderUsingMaven provider = new JavaSourceProviderUsingMaven();
              JavaContext context = provider.createFromLocalMavenProject(inputProject);

              try {
            	  BaseSource bla = context.getOrCreateSource(null, new File("C:\\MyData\\IDE4\\workspaces\\cobigen-master\\tools-cobigen\\cobigen-cli\\src\\test\\resources\\testdata\\localmavenproject\\maven.project\\core\\src\\main\\java"));
            	  
            	  bla.getContext().getOrCreateType("SampleDataEntity",false).getQualifiedName();
            	  
                  context.getClassLoader()
                      .loadClass("com.devonfw.poc.jwtsample.authormanagement.dataaccess.api.AuthorEntity");
              } catch (ClassNotFoundException e) {
                  e.printStackTrace();
              }

              File classFile =
                  inputFile.toPath().resolve("src/main/java/com/devonfw/poc/jwtsample/authormanagement/"
                      + "dataaccess/api/AuthorEntity.java").toFile();

             Object  input = InputPreProcessor.process(cg, classFile, context.getClassLoader());
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
