package com.devonfw.cobigen.api.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;

/**
 * Utilities related to the cobigen configurations including:
 *
 * 1. templates location
 */
public class ConfigurationUtil {

    /** Logger instance */
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationUtil.class);

    /**
     * The method finds location of templates. It could be CobiGen_Templates folder or a template artifact
     * @return template location uri if exist, otherwise null
     */
    public static URI findTemplatesLocation() {
        Path cobigenHome = ConfigurationUtil.getCobiGenHomePath();
        Path configFile = cobigenHome.resolve(ConfigurationConstants.COBIGEN_CONFIG_FILE);

        if (configFile != null && Files.exists(configFile)) {
            LOG.debug("Custom cobigen configuration found at {}", configFile);
            Properties props = readConfigrationFile(configFile);
            String templatesLocation = props.getProperty(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH);
            if (StringUtils.isNotEmpty(templatesLocation)) {
                LOG.info("Custom templates path found. Taking templates from {}", templatesLocation);
                Path templatesPath = Paths.get(templatesLocation);
                if (Files.exists(templatesPath)) {
                    return Paths.get(templatesLocation).toUri();
                } else {
                    LOG.info("Value of property {} in {} is invalid. Fall back to templates from {}",
                        ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH, configFile,
                        ConfigurationUtil.getTemplatesFolderPath(cobigenHome));
                }
            } else {
                LOG.info("Property {} is not set in {}. Fall back to templates from {}",
                    ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH, configFile,
                    ConfigurationUtil.getTemplatesFolderPath(cobigenHome));
            }
        } else {
            LOG.info("No custom templates configuration found. Getting templates from {}",
                ConfigurationUtil.getTemplatesFolderPath(cobigenHome));
        }
        return findTemplates(cobigenHome);
    }

    /**
     * This is a helper method to read a given cobigen configuration file
     * @param cobigenConfigFile
     *            cobigen configuration file
     * @return Properties containing configuration
     */
    private static Properties readConfigrationFile(Path cobigenConfigFile) {
        Properties props = new Properties();
        try {
            String configFileContents = Files.readAllLines(cobigenConfigFile, Charset.forName("UTF-8")).stream()
                .collect(Collectors.joining("\n"));
            configFileContents = configFileContents.replace("\\", "\\\\");
            try (StringReader strReader = new StringReader(configFileContents)) {
                props.load(strReader);
            }
        } catch (IOException e) {
            throw new CobiGenRuntimeException("An error occured while reading the config file " + cobigenConfigFile, e);
        }
        return props;
    }

    /**
     * This is a helper method to find templates in cobigen home
     * @param home
     *            cobigen configuration home directory
     * @return templates location if found, otherwise null
     */
    private static URI findTemplates(Path home) {
        Path templatesPath = ConfigurationUtil.getTemplatesFolderPath(home);
        Path templatesFolderPath = templatesPath.resolve(ConfigurationConstants.COBIGEN_TEMPLATES);
        if (Files.exists(templatesFolderPath)) {
            // use Cobigen_Templates folder
            return templatesFolderPath.toUri();
        } else {
            // use template jar
            Path jarPath = getTemplateJar(templatesPath);
            if (jarPath != null) {
                return jarPath.toUri();
            }
        }
        LOG.info("Could not find any templates in cobigen home directory {}. Downloading...",
            ConfigurationUtil.getCobiGenHomePath());

        TemplatesJarUtil.downloadLatestDevon4jTemplates(true, templatesPath.toFile());
        TemplatesJarUtil.downloadLatestDevon4jTemplates(false, templatesPath.toFile());
        return getTemplateJar(templatesPath).toUri();
    }

    /**
     * @param templatesPath
     *            the templates cache directory
     *
     * @return the path of the templates jar
     */
    private static Path getTemplateJar(Path templatesPath) {
        File templateJar = TemplatesJarUtil.getJarFile(false, templatesPath.toFile());
        if (templateJar != null && Files.exists(templatesPath)) {
            return templateJar.toPath();
        }
        return null;
    }

    /**
     * Returns the CobiGen home directory, or creates a new one if it does not exist
     * @return {@link Path} of the CobiGen home directory
     */
    public static Path getCobiGenHomePath() {

        String envValue = System.getenv(ConfigurationConstants.CONFIG_ENV_HOME);
        Path cobiGenPath;
        if (!StringUtils.isEmpty(envValue)) {
            LOG.info("Custom configuration folder configured in environment variable {}={}",
                ConfigurationConstants.CONFIG_ENV_HOME, envValue);
            cobiGenPath = Paths.get(envValue);
        } else {
            cobiGenPath = ConfigurationConstants.DEFAULT_HOME;
        }

        // We first check whether we already have a directory
        if (Files.exists(cobiGenPath)) {
            return cobiGenPath;
        }

        try {
            Files.createDirectories(cobiGenPath);
        } catch (IOException e) {
            throw new CobiGenRuntimeException("Unable to create cobigen home directory at " + cobiGenPath);
        }
        return cobiGenPath;
    }

    /**
     * Returns the templates home directory (which is located inside CobiGen home folder), or creates a new
     * one if it does not exist
     * @return {@link Path} of the templates home directory
     */
    public static Path getTemplatesFolderPath() {
        return getTemplatesFolderPath(getCobiGenHomePath());
    }

    /**
     * Returns the templates home directory (which is located inside CobiGen home folder), or creates a new
     * one if it does not exist
     * @param home
     *            cobigen configuration home directory
     * @return {@link Path} of the templates home directory
     */
    private static Path getTemplatesFolderPath(Path home) {

        Path templatesPath = home.resolve(ConfigurationConstants.TEMPLATES_FOLDER);

        // We first check whether we already have a directory
        if (Files.exists(templatesPath)) {
            return templatesPath;
        }

        try {
            Files.createDirectories(templatesPath);
        } catch (IOException e) {
            throw new CobiGenRuntimeException("Unable to create path " + templatesPath);
        }
        return templatesPath;
    }

    /**
     * Returns the directory where the external processes are located, or creates a new one if it was not
     * present
     * @param processPath
     *            name of the process
     * @return {@link Path} of the external processes home directory
     */
    public static Path getExternalProcessesPath(String processPath) {

        Path home = getCobiGenHomePath();

        // We first check whether we already have a directory
        Path externalServersPath = home.resolve(processPath);
        if (Files.exists(externalServersPath)) {
            return externalServersPath;
        }

        if (new File(externalServersPath.toUri()).mkdir()) {
            return externalServersPath;
        } else {
            // Folder has not been created
            return null;
        }

    }

}
