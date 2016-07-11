package com.capgemini.cobigen.config.versioning;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.config.constant.ContextConfigurationVersion;
import com.capgemini.cobigen.config.constant.TemplatesConfigurationVersion;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.exceptions.NotYetSupportedException;

/**
 * The version validator checks the compatibility of CobiGen and its configuration files
 *
 * @author <a href="m_brunnl@cs.uni-kl.de">Malte Brunnlieb</a>
 * @version $Revision$
 */
public class VersionValidator {

    /**
     * VersionValidator type.
     * @author mbrunnli (May 17, 2016)
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
    private List<Float> versionSteps;

    /** Configuration name (just for logging) */
    private String configName;

    /**
     * The constructor.
     * @param validatorType
     *            Validator {@link Type}
     * @param cobiGenVersion
     *            CobiGen version to validate against
     * @author mbrunnli (May 17, 2016)
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
        currentCobiGenVersionStr =
            currentCobiGenVersionStr.substring(0, currentCobiGenVersionStr.lastIndexOf("."));
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
            for (Float versionStep : versionSteps) {
                // If there exists a version step (breaking change) in between, throw an error
                if (configVersion < versionStep && versionStep <= currentCobiGenVersion) {
                    LOG.warn("{} version too old for current CobiGen version. CobiGen: {} / {}: {}",
                        configName, currentCobiGenVersionStr, configName, configVersion);
                    throw new InvalidConfigurationException("The " + configName + " with version '"
                        + configVersion + "' has to be upgraded to a compatible " + configName + " version.");
                }
            }
            LOG.debug("Compatible {} as no breaking changes found. CobiGen: {} / {}: {}", configName,
                currentCobiGenVersionStr, configName, configVersion);
        }
    }
}
