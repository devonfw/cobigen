package com.devonfw.cobigen.retriever.reader.to.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Class which represents the templateSetConfiguration of the template-set.xml
 */
@XmlRootElement(name = "templateSetConfiguration")
public class TemplateSetConfiguration {

  /**
   * Represents the contextConfiguration element of the template-set.xml
   */
  private ContextConfiguration contextConfiguration;

  /**
   * Represents the templatesConfiguration element of the template-set.xml
   */
  private TemplatesConfiguration templatesConfiguration;

  /** Represents the version of the template-set.xml */
  private String version;

  /**
   * @return version
   */
  public String getVersion() {

    return this.version;
  }

  /**
   * @param version new value of {@link #getversion}.
   */
  public void setVersion(String version) {

    this.version = version;
  }

  /**
   * @return contextConfiguration
   */
  public ContextConfiguration getContextConfiguration() {

    return this.contextConfiguration;
  }

  /**
   * @param contextConfiguration new value of {@link #getcontextConfiguration}.
   */
  @XmlElement(name = "contextConfiguration")
  public void setContextConfiguration(ContextConfiguration contextConfiguration) {

    this.contextConfiguration = contextConfiguration;
  }

  /**
   * @return templatesConfiguration
   */
  public TemplatesConfiguration getTemplatesConfiguration() {

    return this.templatesConfiguration;
  }

  /**
   * @param templatesConfiguration new value of {@link #gettemplatesConfiguration}.
   */
  @XmlElement(name = "templatesConfiguration")
  public void setTemplatesConfiguration(TemplatesConfiguration templatesConfiguration) {

    this.templatesConfiguration = templatesConfiguration;
  }

}
