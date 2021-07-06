package com.devonfw.cobigen.impl.config.upgrade;

import java.math.BigDecimal;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.NotYetSupportedException;
import com.devonfw.cobigen.impl.config.ContextConfiguration;
import com.devonfw.cobigen.impl.config.constant.ContextConfigurationVersion;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * This class encompasses all logic for legacy context configuration detection and upgrading these to the
 * latest supported version.
 * @author mbrunnli (Jun 22, 2015)
 */
public class ContextConfigurationUpgrader extends AbstractConfigurationUpgrader<ContextConfigurationVersion> {

    /**
     * Creates a new {@link ContextConfigurationUpgrader} instance.
     * @author mbrunnli (Jun 23, 2015)
     */
    public ContextConfigurationUpgrader() {
        super(ContextConfigurationVersion.v1_0, ContextConfiguration.class,
            ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
    }

    @Override
    protected ConfigurationUpgradeResult performNextUpgradeStep(ContextConfigurationVersion source,
        Object previousConfigurationRootNode) throws Exception {

        ConfigurationUpgradeResult result = new ConfigurationUpgradeResult();

        switch (source) {
        case v2_0:
            // to v2.1

            MapperFactory mapperFactory =
                new DefaultMapperFactory.Builder().useAutoMapping(true).mapNulls(true).build();
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
            MapperFacade mapper = mapperFactory.getMapperFacade();
            com.devonfw.cobigen.impl.config.entity.io.v2_1.ContextConfiguration upgradedConfig =
                mapper.map(previousConfigurationRootNode,
                    com.devonfw.cobigen.impl.config.entity.io.v2_1.ContextConfiguration.class);
            upgradedConfig.setVersion(new BigDecimal("2.1"));

            result.setResultConfigurationJaxbRootNode(upgradedConfig);

            break;
        default:
            throw new NotYetSupportedException(
                "An upgrade of the context configuration from a version previous to v2.0 is not yet supported.");
        }

        return result;
    }

}
