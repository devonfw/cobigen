package com.cobigen.picocli.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cobigen.picocli.TestPicocli;
import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.impl.util.TemplatesJarUtil;
import com.devonfw.cobigen.javaplugin.JavaTriggerInterpreter;
import com.devonfw.cobigen.maven.validation.InputPreProcessor;
import com.devonfw.cobigen.textmerger.TextAppender;
import com.devonfw.cobigen.tsplugin.merger.TypeScriptMerger;
import com.devonfw.cobigen.xmlplugin.XmlTriggerInterpreter;
import com.google.common.collect.Lists;

public class CreateJarFile {
	private static Logger logger = LoggerFactory.getLogger(CreateJarFile.class);

	public CreateJarFile() {
		super();
	}

	File jarFile = null;	
	File jarPath = new File("template_Jar/cobigen_jar");	
	File jarFileDir = jarPath.getAbsoluteFile();	
	
	/**
	 * @param 
	 * 		User input entity file
	 * */
	public void createJarAndGenerateIncr(File inputFile) {
		jarFile = TemplatesJarUtil.getJarFile(true, jarFileDir);
		logger.info("get jar file");
		
		if (jarFile != null) {
			try {
				CobiGen cg = CobiGenFactory.create(jarFile.toURI());
				Object input = null;
				try {
					input = InputPreProcessor.process(cg, inputFile, null);					

					List<IncrementTo> matchingIncrements = cg.getMatchingIncrements(input);
					if (matchingIncrements.size() > 0) {

						System.out.println("Increments Available = " + matchingIncrements.get(0).getDescription());
					}
					cg.generate(input, matchingIncrements, Paths.get("C:\\Users\\syadav9\\Desktop\\temp"));
					System.out.println("Successfully generated Template");
				} catch (MojoFailureException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				List<IncrementTo> matchingIncrements = cg.getMatchingIncrements(input);
				if (matchingIncrements.size() > 0) {

					System.out.println("Increments Available = " + matchingIncrements.get(0).getDescription());
				}
			} catch (InvalidConfigurationException e) {
				// if the context configuration is not valid
				e.printStackTrace();
			} catch (IOException e) {
				// If I/O operation failed then it will throw exception
				e.printStackTrace();
			}

			

			logger.info("After create method");
		}

	}
	/**
	 * Registers the given triggerInterpreter,tsmerge, to be registered
	 */
	public void registerPlugin() {
		JavaTriggerInterpreter javaTriger = new JavaTriggerInterpreter("java");
		PluginRegistry.registerTriggerInterpreter(javaTriger);
		//
		TypeScriptMerger tsmerger = new TypeScriptMerger("tsmerge", false);
		PluginRegistry.registerMerger(tsmerger);
		XmlTriggerInterpreter xmlTriggerInterpreter = new XmlTriggerInterpreter("xml");
		PluginRegistry.registerTriggerInterpreter(xmlTriggerInterpreter);

		List<Merger> merger = Lists.newLinkedList();
		merger.add(new TextAppender("textmergerActivator", false));
	}
	/**
	 * Validating user input file is correct or not
	 * @param inputFile 
	 * 	user input file
	 * */
	public boolean validateFile(File inputFile) {
		if (!inputFile.exists() || !inputFile.canRead()) {
			logger.info("The file " + inputFile.getAbsolutePath() + " is not a valid input for CobiGen.");
			try {
				throw new Exception("Could not read input file " + inputFile.getAbsolutePath());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}


}







