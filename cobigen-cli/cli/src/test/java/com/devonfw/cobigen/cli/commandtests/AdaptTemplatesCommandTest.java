package com.devonfw.cobigen.cli.commandtests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.devonfw.cobigen.cli.CobiGenCLI;
import com.devonfw.cobigen.cli.utils.ConfigurationUtils;
import com.ea.agentloader.AgentLoader;

import classloader.Agent;

/**
 * Tests the usage of the adapt-templates command. Warning: Java 9+ requires -Djdk.attach.allowAttachSelf=true
 * to be present among JVM startup arguments.
 */
public class AdaptTemplatesCommandTest {

    /** Test resources root path */
    private static String testFileRootPath = "src/test/resources/testdata/";

    /**
     * We need to dynamically load the Java agent before the tests. Note that Java 9 requires
     * -Djdk.attach.allowAttachSelf=true to be present among JVM startup arguments.
     */
    @Before
    public void loadJavaAgent() {
        AgentLoader.loadAgentClass(Agent.class.getName(), "");
    }

    /** Declare ArrayList variable for adding generated files */
    private ArrayList<File> generatedFilesList = new ArrayList<>();

    /**
     * Checks if adapt-templates command successfully created custom cobigen templates folder
     */
    @Test
    public void adaptTemplatesTest() {

        String args[] = new String[1];
        args[0] = "adapt-templates";

        CobiGenCLI.main(args);

        Path cobigenTemplatesFolderPath =
            ConfigurationUtils.getCobigenCliRootPath().resolve(ConfigurationUtils.COBIGEN_TEMPLATES);

        assertTrue(Files.exists(cobigenTemplatesFolderPath));
    }

    /**
     * Checks if adapt-templates command successfully created a custom cobigen templates folder at a new
     * location stored in configuration file
     */
    @Test
    public void customTemplatesLocationTest() {

        File outputRootPath = new File(testFileRootPath + "generatedcode/root");

        String args[] = new String[3];
        args[0] = "adapt-templates";
        args[1] = "--custom-location";
        args[2] = outputRootPath.getAbsolutePath();

        CobiGenCLI.main(args);

        Path cobigenTemplatesFolderPath = ConfigurationUtils.getCobigenTemplatesFolderFile().toPath();

        assertTrue(Files.exists(cobigenTemplatesFolderPath));

        File generatedFiles = cobigenTemplatesFolderPath.toFile();

        generatedFilesList.add(generatedFiles);
        // If you want to remove the generated files
        AdaptTemplatesCommandTest.deleteGeneratedFiles(generatedFilesList);
        generatedFilesList.clear();
    }

    /**
     * This method is check whether generated file is exist or not
     * @param generateFiles
     *            list of generated files
     */
    private static void deleteGeneratedFiles(ArrayList<File> generateFiles) {

        for (File generatedFile : generateFiles) {
            assertTrue(generatedFile.exists());
            try {
                FileUtils.deleteDirectory(generatedFile);
            } catch (IOException e) {
                continue;
            }
        }
    }
}
