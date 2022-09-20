package com.devonfw.cobigen.api.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.to.model.MavenSettingsModel;

/**
 * Utils to operate with maven's settings.xml
 *
 */
public class MavenSettingsUtil {

  public static void main(String[] args) {

    MavenSettingsUtil.generateMavenSettingsModel();

  }

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(MavenSettingsUtil.class);

  /**
   * Maps parts of maven's settings.xml to a Java class Mapping includes: settings-, profiles-, profile-, repository-,
   * repositories-, servers-, and server-elements
   *
   * @return Java class, on which parts of the settings.xml are mapped to
   */
  public static MavenSettingsModel generateMavenSettingsModel() {

    LOG.info("Unmarshal maven's settings.xml");

    Path settingsXMLPath = determineMavenSettingsPath();

    try {
      File initialFile = new File(settingsXMLPath.toString());
      JAXBContext jaxbContext = JAXBContext.newInstance(MavenSettingsModel.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      MavenSettingsModel model = (MavenSettingsModel) jaxbUnmarshaller.unmarshal(initialFile);
      // Für Präsentation (Haltepunkt setzen)
      // System.out.println(model);

      LOG.debug("Successfully unmarshalled maven's settings.xml");
      return model;
    } catch (JAXBException e) {
      LOG.error("Unable to unmarshal maven's settings.xml");
      throw new CobiGenRuntimeException("", e);
    }
  }

  /**
   * Determines the path of maven's settings.xml
   *
   * @return Path to settings.xml
   */
  private static Path determineMavenSettingsPath() {

    LOG.info("Determine path of maven's settings.xml");
    Path repositoryPath = MavenUtil.determineMavenRepositoryPath();
    String settingsXMLString = repositoryPath.toString().replaceAll("repository", "settings.xml");
    LOG.debug("Determined {} as maven's settings.xml path.", settingsXMLString);
    return Paths.get(settingsXMLString);
  }

}
