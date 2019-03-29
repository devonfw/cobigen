package com.cobigen.picocli;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import org.eclipse.core.runtime.IPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cobigen.picocli.utils.CreateJarFile;
import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.to.IncrementTo;

import org.apache.commons.io.Charsets;
import org.apache.maven.plugin.MojoFailureException;
import org.eclipse.core.resources.ResourcesPlugin;
import com.devonfw.cobigen.eclipse.common.tools.ResourcesPluginUtil;
import com.devonfw.cobigen.eclipse.generator.CobiGenWrapper;
import com.devonfw.cobigen.eclipse.generator.java.JavaInputGeneratorWrapper;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.impl.util.TemplatesJarUtil;
import com.devonfw.cobigen.javaplugin.JavaTriggerInterpreter;
import com.devonfw.cobigen.javaplugin.inputreader.JavaInputReader;
import com.devonfw.cobigen.maven.GenerateMojo;
import com.devonfw.cobigen.maven.validation.InputPreProcessor;
import com.devonfw.cobigen.textmerger.TextAppender;
import com.devonfw.cobigen.textmerger.TextMergerPluginActivator;
import com.devonfw.cobigen.tsplugin.merger.TypeScriptMerger;
import com.devonfw.cobigen.xmlplugin.XmlTriggerInterpreter;
import com.google.common.collect.Lists;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
@Command(name = "TestPicocli", header = "%n@|TestPicocli Hello world demo|@")
public class TestPicocli {
	private static Logger logger = LoggerFactory.getLogger(TestPicocli.class);
	GenerateMojo generateMojo = new GenerateMojo();

	public static void main(String... args) throws Exception {
		logger.info("start main method");
		logger.info("Please enter input from command prompt");
		Scanner inputReader = new Scanner(System.in);
		String userInput = inputReader.nextLine();
		File inputFile = new File(userInput);
		CreateJarFile createjarFile = new CreateJarFile();
			
		File jarPath = new File("template_Jar/cobigen_jar");
		//URL resource = TestPicocli.class.getResource("/cobigen_jar");
		File jarFileDir = jarPath.getAbsoluteFile();	
		if (!jarPath.exists()) {
			jarPath.mkdir();

		}

		try {
			TemplatesJarUtil.downloadLatestDevon4jTemplates(true, jarFileDir);
			createjarFile.validateFile(inputFile);
			createjarFile.createJarAndGenerateIncr(inputFile);

		} catch (MalformedURLException e1) {
			// if a path of one of the class path entries is not a valid URL
			e1.printStackTrace();
		} catch (IOException e1) {
			// IOException occurred
			e1.printStackTrace();
		}
		logger.info("successfully call cobigen create method");
		System.out.println("successfully call cobigen create method");

	}

}
