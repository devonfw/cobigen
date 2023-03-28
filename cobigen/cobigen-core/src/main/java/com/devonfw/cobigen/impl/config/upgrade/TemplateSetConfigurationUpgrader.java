package com.devonfw.cobigen.impl.config.upgrade;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.NotYetSupportedException;
import com.devonfw.cobigen.impl.config.TemplateSetConfiguration;
import com.devonfw.cobigen.impl.config.constant.TemplateSetConfigurationVersion;

/**
 * This class encompasses all logic for template-set configuration detection and upgrading these to the latest supported
 * version.
 */
public class TemplateSetConfigurationUpgrader extends AbstractConfigurationUpgrader<TemplateSetConfigurationVersion> {

  /**
   * Creates a new {@link TemplateConfigurationUpgrader} instance.
   *
   */
  public TemplateSetConfigurationUpgrader() {

    super(TemplateSetConfigurationVersion.v1_0, TemplateSetConfiguration.class,
        ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);
  }

  @Override
  protected List<ConfigurationUpgradeResult> performNextUpgradeStep(TemplateSetConfigurationVersion source,
      Object previousConfigurationRootNode, Path configurationRoot) throws Exception {

    List<ConfigurationUpgradeResult> results = new ArrayList<>();
    switch (source) {
      case v1_0:
        LOG.info("No Upgrade needed, v1 is the lastest version");
        break;
      default:
        throw new NotYetSupportedException("This template set configuration version is not supported yet.");
    }

    return results;

  }

}
