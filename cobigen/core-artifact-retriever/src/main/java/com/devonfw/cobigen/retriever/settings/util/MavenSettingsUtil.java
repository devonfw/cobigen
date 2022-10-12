package com.devonfw.cobigen.retriever.settings.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.plexus.components.cipher.DefaultPlexusCipher;
import org.sonatype.plexus.components.cipher.PlexusCipherException;
import org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;
import org.sonatype.plexus.components.sec.dispatcher.SecUtil;
import org.sonatype.plexus.components.sec.dispatcher.model.SettingsSecurity;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.util.MavenUtil;
import com.devonfw.cobigen.retriever.settings.util.to.model.MavenSettingsModel;
import com.devonfw.cobigen.retriever.settings.util.to.model.MavenSettingsProfileModel;
import com.devonfw.cobigen.retriever.settings.util.to.model.MavenSettingsRepositoryModel;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * Utils to operate with maven's settings.xml
 *
 */
public class MavenSettingsUtil {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(MavenSettingsUtil.class);

  /**
   * @return repositories from active profiles with injected mirror urls
   */
  public static List<MavenSettingsRepositoryModel> getRepositoriesFromMavenSettings() {

    MavenSettingsModel model = generateMavenSettingsModel(MavenUtil.determineMavenSettings());

    List<MavenSettingsRepositoryModel> activeRepos = new LinkedList<>();

    String activeProfiles = "";

    try {
      activeProfiles = MavenUtil.determineActiveProfiles();
    } catch (IOException e) {
      throw new CobiGenRuntimeException("Unable to read active profiles.", e);
    }

    activeRepos = getRepositoriesOfActiveProfiles(model, activeProfiles);

    MavenMirrorUtil.injectMirrorUrl(activeRepos, model.getMirrors().getMirrorList());

    return activeRepos;
  }

  /**
   * Maps parts of maven's settings.xml to a Java class Mapping includes: settings-, profiles-, profile-, repository-,
   * repositories-, servers-, and server-elements
   *
   * @param mavenSettings string of maven's settings.xml
   *
   * @return Java class, on which parts of the settings.xml are mapped to
   */
  public static MavenSettingsModel generateMavenSettingsModel(String mavenSettings) {

    LOG.info("Unmarshal maven's settings.xml");
    mavenSettings = prepareSettings(mavenSettings);
    try {
      StringReader reader = new StringReader(mavenSettings);
      JAXBContext jaxbContext = JAXBContext.newInstance(MavenSettingsModel.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      MavenSettingsModel model = (MavenSettingsModel) jaxbUnmarshaller.unmarshal(reader);

      LOG.debug("Successfully unmarshalled maven's settings.xml");
      return model;
    } catch (JAXBException e) {
      throw new CobiGenRuntimeException("Unable to unmarshal maven's settings.xml", e);
    }
  }

  /**
   * @param mavenSettings string of maven's settings.xml
   *
   * @return string of prepared maven's settings, ready to be unmarshalled
   */
  private static String prepareSettings(String mavenSettings) {

    String preparedMavenSettings = mavenSettings.substring(mavenSettings.indexOf("<settings"));
    preparedMavenSettings = preparedMavenSettings.substring(preparedMavenSettings.indexOf(">") + 1);
    preparedMavenSettings = "<settings>" + preparedMavenSettings;
    return preparedMavenSettings;
  }

  /**
   * @param model Class, on which maven's settings.xml have been mapped to
   * @param string with active profiles
   *
   * @return List of repositories of active profiles in maven's settings.xml
   */
  private static List<MavenSettingsRepositoryModel> getRepositoriesOfActiveProfiles(MavenSettingsModel model,
      String activeProfiles) {

    LOG.debug("Determining repositories of active profiles of maven's settings.xml");

    List<MavenSettingsRepositoryModel> repositoriesOfActiveProfiles = new LinkedList<>();

    List<MavenSettingsProfileModel> profilesList = model.getProfiles().getProfileList();

    for (MavenSettingsProfileModel profile : profilesList) {
      if (profile.getRepositories() != null && activeProfiles.contains(profile.getId())) {
        repositoriesOfActiveProfiles.addAll(profile.getRepositories().getRepositoryList());
      }
    }
    return repositoriesOfActiveProfiles;
  }

  /**
   * Reads the settings security file and returns a {@link SettingsSecurity}
   *
   * @param settingsSecurityFile absolute file path to the settings security file
   * @return {@link SettingsSecurity}
   * @throws SecDispatcherException if an error occurred while reading the settings security file
   */
  public static SettingsSecurity readSettingsSecurity(String settingsSecurityFile) throws SecDispatcherException {

    return SecUtil.read(settingsSecurityFile, true);
  }

  /**
   * Decrypts the encrypted password using the decrypted plain text master key
   *
   * @param encryptedPassword String of the encrypted password
   * @param decryptedMasterKey String of the master key
   * @return String of the decrypted password
   * @throws PlexusCipherException if an error occurred during decryption
   */
  public static String decryptPassword(String encryptedPassword, String decryptedMasterKey)
      throws PlexusCipherException {

    DefaultPlexusCipher plexusCipher = new DefaultPlexusCipher();
    return plexusCipher.decryptDecorated(encryptedPassword, decryptedMasterKey);
  }

  /**
   * Decrypts the encrypted master password and returns the master password as a plain text
   *
   * @param encryptedMasterPassword String of the encrypted master password
   * @return String of the decrypted master password
   * @throws PlexusCipherException
   */
  public static String decryptMasterPassword(String encryptedMasterPassword) throws PlexusCipherException {

    return decryptPassword(encryptedMasterPassword, DefaultSecDispatcher.SYSTEM_PROPERTY_SEC_LOCATION);
  }
}
