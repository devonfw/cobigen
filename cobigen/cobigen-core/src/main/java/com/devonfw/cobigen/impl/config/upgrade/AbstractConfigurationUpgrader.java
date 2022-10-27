package com.devonfw.cobigen.impl.config.upgrade;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.devonfw.cobigen.api.constants.BackupPolicy;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.exception.NotYetSupportedException;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.api.util.ExceptionUtil;
import com.devonfw.cobigen.api.util.JvmUtil;
import com.devonfw.cobigen.impl.exceptions.BackupFailedException;
import com.google.common.collect.Lists;

import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.Unmarshaller;

/**
 * This class encompasses all logic for upgrading CobiGen configurations including
 * <ul>
 * <li>detection of all released schema compatibilities</li>
 * <li>upgrading legacy configurations to the latest supported version</li>
 * </ul>
 *
 * @param <VERSIONS_TYPE> Type of the version enum listing all supported versions (increasing declaration).
 */
public abstract class AbstractConfigurationUpgrader<VERSIONS_TYPE extends Enum<?>> {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(AbstractConfigurationUpgrader.class);

  /** All enum values from the versions {@link Enum} */
  private VERSIONS_TYPE[] versions;

  /**
   * Name of the configuration (simple type of the {@link #configurationJaxbRootNode} with whitespaces)
   */
  private String configurationName;

  /** Standardized file name of the configuration */
  private String configurationFilename;

  /**
   * Name of the configuration (simple type of the {@link #configurationJaxbRootNode} appended by .xsd)
   */
  private String configurationXsdFilename;

  /** JAXB root object class of the configuration for (un-)marshalling */
  private Class<?> configurationJaxbRootNode;

  /**
   * Package prefix generated JAXB classes of the configurations (analogous to the pom.xml specification)
   */
  private static final String JAXB_PACKAGE_PREFIX = "com.devonfw.cobigen.impl.config.entity.io";

  /**
   * Creates a new instance for the abstract implementation of an configuration upgrader.
   *
   * @param version an arbitrary instance to perform reflection on during upgrading.
   * @param configurationJaxbRootNode JAXB root object class of the configuration for (un-)marshalling.
   * @param configurationFilename standardized file name of the configuration
   * @author mbrunnli (Jun 23, 2015)
   */
  @SuppressWarnings("unchecked")
  AbstractConfigurationUpgrader(VERSIONS_TYPE version, Class<?> configurationJaxbRootNode,
      String configurationFilename) {

    this.configurationJaxbRootNode = configurationJaxbRootNode;
    this.configurationFilename = configurationFilename;

    // split camel case
    this.configurationName = StringUtils
        .join(configurationJaxbRootNode.getSimpleName().split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=-Z][a-z])"), " ");

    this.configurationXsdFilename = StringUtils.uncapitalize(configurationJaxbRootNode.getSimpleName()) + ".xsd";

