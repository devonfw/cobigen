package com.devonfw.cobigen.unittest.config;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;

import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.impl.config.TemplateSetConfiguration;
import com.devonfw.cobigen.impl.util.ConfigurationFinder;

/**
 * mdukhan Class to test the new properties Reader (config.properties)
 *
 */
public class ConfigurationFinderTest {

  @Test

  public void TestNewPropertiesReader() {

    String testFileRootPath = "src/test/resources/testdata/unittest/config/config.properties";
    // Path newproperties = cobigenConfigFile + "config.properties";
    Properties apptest = new Properties();
    try {
      apptest.load(new FileInputStream(testFileRootPath));

      String[] groupIds = apptest.getProperty("template-sets.groupIds").split(",");
      assertEquals(groupIds[0], "devonfw-cobigen-bla");
      assertEquals(groupIds[1], "abcd");
      assertEquals(groupIds[2], "blablob");

      String IsSnapshot = apptest.getProperty("template-sets.allow-snapshots");
      assertEquals(IsSnapshot, "true");

      String IsDisableLookup = apptest.getProperty("template-sets.disable-default-lookup");
      assertEquals(IsDisableLookup, "false");

    } catch (Exception e) {
    }

  }

  @Test
  @Ignore
  /**
   * TODO Test readTemplateSetConfiguration Method inside ConfigurationFinder Class if config.properties found.
   */
  public void readTemplateSetConfigurationTest() {

    // URI TestConfigLocation = ConfigurationFinder.findTemplatesLocation();

    Path cobigenHome = CobiGenPaths.getCobiGenHomePath();
    // Path configFile = cobigenHome.resolve(ConfigurationConstants.COBIGEN_CONFIG_FILE);
    // configFile
    // URI TestConfigLocation = configFile.toUri();
    // Path pomFile = cobigenHome.resolve("config.properties");
    ConfigurationFinder cFinder = new ConfigurationFinder();
    // instead of reaching .cobigen/.cobigen we need to go a up to the parent folder to reach ./cobigen
    Path ParentPath = cobigenHome.getParent();
    try {
      TemplateSetConfiguration TestProperties = cFinder.readTemplateSetConfiguration(ParentPath.toUri());
      List<String> GroupIds = TestProperties.getGroupIds();

      assertEquals(GroupIds.get(0), "devonfw-cobigen-bla");
      assertEquals(GroupIds.get(1), "abcd");
      assertEquals(GroupIds.get(2), "blablob");
      assertEquals(TestProperties.isAllowSnapshots(), true);
      assertEquals(TestProperties.isDisableLookup(), false);
      assertEquals(TestProperties.getHideTemplates(), null);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Test
  /**
   * Test readTemplateSetConfiguration Method inside ConfigurationFinder Class if config.properties not found.
   */
  public void readTemplateSetDefaultConfigurationTest() {

    Path cobigenHome = CobiGenPaths.getCobiGenHomePath();
    ConfigurationFinder cFinder = new ConfigurationFinder();
    Path ParentPath = cobigenHome.getParent();
    try {
      TemplateSetConfiguration TestProperties = cFinder.readTemplateSetConfiguration(ParentPath.toUri());
      List<String> GroupIds = TestProperties.getGroupIds();

      // by default should be com.devonfw.cobigen
      assertEquals(GroupIds.get(0), "com.devonfw.cobigen");
      // by default should be false
      assertEquals(TestProperties.isAllowSnapshots(), false);
      // by default should be false
      assertEquals(TestProperties.isDisableLookup(), false);
      // by default should be null
      assertEquals(TestProperties.getHideTemplates(), null);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
