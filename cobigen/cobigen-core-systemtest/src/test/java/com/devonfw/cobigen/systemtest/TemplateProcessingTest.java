package com.devonfw.cobigen.systemtest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.TemplateAdapter;
import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.TemplateSelectionForAdaptionException;
import com.devonfw.cobigen.api.exception.UpgradeTemplatesNotificationException;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.api.util.mavencoordinate.MavenCoordinatePair;
import com.devonfw.cobigen.impl.adapter.TemplateAdapterImpl;
import com.devonfw.cobigen.systemtest.common.AbstractApiTest;

/**
 * Test suite for extract templates scenarios.
 */
public class TemplateProcessingTest extends AbstractApiTest {

  // TODO jars has to be updated
  /**
   * Root path to all resources used in tests that test the structure of the template sets.
   */
  private static String testFileRootPathTemplateSets = apiTestsRootPath + "AdaptTemplateSetsTest/";

  /**
   * Root path to all resources used in tests that test the old monolithic template structure.
   */
  private static String testFileRootPathMonolithicTemplates = apiTestsRootPath + "AdaptMonolithicTemplatesTest/";

  /** Temporary files rule to create temporary folders or files */
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  /**
   * temporary project to store CobiGen home for a project with the new template structure consisting of template sets.
   */
  Path cobiGenHomeTemplateSets;

  /**
   * temporary project to store CobiGen home for a project with the old template structure consisting of a monolitihic
   * template set
   */
  Path cobiGenHomeMonolithicTemplates;

  /**
   * Creates a temporary CobiGen home directory for each test. A separate directory to test the old and new structure.
   *
   * @throws IOException if an Exception occurs
   */
  @Before
  public void prepare() throws IOException {

    this.cobiGenHomeTemplateSets = this.tempFolder.newFolder("playground", "templateSetsHome").toPath();
    this.cobiGenHomeMonolithicTemplates = this.tempFolder.newFolder("playground", "templatesMonolithicHome").toPath();
  }

