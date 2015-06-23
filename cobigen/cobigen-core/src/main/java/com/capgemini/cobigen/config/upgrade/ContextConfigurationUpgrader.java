package com.capgemini.cobigen.config.upgrade;

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
import com.capgemini.cobigen.config.entity.io.ContextConfiguration;
import com.capgemini.cobigen.config.upgrade.version.ContextConfigurationVersion;
import com.capgemini.cobigen.exceptions.NotYetSupportedException;

/**
 * This class encompasses all logic for legacy context configuration detection and upgrading these to the
 * latest supported version.
 * @author mbrunnli (Jun 22, 2015)
 */
public class ContextConfigurationUpgrader {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(ContextConfigurationUpgrader.class);

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
                    schemaFactory.newSchema(new StreamSource(TemplateConfigurationUpgrader.class
                        .getResourceAsStream("/schema/" + lv.toString() + "/contextConfiguration.xsd")));
                unmarschaller.setSchema(schema);
                Object rootNode = unmarschaller.unmarshal(Files.newInputStream(contextFile));

                // check, whether the read node can be casted to the correct configuration root node object
                Class<?> configurationNodeObject =
                    TemplateConfigurationUpgrader.class.getClassLoader().loadClass(
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
