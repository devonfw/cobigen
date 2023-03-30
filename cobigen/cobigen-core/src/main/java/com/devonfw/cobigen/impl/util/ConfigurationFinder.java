package com.devonfw.cobigen.impl.util;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.api.util.MavenCoordinate;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.impl.config.ConfigurationProperties;

/**
 * Utilities related to the CobiGen configurations including:
 *
 * 1. templates location
 */
public class ConfigurationFinder {

  /** Logger instance */
  private static final Logger LOG = LoggerFactory.getLogger(ConfigurationFinder.class);

  /**
   * Retrieves {@link ConfigurationProperties} from .properties file if valid properties were found otherwise returns
   * default values
   *
   * @param propertiesPath Path to a .properties file
   * @return {@link ConfigurationProperties} instance
   *
   */
  public static ConfigurationProperties retrieveCobiGenProperties(Path propertiesPath) {

    Properties props = new Properties();
    try {
      props = readConfigurationFile(propertiesPath);
    } catch (InvalidConfigurationException e) {
      LOG.info("This path {} is invalid. The default Config values will be loaded instead.", propertiesPath);
    }

    String groupId = ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_GROUPIDS;
    String snapshot = ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_SNAPSHOTS;
    String hide = ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_HIDE;
    String disableLookup = ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_DISABLE_LOOKUP;
    String defaultGroupId = ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_DEFAULT_GROUPID;
    String templateSetsInstalled = ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_INSTALLED;

    List<String> groupIdsList = new ArrayList<>();
    if (props.getProperty(groupId) != null) {
      groupIdsList = Arrays.asList(props.getProperty(groupId).split(","));
    }
    // Creating a new ArrayList object which can be modified and prevents UnsupportedOperationException.
    List<String> groupIds = new ArrayList<>(groupIdsList);
    if (props.getProperty(disableLookup) == null || props.getProperty(disableLookup).equals("false"))
      if (!groupIds.contains(defaultGroupId))
        groupIds.add(defaultGroupId);

    boolean useSnapshots;
    useSnapshots = false;
    if (props.getProperty(snapshot) != null) {
      if (props.getProperty(snapshot).equals("true"))
        useSnapshots = true;
    }

    List<String> hiddenIdsString = new ArrayList<>();
    if (props.getProperty(hide) != null) {
      hiddenIdsString = Arrays.asList(props.getProperty(hide).split(","));
    }

    List<String> mavenCoordinates = new ArrayList<>();
    if (props.getProperty(templateSetsInstalled) != null) {
      mavenCoordinates = Arrays.asList(props.getProperty(templateSetsInstalled).split(","));
    }

    List<MavenCoordinate> hiddenIds = MavenCoordinateUtil.convertToMavenCoordinates(hiddenIdsString);
    List<MavenCoordinate> convertedMavenCoordinates = MavenCoordinateUtil.convertToMavenCoordinates(mavenCoordinates);

    ConfigurationProperties configurationProperties = new ConfigurationProperties(groupIds, useSnapshots, hiddenIds,
        convertedMavenCoordinates);

    return configurationProperties;
  }

  /**
   * The method finds location of templates. It could be CobiGen_Templates folder or a template artifact
   *
   * @return template location URI if exist, otherwise null
   */
  public static URI findTemplatesLocation() {

    Path cobigenHome = CobiGenPaths.getCobiGenHomePath();
    Path configFile = cobigenHome.resolve(ConfigurationConstants.COBIGEN_CONFIG_FILE);

    if (configFile != null && Files.exists(configFile) && !Files.isDirectory(configFile)) {
      LOG.debug("Custom cobigen configuration found at {}", configFile);
      Properties props = readConfigurationFile(configFile);
      String templatesLocation = props.getProperty(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH);
      String templateSetsLocation = props.getProperty(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_PATH);

      // use new template set configuration
      Path templateSetsFolderLocation = getTemplatesFolderLocation(cobigenHome, configFile, templateSetsLocation);
      if (templateSetsFolderLocation != null && Files.exists(templateSetsFolderLocation)) {
        return templateSetsFolderLocation.toUri();

      }
      // use old templates configuration
      Path templatesFolderLocation = getTemplatesFolderLocation(cobigenHome, configFile, templatesLocation);
      if (templatesFolderLocation != null && Files.exists(templatesFolderLocation)) {
        return templatesFolderLocation.toUri();
      }
    }
    return findTemplates(cobigenHome);
  }

