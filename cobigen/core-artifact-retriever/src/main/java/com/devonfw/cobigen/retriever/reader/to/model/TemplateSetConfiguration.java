package com.devonfw.cobigen.retriever.reader.to.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Class which represents the templateSetConfiguration of the template-set.xml
 */
@XmlRootElement(name = "templateSetConfiguration")
public class TemplateSetConfiguration {

  /**
   * Represents the triggers element of the template-set.xml
   */
  private TemplateSetTrigger triggers;

  /**
   * Represents the increments element of the template-set.xml
   */
  private TemplateSetIncrements increments;

  /** Represents the version of the template-set.xml */
  private String version;

  /**
   * @return triggers
   */
  public TemplateSetTrigger getTriggers() {

    return this.triggers;
  }

  /**
   * @param triggers new value of {@link #gettriggers}.
   */
  @XmlElement(name = "trigger")
  public void setTriggers(TemplateSetTrigger triggers) {

    this.triggers = triggers;
  }

  /**
   * Represents the tags element of the template-set.xml
   */
  TemplateSetTags tags;

  /**
   * @return increments
   */
  public TemplateSetIncrements getIncrements() {

    return this.increments;
  }

  /**
   * @param increments new value of {@link #getincrements}.
   */
  @XmlElement(name = "increments")
  public void setIncrements(TemplateSetIncrements increments) {

    this.increments = increments;
  }

  /**
   * @return tags
   */
  public TemplateSetTags getTags() {

    return this.tags;
  }

  /**
   * @param tags new value of {@link #gettags}.
   */
  @XmlElement(name = "tags")
  public void setTags(TemplateSetTags tags) {

    this.tags = tags;
  }

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

}
