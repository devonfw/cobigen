package com.devonfw.cobigen.retriever.reader;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.retriever.reader.to.model.MavenSettingsIncrement;
import com.devonfw.cobigen.retriever.reader.to.model.MavenSettingsTag;
import com.devonfw.cobigen.retriever.reader.to.model.MavenTemplateSetConfiguration;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 *
 * Reader for human readable data extracted from template-set.xmls
 *
 */
public class TemplateSetArtifactReader {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(TemplateSetArtifactReader.class);

  /** List of tag names */
  List<String> tagNames;

  /** List of increment descriptions */
  List<String> incrementDescriptions;

  /**
   *
   * The constructor. Initializes fields
   */
  public TemplateSetArtifactReader() {

    MavenTemplateSetConfiguration templateSetConfiguration = new MavenTemplateSetConfiguration();

    if (templateSetConfiguration.getTags() != null) {
      for (MavenSettingsTag tag : templateSetConfiguration.getTags().getTagsList()) {
        this.tagNames.add(tag.getName());
      }
    }

    if (templateSetConfiguration.getIncrements() != null) {
      for (MavenSettingsIncrement increment : templateSetConfiguration.getIncrements().getIncrementList()) {
        this.incrementDescriptions.add(increment.getDescription());
      }
    }

  }

  /**
   * Maps human readable parts of template-set to a Java class
   *
   * @param templateSetFilePath string
   *
   * @return Java class, on which parts of the template-set is mapped to
   */
  public static MavenTemplateSetConfiguration generateMavenTemplateSetConfiguration(Path templateSetFilePath) {

    String templateSetFileContent = "";

    try {
      LOG.debug("Trying to read template set artifact file at: {}", templateSetFilePath.toAbsolutePath());
      templateSetFileContent = Files.readString(templateSetFilePath);
    } catch (IOException e) {
      throw new CobiGenRuntimeException("Unable to read test template-set.xml file", e);
    }

    LOG.debug("Trying to unmarshall template set artifact file: {}", templateSetFilePath.getFileName());
    templateSetFileContent = reduceXmlFile(templateSetFileContent);
    try {

      StringReader reader = new StringReader(templateSetFileContent);
      JAXBContext jaxbContext = JAXBContext.newInstance(MavenTemplateSetConfiguration.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      MavenTemplateSetConfiguration model = (MavenTemplateSetConfiguration) jaxbUnmarshaller.unmarshal(reader);

      LOG.debug("Successfully unmarshalled template set artifact file: {}", templateSetFilePath.getFileName());
      return model;
    } catch (JAXBException e) {
      throw new CobiGenRuntimeException("CobiGen was unable to unmarshal the template set artifact file.", e);
    }
  }

  /**
   * Removes the templateSetConfiguration tag from the xml string
   *
   * @param templateSetFileContent string of template set xml file
   *
   * @return string of prepared template set, ready to be unmarshalled
   */
  private static String reduceXmlFile(String templateSetFileContent) {

    String preparedTemplateSet = templateSetFileContent
        .substring(templateSetFileContent.indexOf("<templateSetConfiguration"));
    preparedTemplateSet = preparedTemplateSet.substring(preparedTemplateSet.indexOf(">") + 1);
    preparedTemplateSet = "<templateSetConfiguration>" + preparedTemplateSet;
    return preparedTemplateSet;
  }

  /**
   * @return tagNames
   */
  public List<String> getTagNames() {

    return this.tagNames;
  }

  /**
   * @return incrementDescriptions
   */
  public List<String> getIncrementDescriptions() {

    return this.incrementDescriptions;
  }
}
