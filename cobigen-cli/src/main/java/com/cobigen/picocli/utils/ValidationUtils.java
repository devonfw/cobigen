package com.cobigen.picocli.utils;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities class for validating user's input
 */
public final class ValidationUtils {

    /**
     * Logger useful for printing information
     */
    private static Logger logger = LoggerFactory.getLogger(ValidationUtils.class);

    /**
     * Extension of a POM file
     */
    private static final String POM_EXTENSION = "xml";

    /**
     * Tries to find a pom.xml file in the passed folder
     * @param source
     *            folder where we check if a pom.xml file is found
     * @return the pom.xml file if it was found, null otherwise
     */
    public static File findPom(File source) {

        String filename = source.getName();
        if (source.isFile()) {
            String basename;
            File pomFile;
            int lastDot = filename.lastIndexOf('.');
            if (lastDot > 0) {
                basename = filename.substring(0, lastDot);
                pomFile = new File(source.getParent(), basename + POM_EXTENSION);
                if (pomFile.exists() || pomFile.toString().contains("pom.xml")) {
                    logger.info("User is in valid maven project project ");
                    return pomFile;

                }
            }
            int lastSlash = filename.indexOf('-');
            if (lastSlash > 0) {
                basename = filename.substring(0, lastSlash);
                pomFile = new File(source.getParent(), basename + POM_EXTENSION);
                if (pomFile.exists()) {
                    return pomFile;
                }
            }
        } else if (source.isDirectory()) {
            return findPomFromFolder(source, 0);
        }
        return null;
    }

    /**
     * Recursively tries to find a pom.xml file in the parent folders
     * @param folder
     *            folder where we want to recursively find the pom.xml
     * @param recursion
     *            current recursion level
     * @return the pom.xml file if it was found, null otherwise
     */
    private static File findPomFromFolder(File folder, int recursion) {

        if (folder == null) {
            return null;
        }
        String POM_XML = "pom.xml";
        File pomFile = new File(folder, POM_XML);
        if (pomFile.exists()) {
            logger.info("You are in a project folder, I assume you want to generate code from/in this project . "
                + "If this is the wrong folder enter \"change folder\".");
            return pomFile;
        }
        if (recursion > 4) {
            return null;
        }
        return findPomFromFolder(folder.getParentFile(), recursion + 1);
    }

    /**
     * Validating user input file is correct or not. We check if file exists and it can be read
     *
     * @param inputFile
     *            user input file
     * @return true when file is valid
     */
    public boolean validateFile(File inputFile) {
        if (inputFile == null) {
            return false;
        }

        if (!inputFile.exists()) {
            logger.error("The input file " + inputFile.getAbsolutePath() + " has not been found on your system.");
            return false;
        }

        if (!inputFile.canRead()) {
            logger.error("The input file " + inputFile.getAbsolutePath()
                + " cannot be read. Please check file permissions on the file");
            return false;
        }
        return true;
    }

}
