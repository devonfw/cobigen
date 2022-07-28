package com.devonfw.cobigen.unittest.templates;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.constants.TemplatesJarConstants;
import com.devonfw.cobigen.api.util.MavenCoordinate;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.unittest.config.common.AbstractUnitTest;

/**
 * Test suite for {@link TemplatesJarUtil}
 */
public class TemplateJarDownloaderTest extends AbstractUnitTest {

  /** Root path to all resources used in this test case */
  private static String testFileRootPath = "src/test/resources/testdata/unittest/config/templatesJar/";

  /** JUnit Rule to create and automatically cleanup temporarily files/folders */
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  /** List with maven coordinates for template downloader */
  public List<MavenCoordinate> mavenCoordinatesList;

  /**
   *
   */
  public File templateLocation;

  /**
   * @throws Exception
   */
  @Before
  public void init() throws Exception {

    this.templateLocation = this.tempFolder.newFolder("templateLocation");
    this.mavenCoordinatesList = new ArrayList<>();
  }

  @After
  public void cleanup() {

    this.mavenCoordinatesList.clear();
    this.tempFolder.delete();
  }

  private MavenCoordinate createMavenCoordinateForDevon4jTemplates(String version) {

    String versionNotNull = version == null ? "LATEST" : version;
    return new MavenCoordinate(TemplatesJarConstants.DEVON4J_TEMPLATES_GROUPID,
        TemplatesJarConstants.DEVON4J_TEMPLATES_ARTIFACTID, versionNotNull);
  }

  @Test
  public void testDownloadTemplatesAlreadyExisting() {

    this.mavenCoordinatesList.add(createMavenCoordinateForDevon4jTemplates("2021.12.006"));
    TemplatesJarUtil.downloadTemplatesByMavenCoordinates(false, this.templateLocation, this.mavenCoordinatesList);
    File[] FilesList = this.templateLocation.listFiles();
    assertEquals(this.templateLocation.listFiles().length, 1);
    for (File f : FilesList) {
      assert (f.getName().equals("templates-devon4j-2021.12.006.jar"));
    }
    TemplatesJarUtil.downloadTemplatesByMavenCoordinates(false, this.templateLocation, this.mavenCoordinatesList);
    assert (this.templateLocation.listFiles().length == 1);
  }

  @Test
  public void testDownloadTemplatesWithoutVersion() {

    this.mavenCoordinatesList.add(createMavenCoordinateForDevon4jTemplates(""));
    TemplatesJarUtil.downloadTemplatesByMavenCoordinates(false, this.templateLocation, this.mavenCoordinatesList);
    File[] FilesList = this.templateLocation.listFiles();
    assertEquals(this.templateLocation.listFiles().length, 1);
    for (File f : FilesList) {
      assert (f.getName().contains("templates-devon4j"));
    }
    this.mavenCoordinatesList.add(createMavenCoordinateForDevon4jTemplates("LATEST"));
    this.mavenCoordinatesList.remove(0);
    TemplatesJarUtil.downloadTemplatesByMavenCoordinates(false, this.templateLocation, this.mavenCoordinatesList);

  }

  @Test
  public void testDownloadTemplatesWithLATEST() {

    this.mavenCoordinatesList.add(createMavenCoordinateForDevon4jTemplates("LATEST"));
    TemplatesJarUtil.downloadTemplatesByMavenCoordinates(false, this.templateLocation, this.mavenCoordinatesList);
    File[] FilesList = this.templateLocation.listFiles();
    assertEquals(this.templateLocation.listFiles().length, 1);
    for (File f : FilesList) {
      assert (f.getName().contains("templates-devon4j"));
    }

  }

  /**
   * @throws Exception
   */
  @Test
  public void testDownloadTemplatesAlreadyAdapted() throws Exception {

    File adapted = this.tempFolder.newFolder("templateLocation/adapted");
    this.mavenCoordinatesList.add(createMavenCoordinateForDevon4jTemplates(null));
    TemplatesJarUtil.downloadTemplatesByMavenCoordinates(false, this.templateLocation, this.mavenCoordinatesList);
    // Download of Templates should not start
    assert (this.templateLocation.listFiles().length == 1);
    assert (this.templateLocation.listFiles(File::isFile)) != null;

  }

  /**
   * Tests the valid upgrade of a templates jar
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectTemplatesUpgrade() throws Exception {

    // preparation
    this.tempFolder.newFile("templates-devon4j-3.0.0.jar");
    File tmpJarFolder = this.tempFolder.getRoot();

    // Perform download
    TemplatesJarUtil.downloadLatestDevon4jTemplates(true, tmpJarFolder);

    // Assert
    assertThat(tmpJarFolder.list().length).isEqualTo(2); // It should download also the sources
    assertThat(tmpJarFolder.list()[0].contains("templates-devon4j-3.0.0")).isFalse();
    assertThat(tmpJarFolder.list()[1].contains("templates-devon4j-3.0.0")).isFalse();

  }

}
