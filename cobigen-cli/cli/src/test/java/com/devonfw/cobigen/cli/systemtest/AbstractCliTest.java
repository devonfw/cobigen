package com.devonfw.cobigen.cli.systemtest;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.cli.commands.CobiGenCommand;
import com.devonfw.cobigen.cli.commands.GenerateCommand;

import picocli.CommandLine;

/** Common test implementation for CLI tests */
public class AbstractCliTest {

    /** Temporary files rule to create temporary folders or files */
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    /** Current home directory */
    protected Path currentHome;

    /** Temporary configuration home location */
    protected File tmpConfigurationFolder;

    /** SUT: Commandline to pass arguments to */
    protected CommandLine commandLine;

    protected static Path devTemplatesPath;

    @BeforeClass
    public static void determineDevTemplatesPath() throws URISyntaxException {
        devTemplatesPath = new File(GenerateCommand.class.getProtectionDomain().getCodeSource().getLocation().toURI())
            .getParentFile().getParentFile().getParentFile().getParentFile().toPath().resolve("cobigen-templates")
            .resolve("templates-devon4j");

        // make sure, that the templates project has been compiled. If not, throw an assertion
        // this is needed to run the latest templates properly from the folder
        assertThat(devTemplatesPath.resolve("target").resolve("classes")).exists();
    }

    @Before
    public void setupTestIsolation() throws IOException {
        // needs to be re-initialized to get a clear instantiation by the commandline on every execution
        commandLine = new CommandLine(new CobiGenCommand());

        currentHome = tempFolder.newFolder("cobigen-test-home").toPath();
        runWithLatestTemplates();
    }

    /**
     * Configure CLI to take the latest templates currently in development. Overwrite to disable in case you
     * need.
     * @throws IOException
     *             in case the config file could not be written
     */
    public void runWithLatestTemplates() throws IOException {
        Path configFile = currentHome.resolve(ConfigurationConstants.COBIGEN_CONFIG_FILE);
        Files.write(configFile,
            (ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH + "=" + devTemplatesPath.toString()).getBytes());
    }

    /**
     * This method check the return code from picocli
     * @param args
     *            execution arguments
     */
    protected void execute(String[] args) {
        try {
            withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, currentHome.toString()).execute(() -> {
                String[] debugArgs = Arrays.copyOf(args, args.length + 1);
                debugArgs[args.length] = "-v";
                int exitCode = commandLine.execute(debugArgs);
                assertThat(exitCode).isEqualTo(0);
            });
        } catch (Exception e) {
            throw new AssertionError("failed to execute withEnvironmentVariable environment", e);
        }
    }

}
