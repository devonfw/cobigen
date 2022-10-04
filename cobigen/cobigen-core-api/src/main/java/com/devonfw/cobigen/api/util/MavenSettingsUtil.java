package com.devonfw.cobigen.api.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.to.model.MavenSettingsModel;
import com.devonfw.cobigen.api.to.model.MavenSettingsProfileModel;
import com.devonfw.cobigen.api.to.model.MavenSettingsRepositoryModel;

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
}
