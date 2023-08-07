package com.devonfw.cobigen.impl.config.reader;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.config.constant.TemplatesConfigurationVersion;
import com.devonfw.cobigen.impl.config.entity.TemplateSet;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration;
import com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration;
import com.devonfw.cobigen.impl.config.entity.io.TemplatesConfiguration;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@link TemplateSetReader} combines everything from the {@link TemplatesConfigurationReader} and
 * {@link ContextConfigurationReader}
 */
public class TemplateSetReader extends JaxbDeserializer {

  private final Path templateSetFile;
  /**
   * List with the paths of the configuration locations for the template-set.xml files
   */
  // TODO: Check if this map can replace templateSetFile and configLocation, see:
  // https://github.com/devonfw/cobigen/issues/1668
  private Map<Path, Path> configLocations = new HashMap<>();
  private ContextConfiguration contextConfiguration;

  private com.devonfw.cobigen.impl.config.ContextConfiguration contextConfigurationBo;

  private TemplatesConfiguration templatesConfiguration;

  private final ConfigurationReader configurationReader;


  /**
   * The TemplateSetConfigurationManager manages adapted and downloaded template sets
   * <p>
   * TODO: Check if it can be integrated into this reader, see: https://github.com/devonfw/cobigen/issues/1668
   */
//  private final TemplateSetConfigurationManager templateSetConfigurationManager = new TemplateSetConfigurationManager();


  public TemplateSetReader(Path rootDir, ConfigurationReader configurationReader) {
    this.templateSetFile = rootDir.resolve(ConfigurationConstants.MAVEN_CONFIGURATION_RESOURCE_FOLDER).resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);
    this.configurationReader = configurationReader;
    deserializeConfigFile();
  }

  /**
   * Reads the template set xml file and initializes the templates and context configurations and readers
   */
  private void deserializeConfigFile() {

    TemplateSetConfiguration templateSetConfiguration = deserialize(templateSetFile, com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration.class, TemplatesConfigurationVersion.class, "templateSetConfiguration");
    contextConfiguration = templateSetConfiguration.getContextConfiguration();
    templatesConfiguration = templateSetConfiguration.getTemplatesConfiguration();
  }

  public com.devonfw.cobigen.impl.config.ContextConfiguration readContextConfiguration() {
    if(contextConfigurationBo == null) {
      contextConfigurationBo = new ContextConfigurationReader(contextConfiguration, templateSetFile).read();
    }
    return contextConfigurationBo;
  }

  public com.devonfw.cobigen.impl.config.TemplatesConfiguration readTemplatesConfiguration(Trigger trigger) {
    return new TemplatesConfigurationReader(templatesConfiguration, templateSetFile.getParent(), configurationReader, templateSetFile).read(trigger);
  }
}
