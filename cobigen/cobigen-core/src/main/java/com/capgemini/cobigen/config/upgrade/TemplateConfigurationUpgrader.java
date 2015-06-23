package com.capgemini.cobigen.config.upgrade;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.config.constant.ConfigurationConstants;
import com.capgemini.cobigen.config.upgrade.version.TemplatesConfigurationVersion;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.exceptions.NotYetSupportedException;
import com.capgemini.cobigen.exceptions.TechnicalRuntimeException;

/**
 * This class encompasses all logic for legacy templates configuration detection and upgrading these to the
 * latest supported version.
 * @author mbrunnli (Jun 22, 2015)
 */
public class TemplateConfigurationUpgrader {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(TemplateConfigurationUpgrader.class);

    /**
     * Checks, whether the templates configuration can be read with an old schema version.
     * @param templatesRoot
     *            the root folder containing the templates configuration
     * @return the newest schema version, the configuration is compliant with or <code>null</code> if the
     *         configuration is not compliant to any.
     * @author mbrunnli (Jun 22, 2015)
     */
    public static TemplatesConfigurationVersion resolveLatestCompatibleSchemaVersion(Path templatesRoot) {
        LOG.debug("Try reading template configuration with legacy schemas.");

        TemplatesConfigurationVersion[] versions = TemplatesConfigurationVersion.values();
        Path templatesConfigFile = templatesRoot.resolve(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);

        for (int i = versions.length - 1; i >= 0; i--) {
            TemplatesConfigurationVersion lv = versions[i];
            LOG.debug("Try template configuration schema '{}'.", lv.toString());
            try {
                Class<?> templatesConfigurationClass =
                    TemplateConfigurationUpgrader.class.getClassLoader().loadClass(
                        ConfigurationConstants.JAXB_PACKAGE_PREFIX + "." + lv.name() + "."
                            + "TemplatesConfiguration");

                Unmarshaller unmarschaller =
                    JAXBContext.newInstance(templatesConfigurationClass).createUnmarshaller();
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema =
                    schemaFactory.newSchema(new StreamSource(TemplateConfigurationUpgrader.class
                        .getResourceAsStream("/schema/" + lv.toString() + "/templatesConfiguration.xsd")));
                unmarschaller.setSchema(schema);
                Object rootNode = unmarschaller.unmarshal(Files.newInputStream(templatesConfigFile));

                // check, whether the read node can be casted to the correct configuration root node object
                if (!templatesConfigurationClass.isAssignableFrom(rootNode.getClass())) {
                    LOG.debug("It was not possible to read template configuration with schema '{}' .",
                        lv.toString());
                } else {
                    LOG.debug("It was possible to read template configuration with schema '{}' .",
                        lv.toString());
                    return lv;
                }
            } catch (Throwable e) {
                LOG.debug("Not able to read template configuration with schema '{}' .", lv.toString(), e);
            }
        }
        return null;
    }

    /**
     * Upgrades the templates configuration on top of the templates folder to the latest supported version.
     * @param templatesRoot
     *            the root folder containing the templates configuration
     * @param ignoreFailedBackup
     *            If is set to <code>true</code>, the backup will silently log a failed backup and return
     *            successfully. Otherwise it will throw a {@link TechnicalRuntimeException}.
     * @return if manual adoptions has to be performed after upgrading
     * @author mbrunnli (Jun 22, 2015)
     */
    public static boolean upgradeTemplatesConfigurationToLatestVersion(Path templatesRoot,
        boolean ignoreFailedBackup) {
        boolean manualAdoptionsNecessary = false;

        TemplatesConfigurationVersion currentVersion = resolveLatestCompatibleSchemaVersion(templatesRoot);
        Path contextFile = templatesRoot.resolve(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);
        if (currentVersion == null) {
            throw new InvalidConfigurationException(contextFile.toUri().toString(),
                "Templates configuration does not match any current or legacy schema definitions.");
        } else {

            TemplatesConfigurationVersion latestVersion =
                TemplatesConfigurationVersion.values()[TemplatesConfigurationVersion.values().length - 1];

            TemplatesConfigurationVersion currSource = null;
            for (TemplatesConfigurationVersion tcv : TemplatesConfigurationVersion.values()) {
                if (currentVersion == latestVersion) {
                    break; // already up to date
                }
                if (currentVersion == tcv) {
                    currSource = currentVersion;

                    manualAdoptionsNecessary |=
                        upgradeTemplatesConfigurationToNextVersion(currSource, contextFile,
                            ignoreFailedBackup);

                    // implicitly check upgrade step
                    currentVersion = resolveLatestCompatibleSchemaVersion(templatesRoot);

                    if (currentVersion == null) {
                        throw new TechnicalRuntimeException(
                            "An error occurred while upgrading templates configuration from version "
                                + currSource + " to " + currentVersion + ".");
                    }
                }
            }
        }

        return manualAdoptionsNecessary;
    }

