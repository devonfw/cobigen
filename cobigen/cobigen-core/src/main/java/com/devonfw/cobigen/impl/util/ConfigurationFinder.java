package com.devonfw.cobigen.impl.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;

/**
 * Utilities related to the cobigen configurations including:
 *
 * 1. templates location
 */
public class ConfigurationFinder {

  /** Logger instance */
  private static final Logger LOG = LoggerFactory.getLogger(ConfigurationFinder.class);

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
      if (StringUtils.isNotEmpty(templatesLocation)) {
        LOG.info("Custom templates path found. Taking templates from {}", templatesLocation);
        Path templatesPath = Paths.get(templatesLocation);
        if (Files.exists(templatesPath)) {
          return Paths.get(templatesLocation).toUri();
        } else {
          LOG.info("Value of property {} in {} is invalid. Fall back to templates from {}",
              ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH, configFile,
              CobiGenPaths.getTemplatesFolderPath(cobigenHome));
        }
      } else {
        LOG.info("Property {} is not set in {}. Fall back to templates from {}",
            ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH, configFile,
            CobiGenPaths.getTemplatesFolderPath(cobigenHome));
      }
    } else {
      LOG.info("No custom templates configuration found. Getting templates from {}",
          CobiGenPaths.getTemplatesFolderPath(cobigenHome));
    }
    return findTemplates(cobigenHome);
  }

  /**
   * This is a helper method to read a given cobigen configuration file
   *
   * @param cobigenConfigFile cobigen configuration file
   * @return Properties containing configuration
   */
  public static Properties readConfigurationFile(Path cobigenConfigFile) {

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
   * Checks if .cobigen properties file contains any add-generated-annotation
   *
   * @return the value of add-generated-annotation
   * @throws IOException
   */
  public static boolean checkGeneratedAnnotationInProperties() {

    Boolean defaultGeneratedAnnotation = true;
    Path cobigenHome = CobiGenPaths.getCobiGenHomePath();
    Path dotCobigenFilePath = cobigenHome.resolve(ConfigurationConstants.COBIGEN_CONFIG_FILE);
    Properties dotCobigenProperties = null;

    if (Files.exists(dotCobigenFilePath)) {
      // read from the file
      dotCobigenProperties = readConfigurationFile(dotCobigenFilePath);
      if (dotCobigenProperties.containsKey(ConfigurationConstants.ADD_GENERATED_ANNOTATION)) {
        String value = dotCobigenProperties.getProperty(ConfigurationConstants.ADD_GENERATED_ANNOTATION);
        defaultGeneratedAnnotation = Boolean.valueOf(value);
      }
      // add generated annotation set to true (default behaviour)
      else {
        FileSystemUtil.addGeneratedAnnotationproperty(dotCobigenProperties, dotCobigenFilePath);
      }
    }
    // if .cobigen file do not exist then it creates one and set add-generated-annotation to true
    else {
      FileSystemUtil.addGeneratedAnnotationproperty(dotCobigenProperties, dotCobigenFilePath);
    }
    return defaultGeneratedAnnotation;
  }

  /**
   * This is a helper method to find templates in cobigen home
   *
   * @param home cobigen configuration home directory
   * @return templates location if found, otherwise null
   */
  private static URI findTemplates(Path home) {

    Path templatesPath = CobiGenPaths.getTemplatesFolderPath(home);
    Path templatesFolderPath = templatesPath.resolve(ConfigurationConstants.COBIGEN_TEMPLATES);

    // 1. use Cobigen_Templates folder
    if (Files.exists(templatesFolderPath)) {
      return templatesFolderPath.toUri();
    }

    // 2. use template jar
    Path jarPath = getTemplateJar(templatesPath);
    if (jarPath != null) {
      return jarPath.toUri();
    }

    // 3.try finding a jar on current classpath

    LOG.info("Could not find any templates in cobigen home directory {}. Downloading...",
        CobiGenPaths.getCobiGenHomePath());

    TemplatesJarUtil.downloadLatestDevon4jTemplates(true, templatesPath.toFile());
    TemplatesJarUtil.downloadLatestDevon4jTemplates(false, templatesPath.toFile());
    return getTemplateJar(templatesPath).toUri();
  }

  /**
   * @param templatesPath the templates cache directory
   *
   * @return the path of the templates jar
   */
  private static Path getTemplateJar(Path templatesPath) {

    File templateJar = TemplatesJarUtil.getJarFile(false, templatesPath.toFile());
    if (templateJar != null && Files.exists(templatesPath)) {
      return templateJar.toPath();
    }
    return null;
  }

}
