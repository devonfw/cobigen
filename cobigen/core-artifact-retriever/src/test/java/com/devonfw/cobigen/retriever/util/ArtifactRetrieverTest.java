package com.devonfw.cobigen.retriever.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.retriever.settings.util.MavenSettingsUtil;
import com.devonfw.cobigen.retriever.settings.util.to.model.MavenSettingsModel;

public class ArtifactRetrieverTest {

  private final static String testdataRoot = "src/test/resources/testdata/unittest/ArtifactRetrieverTest";

  private static MavenSettingsModel modelNonProxy;

  private static MavenSettingsModel modelProxy;

  private static String mavenSettingsNonProxy;

  private static String mavenSettingsProxy;

  /**
   * Used to initialize data needed for the tests
   */
  @BeforeClass
  public static void setUpClass() {

    try {
      mavenSettingsNonProxy = Files.readString(Paths.get(testdataRoot).resolve("settingsNonProxy.xml"));
      mavenSettingsProxy = Files.readString(Paths.get(testdataRoot).resolve("settingsProxy.xml"));
    } catch (IOException e) {
      throw new CobiGenRuntimeException("Unable to read test settings.xml", e);
    }
    modelNonProxy = MavenSettingsUtil.generateMavenSettingsModel(mavenSettingsNonProxy);
    modelProxy = MavenSettingsUtil.generateMavenSettingsModel(mavenSettingsProxy);
  }

  @Test
  public void testRetrieveTemplateSetXmlDownloadLinks() {

  }

}
