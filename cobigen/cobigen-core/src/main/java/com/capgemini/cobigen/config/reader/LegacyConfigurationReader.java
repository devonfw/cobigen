package com.capgemini.cobigen.config.reader;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.config.constant.ConfigurationConstants;
import com.capgemini.cobigen.config.constant.ContextConfigurationVersion;
import com.capgemini.cobigen.config.constant.TemplatesConfigurationVersion;
import com.capgemini.cobigen.config.entity.io.ContextConfiguration;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.exceptions.NotYetSupportedException;

/**
 * Context configuration reader for legacy configurations.
 * @author mbrunnli (Jun 22, 2015)
 */
public class LegacyConfigurationReader {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(LegacyConfigurationReader.class);

    /**
     * Checks, whether the templates configuration can be read with an old schema version.
     * @param templatesRoot
     * @return the newest schema version, the configuration is compliant with or <code>null</code> if the
     *         configuration is not compliant to any.
     * @author mbrunnli (Jun 22, 2015)
     */
    public static TemplatesConfigurationVersion resolveLatestCompatibleTemplatesConfigurationSchemaVersion(
        Path templatesRoot) {
        LOG.debug("Try reading template configuration with legacy schemas.");

        TemplatesConfigurationVersion[] versions = TemplatesConfigurationVersion.values();
        for (int i = versions.length - 1; i >= 0; i--) {
            TemplatesConfigurationVersion lv = versions[i];
            LOG.debug("Try template configuration schema '{}'.", lv.toString());
            try {
                Path contextFile = templatesRoot.resolve(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);

                Unmarshaller unmarschaller =
                    JAXBContext.newInstance(ContextConfiguration.class).createUnmarshaller();
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema =
                    schemaFactory.newSchema(new StreamSource(LegacyConfigurationReader.class
                        .getResourceAsStream("/schema/" + lv.toString() + "/templatesConfiguration.xsd")));
                unmarschaller.setSchema(schema);
                Object rootNode = unmarschaller.unmarshal(Files.newInputStream(contextFile));

                // check, whether the read node can be casted to the correct configuration root node object
                Class<?> configurationNodeObject =
                    LegacyConfigurationReader.class.getClassLoader().loadClass(
                        ConfigurationConstants.JAXB_PACKAGE_PREFIX + "." + lv.name()
                            + ContextConfiguration.class.getSimpleName());
                if (!configurationNodeObject.isAssignableFrom(rootNode.getClass())) {
                    LOG.debug("It was not possible to read template configuration with schema '{}' .",
                        lv.toString());
                } else {
                    LOG.debug("It was possible to read template configuration with schema '{}' .",
                        lv.toString());
                    return lv;
                }
            } catch (Throwable e) {
                LOG.debug("Not able to read template configuration with schema '{}' .", lv.toString());
            }
        }
        return null;
    }

    /**
     * Checks, whether the context configuration can be read with an old schema version.
     * @param configurationRoot
     * @return the newest schema version, the configuration is compliant with or <code>null</code> if the
     *         configuration is not compliant to any.
     * @author mbrunnli (Jun 22, 2015)
     */
    public static ContextConfigurationVersion resolveLatestCompatibleContextConfigurationSchemaVersion(
        Path configurationRoot) {
        LOG.debug("Try reading context configuration with legacy schemas.");

        ContextConfigurationVersion[] versions = ContextConfigurationVersion.values();
        for (int i = versions.length - 1; i >= 0; i--) {
            ContextConfigurationVersion lv = versions[i];
            LOG.debug("Try context configuration schema '{}'.", lv.toString());
            try {
                Path contextFile = configurationRoot.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);

                Unmarshaller unmarschaller =
                    JAXBContext.newInstance(ContextConfiguration.class).createUnmarshaller();
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema =
                    schemaFactory.newSchema(new StreamSource(LegacyConfigurationReader.class
                        .getResourceAsStream("/schema/" + lv.toString() + "/contextConfiguration.xsd")));
                unmarschaller.setSchema(schema);
                Object rootNode = unmarschaller.unmarshal(Files.newInputStream(contextFile));

                // check, whether the read node can be casted to the correct configuration root node object
                Class<?> configurationNodeObject =
                    LegacyConfigurationReader.class.getClassLoader().loadClass(
                        ConfigurationConstants.JAXB_PACKAGE_PREFIX + "." + lv.name()
                            + ContextConfiguration.class.getSimpleName());
                if (!configurationNodeObject.isAssignableFrom(rootNode.getClass())) {
                    LOG.debug("It was not possible to read context configuration with schema '{}' .",
                        lv.toString());
                } else {
                    LOG.debug("It was possible to read context configuration with schema '{}' .",
                        lv.toString());
                    return lv;
                }
            } catch (Throwable e) {
                LOG.debug("Not able to read context configuration with schema '{}' .", lv.toString());
            }
        }
        return null;
    }

    /**
     *
     * @param templatesRoot
     * @return
     * @author mbrunnli (Jun 22, 2015)
     */
    public static boolean upgradeTemplatesConfigurationToLatestVersion(Path templatesRoot) {
        boolean manualAdoptionsNecessary = false;

        TemplatesConfigurationVersion currentVersion =
            resolveLatestCompatibleTemplatesConfigurationSchemaVersion(templatesRoot);
        if (currentVersion == null) {
            Path contextFile = templatesRoot.resolve(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);
            throw new InvalidConfigurationException(contextFile.toUri().toString(),
                "Templates configuration does not match any current or legacy schema definition.");
        } else {
            boolean started = false;
            // values() returns the version steps in order of declaration
            for (TemplatesConfigurationVersion nextVersionStep : TemplatesConfigurationVersion.values()) {
                if (started) {
                    manualAdoptionsNecessary |= upgradeTemplatesConfigurationStepwise(currentVersion);
                }

                if (nextVersionStep == currentVersion) {
                    started = true;
                }
            }
        }

        return manualAdoptionsNecessary;
    }

    /**
     *
     * @param source
     * @return
     * @author mbrunnli (Jun 22, 2015)
     */
    private static boolean upgradeTemplatesConfigurationStepwise(TemplatesConfigurationVersion source) {
        boolean manualAdoptionsNecessary = false;

        switch (source) {
        case v1_2:
            // to v2.1
            // TODO MB
            break;
        default:
            throw new NotYetSupportedException(
                "An upgrade of the templates configuration from a version previous to v1.2 is not yet supported.");
        }

        return manualAdoptionsNecessary;
    }

    /**
     *
     * @param source
     * @return
     * @author mbrunnli (Jun 22, 2015)
     */
    private static boolean upgradeContextConfigurationStepwise(ContextConfigurationVersion source) {
        boolean manualAdoptionsNecessary = false;

        switch (source) {
        case v2_0:
            // to v2.1
            // TODO MB
            break;
        default:
            throw new NotYetSupportedException(
                "An upgrade of the context configuration from a version previous to v2.0 is not yet supported.");
        }

        return manualAdoptionsNecessary;
    }

}
