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
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.impl.config.ConfigurationFactory;
import com.devonfw.cobigen.impl.config.TemplateSetConfiguration;

/**
 * Utilities related to the cobigen configurations including:
 *
 * 1. templates location
 */
public class ConfigurationFinder {

  /** Logger instance */
  private static final Logger LOG = LoggerFactory.getLogger(ConfigurationFinder.class);

  /**
   * load properties from .properties file into TemplateSetConfiguration if found valid properties otherwise load
   * default values
   *
   * @param path to a .properties file
   * @return TemplateSetConfiguration instance
   */
  public static TemplateSetConfiguration loadTemplateSetConfigurations(Path path) {

    Properties props = new Properties();
    try {
      props = readConfigurationFile(path);
    } catch (InvalidConfigurationException e) {
      LOG.info("This path {} is invalid. The default Config values will be loaded instead.", path);
    }

    String groupId = ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_GROUPIDS;
    String snapshot = ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_SNAPSHOTS;
    String hide = ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_HIDE;
    String disableLookup = ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_DISABLE_LOOKUP;
    String defaultGroupId = ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_DEFAULT_GROUPID;

    List<String> groupIdsList = (props.getProperty(groupId) != null)
        ? Arrays.asList(props.getProperty(groupId).split(","))
        : new ArrayList<>();
    // Creating a new ArrayList object which can be modified and prevents UnsupportedOperationException.
    List<String> groupIds = new ArrayList<>(groupIdsList);
    if (props.getProperty(disableLookup) == null || props.getProperty(disableLookup).equals("false"))
      if (!groupIds.contains(defaultGroupId))
        groupIds.add(defaultGroupId);

    boolean useSnapshots = props.getProperty(snapshot) == null || props.getProperty(snapshot).equals("false") ? false
        : true;
    List<String> hiddenIds = (props.getProperty(hide) != null) ? Arrays.asList(props.getProperty(hide).split(","))
        : new ArrayList<>();

    // TODO: Where do we get the configRoot from?
    ConfigurationFactory configurationFactory = new ConfigurationFactory(null);
    return configurationFactory.getTemplateSetConfiguration(groupIds, useSnapshots, hiddenIds);
  }

  /**
   * The method finds location of templates. It could be CobiGen_Templates folder or a template artifact
   *
   * @return template location uri if exist, otherwise null
   */
  public static URI findTemplatesLocation() {

    Path cobigenHome = CobiGenPaths.getCobiGenHomePath();
    Path configFile = cobigenHome.resolve(ConfigurationConstants.COBIGEN_CONFIG_FILE);

    if (configFile != null && Files.exists(configFile)) {
      LOG.debug("Custom cobigen configuration found at {}", configFile);
      Properties props = readConfigurationFile(configFile);
      String templatesLocation = props.getProperty(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH);
      String templateSetsLocation = props.getProperty(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_PATH);

      // use old templates configuration
      Path templatesFolderLocation = getTemplatesFolderLocation(cobigenHome, configFile, templatesLocation);
      if (templatesFolderLocation != null && Files.exists(templatesFolderLocation)) {
        return templatesFolderLocation.toUri();
      }

      // use new template set configuration
      Path templateSetsFolderLocation = getTemplatesFolderLocation(cobigenHome, configFile, templateSetsLocation);
      if (templateSetsFolderLocation != null && Files.exists(templateSetsFolderLocation)) {
        return templateSetsFolderLocation.toUri();
      }

    } else {
      LOG.info("No custom templates configuration found. Getting templates from {}",
          CobiGenPaths.getTemplateSetsFolderPath(cobigenHome));
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
   * This is a helper method to read a given cobigen configuration file
   *
   * @param cobigenConfigFile cobigen configuration file
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

    // 1. use old Cobigen_Templates folder
    if (Files.exists(templatesFolderPath)) {
      return templatesFolderPath.toUri();
    }

    // 2. use template jar
    if (Files.exists(templatesPath)) {
      Path jarFilePath = TemplatesJarUtil.getJarFile(false, templatesPath);
      if (jarFilePath != null && Files.exists(jarFilePath)) {
        return jarFilePath.toUri();
      }
    }

    // 3. create/use new template sets folder
    Path templateSetsFolderPath = CobiGenPaths.getTemplateSetsFolderPath(home, true);

    Path templateSetsAdaptedFolderPath = templateSetsFolderPath.resolve(ConfigurationConstants.ADAPTED_FOLDER);
    Path templateSetsDownloadedFolderPath = templateSetsFolderPath.resolve(ConfigurationConstants.DOWNLOADED_FOLDER);

    // 4. check adapted and downloaded folder
    if (Files.exists(templateSetsAdaptedFolderPath) || Files.exists(templateSetsDownloadedFolderPath)) {
      return templateSetsFolderPath.toUri();
    }

    // 5. download template set jars

    LOG.info("Could not find any templates in cobigen home directory {}. Downloading...",
        CobiGenPaths.getCobiGenHomePath());

    TemplatesJarUtil.downloadLatestDevon4jTemplates(true, templatesPath.toFile());
    TemplatesJarUtil.downloadLatestDevon4jTemplates(false, templatesPath.toFile());
    return templateSetsFolderPath.toUri();
  }

}
