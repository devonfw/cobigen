package com.devonfw.cobigen.impl.config.upgrade;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.NotYetSupportedException;
import com.devonfw.cobigen.impl.config.ContextConfiguration;
import com.devonfw.cobigen.impl.config.constant.ContextConfigurationVersion;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * This class encompasses all logic for legacy context configuration detection
 * and upgrading these to the latest
 * supported version.
 *
 * @author mbrunnli (Jun 22, 2015)
 */
public class ContextConfigurationUpgrader extends AbstractConfigurationUpgrader<ContextConfigurationVersion> {

	/** Logger instance. */
	private static final Logger LOG = LoggerFactory.getLogger(ContextConfigurationUpgrader.class);

  /**
   * Creates a new {@link ContextConfigurationUpgrader} instance.
   *
   * @author mbrunnli (Jun 23, 2015)
   */
  public ContextConfigurationUpgrader() {

    super(ContextConfigurationVersion.v1_0, ContextConfiguration.class, ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
  }

  @Override
  protected List<ConfigurationUpgradeResult> performNextUpgradeStep(ContextConfigurationVersion source,
      Object previousConfigurationRootNode, Path configurationRoot) throws Exception {

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

        result.setResultConfigurationJaxbRootNode(upgradedConfig_2_1, configurationRoot);
        results.add(result);

        break;
      case v2_1:
    	  // Upgrade Process from v2_1 to v3_0 will be done in TemplateSetUpgrader class from the upgradeConfigurationtoLatesVersion function
    	  TemplateSetUpgrader u = new TemplateSetUpgrader();
    	  Map<com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration, Path> contextMap = u.upgradeTemplatesToTemplateSets(configurationRoot);
    	  for(com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration context: contextMap.keySet()) {
    		  ConfigurationUpgradeResult temp = new ConfigurationUpgradeResult();
    		  temp.setResultConfigurationJaxbRootNode(context, contextMap.get(context));
    		  results.add(temp);
    	  }
//    	  for(Path context: contexts) {
//    		  List<ContextConfigurationVersion> list= Arrays.asList(this.resolveLatestCompatibleSchemaVersion(context));
//    		  for(ContextConfigurationVersion version : list) {
//    			  if ( version == null) {
//    				  LOG.error("VERsion ist null");
//    			  }else if( version != ContextConfigurationVersion.v3_0) {
//    				 LOG.error("Version ist falsch");
//    			  }else {
//    				  LOG.info("Sucess");
//    			  }
//    		  }
//    	  }
        break;
      default:
        throw new NotYetSupportedException(
            "An upgrade of the context configuration from a version previous to v2.0 is not yet supported.");
    }

    return results;
  }

}
