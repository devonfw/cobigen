package com.devonfw.cobigen.api.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.to.MavenSettingsModel;

/**
 * TODO fberger This type ...
 *
 */
public class MavenSettingsUtil {

  public static void main(String[] args) {

    MavenSettingsUtil.getRepositories();

  }

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(MavenSettingsUtil.class);

  public static void getRepositories() {

    Path repositoryPath = MavenUtil.determineMavenRepositoryPath();
    String settingsXMLString = repositoryPath.toString().replaceAll("repository", "settings.xml");
    Path settingsXMLPath = Paths.get(settingsXMLString);

    try {
      File initialFile = new File(settingsXMLString);
      JAXBContext jaxbContext = JAXBContext.newInstance(MavenSettingsModel.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      MavenSettingsModel model = (MavenSettingsModel) jaxbUnmarshaller.unmarshal(initialFile);
      System.out.println(model.getLocalRepository());
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    System.out.println("läuft");

  }

}