  /**
   * Gets folder location of templates
   *
   * @param cobigenHome Path of CobiGen home directory
   * @param configFile Path of configuration file
   * @param templatesLocation String of templatesLocation property
   */
  private static Path getTemplatesFolderLocation(Path cobigenHome, Path configFile, String templatesLocation) {

    if (StringUtils.isNotEmpty(templatesLocation)) {
      LOG.info("Custom templates path found. Taking templates from {}", templatesLocation);
      Path templatesPath = Paths.get(templatesLocation);
      if (Files.exists(templatesPath)) {
        return Paths.get(templatesLocation);
      } else {
        LOG.info("Value of property {} in {} is invalid. Fall back to templates from {}",
            ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH, configFile,
            CobiGenPaths.getTemplatesFolderPath(cobigenHome));
        return null;
      }
    } else {
      LOG.info("Property {} is not set in {}. Fall back to templates from {}",
          ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH, configFile,
          CobiGenPaths.getTemplatesFolderPath(cobigenHome));
      return null;
    }
  }

  /**
   * This is a helper method to read a given CobiGen configuration file
   *
   * @param cobigenConfigFile CobiGen configuration file
   * @throws InvalidConfigurationException if the file isn't present or the path is invalid
   * @return Properties containing configuration
   */
  private static Properties readConfigurationFile(Path cobigenConfigFile) {

    Properties props = new Properties();
    try {
      String configFileContents = Files.readAllLines(cobigenConfigFile, Charset.forName("UTF-8")).stream()
          .collect(Collectors.joining("\n"));
      configFileContents = configFileContents.replace("\\", "\\\\");
      try (StringReader strReader = new StringReader(configFileContents)) {
        props.load(strReader);
      }
    } catch (IOException e) {
      throw new InvalidConfigurationException("An error occured while reading the config file " + cobigenConfigFile, e);
    }
    return props;
  }

  /**
   * This is a helper method to find templates or template sets in CobiGen home
   *
   * @param home CobiGen configuration home directory
   * @return templates / template sets location if found, otherwise null
   */
  private static URI findTemplates(Path home) {

    Path templatesPath = CobiGenPaths.getTemplatesFolderPath(home);
    Path templatesFolderPath = templatesPath.resolve(ConfigurationConstants.COBIGEN_TEMPLATES);

    // 1. create/use new template sets folder
    Path templateSetsFolderPath = CobiGenPaths.getTemplateSetsFolderPath(home, false);

    Path templateSetsAdaptedFolderPath = templateSetsFolderPath.resolve(ConfigurationConstants.ADAPTED_FOLDER);
    Path templateSetsDownloadedFolderPath = templateSetsFolderPath.resolve(ConfigurationConstants.DOWNLOADED_FOLDER);

    // 2. check adapted and downloaded folder
    if (Files.exists(templateSetsAdaptedFolderPath) || Files.exists(templateSetsDownloadedFolderPath)) {
      return templateSetsFolderPath.toUri();
    }

    // 3. use old Cobigen_Templates folder
    if (Files.exists(templatesFolderPath)) {
      return templatesFolderPath.toUri();
    }

    // 4. use template jar
    if (Files.exists(templatesPath)) {
      Path jarFilePath = TemplatesJarUtil.getJarFile(false, templatesPath);
      if (jarFilePath != null && Files.exists(jarFilePath)) {
        return jarFilePath.toUri();
      }
    }
    templateSetsFolderPath = CobiGenPaths.getTemplateSetsFolderPath(home, true);
    return templateSetsFolderPath.toUri();
  }

}
