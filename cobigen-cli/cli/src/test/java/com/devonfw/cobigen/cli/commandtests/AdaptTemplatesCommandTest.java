package com.devonfw.cobigen.cli.commandtests;

import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

import com.devonfw.cobigen.cli.CobiGenCLI;
import com.devonfw.cobigen.cli.utils.CobiGenUtils;
import com.ea.agentloader.AgentLoader;

import classloader.Agent;

/**
 *
 */
public class AdaptTemplatesCommandTest {

    /**
     * We need to dynamically load the Java agent before the tests. Note that Java 9 requires
     * -Djdk.attach.allowAttachSelf=true to be present among JVM startup arguments.
     */
    @Before
    public void loadJavaAgent() {
        AgentLoader.loadAgentClass(Agent.class.getName(), "");
    }

    /**
     * Checks if adapt-templates command successfully created custom cobigen templates folder
     */
    @Test
    public void adaptTemplatesTest() {

        String args[] = new String[1];
        args[0] = "adapt-templates";

        CobiGenCLI.main(args);

        CobiGenUtils utils = new CobiGenUtils();

        Path cobigenTemplatesFolderPath = utils.getCobigenTemplatesFolderPath();

        assertTrue(Files.exists(cobigenTemplatesFolderPath));
    }
}
