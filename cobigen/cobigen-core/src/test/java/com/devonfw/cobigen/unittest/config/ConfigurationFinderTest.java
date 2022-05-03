package com.devonfw.cobigen.unittest.config;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.util.ConfigurationFinder;

/**
 * mdukhan Class to test the new properties Reader (config.properties)
 *
 */
public class ConfigurationFinderTest {

  /**
   * Test Constants and reading properties from example config.properties
   */
  @Test
  public void TestNewPropertiesReader() {

    String testFileRootPath = "src/test/resources/testdata/unittest/config/config.properties";
    Properties apptest = new Properties();
    try {
      apptest.load(new FileInputStream(testFileRootPath));
      String templateSetsLocation = apptest.getProperty(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_GROUPIDS);
      System.out.println(templateSetsLocation);
      String[] groupIds = apptest.getProperty("template-sets.groupIds").split(",");
      assertEquals(groupIds[0], "devonfw-cobigen-bla");
      assertEquals(groupIds[1], "abcd");
      assertEquals(groupIds[2], "blablob");

      String IsSnapshot = apptest.getProperty(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_SNAPSHOTS);
      assertEquals(IsSnapshot, "true");

      String IsDisableLookup = apptest.getProperty(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_LOOKUP);
      assertEquals(IsDisableLookup, "false");

    } catch (Exception e) {
    }

  }

  /**
   * Test CheckTemplateSetConfiguration Method inside ConfigurationFinder Class if valid properties found.
   */
  @Test
  public void readTemplateSetConfigurationTest() {

    Properties apptest = new Properties();

    apptest.setProperty(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_GROUPIDS, "abcd");
    apptest.setProperty(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_SNAPSHOTS, "true");
    apptest.setProperty(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_LOOKUP, "true");
    apptest.setProperty(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_HIDE, "templateblablob");

    apptest = ConfigurationFinder.checkTemplateSetConfiguration(apptest);
    assertEquals(apptest.getProperty(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_GROUPIDS), "abcd");
    assertEquals(apptest.getProperty(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_SNAPSHOTS), "true");
    assertEquals(apptest.getProperty(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_LOOKUP), "true");
    assertEquals(apptest.getProperty(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_HIDE), "templateblablob");

  }

  /**
   * Test CheckTemplateSetConfiguration Method inside ConfigurationFinder Class if properties not correclty set by the
   * user, to set to the default values
   *
   * @throws IOException
   * @throws FileNotFoundException
   */
  @Test
  public void readTemplateSetDefaultConfigurationTest() throws FileNotFoundException, IOException {

    Properties apptest = new Properties();

    apptest = ConfigurationFinder.checkTemplateSetConfiguration(apptest);

    // // by default should be com.devonfw.cobigen
    assertEquals(apptest.getProperty(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_GROUPIDS),
        "com.devonfw.cobigen");
    // // by default should be false
    assertEquals(apptest.getProperty(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_SNAPSHOTS), "false");
    // // by default should be false
    assertEquals(apptest.getProperty(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_LOOKUP), "false");
    // // by default should be null
    assertEquals(apptest.getProperty(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_HIDE), "null");

  }

}
