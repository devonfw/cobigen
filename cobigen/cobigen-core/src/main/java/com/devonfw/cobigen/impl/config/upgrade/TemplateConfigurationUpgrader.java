package com.devonfw.cobigen.impl.config.upgrade;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.NotYetSupportedException;
import com.devonfw.cobigen.impl.config.constant.TemplatesConfigurationVersion;
import com.devonfw.cobigen.impl.config.entity.io.v1_2.Increment;
import com.devonfw.cobigen.impl.config.entity.io.v1_2.IncrementRef;
import com.devonfw.cobigen.impl.config.entity.io.v1_2.Template;
import com.devonfw.cobigen.impl.config.entity.io.v1_2.TemplateExtension;
import com.devonfw.cobigen.impl.config.entity.io.v1_2.TemplateRef;
import com.devonfw.cobigen.impl.config.entity.io.v1_2.TemplateScan;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * This class encompasses all logic for legacy templates configuration detection and upgrading these to the latest
 * supported version.
 *
 * @author mbrunnli (Jun 22, 2015)
 */
public class TemplateConfigurationUpgrader extends AbstractConfigurationUpgrader<TemplatesConfigurationVersion> {

  /**
   * Creates a new {@link TemplateConfigurationUpgrader} instance.
   *
   * @author mbrunnli (Jun 23, 2015)
   */
  public TemplateConfigurationUpgrader() {

    super(TemplatesConfigurationVersion.v1_0,
        com.devonfw.cobigen.impl.config.entity.io.v1_0.TemplatesConfiguration.class,
        ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);
  }

  @Override
  protected List<ConfigurationUpgradeResult> performNextUpgradeStep(TemplatesConfigurationVersion source,
      Object previousConfigurationRootNode, Path configurationRoot) throws Exception {

    List<ConfigurationUpgradeResult> results = new ArrayList<>();
    ConfigurationUpgradeResult result = new ConfigurationUpgradeResult();

    switch (source) {
      case v1_2: // to v2.1
      {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().mapNulls(true).useAutoMapping(true).build();
        mapperFactory.classMap(Template.class, com.devonfw.cobigen.impl.config.entity.io.v2_1.Template.class)
            .field("id", "name").byDefault().register();
        mapperFactory.classMap(Increment.class, com.devonfw.cobigen.impl.config.entity.io.v2_1.Increment.class)
            .field("id", "name").field("templateRefOrIncrementRef", "templateRefOrIncrementRefOrTemplateScanRef")
            .byDefault().register();
        mapperFactory
            .classMap(TemplateExtension.class, com.devonfw.cobigen.impl.config.entity.io.v2_1.TemplateExtension.class)
            .field("idref", "ref").byDefault().register();
        mapperFactory.classMap(TemplateScan.class, com.devonfw.cobigen.impl.config.entity.io.v2_1.TemplateScan.class)
            .field("templateIdPrefix", "templateNamePrefix").byDefault().register();
        mapperFactory.classMap(TemplateRef.class, com.devonfw.cobigen.impl.config.entity.io.v2_1.TemplateRef.class)
            .field("idref", "ref").byDefault().register();
        mapperFactory.classMap(IncrementRef.class, com.devonfw.cobigen.impl.config.entity.io.v2_1.IncrementRef.class)
            .field("idref", "ref").byDefault().register();
        MapperFacade mapper = mapperFactory.getMapperFacade();
        com.devonfw.cobigen.impl.config.entity.io.v2_1.TemplatesConfiguration upgradedConfig = mapper.map(
            previousConfigurationRootNode, com.devonfw.cobigen.impl.config.entity.io.v2_1.TemplatesConfiguration.class);
        upgradedConfig.setVersion(new BigDecimal("2.1"));

        result.setResultConfigurationJaxbRootNodeAndPath(upgradedConfig,
            configurationRoot.resolve(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME));
        results.add(result);
      }
        break;
      case v2_1: { // to 4.0
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().mapNulls(true).useAutoMapping(true).build();
        MapperFacade mapper = mapperFactory.getMapperFacade();
        com.devonfw.cobigen.impl.config.entity.io.v4_0.TemplatesConfiguration upgradedConfig = mapper.map(
            previousConfigurationRootNode, com.devonfw.cobigen.impl.config.entity.io.v4_0.TemplatesConfiguration.class);
        upgradedConfig.setVersion(new BigDecimal("4.0"));

        result.setResultConfigurationJaxbRootNodeAndPath(upgradedConfig,
            configurationRoot.resolve(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME));
        results.add(result);
      }
        break;
      case v4_0: { // to 5.0
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().mapNulls(true).useAutoMapping(true).build();
        MapperFacade mapper = mapperFactory.getMapperFacade();
        com.devonfw.cobigen.impl.config.entity.io.v5_0.TemplatesConfiguration upgradedConfig = mapper.map(
            previousConfigurationRootNode, com.devonfw.cobigen.impl.config.entity.io.v5_0.TemplatesConfiguration.class);
        upgradedConfig.setVersion(new BigDecimal("5.0"));

        result.setResultConfigurationJaxbRootNodeAndPath(upgradedConfig,
            configurationRoot.resolve(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME));
        results.add(result);
      }
        break;
      default:
        throw new NotYetSupportedException("An upgrade of the templates configuration from version " + source
            + " to a newer one is currently not supported.");
    }

    return results;
  }

}
