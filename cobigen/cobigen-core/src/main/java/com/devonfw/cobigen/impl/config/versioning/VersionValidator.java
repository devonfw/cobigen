package com.devonfw.cobigen.impl.config.versioning;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.exception.NotYetSupportedException;
import com.devonfw.cobigen.impl.config.constant.ContextConfigurationVersion;
import com.devonfw.cobigen.impl.config.constant.TemplatesConfigurationVersion;

/**
 * The version validator checks the compatibility of CobiGen and its configuration files
 */
public class VersionValidator {

    /**
     * VersionValidator type.
     */
    public enum Type {
        /** Validates the {@link ContextConfigurationVersion} */
        CONTEXT_CONFIGURATION,
        /** Validates the {@link TemplatesConfigurationVersion} */
        TEMPLATES_CONFIGURATION
    }

    /** Logger instance */
    private static final Logger LOG = LoggerFactory.getLogger(VersionValidator.class);

    /** CobiGen version to validate against */
    private String cobiGenVersion;

    /** Version steps of the configuration */
    private Map<Float, Boolean> versionSteps;

    /** Configuration name (just for logging) */
    private String configName;

    /**
     * The constructor.
     * @param validatorType
     *            Validator {@link Type}
     * @param cobiGenVersion
     *            CobiGen version to validate against
     */
    public VersionValidator(Type validatorType, String cobiGenVersion) {
        this.cobiGenVersion = cobiGenVersion;

        switch (validatorType) {
        case CONTEXT_CONFIGURATION:
            configName = "context configuration";
            versionSteps = ContextConfigurationVersion.valuesSorted();
            break;
        case TEMPLATES_CONFIGURATION:
            configName = "templates configuration";
            versionSteps = TemplatesConfigurationVersion.valuesSorted();
            break;
        default:
            throw new NotYetSupportedException("Unknown configuration type.");
        }
    }

    /**
     * Validates the given version with the running instance of CobiGen.
     *
     * @param configVersion
     *            version to be validated
     */
    public void validate(float configVersion) {

        Float currentCobiGenVersion;
        String currentCobiGenVersionStr = cobiGenVersion;
        currentCobiGenVersionStr = currentCobiGenVersionStr.substring(0, currentCobiGenVersionStr.lastIndexOf("."));
        currentCobiGenVersion = Float.parseFloat(currentCobiGenVersionStr);

        if (configVersion == currentCobiGenVersion) {
            // valid -> first version of CobiGen supporting this configuration
            LOG.debug("Compatible {} due to version declaration. CobiGen: {} / {}: {}", configName,
                currentCobiGenVersionStr, configName, configVersion);
            return;
        } else if (configVersion > currentCobiGenVersion) {
            LOG.error("CobiGen version to old for {} version. CobiGen: {} / {}: {}", configName,
                currentCobiGenVersionStr, configName, configVersion);
            throw new InvalidConfigurationException("The version '" + configVersion + "' of the " + configName
                + " is unknown to the current version of CobiGen '" + currentCobiGenVersionStr
                + "'. No automatic upgrade could be started. Please check your configuration or upgrade CobiGen first.");
        } else { // configVersion < currentCobiGenVersion
            for (Entry<Float, Boolean> versionStep : versionSteps.entrySet()) {
                // newer version step which is not backward compatible
                if (versionStep.getKey() > configVersion && versionStep.getKey() <= currentCobiGenVersion
                    && !versionStep.getValue()) {
                    LOG.warn("{} version too old for current CobiGen version. CobiGen: {} / {}: {}", configName,
                        currentCobiGenVersionStr, configName, configVersion);
                    throw new InvalidConfigurationException("The " + configName + " with version '" + configVersion
                        + "' has to be upgraded to a compatible " + configName + " version.");
                }
            }
            LOG.debug("Compatible {} as no breaking changes found. CobiGen: {} / {}: {}", configName,
                currentCobiGenVersionStr, configName, configVersion);
        }
    }
}
