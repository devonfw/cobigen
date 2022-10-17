package com.devonfw.cobigen.retriever.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.retriever.settings.util.MavenProxyUtil;
import com.devonfw.cobigen.retriever.settings.util.MavenSettingsUtil;
import com.devonfw.cobigen.retriever.settings.util.to.model.MavenSettingsModel;
import com.devonfw.cobigen.retriever.settings.util.to.model.MavenSettingsProxyModel;
import com.devonfw.cobigen.retriever.settings.util.to.model.MavenSettingsRepositoryModel;

/**
 * Test class for MavenProxyUtil
 */
public class MavenProxyUtilTest {

  /** Test data root path */
  private static final String testdataRoot = "src/test/resources/testdata/unittest/MavenProxyUtilTest";

  /** Repositories on which the tests are performed on */
  private static List<MavenSettingsRepositoryModel> repositoriesList;

  /** Proxies on which the tests are performed on */
  private static List<MavenSettingsProxyModel> proxiesList;

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

    repositoriesList = new ArrayList<>();
    proxiesList = new ArrayList<>();

    // Get all the repositories for the test
    repositoriesList = new ArrayList<>();
    repositoriesList.addAll(model.getProfiles().getProfileList().get(0).getRepositories().getRepositoryList());
    repositoriesList.addAll(model.getProfiles().getProfileList().get(1).getRepositories().getRepositoryList());
    repositoriesList.addAll(model.getProfiles().getProfileList().get(2).getRepositories().getRepositoryList());

    // Get all the mirrors for the test
    proxiesList = new ArrayList<>();
    proxiesList = new LinkedList<>();
    proxiesList.addAll(model.getProxies().getProxyList());

  }

  /**
   *
   */
  @Test
  public void testObtainRepositoriesWithProxiesMultipleNonProxyHosts() {

    List<MavenSettingsRepositoryModel> result = MavenProxyUtil.obtainRepositoriesWithProxies(repositoriesList,
        proxiesList.get(0));

    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0).getUrl()).isEqualTo("localhost");
  }

  /**
   *
   */
  @Test
  public void testObtainRepositoriesWithWildcard() {

    List<MavenSettingsRepositoryModel> result = MavenProxyUtil.obtainRepositoriesWithProxies(repositoriesList,
        proxiesList.get(1));

    assertThat(result.size()).isEqualTo(0);
  }

  /**
   *
   */
  @Test
  public void testObtainRepositoriesWithProtocol() {

    List<MavenSettingsRepositoryModel> result = MavenProxyUtil.obtainRepositoriesWithProxies(repositoriesList,
        proxiesList.get(2));

    assertThat(result.size()).isEqualTo(3);
  }

}