  /**
   * Tests if template sets can be extracted properly
   *
   * @throws IOException if an Exception occurs
   * @throws Exception test fails
   */
  @Test
  public void extractTemplateSetsTest() throws IOException, Exception {

    Path devTemplateSetPath = new File(
        TemplateProcessingTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile()
            .getParentFile().getParentFile().getParentFile().toPath().resolve("cobigen-templates")
            .resolve("crud-java-server-app").resolve("target");
    File jars = devTemplateSetPath.toFile();
    List<String> filenames = new ArrayList<>(2);
    for (File file : jars.listFiles()) {
      if (file.getName().endsWith(".jar")) {
        filenames.add(file.getName());
      }
    }
    if (Files.exists(devTemplateSetPath)) {
      Path downloadedTemplateSetsPath = this.cobiGenHomeTemplateSets
          .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER).resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
      if (!Files.exists(downloadedTemplateSetsPath)) {
        Files.createDirectories(downloadedTemplateSetsPath);
      }
      for (String jarFilename : filenames) {
        Files.copy(devTemplateSetPath.resolve(jarFilename),
            downloadedTemplateSetsPath.resolve(jarFilename.replace("-SNAPSHOT", "")));
      }
    }

    Path cobigenTemplateSetsFolderPath = this.cobiGenHomeTemplateSets
        .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    Path downloadedTemplateSetsFolderPath = cobigenTemplateSetsFolderPath
        .resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
    Path adaptedTemplateSetsFolderPath = cobigenTemplateSetsFolderPath.resolve(ConfigurationConstants.ADAPTED_FOLDER);

    if (!Files.exists(cobigenTemplateSetsFolderPath)) {
      Files.createDirectories(cobigenTemplateSetsFolderPath);
    }

    List<Path> jarPaths = TemplatesJarUtil.getJarFiles(downloadedTemplateSetsFolderPath);
    List<Path> filteredJars = new ArrayList<>();
    for (Path jarPath : jarPaths) {
      if (jarPath.toString().contains("sources")) {
        filteredJars.add(TemplatesJarUtil.getJarFile(true, downloadedTemplateSetsFolderPath));
      } else {
        filteredJars.add(TemplatesJarUtil.getJarFile(false, downloadedTemplateSetsFolderPath));
      }

    }
    TemplateAdapter templateAdapter = new TemplateAdapterImpl(cobigenTemplateSetsFolderPath);

    Exception exception = assertThrows(TemplateSelectionForAdaptionException.class, () -> {
      templateAdapter.adaptTemplates();
    });

    List<Path> templateSetJars = ((TemplateSelectionForAdaptionException) exception).getTemplateSets();
    templateAdapter.adaptTemplateSets(templateSetJars, adaptedTemplateSetsFolderPath, false);

    assertThat(cobigenTemplateSetsFolderPath).exists();
    assertThat(downloadedTemplateSetsFolderPath).exists();
    assertThat(adaptedTemplateSetsFolderPath).exists();

    // check if adapted template set exists
    Path templateSet = adaptedTemplateSetsFolderPath.resolve("crud-java-server-app-2021.12.007");
    Path templateSetSources = adaptedTemplateSetsFolderPath.resolve("crud-java-server-app-2021.12.007-sources");
    // throwing a error
    assertThat(templateSet).exists();
    assertThat(templateSetSources).exists();
    // check if context configuration exists
    assertThat(templateSet.resolve(ConfigurationConstants.TEMPLATES_FOLDER)).exists();
    assertThat(templateSet.resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME)).exists();
    assertThat(templateSetSources.resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME)).exists();
    // validate correct folder structure
    assertThat(templateSet.resolve(ConfigurationConstants.TEMPLATE_SET_FREEMARKER_FUNCTIONS_FILE_NAME)).exists();
    assertThat(templateSetSources.resolve(ConfigurationConstants.TEMPLATE_SET_FREEMARKER_FUNCTIONS_FILE_NAME)).exists();
    // validate maven specific contents
    assertThat(templateSet.resolve("pom.xml")).exists();
  }

  /**
   * Tests if template sets can be extracted properly by making use of the new MavenCoordinatePair data structure
   *
   * @throws IOException if an Exception occurs
   * @throws Exception test fails
   */
  @Test
  public void extractTemplateSetsTestWithTemplateSetJarFolderStructure() throws IOException, Exception {

    // Given
    Path devTemplateSetPath1 = new File(
        TemplateProcessingTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile()
            .getParentFile().getParentFile().getParentFile().toPath().resolve("cobigen-templates")
            .resolve("crud-java-server-app").resolve("target");
    Path devTemplateSetPath2 = new File(
        TemplateProcessingTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile()
            .getParentFile().getParentFile().getParentFile().toPath().resolve("cobigen-templates")
            .resolve("crud-java-server-app-complex").resolve("target");
    Path devTemplateSetPath3 = new File(
        TemplateProcessingTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile()
            .getParentFile().getParentFile().getParentFile().toPath().resolve("cobigen-templates")
            .resolve("crud-openapi-java-server-app").resolve("target");

    // Output
    List<List<String>> adaptedTemplates = new ArrayList<>(Arrays.asList(
        Arrays.asList("crud-java-server-app-2021.12.007", "crud-java-server-app-2021.12.007-sources"),
        Arrays.asList("crud-java-server-app-complex-2021.12.007", "crud-java-server-app-complex-2021.12.007-sources"),
        Arrays.asList("crud-openapi-java-server-app-2021.12.007", "crud-openapi-java-server-app-2021.12.007-sources")));

    // Map to process jars
    Map<Path, List<String>> filemap = Map.of(devTemplateSetPath1, new ArrayList<>(), devTemplateSetPath2,
        new ArrayList<>(), devTemplateSetPath3, new ArrayList<>());

    filemap.forEach((dir, filenames) -> {
      for (File file : dir.toFile().listFiles()) {
        if (file.getName().endsWith(".jar")) {
          filenames.add(file.getName());
        }
      }
    });

    // Copy given files to the test directories
    if (Files.exists(devTemplateSetPath1) && Files.exists(devTemplateSetPath2) && Files.exists(devTemplateSetPath3)) {
      Path downloadedTemplateSetsPath = this.cobiGenHomeTemplateSets
          .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER).resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
      if (!Files.exists(downloadedTemplateSetsPath)) {
        Files.createDirectories(downloadedTemplateSetsPath);
      }

      filemap.forEach((dir, filenames) -> {
        for (String jarFilename : filenames) {
          try {
            Files.copy(dir.resolve(jarFilename),
                downloadedTemplateSetsPath.resolve(jarFilename.replace("-SNAPSHOT", "")));
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      });
    }

    // Prepare the test directories
    Path cobigenTemplateSetsFolderPath = this.cobiGenHomeTemplateSets
        .resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    Path downloadedTemplateSetsFolderPath = cobigenTemplateSetsFolderPath
        .resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
    Path adaptedTemplateSetsFolderPath = cobigenTemplateSetsFolderPath.resolve(ConfigurationConstants.ADAPTED_FOLDER);

    if (!Files.exists(cobigenTemplateSetsFolderPath)) {
      Files.createDirectories(cobigenTemplateSetsFolderPath);
    }

    // Gather information about download directory
    List<MavenCoordinatePair> mavenCoordinatePairs = TemplatesJarUtil
        .getTemplateSetJarFolderStructure(downloadedTemplateSetsFolderPath);

    List<String> flatAdaptedTemplates = adaptedTemplates.stream().flatMap(list -> list.stream())
        .collect(Collectors.toList());

    // Check if the data structure aligns with the given files
    for (MavenCoordinatePair pair : mavenCoordinatePairs) {
      // check if MavenCoordinateState specific attributes are set
      assertThat(pair.isValidJarAndSourcesJarPair()).isTrue();
      assertThat(pair.getValue0().getMavenCoordinateLocalPath()).exists();
      assertThat(pair.getValue1().getMavenCoordinateLocalPath()).exists();
      assertThat(pair.getValue0().isPresent()).isTrue();
      assertThat(pair.getValue1().isPresent()).isTrue();
      assertThat(pair.getValue0().isValidMavenCoordinate()).isTrue();
      assertThat(pair.getValue1().isValidMavenCoordinate()).isTrue();
      // Check if the data structure contains the specific output
      Optional<String> notSourcesJar = flatAdaptedTemplates.stream()
          .filter(str -> str.equals(pair.getValue0().getArtifactId() + "-" + pair.getValue0().getVersion())
              && pair.getValue0().isSource())
          .findFirst();
      Optional<String> sourcesJar = flatAdaptedTemplates.stream()
          .filter(str -> str.equals(pair.getValue1().getArtifactId() + "-" + pair.getValue1().getVersion())
              && !pair.getValue1().isSource())
          .findFirst();
      assertThat(notSourcesJar.isPresent() && sourcesJar.isPresent());

    }

    // Adapt the templates
    TemplateAdapter templateAdapter = new TemplateAdapterImpl(cobigenTemplateSetsFolderPath);

    Exception exception = assertThrows(TemplateSelectionForAdaptionException.class, () -> {
      templateAdapter.adaptTemplates();
    });

    List<Path> templateSetJars = ((TemplateSelectionForAdaptionException) exception).getTemplateSets();
    templateAdapter.adaptTemplateSets(templateSetJars, adaptedTemplateSetsFolderPath, false);

    // Run extensive checks
    assertThat(cobigenTemplateSetsFolderPath).exists();
    assertThat(downloadedTemplateSetsFolderPath).exists();
    assertThat(adaptedTemplateSetsFolderPath).exists();

    for (List<String> adapted : adaptedTemplates) {
      String notSourceDir = adapted.get(0);
      String sourceDir = adapted.get(1);
      // check if adapted template set exists
      Path templateSet = adaptedTemplateSetsFolderPath.resolve(notSourceDir);
      Path templateSetSources = adaptedTemplateSetsFolderPath.resolve(sourceDir);
      // throwing a error
      assertThat(templateSet).exists();
      assertThat(templateSetSources).exists();
      // check if context configuration exists
      assertThat(templateSet.resolve(ConfigurationConstants.TEMPLATES_FOLDER)).exists();
      assertThat(templateSet.resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME)).exists();
      assertThat(templateSetSources.resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME)).exists();
      // validate correct folder structure
      assertThat(templateSet.resolve(ConfigurationConstants.TEMPLATE_SET_FREEMARKER_FUNCTIONS_FILE_NAME)).exists();
      assertThat(templateSetSources.resolve(ConfigurationConstants.TEMPLATE_SET_FREEMARKER_FUNCTIONS_FILE_NAME))
          .exists();
      // validate maven specific contents
      assertThat(templateSet.resolve("pom.xml")).exists();
    }

  }

  /**
   * Test of extract templates with old CobiGen_Templates project existing
   *
   * @throws IOException if an Exception occurs
   */
  @Test
  public void extractTemplatesWithOldConfiguration() throws IOException {

    FileUtils.copyDirectory(new File(testFileRootPathMonolithicTemplates),
        this.cobiGenHomeMonolithicTemplates.toFile());

    Path cobigenTemplatesParent = this.cobiGenHomeMonolithicTemplates
        .resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH);

    Path cobigenTemplatesProject = cobigenTemplatesParent.resolve(ConfigurationConstants.COBIGEN_TEMPLATES);

    TemplateAdapter templateAdapter = new TemplateAdapterImpl(cobigenTemplatesParent);
    assertThrows(UpgradeTemplatesNotificationException.class, () -> {
      templateAdapter.adaptTemplates();
    });

    assertThat(cobigenTemplatesProject).exists().isDirectory();
    assertThat(cobigenTemplatesProject.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)).exists().isDirectory();
    assertThat(cobigenTemplatesProject.resolve("src/main/java")).exists().isDirectory();
  }
}
