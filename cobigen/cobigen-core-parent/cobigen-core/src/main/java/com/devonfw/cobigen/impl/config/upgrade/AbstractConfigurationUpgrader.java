package com.devonfw.cobigen.impl.config.upgrade;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.devonfw.cobigen.api.constants.BackupPolicy;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.exception.NotYetSupportedException;
import com.devonfw.cobigen.impl.exceptions.BackupFailedException;
import com.devonfw.cobigen.impl.util.ExceptionUtil;

/**
 * This class encompasses all logic for upgrading CobiGen configurations including
 * <ul>
 * <li>detection of all released schema compatibilities</li>
 * <li>upgrading legacy configurations to the latest supported version</li>
 * </ul>
 * @param <VERSIONS_TYPE>
 *            Type of the version enum listing all supported versions (increasing declaration).
 */
public abstract class AbstractConfigurationUpgrader<VERSIONS_TYPE extends Enum<?>> {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractConfigurationUpgrader.class);

    /** All enum values from the versions {@link Enum} */
    private VERSIONS_TYPE[] versions;

    /** Name of the configuration (simple type of the {@link #configurationJaxbRootNode} with whitespaces) */
    private String configurationName;

    /** Standardized file name of the configuration */
    private String configurationFilename;

    /** Name of the configuration (simple type of the {@link #configurationJaxbRootNode} appended by .xsd) */
    private String configurationXsdFilename;

    /** JAXB root object class of the configuration for (un-)marshalling */
    private Class<?> configurationJaxbRootNode;

    /**
     * Package prefix generated JAXB classes of the configurations (analogous to the pom.xml specification)
     */
    private static final String JAXB_PACKAGE_PREFIX = "com.devonfw.cobigen.impl.config.entity.io";

    /**
     * Creates a new instance for the abstract implementation of an configuration upgrader.
     * @param version
     *            an arbitrary instance to perform reflection on during upgrading.
     * @param configurationJaxbRootNode
     *            JAXB root object class of the configuration for (un-)marshalling.
     * @param configurationFilename
     *            standardized file name of the configuration
     * @author mbrunnli (Jun 23, 2015)
     */
    @SuppressWarnings("unchecked")
    AbstractConfigurationUpgrader(VERSIONS_TYPE version, Class<?> configurationJaxbRootNode,
        String configurationFilename) {
        this.configurationJaxbRootNode = configurationJaxbRootNode;
        this.configurationFilename = configurationFilename;

        // split camel case
        configurationName = StringUtils
            .join(configurationJaxbRootNode.getSimpleName().split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=-Z][a-z])"), " ");

        configurationXsdFilename = StringUtils.uncapitalize(configurationJaxbRootNode.getSimpleName()) + ".xsd";

        // determine all "version enum" values
        try {
            this.versions = (VERSIONS_TYPE[]) version.getClass().getMethod("values").invoke(version);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
            | SecurityException e) {
            throw new CobiGenRuntimeException(
                "Unexpected happend! Could not determine values of the enum '" + version + "'.", e);
        }
    }

    /**
     * Checks, whether the configuration is compliant to the latest supported version.
     * @param configurationRoot
     *            the root folder containing the configuration
     * @return <code>true</code> if the configuration is up-to-date, <br>
     *         otherwise <code>false</code>
     */
    public boolean isCompliantToLatestSupportedVersion(Path configurationRoot) {
        return resolveLatestCompatibleSchemaVersion(configurationRoot, true) != null;
    }

    /**
     * Checks, whether the configuration can be read with an old schema version.
     * @param configurationRoot
     *            the root folder containing the configuration
     * @return the newest schema version, the configuration is compliant with or <code>null</code> if the
     *         configuration is not compliant to any.
     */
    public VERSIONS_TYPE resolveLatestCompatibleSchemaVersion(Path configurationRoot) {
        return resolveLatestCompatibleSchemaVersion(configurationRoot, false);
    }

    /**
     * Checks, whether the configuration can be read with an old schema version.
     * @param configurationRoot
     *            the root folder containing the configuration
     * @param justCheckLatestVersion
     *            just checks latest supported version and returns
     * @return the newest schema version, the configuration is compliant with or <code>null</code> if the
     *         configuration is not compliant to any.
     */
    private VERSIONS_TYPE resolveLatestCompatibleSchemaVersion(Path configurationRoot, boolean justCheckLatestVersion) {
        LOG.info("Try reading {} (including trails with legacy schema).", configurationName);

        Path configurationFile = configurationRoot.resolve(configurationFilename);

        for (int i = versions.length - 1; i >= 0; i--) {
            VERSIONS_TYPE lv = versions[i];
            LOG.info("Try {} schema '{}'.", configurationName, lv.toString());
            try {
                Class<?> jaxbConfigurationClass = getJaxbConfigurationClass(lv);
                Object rootNode = unmarshallConfiguration(configurationFile, lv, jaxbConfigurationClass);

                // check, whether the read node can be casted to the correct configuration root node object
                if (!jaxbConfigurationClass.isAssignableFrom(rootNode.getClass())) {
                    LOG.info("It was not possible to read {} with schema '{}' .", configurationName, lv.toString());
                } else {
                    LOG.info("It was possible to read {} with schema '{}' .", configurationName, lv.toString());
                    return lv;
                }
            } catch (Throwable e) {
                Throwable cause = ExceptionUtil.getCause(e, SAXParseException.class, UnmarshalException.class);
                if (cause != null && cause.getMessage() != null) {
                    LOG.info("Not able to read template configuration with schema '{}': {}", lv.toString(),
                        cause.getMessage());
                } else {
                    LOG.warn("Not able to read template configuration with schema '{}' .", lv.toString(), e);
                }
            }

            if (justCheckLatestVersion) {
                LOG.info("Could not read configuration {} with schema {} (latest).", configurationName,
                    versions[versions.length - 1]);
                return null;
            }
        }
        LOG.info("Could not read configuration {} (including trails with legacy schema).", configurationName);
        return null;
    }

    /**
     * Upgrades the configuration to the latest supported version.
     * @param configurationRoot
     *            the root folder containing the configuration with the specified
     *            {@link #configurationFilename}. See
     *            {@link #AbstractConfigurationUpgrader(Enum, Class, String)} for more information.
     * @param backupPolicy
     *            the {@link BackupPolicy} to choose if a backup is necessary or not.
     * @return if manual adoptions has to be performed after upgrading
     * @throws BackupFailedException
     *             if the backup could not be created
     */
    public boolean upgradeConfigurationToLatestVersion(Path configurationRoot, BackupPolicy backupPolicy) {
        boolean manualAdoptionsNecessary = false;

        VERSIONS_TYPE currentVersion = resolveLatestCompatibleSchemaVersion(configurationRoot);
        Path configurationFile = configurationRoot.resolve(configurationFilename);
        if (currentVersion == null) {
            throw new InvalidConfigurationException(configurationFile.toUri().toString(),
                StringUtils.capitalize(configurationName)
                    + " does not match any current or legacy schema definitions.");
        } else {
            VERSIONS_TYPE latestVersion = versions[versions.length - 1];
            // increasing iteration of versions
            for (int i = 0; i < versions.length; i++) {
                if (currentVersion == latestVersion) {
                    break; // already up to date
                }
                if (currentVersion == versions[i]) {
                    LOG.info("Upgrading {} '{}' from version {} to {}...", configurationName, configurationFile.toUri(),
                        versions[i], versions[i + 1]);

                    Object rootNode;
                    try {
                        Class<?> jaxbConfigurationClass = getJaxbConfigurationClass(versions[i]);
                        rootNode = unmarshallConfiguration(configurationFile, versions[i], jaxbConfigurationClass);

                        createBackup(configurationFile, backupPolicy);

                        // NotYetSupportedException
                        ConfigurationUpgradeResult result = performNextUpgradeStep(versions[i], rootNode);
                        manualAdoptionsNecessary |= result.areManualAdoptionsNecessary();

                        try (OutputStream out = Files.newOutputStream(configurationFile)) {
                            JAXB.marshal(result.getResultConfigurationJaxbRootNode(), out);
                        }

                        // implicitly check upgrade step
                        currentVersion = resolveLatestCompatibleSchemaVersion(configurationRoot);

                    } catch (NotYetSupportedException | BackupFailedException e) {
                        throw e;
                    } catch (Throwable e) {
                        throw new CobiGenRuntimeException("An unexpected exception occurred while upgrading the "
                            + configurationName + " from version '" + versions[i] + "' to '" + versions[i + 1] + "'.",
                            e);
                    }

                    // if CobiGen does not understand the upgraded file... throw exception
                    if (currentVersion == null) {
                        throw new CobiGenRuntimeException("An error occurred while upgrading " + configurationName
                            + " from version " + versions[i] + " to " + versions[i + 1] + ".");
                    }
                }
            }
        }

        return manualAdoptionsNecessary;
    }

    /**
     * Upgrades the given configuration to the latest supported version.
     * @param source
     *            version from which to upgrade to the latest version.
     * @param previousConfigurationRootNode
     *            JAXB configuration root node of the configuration to be upgraded
     * @return {@link ConfigurationUpgradeResult}, which contains the JAXB root node of the upgraded
     *         configuration. Further it contains a flag to indicate manual adoptions to be necessary after
     *         upgrading.
     * @throws Exception
     *             Any exception thrown during processing will be wrapped into a
     *             {@link CobiGenRuntimeException} by the {@link AbstractConfigurationUpgrader} with an
     *             appropriate message. {@link NotYetSupportedException}s will be forwarded untouched to the
     *             user.
     */
    protected abstract ConfigurationUpgradeResult performNextUpgradeStep(VERSIONS_TYPE source,
        Object previousConfigurationRootNode) throws Exception;

    /**
     * Creates a backup of the given file. If ignoreFailedBackup is set to <code>true</code>, the backup will
     * silently log a failed backup and return successfully.
     * @param file
     *            to be backed up
     * @param backupPolicy
     *            the {@link BackupPolicy} to choose if a backup is necessary or not. It will throw a
     *            {@link CobiGenRuntimeException} if a backup was enforced but the creation of the backup
     *            failed.
     * @throws BackupFailedException
     *             if the backup could not be created
     */
    private void createBackup(Path file, BackupPolicy backupPolicy) {
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
                switch (backupPolicy) {
                case NO_BACKUP:
                    // nothing to do because backup was not needed anyway
                    break;
                case BACKUP_IF_POSSIBLE:
                    LOG.warn("Could not write backup of the configuration file ('{}').", backupPath.toUri());
                    break;
                case ENFORCE_BACKUP:
                    throw new BackupFailedException("Upgrade failed. Not possible to create the backup in '"
                        + backupPath.toUri() + "' before upgrading the configuration.", e);
                }
                break;
            }
        }
    }

    /**
     * Determines and loads the class of the JAXB configuration root node with respect to a specific schema
     * version.
     * @param lv
     *            version to load class of the JAXB configuration root node for
     * @return the class of the JAXB configuration root node with respect to the given schema version
     * @throws ClassNotFoundException
     *             if the determined JAXB configuration root node class could not be found
     */
    private Class<?> getJaxbConfigurationClass(VERSIONS_TYPE lv) throws ClassNotFoundException {
        Class<?> configurationClass =
            getClass().getClassLoader().loadClass(AbstractConfigurationUpgrader.JAXB_PACKAGE_PREFIX + "." + lv.name()
                + "." + configurationJaxbRootNode.getSimpleName());
        return configurationClass;
    }

    /**
     * Unmarschalls the given configuration file with respect to the correct schema version.
     * @param configurationFile
     *            configuration file to be unmarshalled
     * @param lv
     *            schema version to be used for unmarschalling
     * @param jaxbConfigurationClass
     *            see {@link #getJaxbConfigurationClass(Enum)}
     * @return the unmarschalled JAXB object representation of the configuration
     * @throws JAXBException
     *             if anything JAXB related happened
     * @throws SAXException
     *             if the parser could not parse the schema
     * @throws IOException
     *             if the configuration file could not be read
     */
    private Object unmarshallConfiguration(Path configurationFile, VERSIONS_TYPE lv, Class<?> jaxbConfigurationClass)
        throws JAXBException, SAXException, IOException {
        Unmarshaller unmarschaller = JAXBContext.newInstance(jaxbConfigurationClass).createUnmarshaller();
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(new StreamSource(
            getClass().getResourceAsStream("/schema/" + lv.toString() + "/" + configurationXsdFilename)));
        unmarschaller.setSchema(schema);
        Object rootNode;
        try (InputStream in = Files.newInputStream(configurationFile)) {
            rootNode = unmarschaller.unmarshal(in);
        }
        return rootNode;
    }
}
