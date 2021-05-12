package com.devonfw.cobigen.cli.commandtests;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;

import com.devonfw.cobigen.cli.constants.MavenConstants;
import com.devonfw.cobigen.cli.utils.CobiGenUtils;

/**
 *
 */
public class AbstractCliTest {

    @Before
    public void cleanupBootstrapFiles() throws URISyntaxException, IOException {
        File cliLocation = CobiGenUtils.getCliLocation();
        Path classPathFile = cliLocation.toPath().resolve(MavenConstants.CLASSPATH_OUTPUT_FILE);
        Files.deleteIfExists(classPathFile);
        Path pomFile = cliLocation.toPath().resolve(MavenConstants.POM);
        Files.deleteIfExists(pomFile);
    }
}
