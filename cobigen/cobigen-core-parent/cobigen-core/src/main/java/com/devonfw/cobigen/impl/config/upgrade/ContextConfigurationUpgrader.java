package com.devonfw.cobigen.impl.config.upgrade;

import java.io.InputStream;
import java.math.BigDecimal;

import org.dozer.DozerBeanMapper;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.NotYetSupportedException;
import com.devonfw.cobigen.impl.config.ContextConfiguration;
import com.devonfw.cobigen.impl.config.constant.ContextConfigurationVersion;

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

            DozerBeanMapper mapper = new DozerBeanMapper();
            try (InputStream stream =
                getClass().getResourceAsStream("/dozer/config/upgrade/contextConfiguration-v2.0-v2.1.xml")) {
                mapper.addMapping(stream);
            }
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