    /**
     * Upgrades the given templates configuration to the latest supported version.
     * @param source
     *            {@link TemplatesConfigurationVersion} from which to upgrade to the latest version.
     * @param templatesConfiguration
     *            {@link Path} to the templates configuration file
     * @param ignoreFailedBackup
     *            If is set to <code>true</code>, the backup will silently log a failed backup and return
     *            successfully. Otherwise it will throw a {@link TechnicalRuntimeException}.
     * @return if manual adoptions has to be performed after upgrading
     * @author mbrunnli (Jun 22, 2015)
     */
    private static boolean upgradeTemplatesConfigurationToNextVersion(TemplatesConfigurationVersion source,
        Path templatesConfiguration, boolean ignoreFailedBackup) {
        boolean manualAdoptionsNecessary = false;

        switch (source) {
        case v1_2: // to v2.1
            LOG.info("Upgrading templates configuration '{}' from version {} to {}...",
                templatesConfiguration.toUri(), source, TemplatesConfigurationVersion.v2_1);

            try {
                Unmarshaller unmarschaller =
                    JAXBContext.newInstance(
                        com.capgemini.cobigen.config.entity.io.v1_2.TemplatesConfiguration.class)
                        .createUnmarshaller();

                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema =
                    schemaFactory
                        .newSchema(new StreamSource(TemplateConfigurationUpgrader.class
                            .getResourceAsStream("/schema/" + source.toString()
                                + "/templatesConfiguration.xsd")));
                unmarschaller.setSchema(schema);
                try (InputStream in = Files.newInputStream(templatesConfiguration)) {
                    Object rootNode = unmarschaller.unmarshal(in);
                    com.capgemini.cobigen.config.entity.io.v1_2.TemplatesConfiguration oldConfig =
                        (com.capgemini.cobigen.config.entity.io.v1_2.TemplatesConfiguration) rootNode;

                    DozerBeanMapper mapper = new DozerBeanMapper();
                    try (InputStream stream =
                        TemplateConfigurationUpgrader.class
                            .getResourceAsStream("/dozer/config/upgrade/templatesConfiguration-v1.2-v2.1.xml")) {
                        mapper.addMapping(stream);
                    }
                    com.capgemini.cobigen.config.entity.io.v2_1.TemplatesConfiguration upgradedConfig =
                        mapper.map(oldConfig,
                            com.capgemini.cobigen.config.entity.io.v2_1.TemplatesConfiguration.class);
                    upgradedConfig.setVersion(new BigDecimal("2.1"));

                    createBackup(templatesConfiguration, ignoreFailedBackup);

                    try (OutputStream out = Files.newOutputStream(templatesConfiguration)) {
                        JAXB.marshal(upgradedConfig, out);
                    }
                }
            } catch (Throwable e) {
                throw new TechnicalRuntimeException(
                    "An unexpected exception occurred while upgrading the templates configuration from version '"
                        + source.toString()
                        + "' to '"
                        + TemplatesConfigurationVersion.values()[TemplatesConfigurationVersion.values().length - 1]
                        + "'.", e);
            }
            break;
        default:
            throw new NotYetSupportedException("An upgrade of the templates configuration from a version "
                + source + " to a newer one is currently not supported.");
        }

        return manualAdoptionsNecessary;
    }

    /**
     * Creates a backup of the given file. If ignoreFailedBackup is set to <code>true</code>, the backup will
     * silently log a failed backup and return successfully.
     * @param file
     *            to be backed up
     * @param ignoreFailedBackup
     *            If is set to <code>true</code>, the backup will silently log a failed backup and return
     *            successfully. Otherwise it will throw a {@link TechnicalRuntimeException}.
     * @author mbrunnli (Jun 22, 2015)
     */
    private static void createBackup(Path file, boolean ignoreFailedBackup) {
        for (int i = 0;; i++) {
            Pattern p = Pattern.compile("(.+\\.)([^\\.]+)");
            Matcher matcher = p.matcher(file.getFileName().toString());
            String backupFilename;
            if (matcher.matches()) {
                backupFilename = matcher.group(1) + "bak" + (i == 0 ? "" : i) + "." + matcher.group(2);
            } else {
                backupFilename = file.getFileName().toString().concat(".bak" + (i == 0 ? "" : i));
            }
            Path backupPath = file.resolveSibling(backupFilename);
            try {
                Files.copy(file, backupPath, StandardCopyOption.COPY_ATTRIBUTES);
                LOG.info("Backup of templates configuration file created ('{}').", backupPath.toUri());
                break;
            } catch (FileAlreadyExistsException e) {
                continue;
            } catch (UnsupportedOperationException | IOException | SecurityException e) {
                if (ignoreFailedBackup) {
                    LOG.info("Could not write backup of the configuration file ('{}').", backupPath.toUri());
                } else {
                    throw new TechnicalRuntimeException(
                        "Upgrade failed. Not possible to create the backup in '" + backupPath.toUri()
                            + "' before upgrading the configuration.", e);
                }
                break;
            }
        }
    }

}
