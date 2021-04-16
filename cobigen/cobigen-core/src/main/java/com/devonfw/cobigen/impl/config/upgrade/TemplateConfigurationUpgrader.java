package com.devonfw.cobigen.impl.config.upgrade;

import java.io.InputStream;
import java.math.BigDecimal;

import org.dozer.DozerBeanMapper;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.NotYetSupportedException;
import com.devonfw.cobigen.impl.config.constant.TemplatesConfigurationVersion;
import com.devonfw.cobigen.impl.config.entity.io.TemplatesConfiguration;

/**
 * This class encompasses all logic for legacy templates configuration detection and upgrading these to the
 * latest supported version.
 * @author mbrunnli (Jun 22, 2015)
 */
public class TemplateConfigurationUpgrader extends AbstractConfigurationUpgrader<TemplatesConfigurationVersion> {

    /**
     * Creates a new {@link TemplateConfigurationUpgrader} instance.
     * @author mbrunnli (Jun 23, 2015)
     */
    public TemplateConfigurationUpgrader() {
        super(TemplatesConfigurationVersion.v1_0, TemplatesConfiguration.class,
            ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);
    }

    @Override
    protected ConfigurationUpgradeResult performNextUpgradeStep(TemplatesConfigurationVersion source,
        Object previousConfigurationRootNode) throws Exception {

        ConfigurationUpgradeResult result = new ConfigurationUpgradeResult();

        switch (source) {
        case v1_2: // to v2.1
        {
            DozerBeanMapper mapper = new DozerBeanMapper();
            try (InputStream stream =
                getClass().getResourceAsStream("/dozer/config/upgrade/templatesConfiguration-v1.2-v2.1.xml")) {
                mapper.addMapping(stream);
            }
            com.devonfw.cobigen.impl.config.entity.io.v2_1.TemplatesConfiguration upgradedConfig =
                mapper.map(previousConfigurationRootNode,
                    com.devonfw.cobigen.impl.config.entity.io.v2_1.TemplatesConfiguration.class);
            upgradedConfig.setVersion(new BigDecimal("2.1"));

            result.setResultConfigurationJaxbRootNode(upgradedConfig);
        }
            break;
        default:
            throw new NotYetSupportedException("An upgrade of the templates configuration from version " + source
                + " to a newer one is currently not supported.");
        }

        return result;
    }

}
