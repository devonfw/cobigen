package com.devonfw.cobigen.retriever.reader;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.retriever.reader.to.model.TemplateSetConfiguration;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * Reader for human readable data extracted from template-set.xmls
 */
public class TemplateSetArtifactReader {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(TemplateSetArtifactReader.class);

  /** Version of the template set */
  private String templateSetVersion;

  /** {@link TemplateSetConfiguration} of the template set */
  private TemplateSetConfiguration templateSetConfiguration;

  /**
   * The constructor. Initializes the template set version and the {@link TemplateSetConfiguration}
   *
   * @param templateSetFilePath Path to the template-set.xml
   */
  public TemplateSetArtifactReader(Path templateSetFilePath) {

    this.templateSetVersion = parseVersionFromTemplateSetFile(templateSetFilePath);
    this.templateSetConfiguration = generateMavenTemplateSetConfiguration(templateSetFilePath);
  }

  /**
   * Maps human readable parts of template-set to a Java class
   *
   * @param templateSetFilePath string
   *
   * @return Java class, on which parts of the template-set is mapped to
   */
  private TemplateSetConfiguration generateMavenTemplateSetConfiguration(Path templateSetFilePath) {

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
      JAXBContext jaxbContext = JAXBContext.newInstance(TemplateSetConfiguration.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      TemplateSetConfiguration model = (TemplateSetConfiguration) jaxbUnmarshaller.unmarshal(reader);

      LOG.debug("Successfully unmarshalled template set artifact file: {}", templateSetFilePath.getFileName());
      return model;
    } catch (JAXBException e) {
      throw new CobiGenRuntimeException("CobiGen was unable to unmarshal the template set artifact file.", e);
    }
  }

  /**
   * Parses the version number from the template-set.xml file
   *
   * @param templateSetFile Path to the template-set.xml
   * @return String of version number
   */
  private String parseVersionFromTemplateSetFile(Path templateSetFile) {

    Pattern pattern = Pattern.compile("[-][\\d]*[.][\\d]*[.][\\d]*[-]");
    Matcher matcher = pattern.matcher(templateSetFile.getFileName().toString());
    String templateSetversion = "";
    if (matcher.find()) {
      templateSetversion = matcher.group();
    }

    return templateSetversion.replace("-", "");
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
   * @return templateSetVersion
   */
  public String getTemplateSetVersion() {

    return this.templateSetVersion;
  }

  /**
   * @return templateSetConfiguration
   */
  public TemplateSetConfiguration getTemplateSetConfiguration() {

    return this.templateSetConfiguration;
  }

}
