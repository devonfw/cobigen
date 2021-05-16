package com.devonfw.cobigen.cli.commandtests;

import static org.junit.Assert.assertTrue;
//import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import com.devonfw.cobigen.api.util.ConfigurationUtil;

/**
 * Tests the usage of the adapt-templates command. Warning: Java 9+ requires -Djdk.attach.allowAttachSelf=true
 * to be present among JVM startup arguments.
 */
public class AdaptTemplatesCommandTest extends AbstractCliTest {

    /** Test resources root path */
    private static String testFileRootPath = "src/test/resources/testdata/";

    /**
     * Checks if adapt-templates command successfully created cobigen templates folder
     * @throws IOException
     */
    @Test
    public void adaptTemplatesTest() throws IOException {

        String args[] = new String[1];
        args[0] = "adapt-templates";

        execute(args);

        Path cobigenTemplatesFolderPath = ConfigurationUtil.getTemplatesFolderPath();
        assertTrue(Files.exists(cobigenTemplatesFolderPath));
        assertTrue(Files.list(cobigenTemplatesFolderPath).count() > 0);

        // Path cobigenTemplatesFolderPath =
        // ConfigurationUtils.getCobigenCliRootPath().resolve(ConfigurationUtils.COBIGEN_TEMPLATES);

        // assertThat(Files.exists(cobigenTemplatesFolderPath));
    }
}
