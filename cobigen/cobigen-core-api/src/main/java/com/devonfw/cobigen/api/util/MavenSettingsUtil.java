package com.devonfw.cobigen.api.util;

import java.io.StringReader;

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
  public static MavenSettingsModel generateMavenSettingsModel() {

    LOG.info("Unmarshal maven's settings.xml");

    String mavenSettings = prepareSettings();
    try {
      StringReader reader = new StringReader(mavenSettings);
      JAXBContext jaxbContext = JAXBContext.newInstance(MavenSettingsModel.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      MavenSettingsModel model = (MavenSettingsModel) jaxbUnmarshaller.unmarshal(reader);

      LOG.debug("Successfully unmarshalled maven's settings.xml");
      return model;
    } catch (JAXBException e) {
      LOG.error("Unable to unmarshal maven's settings.xml");
      throw new CobiGenRuntimeException("Unable to unmarshal maven's settings.xml", e);
    }
  }

  private static String prepareSettings() {

    String mavenSettings = MavenUtil.determineMavenSettings();
    String preparedMavenSettings = mavenSettings.substring(mavenSettings.indexOf("<settings"));
    preparedMavenSettings = preparedMavenSettings.substring(preparedMavenSettings.indexOf(">") + 1);
    preparedMavenSettings = "<settings>" + preparedMavenSettings;
    return preparedMavenSettings;
  }
}