    // determine all "version enum" values
    try {
      this.versions = (VERSIONS_TYPE[]) version.getClass().getMethod("values").invoke(version);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
        | SecurityException e) {
      throw new CobiGenRuntimeException("Unexpected happend! Could not determine values of the enum '" + version + "'.",
          e);
    }
  }

  /**
   * Checks, whether the configuration is compliant to the latest supported version.
   *
   * @param configurationRoot the root folder containing the configuration
   * @return <code>true</code> if the configuration is up-to-date, <br>
   *         otherwise <code>false</code>
   */
  public boolean isCompliantToLatestSupportedVersion(Path configurationRoot) {

    return resolveLatestCompatibleSchemaVersion(configurationRoot, true, null) != null;
  }

  /**
   * Checks, whether the configuration can be read with an old schema version.
   *
   * @param configurationRoot the root folder containing the configuration
   * @return the newest schema version, the configuration is compliant with or <code>null</code> if the configuration is
   *         not compliant to any.
   */
  public VERSIONS_TYPE resolveLatestCompatibleSchemaVersion(Path configurationRoot) {

    return resolveLatestCompatibleSchemaVersion(configurationRoot, false, null);
  }

  /**
   * Checks, whether the configuration can be read with an old schema version and a maximum version to be used for the
   * resolver (mainly used for tests)
   *
   * @param configurationRoot the root folder containing the configuration
   * @param maxVersionToUse the latest version to be used for the resolver
   * @return the newest schema version, the configuration is compliant with or <code>null</code> if the configuration is
   *         not compliant to any.
   */
  public VERSIONS_TYPE resolveLatestCompatibleSchemaVersion(Path configurationRoot, VERSIONS_TYPE maxVersionToUse) {

    return resolveLatestCompatibleSchemaVersion(configurationRoot, false, maxVersionToUse);
  }

  /**
   * Checks, whether the configuration can be read with an old schema version.
   *
   * @param configurationRoot the root folder containing the configuration
   * @param justCheckLatestVersion just checks latest supported version and returns
   * @param maxVersion the latest version to be used for the resolver
   * @return the newest schema version, the configuration is compliant with or <code>null</code> if the configuration is
   *         not compliant to any.
   */
  private VERSIONS_TYPE resolveLatestCompatibleSchemaVersion(Path configurationRoot, boolean justCheckLatestVersion,
      VERSIONS_TYPE maxVersion) {

    LOG.info("Try reading {} (including trails with legacy schema).", this.configurationName);

    Path configurationFile = configurationRoot.endsWith(this.configurationFilename) ? configurationRoot
        : configurationRoot.resolve(this.configurationFilename);

    if (maxVersion != null) {
      List<VERSIONS_TYPE> reducedVersionsList = limitVersions(maxVersion);

      for (VERSIONS_TYPE version : Lists.reverse(reducedVersionsList)) {
        if (isConfgurationFileCompatibleToSchemaVersion(version, configurationFile)) {
          return version;
        }
      }
      LOG.info("Could not read configuration {} (with versions limited to: {}).", this.configurationName, maxVersion);
      return null;
    }

    VERSIONS_TYPE[] reversedVersions = Arrays.copyOf(this.versions, this.versions.length);
    ArrayUtils.reverse(reversedVersions);

    for (VERSIONS_TYPE version : Arrays.asList(reversedVersions)) {
      if (isConfgurationFileCompatibleToSchemaVersion(version, configurationFile)) {
        return version;
      }

      if (justCheckLatestVersion) {
        LOG.info("Could not read configuration {} with schema {} (latest).", this.configurationName,
            this.versions[this.versions.length - 1]);
        return null;
      }
    }
    LOG.info("Could not read configuration {} (including trails with legacy schema).", this.configurationName);
    return null;
  }

  /**
   * Limits the version list to the maximum version provided
   *
   * @param maxVersion maximum version to set
   * @return List of new versions reduced by maximum version
   */
  private List<VERSIONS_TYPE> limitVersions(VERSIONS_TYPE maxVersion) {

    List<VERSIONS_TYPE> versionsList = new ArrayList<>(Arrays.asList(this.versions));
    List<VERSIONS_TYPE> reducedVersionsList = new ArrayList<>();
    for (VERSIONS_TYPE version : versionsList) {
      if (version.ordinal() <= maxVersion.ordinal()) {
        reducedVersionsList.add(version);
      }
    }
    return reducedVersionsList;
  }

  /**
   * Checks if the provided version is compatible to the provided configuration file
   *
   * @param version current version to validate against
   * @param configurationFile current configuration file to validate with version
   * @return true if the version is compatible to the configuration file and false if not
   */
  private boolean isConfgurationFileCompatibleToSchemaVersion(VERSIONS_TYPE version, Path configurationFile) {

    LOG.info("Try {} schema '{}'.", this.configurationName, version.toString());
    try {
      Class<?> jaxbConfigurationClass = getJaxbConfigurationClass(version);
      Object rootNode = unmarshallConfiguration(configurationFile, version, jaxbConfigurationClass);

      // check, whether the read node can be casted to the correct configuration root
      // node object
      if (!jaxbConfigurationClass.isAssignableFrom(rootNode.getClass())) {
        LOG.info("It was not possible to read {} with schema '{}' .", this.configurationName, version.toString());
      } else {
        LOG.info("It was possible to read {} with schema '{}' .", this.configurationName, version.toString());
        return true;
      }
    } catch (Throwable e) {
      Throwable cause = ExceptionUtil.getCause(e, SAXParseException.class, UnmarshalException.class);
      if (cause != null && cause.getMessage() != null) {
        LOG.info("Not able to read {} configuration with schema '{}': {}", this.configurationName, version.toString(),
            cause.getMessage());
      } else {
        LOG.warn("Not able to read {} configuration with schema '{}' .", this.configurationName, version.toString(), e);
      }
    }

    return false;
  }

  /**
   * Upgrades the configuration to the latest supported version.
   *
   * @param configurationRoot the root folder containing the configuration with the specified
   *        {@link #configurationFilename}. See {@link #AbstractConfigurationUpgrader(Enum, Class, String)} for more
   *        information.
   * @param backupPolicy the {@link BackupPolicy} to choose if a backup is necessary or not.
   * @return if manual adoptions has to be performed after upgrading
   * @throws BackupFailedException if the backup could not be created
   */
  public boolean upgradeConfigurationToLatestVersion(Path configurationRoot, BackupPolicy backupPolicy) {

    return upgradeConfigurationToLatestVersion(configurationRoot, backupPolicy, null);
  }

  /**
   * Upgrades the configuration to the latest supported version with option to limit lookup of the maximum version.
   *
   * @param templatesProject the path to the templates project
   * @param backupPolicy the {@link BackupPolicy} to choose if a backup is necessary or not.
   * @param maxVersion the latest version to be used for the upgrade (limits the versions to choose from)
   * @return if a manual adoptions has to be performed after upgrading
   * @throws BackupFailedException if the backup could not be created
   *
   *         configurationRoot the root folder containing the configuration with the specified
   *         {@link #configurationFilename}. See {@link #AbstractConfigurationUpgrader(Enum, Class, String)} for more
   *         information.
   */
  public boolean upgradeConfigurationToLatestVersion(Path templatesProject, BackupPolicy backupPolicy,
      VERSIONS_TYPE maxVersion) {

    Path configurationLocation = templatesProject;
    if (this.getClass().equals(ContextConfigurationUpgrader.class))
      configurationLocation = CobiGenPaths.getContextLocation(templatesProject);

    boolean manualAdoptionsNecessary = false;

    VERSIONS_TYPE validatedVersion;
    List<VERSIONS_TYPE> versionsList = new ArrayList<>();

    // check if versions need to be limited
    if (maxVersion != null) {
      versionsList = limitVersions(maxVersion);
      validatedVersion = resolveLatestCompatibleSchemaVersion(configurationLocation, maxVersion);
    } else {
      versionsList = Arrays.asList(this.versions);
      validatedVersion = resolveLatestCompatibleSchemaVersion(configurationLocation);
    }

    Path configurationFile = configurationLocation.resolve(this.configurationFilename);
    if (validatedVersion == null) {
      throw new InvalidConfigurationException(configurationFile.toUri().toString(),
          StringUtils.capitalize(this.configurationName) + " does not match any current or legacy schema definitions.");
    } else {
      VERSIONS_TYPE latestVersion = versionsList.get(0);
      // increasing iteration of versions
      for (int i = 0; i < versionsList.size(); i++) {
        if (validatedVersion == latestVersion) {
          break; // already up to date
        }
        if (validatedVersion == versionsList.get(i)) {
          LOG.info("Upgrading {} '{}' from version {} to {}...", this.configurationName, configurationFile.toUri(),
              versionsList.get(i), versionsList.get(i + 1));

          Object rootNode;
          try {
            Class<?> jaxbConfigurationClass = getJaxbConfigurationClass(versionsList.get(i));
            rootNode = unmarshallConfiguration(configurationFile, versionsList.get(i), jaxbConfigurationClass);

            createBackup(configurationFile, backupPolicy);

            List<ConfigurationUpgradeResult> results = performNextUpgradeStep(versionsList.get(i), rootNode,
                templatesProject);
            for (ConfigurationUpgradeResult result : results) {

              manualAdoptionsNecessary |= result.areManualAdoptionsNecessary();
              try (OutputStream out = Files.newOutputStream(result.getConfigurationPath())) {
                JAXB.marshal(result.getResultConfigurationJaxbRootNode(), out);
              }

              // implicitly check upgrade step
              VERSIONS_TYPE currentVersion = resolveLatestCompatibleSchemaVersion(result.getConfigurationPath());
              // if CobiGen does not understand the upgraded file... throw exception
              if (currentVersion == null) {
                throw new CobiGenRuntimeException("An error occurred while upgrading " + this.configurationName
                    + " from version " + versionsList.get(i) + " to " + versionsList.get(i + 1) + ".");
              }
            }
          } catch (NotYetSupportedException | BackupFailedException e) {
            throw e;
          } catch (Throwable e) {
            throw new CobiGenRuntimeException(
                "An unexpected exception occurred while upgrading the " + this.configurationName + " from version '"
                    + versionsList.get(i) + "' to '" + versionsList.get(i + 1) + "'.",
                e);
          }

        }
      }
    }

    return manualAdoptionsNecessary;
  }

  /**
   * Upgrades the given configuration to the latest supported version.
   *
   * @param source version from which to upgrade to the latest version.
   * @param previousConfigurationRootNode JAXB configuration root node of the configuration to be upgraded
   * @param configurationRoot the directory where the configuration is located
   * @return {@link ConfigurationUpgradeResult}, which contains the JAXB root node of the upgraded configuration.
   *         Further it contains a flag to indicate manual adoptions to be necessary after upgrading.
   * @throws Exception Any exception thrown during processing will be wrapped into a {@link CobiGenRuntimeException} by
   *         the {@link AbstractConfigurationUpgrader} with an appropriate message. {@link NotYetSupportedException}s
   *         will be forwarded untouched to the user.
   */
  protected abstract List<ConfigurationUpgradeResult> performNextUpgradeStep(VERSIONS_TYPE source,
      Object previousConfigurationRootNode, Path configurationRoot) throws Exception;

  /**
   * Creates a backup of the given file. If ignoreFailedBackup is set to <code>true</code>, the backup will silently log
   * a failed backup and return successfully.
   *
   * @param file to be backed up
   * @param backupPolicy the {@link BackupPolicy} to choose if a backup is necessary or not. It will throw a
   *        {@link CobiGenRuntimeException} if a backup was enforced but the creation of the backup failed.
   * @throws BackupFailedException if the backup could not be created
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
        if (!Files.exists(backupPath))
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
   * Determines and loads the class of the JAXB configuration root node with respect to a specific schema version.
   *
   * @param lv version to load class of the JAXB configuration root node for
   * @return the class of the JAXB configuration root node with respect to the given schema version
   * @throws ClassNotFoundException if the determined JAXB configuration root node class could not be found
   */
  private Class<?> getJaxbConfigurationClass(VERSIONS_TYPE lv) throws ClassNotFoundException {

    Class<?> configurationClass = getClass().getClassLoader()
        .loadClass(AbstractConfigurationUpgrader.JAXB_PACKAGE_PREFIX + "." + lv.name() + "."
            + this.configurationJaxbRootNode.getSimpleName());
    return configurationClass;
  }

  /**
   * Unmarschalls the given configuration file with respect to the correct schema version.
   *
   * @param configurationFile configuration file to be unmarshalled
   * @param lv schema version to be used for unmarschalling
   * @param jaxbConfigurationClass see {@link #getJaxbConfigurationClass(Enum)}
   * @return the unmarschalled JAXB object representation of the configuration
   * @throws JAXBException if anything JAXB related happened
   * @throws SAXException if the parser could not parse the schema
   * @throws IOException if the configuration file could not be read
   */
  private Object unmarshallConfiguration(Path configurationFile, VERSIONS_TYPE lv, Class<?> jaxbConfigurationClass)
      throws JAXBException, SAXException, IOException {

    // workaround to make JAXB work in OSGi context by
    // https://github.com/ControlSystemStudio/cs-studio/issues/2530#issuecomment-450991188
    final ClassLoader orig = Thread.currentThread().getContextClassLoader();
    if (JvmUtil.isRunningJava9OrLater()) {
      Thread.currentThread().setContextClassLoader(JAXBContext.class.getClassLoader());
    }

    try {
      Unmarshaller unmarschaller = JAXBContext.newInstance(jaxbConfigurationClass).createUnmarshaller();
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = schemaFactory.newSchema(new StreamSource(
          getClass().getResourceAsStream("/schema/" + lv.toString() + "/" + this.configurationXsdFilename)));
      unmarschaller.setSchema(schema);
      Object rootNode;
      try (InputStream in = Files.newInputStream(configurationFile)) {
        rootNode = unmarschaller.unmarshal(in);
      }
      return rootNode;
    } finally {
      Thread.currentThread().setContextClassLoader(orig);
    }
  }
}
