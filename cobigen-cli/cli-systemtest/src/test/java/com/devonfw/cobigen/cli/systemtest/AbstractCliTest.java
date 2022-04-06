package com.devonfw.cobigen.cli.systemtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;
import org.codehaus.plexus.util.Os;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;

import uk.org.webcompere.systemstubs.security.SystemExit;

/** Common test implementation for CLI tests */
public class AbstractCliTest {

  /** Temporary files rule to create temporary folders or files */
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  /** Temporary directory for the templates project */
  @ClassRule
  public static TemporaryFolder tempFolderTemplates = new TemporaryFolder();

  /** Current home directory */
  protected Path currentHome;

  /** The templates development folder */
  protected static Path devTemplatesPath;

  /** A temp directory containing the templates development folder */
  protected static Path devTemplatesPathTemp;

  /**
   * Determine the templates development folder and create a copy of it in the temp directory
   *
   * @throws URISyntaxException if the path could not be created properly
   * @throws IOException if accessing a template directory directory fails
   */
  @BeforeClass
  public static void determineDevTemplatesPath() throws URISyntaxException, IOException {

    devTemplatesPath = new File(AbstractCliTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
        .getParentFile().getParentFile().getParentFile().getParentFile().toPath().resolve("cobigen-templates");

    Path utilsPom = new File(AbstractCliTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
        .getParentFile().getParentFile().getParentFile().getParentFile().toPath().resolve("cobigen-templates")
        .resolve("templates-devon4j-tests/src/test/resources/utils/pom.xml");

    // create a temporary directory cobigen-templates/template-sets/adapted containing the template sets
    Path tempFolderPath = tempFolderTemplates.getRoot().toPath();
    Path cobigenTemplatePath = tempFolderPath.resolve("cobigen-templates");
    if (!Files.exists(cobigenTemplatePath)) {
      Files.createDirectory(cobigenTemplatePath);

      devTemplatesPathTemp = cobigenTemplatePath.resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      Path templateSetsAdaptedFolder = devTemplatesPathTemp.resolve(ConfigurationConstants.ADAPTED_FOLDER);
      Files.createDirectory(devTemplatesPathTemp);
      Files.createDirectory(templateSetsAdaptedFolder);

      FileUtils.copyDirectory(devTemplatesPath.toFile(), templateSetsAdaptedFolder.toFile());

      List<Path> devTemplateSets = new ArrayList<>();
      try (Stream<Path> files = Files.list(templateSetsAdaptedFolder)) {
        files.forEach(path -> {
          devTemplateSets.add(path);
        });
      }

      for (Path path : devTemplateSets) {
        if (Files.isDirectory(path)) {
          Path resourcesFolder = path.resolve("src/main/resources");
          Path templatesFolder = path.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
          if (Files.exists(resourcesFolder) && !Files.exists(templatesFolder)) {
            try {
              Files.move(resourcesFolder, templatesFolder);
            } catch (IOException e) {
              throw new IOException("Error moving directory " + resourcesFolder, e);
            }
          }

          if (path.getFileName().toString().equals("templates-devon4j-utils")) {
            if (Files.exists(path.resolve("pom.xml"))) {
              try {
                Files.delete(path.resolve("pom.xml"));
              } catch (IOException e) {
                throw new IOException("Error deleting file " + path.resolve("pom.xml"), e);
              }
            }
            try {
              Files.copy(utilsPom, path.resolve("pom.xml"));
            } catch (IOException e) {
              throw new IOException("Error copying file " + utilsPom, e);
            }
          }
        }
      }
    }
  }

  /**
   * Setup home path for cobigen to isolate the tests and configure the templates path to devon4j-templates
   *
   * @throws IOException if the configuration
   */
  @Before
  public void setupTestIsolation() throws IOException {

    this.currentHome = this.tempFolder.newFolder("cobigen-test-home").toPath();
  }

  /**
   * Configure CLI to take the latest templates currently in development. Overwrite to disable in case you need.
   *
   * @throws IOException in case the config file could not be written
   */
  public void runWithLatestTemplates() throws IOException {

    Path configFile = this.currentHome.resolve(ConfigurationConstants.COBIGEN_CONFIG_FILE);
    Files.write(configFile,
        (ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH + "=" + devTemplatesPathTemp.toString()).getBytes());
  }

  /**
   * @see #execute(String[], boolean, boolean)
   */
  @SuppressWarnings("javadoc")
  protected void execute(String[] args, boolean useDevTemplates) throws Exception {

    execute(args, useDevTemplates, false);
  }

  /**
   * This method check the return code from picocli
   *
   * @param args execution arguments
   * @param useDevTemplates use development devon4j-templates
   * @param assureFailure assure failure instead of success of the command execution
   * @throws Exception error
   */
  protected void execute(String[] args, boolean useDevTemplates, boolean assureFailure) throws Exception {

    if (useDevTemplates) {
      runWithLatestTemplates();
    }

    String zipName = "cli.tar.gz";
    Files.copy(
        new File(AbstractCliTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile()
            .getParentFile().getParentFile().toPath().resolve("cli").resolve("target").resolve(zipName),
        this.currentHome.resolve(zipName));

    TarGZipUnArchiver unzip = new TarGZipUnArchiver(this.currentHome.resolve(zipName).toFile());
    ConsoleLoggerManager manager = new ConsoleLoggerManager("debug");
    unzip.enableLogging(manager.getLoggerForComponent("extract-tgz"));
    unzip.setDestDirectory(this.currentHome.toFile());
    unzip.extract();

    String[] debugArgs;

    debugArgs = new String[args.length + 1];
    Path binFolder = this.currentHome.resolve("bin");
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
      debugArgs[0] = binFolder.resolve("cobigen.bat").toString();
    } else {
      debugArgs[0] = binFolder.resolve("cobigen").toString();
    }

    int i = 1;
    for (String arg : args) {
      debugArgs[i] = arg;
      i++;
    }

    if (useDevTemplates) {
      debugArgs = Arrays.copyOf(debugArgs, debugArgs.length + 3);
      debugArgs[debugArgs.length - 3] = "-v";
      debugArgs[debugArgs.length - 2] = "-tp";
      debugArgs[debugArgs.length - 1] = devTemplatesPathTemp.toString();
    } else {
      debugArgs = Arrays.copyOf(debugArgs, debugArgs.length + 1);
      debugArgs[debugArgs.length - 1] = "-v";
    }

    ProcessExecutor pe = new ProcessExecutor()
        .environment(ConfigurationConstants.CONFIG_ENV_HOME, this.currentHome.toString()).command(debugArgs)
        .destroyOnExit()
        .redirectError(Slf4jStream.of(LoggerFactory.getLogger(getClass().getName() + ".cliprocess")).asError())
        .redirectOutput(Slf4jStream.of(LoggerFactory.getLogger(getClass().getName() + ".cliprocess")).asInfo());
    new SystemExit().execute(() -> {
      ProcessResult result = pe.execute();
      int exitCode = result.getExitValue();
      assertThat(exitCode).describedAs("Return code").isEqualTo(assureFailure ? 1 : 0);
    });
  }
}
