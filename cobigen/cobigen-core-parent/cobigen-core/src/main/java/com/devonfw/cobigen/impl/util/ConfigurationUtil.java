package com.devonfw.cobigen.impl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.util.CobiGenPathUtil;

/**
 * Utilities related to the cobigen configurations including:
 *
 * 1. templates location
 */
public class ConfigurationUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationUtil.class);

    /**
     * The method finds location of templates. It could be CobiGen_Templates folder or a template artifact
     * @return template location uri if exist, otherwise null
     */
    public static URI findTemplatesLocation() {
        Path configFile = getConfigurationFile();
        if (configFile != null && Files.exists(configFile)) {
            LOG.info("Configuration file is located at {}", configFile);
            Properties props = readConfigrationFile(configFile);
            String templatesLocation = props.getProperty(ConfigurationConstants.COBIGEN_CONFIG_TEMPLATES_LOCATION_KEY);
            if (StringUtils.isNotEmpty(templatesLocation)) {
                Path templatesPath = Paths.get(templatesLocation);
                if (Files.exists(templatesPath)) {
                    return Paths.get(templatesLocation).toUri();
                } else {
                    LOG.info(
                        "Value of property {} in {} is invalid. Trying to look for templates in cobigen home directory {}",
                        ConfigurationConstants.COBIGEN_CONFIG_TEMPLATES_LOCATION_KEY, configFile,
                        CobiGenPathUtil.getCobiGenFolderPath());
                }
            } else {
                LOG.info("Property {} is not set in {}. Trying to look for templates in cobigen home directory {}",
                    ConfigurationConstants.COBIGEN_CONFIG_TEMPLATES_LOCATION_KEY, configFile,
                    CobiGenPathUtil.getCobiGenFolderPath());
            }
        } else {
            LOG.info("No configuration file is found. Trying to look for templates in cobigen home directory {}",
                CobiGenPathUtil.getCobiGenFolderPath());
        }
        return findTemplatesInCobigenHome();
    }

    /**
     * Find the cobigen configuration file
     * @return a path of cobigen configuration file if exist, otherwise null
     */
    private static Path getConfigurationFile() {
        String envValue = System.getenv(ConfigurationConstants.COBIGEN_CONFIG_DIR);
        if (StringUtils.isNotEmpty(envValue)) {
            return Paths.get(envValue).resolve(ConfigurationConstants.COBIGEN_CONFIG_FILE);
        } else {
            Path cobigenHome = CobiGenPathUtil.getCobiGenFolderPath();
            if (cobigenHome != null) {
                return cobigenHome.resolve(ConfigurationConstants.COBIGEN_CONFIG_FILE);
            }
        }
        return null;
    }

    /**
     * This is a helper method to read a given cobigen configuration file
     * @param cobigenConfigFile:
     *            cobigen configuration file
     * @return Properties containing configuration
     */
    private static Properties readConfigrationFile(Path cobigenConfigFile) {
        Properties props = new Properties();
        try (BufferedReader reader = Files.newBufferedReader(cobigenConfigFile, Charset.forName("UTF-8"))) {
            props.load(reader);
        } catch (IOException e) {
            LOG.error("An error occured while reading the config file {}", cobigenConfigFile, e);
        }
        return props;
    }

    /**
     * This is a helper method to find templates in cobigen home
     * @return templates location if found, otherwise null
     */
    private static URI findTemplatesInCobigenHome() {
        Path templatesPath = CobiGenPathUtil.getTemplatesFolderPath();
        if (templatesPath == null) {
            return null;
        }
        Path templatesFolderPath = templatesPath.resolve(ConfigurationConstants.COBIGEN_TEMPLATES);
        if (Files.exists(templatesFolderPath)) {
            // use Cobigen_Templates folder
            return templatesFolderPath.toUri();
        } else {
            // use template jar
            File templateJar = TemplatesJarUtil.getJarFile(false, templatesPath.toFile());
            if (templateJar != null && Files.exists(templatesPath)) {
                return templateJar.toURI();
            }
        }
        LOG.info(
            "Could not find any templates in cobigen home directory {}. Templates should be placed in the subdirectory /templates/CobiGen_Templates or /templates/<name>.jar",
            CobiGenPathUtil.getCobiGenFolderPath());
        return null;
    }
}
