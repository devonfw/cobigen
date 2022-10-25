package com.devonfw.cobigen.retriever.reader;

import java.io.StringReader;
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

  List<String> tagNames;

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
   * @param template_set string
   *
   * @return Java class, on which parts of the template-set is mapped to
   */
  public static MavenTemplateSetConfiguration generateMavenTemplateSetConfiguration(String template_set) {

    LOG.info("Unmarshal template-set.xml");
    template_set = prepareXmlFile(template_set);
    try {
      StringReader reader = new StringReader(template_set);
      JAXBContext jaxbContext = JAXBContext.newInstance(MavenTemplateSetConfiguration.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      MavenTemplateSetConfiguration model = (MavenTemplateSetConfiguration) jaxbUnmarshaller.unmarshal(reader);

      LOG.debug("Successfully unmarshalled template-set.xml");
      return model;
    } catch (JAXBException e) {
      throw new CobiGenRuntimeException("Unable to unmarshal template-set.xml", e);
    }
  }

  /**
   * @param mavenSettings string of maven's settings.xml
   *
   * @return string of prepared maven's settings, ready to be unmarshalled
   */
  private static String prepareXmlFile(String template_set) {

    String preparedTemplateSet = template_set.substring(template_set.indexOf("<templateSetConfiguration"));
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
