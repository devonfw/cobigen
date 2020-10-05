package com.devonfw.cobigen.cli.commandtests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

import com.devonfw.cobigen.api.util.CobiGenPathUtil;
import com.devonfw.cobigen.cli.commands.CobiGenCommand;
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

    /**
     * Checks if adapt-templates command successfully created cobigen templates folder
     * @throws IOException
     */
    @Test
    public void adaptTemplatesTest() throws IOException {

        String args[] = new String[1];
        args[0] = "adapt-templates";

        commandLine.execute(args);

        Path cobigenTemplatesFolderPath = CobiGenPathUtil.getTemplatesFolderPath();
        assertTrue(Files.exists(cobigenTemplatesFolderPath));
        assertTrue(Files.list(cobigenTemplatesFolderPath).count() > 0);
    }
}
