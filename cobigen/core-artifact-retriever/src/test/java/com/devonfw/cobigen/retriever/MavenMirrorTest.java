package com.devonfw.cobigen.retriever;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.retriever.settings.MavenMirror;
import com.devonfw.cobigen.retriever.settings.MavenSettings;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsMirrorModel;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsModel;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsRepositoryModel;

/**
 * Test class for MavenMirror
 */
public class MavenMirrorTest {

  /** Test data root path */
  private static final String testdataRoot = "src/test/resources/testdata/unittest/MavenMirrorTest";

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
    model = MavenSettings.generateMavenSettingsModel(content);

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
   * Tests the injectInjectMirrorUrl method with the wildcard selector (*)
   */
  @Test
  public void testInjectMirrorUrlMultipleMirrors() {

    List<MavenSettingsRepositoryModel> repositoriesListForInjectionTest = cloneList();

    MavenMirror.injectMirrorUrl(repositoriesListForInjectionTest, List.of(mirrorsList.get(0)));

    assertThat(repositoriesListForInjectionTest.get(0).getUrl()).isEqualTo("http://0.0.0.0/");
    assertThat(repositoriesListForInjectionTest.get(1).getUrl()).isEqualTo("http://0.0.0.0/");
    assertThat(repositoriesListForInjectionTest.get(2).getUrl()).isEqualTo("http://0.0.0.0/");
  }

  /**
   * Tests the injectInjectMirrorUrl method with a single selector (eg. only repo1)
   */
  @Test
  public void testInjectMirrorUrlSingleSelector() {

    List<MavenSettingsRepositoryModel> repositoriesListForInjectionTest = cloneList();

    MavenMirror.injectMirrorUrl(repositoriesListForInjectionTest, List.of(mirrorsList.get(1)));

    assertThat(repositoriesListForInjectionTest.get(0).getUrl()).isEqualTo("http://1.1.1.1/");
    assertThat(repositoriesListForInjectionTest.get(1).getUrl())
        .isEqualTo("https://s01.oss.sonatype.org/content/repositories/snapshots/");
    assertThat(repositoriesListForInjectionTest.get(2).getUrl())
        .isEqualTo("http://s01.oss.sonatype.org/content/repositories/snapshots/");
  }

  /**
   * Tests the injectInjectMirrorUrl method with a multiple selector (eg. repo1, repo2)
   */
  @Test
  public void testInjectMirrorUrlWithMultipleSelectors() {

    List<MavenSettingsRepositoryModel> repositoriesListForInjectionTest = cloneList();

    MavenMirror.injectMirrorUrl(repositoriesListForInjectionTest, List.of(mirrorsList.get(2)));

    assertThat(repositoriesListForInjectionTest.get(0).getUrl()).isEqualTo("http://2.2.2.2/");
    assertThat(repositoriesListForInjectionTest.get(1).getUrl()).isEqualTo("http://2.2.2.2/");
    assertThat(repositoriesListForInjectionTest.get(2).getUrl())
        .isEqualTo("http://s01.oss.sonatype.org/content/repositories/snapshots/");
  }

  /**
   * Tests the injectInjectMirrorUrl method with a exclusion selector (eg. *,!repo1)
   */
  @Test
  public void testInjectMirrorUrlWithExclusionSelector() {

    List<MavenSettingsRepositoryModel> repositoriesListForInjectionTest = cloneList();

    MavenMirror.injectMirrorUrl(repositoriesListForInjectionTest, List.of(mirrorsList.get(3)));

    assertThat(repositoriesListForInjectionTest.get(0).getUrl()).isEqualTo("localhost");
    assertThat(repositoriesListForInjectionTest.get(1).getUrl()).isEqualTo("http://3.3.3.3/");
    assertThat(repositoriesListForInjectionTest.get(2).getUrl()).isEqualTo("http://3.3.3.3/");
  }

  /**
   * Tests the injectInjectMirrorUrl method with an external selector (eg. external:*)
   */
  @Test
  public void testInjectMirrorUrlWithExternalSelector() {

    List<MavenSettingsRepositoryModel> repositoriesListForInjectionTest = cloneList();

    MavenMirror.injectMirrorUrl(repositoriesListForInjectionTest, List.of(mirrorsList.get(4)));

    assertThat(repositoriesListForInjectionTest.get(0).getUrl()).isEqualTo("localhost");
    assertThat(repositoriesListForInjectionTest.get(1).getUrl()).isEqualTo("http://4.4.4.4/");
    assertThat(repositoriesListForInjectionTest.get(2).getUrl()).isEqualTo("http://4.4.4.4/");
  }

  /**
   * Tests the injectInjectMirrorUrl method with an extended external selector (eg. external:http:*)
   */
  @Test
  public void testInjectMirrorUrlWithExtendedExternalSelector() {

    List<MavenSettingsRepositoryModel> repositoriesListForInjectionTest = cloneList();

    MavenMirror.injectMirrorUrl(repositoriesListForInjectionTest, List.of(mirrorsList.get(5)));

    assertThat(repositoriesListForInjectionTest.get(0).getUrl()).isEqualTo("localhost");
    assertThat(repositoriesListForInjectionTest.get(1).getUrl())
        .isEqualTo("https://s01.oss.sonatype.org/content/repositories/snapshots/");
    assertThat(repositoriesListForInjectionTest.get(2).getUrl()).isEqualTo("http://5.5.5.5/");
  }

  /**
   * Tests the injectInjectMirrorUrl method with a multiple mirrors
   */
  @Test
  public void testInjectMirrorUrlWithMultipleMirrors() {

    List<MavenSettingsRepositoryModel> repositoriesListForInjectionTest = cloneList();

    MavenMirror.injectMirrorUrl(repositoriesListForInjectionTest, mirrorsList);

    assertThat(repositoriesListForInjectionTest.get(0).getUrl()).isEqualTo("http://0.0.0.0/");
    assertThat(repositoriesListForInjectionTest.get(1).getUrl()).isEqualTo("http://0.0.0.0/");
    assertThat(repositoriesListForInjectionTest.get(2).getUrl()).isEqualTo("http://0.0.0.0/");
  }

  /**
   * This method creates a new deep copy of the repositoriesList. It is needed because each test method needs it's own
   * list.
   *
   * @return a deep copy of repositoriesList
   */
  private static List<MavenSettingsRepositoryModel> cloneList() {

    List<MavenSettingsRepositoryModel> result = new LinkedList<>();
    for (MavenSettingsRepositoryModel r : repositoriesList) {
      result.add(new MavenSettingsRepositoryModel(r));
    }
    return result;
  }

}
