package com.devonfw.cobigen.cli.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import net.sf.mmm.code.api.CodeName;
import net.sf.mmm.code.base.BaseFile;
import net.sf.mmm.code.base.BasePackage;
import net.sf.mmm.code.base.source.BaseSource;
import net.sf.mmm.code.base.type.BaseType;
import net.sf.mmm.code.impl.java.JavaContext;
import net.sf.mmm.code.impl.java.parser.JavaSourceCodeParserImpl;
import net.sf.mmm.code.impl.java.source.maven.JavaSourceProviderUsingMaven;
import net.sf.mmm.util.io.api.IoMode;
import net.sf.mmm.util.io.api.RuntimeIoException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.cli.CobiGenCLI;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

/**
 * This class contains utilities for parsing user input. It also contains mmm logic to parse user's input file
 * in order to find needed information
 */
public class ParsingUtils {

    /**
     * Logger to output useful information to the user
     */
    private static Logger logger = LoggerFactory.getLogger(CobiGenCLI.class);

    /**
     * Tries to get the Java context by creating a new class loader of the input project that is able to load
     * the input file. We need this in order to perform reflection on the templates.
     *
     * @param inputFile
     *            input file the user wants to generate code from
     * @param inputProject
     *            input project where the input file is located. We need this in order to build the classpath
     *            of the input file
     * @return the Java context created from the input project
     */
    public static JavaContext getJavaContext(File inputFile, File inputProject) {

        JavaContext context = JavaSourceProviderUsingMaven.createFromLocalMavenProject(inputProject, true);
        String fqn = ParsingUtils.getFQN(inputFile);
        try {
            context.getClassLoader().loadClass(fqn);
        } catch (NoClassDefFoundError | ClassNotFoundException e) {
            logger.error("Compiled class " + e.getMessage()
                + " has not been found. Most probably you need to build project " + inputProject.toString() + " .");
            System.exit(1);
        }
        return context;
    }

    /**
     * This method is traversing parent folders until it reaches java folder in order to get the FQN
     *
     * @param inputFile
     * @return qualified name with full package
     */
    private static String getFQN(File inputFile) {
        String simpleName = inputFile.getName().replaceAll("\\.(?i)java", "");
        String packageName = getPackageName(inputFile.getAbsoluteFile().getParentFile(), "");

        return packageName + "." + simpleName;
    }

    /**
     * This method traverse the folder in reverse order from child to parent
     *
     * @param folder
     *            parent input file
     * @param packageName
     * @return package name
     */
    private static String getPackageName(File folder, String packageName) {

        if (folder == null) {
            return null;
        }

        if (folder.getName().toLowerCase().equals("java")) {
            String[] pkgs = packageName.split("\\.");

            packageName = pkgs[pkgs.length - 1];
            // Reverse order as we have traversed folders from child to parent
            for (int i = pkgs.length - 2; i > 0; i--) {
                packageName = packageName + "." + pkgs[i];
            }
            return packageName;
        }
        return getPackageName(folder.getParentFile(), packageName + "." + folder.getName());
    }

    /**
     * Creates a new {@link BasePackage}
     *
     * @param source
     *            of the current context {@link BaseSource}
     * @param qName
     *            the qualified name of the parent {@link CodeName}
     * @return A new {@link BasePackage}
     */
    private static BasePackage createPackage(BaseSource source, CodeName qName) {

        BasePackage parentPkg = source.getRootPackage();
        if (qName == null) {
            return parentPkg;
        }
        String simpleName = qName.getSimpleName();
        CodeName parentName = qName.getParent();
        if (parentName == null) {
            if (simpleName.isEmpty()) {
                return parentPkg;
            }
        } else {
            parentPkg = createPackage(source, parentName);
        }
        return new BasePackage(parentPkg, simpleName, null, null, false);
    }

    /**
     * Creates a new {@link BaseFile}
     * @param className
     *            name of the class
     * @param context
     *            current java project context
     * @return A new {@link BaseFile}
     */
    private static BaseFile createFile(String className, JavaContext context) {

        CodeName qName = context.parseName(className);
        BasePackage pkg = createPackage(context.getSource(), qName.getParent());
        return new BaseFile(pkg, qName.getSimpleName());
    }

    /**
     * Gets the full qualified name of the input file
     * @param inputFile
     *            Java file we want to get its qualified name
     * @param context
     *            current java project context
     * @return The full qualified name of the input file
     */
    public static String getQualifiedName(File inputFile, JavaContext context) {

        String className = inputFile.getName().replace(".java", "");
        BaseFile file = createFile(className, context);
        JavaSourceCodeParserImpl parser = new JavaSourceCodeParserImpl();

        try (Reader reader = new FileReader(inputFile.getAbsolutePath())) {
            BaseType parseType = parser.parseType(reader, file);
            return parseType.getQualifiedName();

        } catch (IOException e) {
            throw new RuntimeIoException(e, IoMode.READ);
        }
    }

    /**
     * Tries to parse a relative path with the current working directory
     * @param inputFiles
     *            list of all input files from the user
     *
     * @param inputFile
     *            input file which we are going to parse to find out whether it is a valid file
     * @param index
     *            location of the input file in the ArrayList of inputs
     * @return true only if the parsed file exists, false otherwise
     *
     */
    public static Boolean parseRelativePath(ArrayList<File> inputFiles, File inputFile, int index) {
        try {
            Path inputFilePath = Paths.get(System.getProperty("user.dir"), inputFile.toString());

            if (inputFilePath.toFile().exists()) {
                inputFiles.set(index, inputFilePath.toFile());
                return true;
            }
        } catch (InvalidPathException e) {
            logger.debug("The path string " + System.getProperty("user.dir") + " + " + inputFile.toString()
                + " cannot be converted to a path");
        }
        return false;
    }

    /**
     * Tries to find the root folder of the project in order to build the classpath. This method is trying to
     * find the first pom.xml file and then getting the folder where is located
     *
     * @param inputFile
     *            passed by the user
     * @return the project folder
     *
     */
    public static File getProjectRoot(File inputFile) {

        File pomFile = ValidationUtils.findPom(inputFile);
        if (pomFile != null) {
            return pomFile.getParentFile();
        }
        logger.debug("Projec root could not be found, therefore we use your current input file location.");
        logger.debug("Using '" + inputFile.getParent() + "' as location where code will be generated");
        return inputFile.getAbsoluteFile().getParentFile();
    }

    /**
     * This method format the runtime generated code with the help of google API
     * @param generatedFiles
     *            List of generation report files
     * @throws FormatterException
     *             if any error occurred while formatting the Java code
     */
    public static void formatJavaSources(Set<Path> generatedFiles) throws FormatterException {
        Set<Path> filesToFormat = generatedFiles;
        Formatter formatter = new Formatter();
        Iterator<Path> itr = filesToFormat.iterator();
        logger.info("Formatting code...");
        while (itr.hasNext()) {
            Path generatedFilePath = itr.next();
            try {
                String unformattedCode = new String(Files.readAllBytes(generatedFilePath));
                String formattedCode = formatter.formatSource(unformattedCode);
                Files.write(generatedFilePath, formattedCode.getBytes());
            } catch (IOException e) {
                logger.error("Unable to read or write the generated file " + generatedFilePath.toString()
                    + " when trying to format it");
                return;
            }
        }
        logger.info("Finished successfully");

    }

}
