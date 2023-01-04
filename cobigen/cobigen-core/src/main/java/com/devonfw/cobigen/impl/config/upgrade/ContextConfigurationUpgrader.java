package com.devonfw.cobigen.impl.config.upgrade;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.NotYetSupportedException;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.impl.config.ContextConfiguration;
import com.devonfw.cobigen.impl.config.constant.ContextConfigurationVersion;
import com.devonfw.cobigen.impl.config.entity.io.v6_0.TemplateSetConfiguration;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * This class encompasses all logic for legacy context configuration detection and upgrading these to the latest
 * supported version.
 */
public class ContextConfigurationUpgrader extends AbstractConfigurationUpgrader<ContextConfigurationVersion> {

  /**
   * Creates a new {@link ContextConfigurationUpgrader} instance.
   */
  public ContextConfigurationUpgrader() {

    super(ContextConfigurationVersion.v1_0, ContextConfiguration.class, ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
  }

  @Override
  protected List<ConfigurationUpgradeResult> performNextUpgradeStep(ContextConfigurationVersion source,
      Object previousConfigurationRootNode, Path templatesLocation) throws Exception {

    Path configurationRoot = CobiGenPaths.getContextLocation(templatesLocation);
    ConfigurationUpgradeResult result = new ConfigurationUpgradeResult();
    MapperFactory mapperFactory;
    MapperFacade mapper;
    com.devonfw.cobigen.impl.config.entity.io.v2_1.ContextConfiguration upgradedConfig_2_1;
    List<ConfigurationUpgradeResult> results = new ArrayList<>();
    switch (source) {
      case v2_0:

        mapperFactory = new DefaultMapperFactory.Builder().useAutoMapping(true).mapNulls(true).build();
        mapperFactory
            .classMap(com.devonfw.cobigen.impl.config.entity.io.v2_0.ContextConfiguration.class,
                com.devonfw.cobigen.impl.config.entity.io.v2_1.ContextConfiguration.class)
            .field("triggers.trigger", "trigger").byDefault().register();
        mapperFactory
            .classMap(com.devonfw.cobigen.impl.config.entity.io.v2_0.ContainerMatcher.class,
                com.devonfw.cobigen.impl.config.entity.io.v2_1.ContainerMatcher.class)
            .field(
                "retrieveObjectsRecursively:{isRetrieveObjectsRecursively|setRetrieveObjectsRecursively(new Boolean(%s))|type=java.lang.Boolean}",
                "retrieveObjectsRecursively:{isRetrieveObjectsRecursively|setRetrieveObjectsRecursively(new Boolean(%s))|type=java.lang.Boolean}")
            .byDefault().register();

        mapper = mapperFactory.getMapperFacade();

        upgradedConfig_2_1 = mapper.map(previousConfigurationRootNode,
            com.devonfw.cobigen.impl.config.entity.io.v2_1.ContextConfiguration.class);
        upgradedConfig_2_1.setVersion(new BigDecimal("2.1"));
        result.setResultConfigurationJaxbRootNodeAndPath(upgradedConfig_2_1,
            configurationRoot.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME));
        results.add(result);

        break;
      case v2_1:
        TemplateSetUpgrader templatesSetUpgrader = new TemplateSetUpgrader();
        Map<TemplateSetConfiguration, Path> templateSetMap = templatesSetUpgrader
            .upgradeTemplatesToTemplateSetsV6(templatesLocation);
        for (TemplateSetConfiguration templateSetConfiguration : templateSetMap.keySet()) {
          ConfigurationUpgradeResult tempResult = new ConfigurationUpgradeResult();
          tempResult.setResultConfigurationJaxbRootNodeAndPath(templateSetConfiguration,
              templateSetMap.get(templateSetConfiguration));
          results.add(tempResult);
        }
        break;
      default:
        throw new NotYetSupportedException(
            "An upgrade of the context configuration from a version previous to v2.0 is not yet supported.");
    }

    return results;
  }

}
