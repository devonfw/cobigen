package com.devonfw.cobigen.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.to.model.MavenSettingsModel;

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
   * Maps parts of maven's settings.xml to a Java class Mapping includes: settings-, profiles-, profile-, repository-,
   * repositories-, servers-, and server-elements
   *
   * @param settingsXMLPath Path to maven's settings.xml
   *
   * @return Java class, on which parts of the settings.xml are mapped to
   */
  public static MavenSettingsModel generateMavenSettingsModel(Path settingsXMLPath) {

    LOG.info("Unmarshal maven's settings.xml");

    try {
      InputStream inputStream = Files.newInputStream(settingsXMLPath);
      JAXBContext jaxbContext = JAXBContext.newInstance(MavenSettingsModel.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      MavenSettingsModel model = (MavenSettingsModel) jaxbUnmarshaller.unmarshal(inputStream);

      LOG.debug("Successfully unmarshalled maven's settings.xml");
      return model;
    } catch (JAXBException | IOException e) {
      LOG.error("Unable to unmarshal maven's settings.xml");
      throw new CobiGenRuntimeException("Unable to unmarshal maven's settings.xml", e);
    }
  }

  /**
   * Determines the path of maven's settings.xml
   *
   * @return Path to settings.xml
   */
  public static Path determineMavenSettingsPath() {

    LOG.info("Determine path of maven's settings.xml");
    Path repositoryPath = MavenUtil.determineMavenRepositoryPath();
    Path settingsXMLPath = repositoryPath.getParent().resolve("settings.xml");
    LOG.debug("Determined {} as maven's settings.xml path.", settingsXMLPath);
    return settingsXMLPath;
  }

}
