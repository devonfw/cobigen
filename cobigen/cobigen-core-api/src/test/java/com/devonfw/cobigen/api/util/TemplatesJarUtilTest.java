package com.devonfw.cobigen.api.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.TestCase;

/**
 * This {@link TestCase} tests the {@link TemplatesJarUtil}
 *
 */
public class TemplatesJarUtilTest {

  /** Temporary files rule to create temporary folders or files */
  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  /** Maven coordinates for the devon4j template */
  public String cobigenDevon4jTemplates = "com.devonfw.cobigen:templates-devon4j:2021.12.006";

  /** List with maven coordinates for template downloader */
  public List<String> mavenCoordinatesList;

  /**
   *
   */
  public File templateLocation;

  /**
   * @throws Exception
   */
  @Before
  public void init() throws Exception {

    this.templateLocation = this.tmpFolder.newFolder("templateLocation");
    this.mavenCoordinatesList = new ArrayList<>();
  }

  @After
  public void cleanup() {

    this.mavenCoordinatesList.clear();
    this.tmpFolder.delete();
  }

  @Test
  public void testDownloadTemplatesAlreadyExisting() {

    this.mavenCoordinatesList.add(this.cobigenDevon4jTemplates);
    TemplatesJarUtil.downloadTemplates(false, this.templateLocation, this.mavenCoordinatesList);
    File[] FilesList = this.templateLocation.listFiles();
    assertEquals(this.templateLocation.listFiles().length, 1);
    for (File f : FilesList) {
      assert (f.getName().equals("templates-devon4j-2021.12.006.jar"));
    }
    TemplatesJarUtil.downloadTemplates(false, this.templateLocation, this.mavenCoordinatesList);
    assert (this.templateLocation.listFiles().length == 1);
  }

  @Test
  public void testDownloadTemplatesWithoutVersion() {

    String[] splited = this.cobigenDevon4jTemplates.split(":");
    this.mavenCoordinatesList.add(splited[0] + ":" + splited[1]);// without version
    TemplatesJarUtil.downloadTemplates(false, this.templateLocation, this.mavenCoordinatesList);
    File[] FilesList = this.templateLocation.listFiles();
    assertEquals(this.templateLocation.listFiles().length, 1);
    for (File f : FilesList) {
      assert (f.getName().contains("templates-devon4j"));
    }
    this.mavenCoordinatesList.add(this.mavenCoordinatesList.get(0) + ":LATEST");
    this.mavenCoordinatesList.remove(0);
    TemplatesJarUtil.downloadTemplates(false, this.templateLocation, this.mavenCoordinatesList);

  }

  @Test
  public void testDownloadTemplatesWithLATEST() {

    String[] splited = this.cobigenDevon4jTemplates.split(":");
    this.mavenCoordinatesList.add(splited[0] + ":" + splited[1] + ":LATEST");// with LATEST
    TemplatesJarUtil.downloadTemplates(false, this.templateLocation, this.mavenCoordinatesList);
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

    File adapted = this.tmpFolder.newFolder("templateLocation/adapted");
    List<String> mavenCoordinatesList = new ArrayList<>();
    mavenCoordinatesList.add(this.cobigenDevon4jTemplates);
    TemplatesJarUtil.downloadTemplates(false, this.templateLocation, mavenCoordinatesList);
    // Download of Templates should not start
    assert (this.templateLocation.listFiles().length == 1);
    assert (this.templateLocation.listFiles(File::isFile)) != null;

  }

  /**
   *
   */
  @Test
  public void testDownloadTemplatesWrongConfigurationKey() {

  }

  /**
   *
   */
  @Test
  public void testDownloadTemplatesWrongMavenCoordinates() {

    fail("Not yet implemented");
  }

}
