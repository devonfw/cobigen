package com.devonfw.cobigen.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.to.model.MavenSettingsMirrorModel;
import com.devonfw.cobigen.api.to.model.MavenSettingsModel;
import com.devonfw.cobigen.api.to.model.MavenSettingsRepositoryModel;
import com.devonfw.cobigen.api.util.MavenSettingsUtil;

/**
 * Test class for MavenMirrorUtil
 */
public class MavenMirrorUtilTest {

  /** Test data root path */
  private static final String testdataRoot = "src/test/resources/testdata/unittest/MavenMirrorUtilTest";

  /** Repositories on which the tests are performed on */
  private static List<MavenSettingsRepositoryModel> repositoriesList;

  /** Mirrors on which the tests are performed on */
  private static List<MavenSettingsMirrorModel> mirrorsList;

  @BeforeClass
  public static void setUpClass() {

    MavenSettingsModel model;
    String content;
    try {
      content = Files.readString(Paths.get(testdataRoot).resolve("settings.xml"));
    } catch (IOException e) {
      throw new CobiGenRuntimeException("Unable to read test settings.xml", e);
    }
    model = MavenSettingsUtil.generateMavenSettingsModel(content);

    // Get all the repositories for the test
    repositoriesList = new LinkedList<>();
    repositoriesList.add(model.getProfiles().getProfileList().get(0).getRepositories().getRepositoryList().get(0));
    repositoriesList.add(model.getProfiles().getProfileList().get(1).getRepositories().getRepositoryList().get(0));

    // Get all the mirrors for the test
    mirrorsList = new LinkedList<>();
    mirrorsList.addAll(model.getMirrors().getMirrorList());
  }

  @Test
  public void testMatchPattern() {

  }

}
