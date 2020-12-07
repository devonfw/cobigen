package com.devonfw.cobigen.cli.commandtests;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.devonfw.cobigen.cli.commands.CobiGenCommand;
import com.devonfw.cobigen.cli.utils.ConfigurationUtils;
import com.ea.agentloader.AgentLoader;

import classloader.Agent;
import picocli.CommandLine;

/**
 * Tests the usage of the adapt-templates command. Warning: Java 9+ requires -Djdk.attach.allowAttachSelf=true
 * to be present among JVM startup arguments.
 */
public class AdaptTemplatesCommandTest {

    /** Test resources root path */
    private static String testFileRootPath = "src/test/resources/testdata/";

    /**
     * Commandline to pass arguments to
     */
    private final CommandLine commandLine = new CommandLine(new CobiGenCommand());

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

        commandLine.execute(args);

        Path cobigenTemplatesFolderPath =
            ConfigurationUtils.getCobigenCliRootPath().resolve(ConfigurationUtils.COBIGEN_TEMPLATES);

        assertThat(Files.exists(cobigenTemplatesFolderPath));
    }

    /**
     * Checks if adapt-templates command successfully created a custom cobigen templates folder at a new
     * location stored in configuration file
     */
    @Test
    public void customTemplatesLocationTest() {

        Path outputRootPath = Paths.get(testFileRootPath).resolve("generatedcode/root").toAbsolutePath();

        String args[] = new String[3];
        args[0] = "adapt-templates";
        args[1] = "--custom-location";
        args[2] = outputRootPath.toString();

        commandLine.execute(args);

        Path cobigenTemplatesFolderPath = ConfigurationUtils.getCobigenTemplatesFolderPath();

        assertThat(Files.exists(cobigenTemplatesFolderPath));

        File generatedFiles = cobigenTemplatesFolderPath.toFile();

        generatedFilesList.add(generatedFiles);
        // If you want to remove the generated files
        AdaptTemplatesCommandTest.deleteGeneratedFiles(generatedFilesList);
        generatedFilesList.clear();
    }

    /**
     * Checks if adapt-templates command throws an error if the input path is faulty/not existing
     */
    @Test
    public void customTemplateLocationInvalidInputThrowsErrorTest() {

        String args[] = new String[3];
        args[0] = "adapt-templates";
        args[1] = "--custom-location";
        args[2] = "invalid/path/to/test";

        assertThat(commandLine.execute(args)).isEqualTo(1);
    }

    /**
     * This method is check whether generated file is exist or not
     * @param generateFiles
     *            list of generated files
     */
    private static void deleteGeneratedFiles(ArrayList<File> generateFiles) {

        for (File generatedFile : generateFiles) {
            assertThat(generatedFile.exists());
            try {
                FileUtils.deleteDirectory(generatedFile);
            } catch (IOException e) {
                continue;
            }
        }
    }
}
