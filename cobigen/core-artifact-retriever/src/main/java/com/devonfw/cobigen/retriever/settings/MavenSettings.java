package com.devonfw.cobigen.retriever.settings;

import java.io.StringReader;
import java.util.ArrayList;
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
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsModel;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsProfileModel;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsProxyModel;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsRepositoryModel;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * Class to operate with maven's settings.xml
 *
 */
public class MavenSettings {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(MavenSettings.class);

  /**
   * @param mavenSettings maven's settings.xml as a string
   * @return repositories from active profiles with injected mirror urls
   */
  public static List<MavenSettingsRepositoryModel> getRepositoriesFromMavenSettings(String mavenSettings) {

    MavenSettingsModel model = generateMavenSettingsModel(mavenSettings);

    List<MavenSettingsRepositoryModel> activeRepos = new LinkedList<>();

    activeRepos = getRepositoriesOfActiveProfiles(model);

    if (model.getMirrors() == null) {
      return activeRepos;
    }

    MavenMirror.injectMirrorUrl(activeRepos, model.getMirrors().getMirrorList());

    return activeRepos;
  }

  /**
   * @param model class, on which maven's settings.xml has been mapped on
   * @return the active proxy or null if their is no active one
   */
  public static MavenSettingsProxyModel getActiveProxy(MavenSettingsModel model) {

    MavenSettingsProxyModel proxy = null;
    if (model.getProxies() != null && model.getProxies().getProxyList() != null) {
      for (MavenSettingsProxyModel p : model.getProxies().getProxyList()) {
        if (p.getActive().equals("true")) {
          proxy = p;
          break;
        }
      }
    }
    return proxy;
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
   *
   * @return List of repositories of active profiles in maven's settings.xml
   */
  private static List<MavenSettingsRepositoryModel> getRepositoriesOfActiveProfiles(MavenSettingsModel model) {

    LOG.debug("Determining repositories of active profiles of maven's settings.xml");

    List<MavenSettingsRepositoryModel> repositoriesOfActiveProfiles = new ArrayList<>();

    List<MavenSettingsProfileModel> profilesList = new ArrayList<>();

    List<String> activeProfileElementList = new ArrayList<>();

    if (model.getActiveProfiles() != null && model.getActiveProfiles().getActiveProfilesList() != null) {
      activeProfileElementList = model.getActiveProfiles().getActiveProfilesList();
    }

    if (model.getProfiles() != null && model.getProfiles().getProfileList() != null) {
      profilesList = model.getProfiles().getProfileList();
    }

    for (MavenSettingsProfileModel profile : profilesList) {

      // Check if profile was activated by the activeProfiles element
      if (activeProfileElementList.contains(profile.getId()) && profile.getRepositories() != null) {
        repositoriesOfActiveProfiles.addAll(profile.getRepositories().getRepositoryList());
        continue;
      }
      // Check if profile was activated by default
      if (profile.getActivation() != null && profile.getRepositories() != null
          && profile.getActivation().getActiveByDefault() != null
          && profile.getActivation().getActiveByDefault().equals("true")) {
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
