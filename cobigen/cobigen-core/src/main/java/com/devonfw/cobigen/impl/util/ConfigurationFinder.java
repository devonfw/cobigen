package com.devonfw.cobigen.impl.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.impl.config.TemplateSetConfiguration;

/**
 * Utilities related to the cobigen configurations including:
 *
 * 1. templates location
 */
public class ConfigurationFinder {

  /** Logger instance */
  private static final Logger LOG = LoggerFactory.getLogger(ConfigurationFinder.class);

  private TemplateSetConfiguration templateSetConfiguration;

  /**
   * The Method finds config.properties related to GroupIds, Snapshotversion, etc.
   *
   * @param configurationLocation
   *
   * @return TemplateSetConfiguration class Object
   * @throws IOException
   */
  public TemplateSetConfiguration readTemplateSetConfiguration(URI configurationLocation) throws IOException {

    this.templateSetConfiguration = new TemplateSetConfiguration();

    Path pomFile = null;
    Path cobigenConfigFile = Paths.get(configurationLocation).resolve(ConfigurationConstants.COBIGEN_CONFIG_FILE);
    Properties apptest = new Properties();

    if (Files.exists(cobigenConfigFile)) {
      // 1. Check if config.properties found
      if (Files.exists(cobigenConfigFile.resolve("config.properties"))) {
        // set the Path to .cobigen Home Folder + config.properties
        pomFile = cobigenConfigFile.resolve("config.properties");

        String newproperties = pomFile.toString();

        // Read textfile
        apptest.load(new FileInputStream(newproperties));

        // Read GroupIds
        if (apptest.getProperty("template-sets.groupIds") != null) {
          String[] groupIds = apptest.getProperty("template-sets.groupIds").split(",");

          List<String> groupIdslist = Arrays.asList(groupIds);

          this.templateSetConfiguration.setGroupIds(groupIdslist);
        } else {
          // By default set only to (public) cobigen groupId
          apptest.setProperty("template-sets.groupIds", "com.devonfw.cobigen");
          this.templateSetConfiguration.setGroupIds(Arrays.asList("com.devonfw.cobigen"));
        }

        // Read Snapshot
        if (apptest.getProperty("template-sets.allow-snapshots") != null) {

          String snapshot = apptest.getProperty("template-sets.allow-snapshots");

          if (snapshot.equals("true")) {
            this.templateSetConfiguration.setAllowSnapshots(true);
          }
        } else {
          // by default false
          apptest.setProperty("template-sets.allow-snapshots", "false");
          this.templateSetConfiguration.setAllowSnapshots(false);
        }

        // Read Lookup
        if (apptest.getProperty("template-sets.disable-default-lookup") != null) {

          String lookup = apptest.getProperty("template-sets.disable-default-lookup");

          if (lookup.equals("true"))
            this.templateSetConfiguration.setDisableLookup(true);
        } else {
          // by default false
          apptest.setProperty("template-sets.disable-default-lookup", "false");
          this.templateSetConfiguration.setDisableLookup(false);
        }

        if (apptest.getProperty("template-sets.hide") != null) {

          String hide = apptest.getProperty("template-sets.hide");
          this.templateSetConfiguration.setHideTemplates(hide);
        } else {
          // by default false
          apptest.setProperty("template-sets.hide", "null");
          this.templateSetConfiguration.setHideTemplates(null);
        }

      }
      // 2. config.properties not Found
      else {
        // default values
        apptest.setProperty("template-sets.groupIds", "com.devonfw.cobigen");
        this.templateSetConfiguration.setGroupIds(Arrays.asList("com.devonfw.cobigen"));
        apptest.setProperty("template-sets.allow-snapshots", "false");
        this.templateSetConfiguration.setAllowSnapshots(false);
        apptest.setProperty("template-sets.disable-default-lookup", "false");
        this.templateSetConfiguration.setDisableLookup(false);
        apptest.setProperty("template-sets.hide", "null");
        this.templateSetConfiguration.setHideTemplates(null);
      }
    }

    return this.templateSetConfiguration;

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
      throw new CobiGenRuntimeException("An error occured while reading the config file " + cobigenConfigFile, e);
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
