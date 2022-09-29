package com.devonfw.cobigen.api;

import static org.assertj.core.api.Assertions.assertThat;

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
import com.devonfw.cobigen.api.util.MavenMirrorUtil;
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

  /**
   * Used to initialize data needed for the tests
   */
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
    repositoriesList.add(model.getProfiles().getProfileList().get(2).getRepositories().getRepositoryList().get(0));

    // Get all the mirrors for the test
    mirrorsList = new LinkedList<>();
    mirrorsList.addAll(model.getMirrors().getMirrorList());
  }

  /**
   * Tests the wildcard (*) of the matchPattern method
   */
  @Test
  public void testMatchPatternWildcard() {

    boolean resultRepo1Mirror1 = MavenMirrorUtil.matchPattern(repositoriesList.get(0),
        mirrorsList.get(0).getMirrorOf());
    boolean resultRepo2Mirror1 = MavenMirrorUtil.matchPattern(repositoriesList.get(1),
        mirrorsList.get(0).getMirrorOf());

    assertThat(resultRepo1Mirror1).isEqualTo(true);
    assertThat(resultRepo2Mirror1).isEqualTo(true);
  }

  /**
   * Tests, whether matchPattern detects specific pattern (eg. repo1, repo2)
   */
  @Test
  public void testMatchPatternSpecificRepos() {

    boolean resultRepo1Mirror2 = MavenMirrorUtil.matchPattern(repositoriesList.get(0),
        mirrorsList.get(1).getMirrorOf());
    boolean resultRepo1Mirror3 = MavenMirrorUtil.matchPattern(repositoriesList.get(0),
        mirrorsList.get(2).getMirrorOf());

    assertThat(resultRepo1Mirror2).isEqualTo(true);
    assertThat(resultRepo1Mirror3).isEqualTo(true);
  }

  /**
   * Tests, whether the exclusion operator (!) works
   */
  @Test
  public void testMatchPatternExclusion() {

    boolean resultRepo1Mirror4 = MavenMirrorUtil.matchPattern(repositoriesList.get(0),
        mirrorsList.get(3).getMirrorOf());
    boolean resultRepo2Mirror4 = MavenMirrorUtil.matchPattern(repositoriesList.get(1),
        mirrorsList.get(3).getMirrorOf());

    assertThat(resultRepo1Mirror4).isEqualTo(false);
    assertThat(resultRepo2Mirror4).isEqualTo(true);
  }

  /**
   * Tests, whether the external operator works (eg. external:*)
   */
  @Test
  public void testMatchPatternExternalSources() {

    boolean resultRepo1Mirror5 = MavenMirrorUtil.matchPattern(repositoriesList.get(0),
        mirrorsList.get(4).getMirrorOf());
    boolean resultRepo2Mirror5 = MavenMirrorUtil.matchPattern(repositoriesList.get(1),
        mirrorsList.get(4).getMirrorOf());

    assertThat(resultRepo1Mirror5).isEqualTo(false);
    assertThat(resultRepo2Mirror5).isEqualTo(true);
  }

  /**
   * Tests, whether the external operator works with patterns (eg. external:http:*)
   */
  @Test
  public void testMatchPatternExternalHTTPSources() {

    boolean resultRepo1Mirror6 = MavenMirrorUtil.matchPattern(repositoriesList.get(0),
        mirrorsList.get(5).getMirrorOf());
    boolean resultRepo2Mirror6 = MavenMirrorUtil.matchPattern(repositoriesList.get(1),
        mirrorsList.get(5).getMirrorOf());
    boolean resultRepo3Mirror6 = MavenMirrorUtil.matchPattern(repositoriesList.get(2),
        mirrorsList.get(5).getMirrorOf());

    assertThat(resultRepo1Mirror6).isEqualTo(false);
    assertThat(resultRepo2Mirror6).isEqualTo(false);
    assertThat(resultRepo3Mirror6).isEqualTo(true);
  }

}
