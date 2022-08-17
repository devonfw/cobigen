package com.devonfw.cobigen.unittest.templates;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.constants.TemplatesJarConstants;
import com.devonfw.cobigen.api.util.MavenCoordinate;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.unittest.config.common.AbstractUnitTest;

/**
 * Test suite for {@link TemplatesJarUtil}
 */
public class TemplateJarDownloaderTest extends AbstractUnitTest {

  /** JUnit Rule to create and automatically cleanup temporarily files/folders */
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  /** List with MavenCoordinates for template downloader */
  public List<MavenCoordinate> mavenCoordinatesList;

  /**
   * Location of the templates sets.
   */
  public File templateLocation;

  /**
   * Downloaded folder for template jars.
   */
  public File downloadedFolder;

  /**
   * Creation of folder structure for the tests
   *
   * @throws Exception
   */
  @Before
  public void init() throws Exception {

    this.templateLocation = this.tempFolder.newFolder("templateLocation");
    this.downloadedFolder = this.tempFolder.newFolder("templateLocation", ConfigurationConstants.DOWNLOADED_FOLDER);
    this.mavenCoordinatesList = new ArrayList<>();
  }

  /**
   * Cleanup after test
   */
  @After
  public void cleanup() {

    this.mavenCoordinatesList.clear();
    this.tempFolder.delete();
  }

  /**
   * Helper function in this test suite to create maven coordinates for cobigen devon4j templates. If the version is
   * null LATEST is used
   *
   * @param version version of the cobigen templates jar. If null LATES will be used.
   * @return returns a {@link MavenCoordinate} object for cobigen devon4j templates.
   */
  private MavenCoordinate createMavenCoordinateForDevon4jTemplates(String version) {

    String versionNotNull = version == null ? "LATEST" : version;
    return new MavenCoordinate(TemplatesJarConstants.DEVON4J_TEMPLATES_GROUPID,
        TemplatesJarConstants.DEVON4J_TEMPLATES_ARTIFACTID, versionNotNull);
  }

  /**
   * Tests if already existing jar will prevent the download of templates
   */
  @Test
  public void testDownloadTemplatesAlreadyExisting() {

    this.mavenCoordinatesList.add(createMavenCoordinateForDevon4jTemplates("2021.12.006"));
    TemplatesJarUtil.downloadTemplatesByMavenCoordinates(this.templateLocation, this.mavenCoordinatesList);
    File[] FilesList = this.downloadedFolder.listFiles();
    assertEquals(this.downloadedFolder.listFiles().length, 2);
    for (File f : FilesList) {
      assertThat(f.getName(), Matchers.either(Matchers.is("templates-devon4j-2021.12.006.jar"))
          .or(Matchers.is("templates-devon4j-2021.12.006-sources.jar")));
    }
    TemplatesJarUtil.downloadTemplatesByMavenCoordinates(this.templateLocation, this.mavenCoordinatesList);
    assertEquals(this.downloadedFolder.listFiles().length, 2);
  }

  /**
   * Tests if templates can be loaded without providing a version
   */
  @Test
  public void testDownloadTemplatesWithoutVersion() {

    this.mavenCoordinatesList.add(createMavenCoordinateForDevon4jTemplates(""));
    TemplatesJarUtil.downloadTemplatesByMavenCoordinates(this.templateLocation, this.mavenCoordinatesList);
    File[] FilesList = this.downloadedFolder.listFiles();
    assertEquals(this.downloadedFolder.listFiles().length, 2);
    for (File f : FilesList) {
      assert (f.getName().contains("templates-devon4j"));
    }
    this.mavenCoordinatesList.add(createMavenCoordinateForDevon4jTemplates("LATEST"));
    this.mavenCoordinatesList.remove(0);
    TemplatesJarUtil.downloadTemplatesByMavenCoordinates(this.templateLocation, this.mavenCoordinatesList);

  }

  /**
   * Test if LATEST templates can be loaded
   */
  @Test
  public void testDownloadTemplatesWithLATEST() {

    this.mavenCoordinatesList.add(createMavenCoordinateForDevon4jTemplates("LATEST"));
    TemplatesJarUtil.downloadTemplatesByMavenCoordinates(this.templateLocation, this.mavenCoordinatesList);
    File[] FilesList = this.downloadedFolder.listFiles();
    assertEquals(this.downloadedFolder.listFiles().length, 2);
    for (File f : FilesList) {
      assertThat(f.getName(), Matchers.either(Matchers.is("templates-devon4j-2021.12.006.jar"))
          .or(Matchers.is("templates-devon4j-2021.12.006-sources.jar")));
    }

  }

  /**
   * Test if no download occurs if a adapted folder exits
   *
   * @throws Exception
   */
  @Test
  public void testDownloadTemplatesAlreadyAdapted() throws Exception {

    File adapted = this.tempFolder.newFolder("templateLocation/adapted");
    this.mavenCoordinatesList.add(createMavenCoordinateForDevon4jTemplates(null));
    TemplatesJarUtil.downloadTemplatesByMavenCoordinates(adapted, this.mavenCoordinatesList);
    // Download of Templates should not start
    assert (this.downloadedFolder.listFiles().length == 0);
    assert (this.downloadedFolder.listFiles(File::isFile)) != null;

  }

  /**
   * Tests the valid upgrade of a templates jar
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectTemplatesUpgrade() throws Exception {

    // preparation
    this.downloadedFolder.delete();
    this.templateLocation.delete();
    this.tempFolder.newFile("templates-devon4j-3.0.0.jar");
    File tmpJarFolder = this.tempFolder.getRoot();

    // Perform download
    TemplatesJarUtil.downloadLatestDevon4jTemplates(false, tmpJarFolder);

    // Assert
    assertThat(tmpJarFolder.list().length).isEqualTo(2); // It should download also the sources
    assertThat(tmpJarFolder.list()[0].contains("templates-devon4j-3.0.0")).isFalse();
    assertThat(tmpJarFolder.list()[1].contains("templates-devon4j-3.0.0")).isFalse();

  }

}
