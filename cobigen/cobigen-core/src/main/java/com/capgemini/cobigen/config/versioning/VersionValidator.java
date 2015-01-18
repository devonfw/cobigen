package com.capgemini.cobigen.config.versioning;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.CobiGen;
import com.capgemini.cobigen.exceptions.IncompatibleConfigurationException;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.google.common.collect.Maps;

/**
 * The version validator checks the compatibility of CobiGen and its configuration files
 *
 * @author <a href="m_brunnl@cs.uni-kl.de">Malte Brunnlieb</a>
 * @version $Revision$
 */
public class VersionValidator {

    /**
     * Logger instance
     */
    private static final Logger LOG = LoggerFactory.getLogger(VersionValidator.class);

    /**
     * Lower boundaries for compatible templates configuration versions. Mapping from templates configuration
     * version to the first compatible CobiGen version.
     */
    private static final Map<BigDecimal, BigDecimal> templatesConfig_compatibleVersionSteps = Maps
        .newLinkedHashMap();

    /**
     * Lower boundaries for incompatible templates configuration versions. Mapping from templates
     * configuration version to the first compatible CobiGen version.
     */
    private static final Map<BigDecimal, BigDecimal> templatesConfig_incompatibleVersionSteps = Maps
        .newLinkedHashMap();

    /**
     * Lower boundaries for compatible context configuration versions. Mapping from context configuration
     * version to the first compatible CobiGen version.
     */
    private static final Map<BigDecimal, BigDecimal> contextConfig_compatibleVersionSteps = Maps
        .newLinkedHashMap();

    /**
     * Lower boundaries for incompatible context configuration versions. Mapping from context configuration
     * version to the first compatible CobiGen version.
     */
    private static final Map<BigDecimal, BigDecimal> contextConfig_incompatibleVersionSteps = Maps
        .newLinkedHashMap();

    static {
        templatesConfig_compatibleVersionSteps.put(new BigDecimal("1.0"), new BigDecimal("1.0"));
        templatesConfig_compatibleVersionSteps.put(new BigDecimal("1.2"), new BigDecimal("1.2"));

        templatesConfig_incompatibleVersionSteps.put(new BigDecimal("1.0"), new BigDecimal("1.0"));

        contextConfig_compatibleVersionSteps.put(new BigDecimal("1.0"), new BigDecimal("1.0"));
        contextConfig_compatibleVersionSteps.put(new BigDecimal("1.3"), new BigDecimal("1.3"));

        contextConfig_incompatibleVersionSteps.put(new BigDecimal("1.0"), new BigDecimal("1.0"));
    }

    /**
     * Validates the given templates configuration version to be compatible with the current CobiGen version
     *
     * @param templatesVersion
     *            templates configuration version
     */
    public static void validateTemplatesConfig(BigDecimal templatesVersion) {

        validateVersion(templatesVersion, templatesConfig_compatibleVersionSteps,
            templatesConfig_incompatibleVersionSteps, "templates configuration");

    }

    /**
     * Validates the given context configuration version to be compatible with the current CobiGen version
     *
     * @param contextVersion
     *            context configuration version
     */
    public static void validateContextConfig(BigDecimal contextVersion) {

        validateVersion(contextVersion, contextConfig_compatibleVersionSteps,
            contextConfig_incompatibleVersionSteps, "context configuration");
    }

    /**
     * Validates the given version with the running instance of CobiGen
     *
     * @param configVersion
     *            version to be validated
     * @param compatibleVersionSteps
     *            Mapping of compatible configuration versions to their first compatible CobiGen version
     * @param incompatibleVersionSteps
     *            Mapping of incompatible configuration versions to their first compatible CobiGen version
     * @param configName
     *            configuration name to be validated. Will be used in log entries and error messages.
     */
    private static void validateVersion(BigDecimal configVersion,
        Map<BigDecimal, BigDecimal> compatibleVersionSteps,
        Map<BigDecimal, BigDecimal> incompatibleVersionSteps, String configName) {

        BigDecimal currentCobiGenVersion;
        String currentCobiGenVersionStr = CobiGen.CURRENT_VERSION;
        currentCobiGenVersionStr =
            currentCobiGenVersionStr.substring(0, currentCobiGenVersionStr.lastIndexOf("."));
        currentCobiGenVersion = new BigDecimal(currentCobiGenVersionStr);

        if (!compatibleVersionSteps.keySet().contains(configVersion)) {
            LOG.error("CobiGen version to old for {} version. CobiGen: {} / {}: {}", configName,
                currentCobiGenVersionStr, configName, configVersion.toString());
            throw new InvalidConfigurationException(
                "The version '"
                    + configVersion.toString()
                    + "' of the "
                    + configName
                    + " is unknown to the current version of CobiGen '"
                    + currentCobiGenVersionStr
                    + "'. No automatic upgrade could be started. Please check your configuration or upgrade CobiGen first.");
        }

        BigDecimal firstCompatibleCobiGenVersion = compatibleVersionSteps.get(configVersion);
        if (currentCobiGenVersion.equals(firstCompatibleCobiGenVersion)) {
            // valid -> first version of CobiGen supporting this configuration
            LOG.debug("Compatible {} due to version declaration. CobiGen: {} / {}: {}", configName,
                currentCobiGenVersionStr, configName, configVersion.toString());
            return;
        } else if (currentCobiGenVersion.compareTo(firstCompatibleCobiGenVersion) > 0) {
            // check whether the current CobiGen version is smaller than the next incompatible version step

            // find n+1 version step
            boolean nFound = false;
            boolean incompatible = false;
            BigDecimal compatibleVersion = null;
            for (Entry<BigDecimal, BigDecimal> entry : incompatibleVersionSteps.entrySet()) {
                if (!incompatible) {
                    if (nFound) {
                        // n+1
                        if (currentCobiGenVersion.compareTo(entry.getValue()) >= 0) {
                            // CobiGen needs an upgraded version of the configuration
                            incompatible = true;
                            // continue to find the the compatible version
                        } else {
                            // valid -> CobiGen version between first compatible version and next incompatible
                            // version
                            LOG.debug("Compatible {} due to version declaration. CobiGen: {} / {}: {}",
                                configName, currentCobiGenVersionStr, configName, configVersion.toString());
                            break;
                        }
                        break;
                    } else if (entry.getValue().equals(firstCompatibleCobiGenVersion)) {
                        nFound = true;
                    }
                } else {
                    // version already marked as incompatible, so find compatible one.
                    if (currentCobiGenVersion.compareTo(entry.getValue()) >= 0) {
                        compatibleVersion = entry.getValue();
                    } else {
                        break; // current CobiGen version is smaller then the next step incompatible
                               // configuration step
                    }
                }
            }

            if (incompatible) {
                LOG.error("{} version too old for current CobiGen version. CobiGen: {} / {}: {}", configName,
                    currentCobiGenVersionStr, configName, configVersion.toString());
                throw new IncompatibleConfigurationException("The "
                    + configName
                    + " with version '"
                    + configVersion.toString()
                    + "' has to be upgraded to the compatible "
                    + configName
                    + " version '"
                    + (compatibleVersion == null ? "No compatible version found"
                        : compatibleVersion.toString()) + "'.");
            }
        } else {
            // The version of the templates configuration is too new for the current CobiGen version
            LOG.error("CobiGen version to old for {} version. CobiGen: {} / {}: {}", configName,
                currentCobiGenVersionStr, configName, configVersion.toString());
            throw new IncompatibleConfigurationException("The current version of CobiGen '"
                + currentCobiGenVersionStr + "' is too old for a " + configName + " with version '"
                + configVersion + "'");
        }
    }
}
