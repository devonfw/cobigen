package com.devonfw.cobigen.retriever;

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
import com.devonfw.cobigen.retriever.settings.MavenProxy;
import com.devonfw.cobigen.retriever.settings.MavenSettings;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsModel;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsProxyModel;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsRepositoryModel;

/**
 * Test class for MavenProxy
 */
public class MavenProxyTest {

  /** Test data root path */
  private static final String testdataRoot = "src/test/resources/testdata/unittest/MavenProxyTest";

  /** Repositories on which the tests are performed on */
  private static List<MavenSettingsRepositoryModel> repositoriesList;

  /** Proxies, on which the tests are performed on */
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
    model = MavenSettings.generateMavenSettingsModel(content);

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
   * Tests, whether a declaration with multiple nonProxyHosts is working
   */
  @Test
  public void testObtainRepositoriesWithProxiesWithMultipleNonProxyHosts() {

    List<MavenSettingsRepositoryModel> result = MavenProxy.obtainRepositories(repositoriesList, proxiesList.get(0),
        true);

    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0).getUrl()).isEqualTo("localhost");
  }

  /**
   * Tests, whether a declaration with a wildcard in nonProxyHosts is working
   */
  @Test
  public void testObtainRepositoriesWithProxiesWithWildcard() {

    List<MavenSettingsRepositoryModel> result = MavenProxy.obtainRepositories(repositoriesList, proxiesList.get(1),
        true);

    assertThat(result.size()).isEqualTo(0);
  }

  /**
   * Tests, whether repositories without proxies (withProxies=false) are returned, when using a wildcard
   */
  @Test
  public void testObtainRepositoriesWithoutProxiesWithWildcard() {

    List<MavenSettingsRepositoryModel> result = MavenProxy.obtainRepositories(repositoriesList, proxiesList.get(1),
        false);

    assertThat(result.size()).isEqualTo(3);
    assertThat(result.get(0).getUrl()).isEqualTo("localhost");
    assertThat(result.get(1).getUrl()).isEqualTo("www.google.com");
    assertThat(result.get(2).getUrl()).isEqualTo("test.example.com");
  }

  /**
   * Tests, whether repositories without proxies (withProxies=false) are returned, when using a selector
   */
  @Test
  public void testObtainRepositoriesWithoutProxiesWithSelector() {

    List<MavenSettingsRepositoryModel> result = MavenProxy.obtainRepositories(repositoriesList, proxiesList.get(0),
        false);

    assertThat(result.size()).isEqualTo(2);
    assertThat(result.get(0).getUrl()).isEqualTo("www.google.com");
    assertThat(result.get(1).getUrl()).isEqualTo("test.example.com");
  }
}
