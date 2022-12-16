package com.devonfw.cobigen.retriever.reader.to.model;

import jakarta.xml.bind.annotation.XmlElement;

public class ContextConfiguration {

  /**
   * Represents the triggers element of the template-set.xml
   */
  private TemplateSetTrigger triggers;

  /**
   * Represents the tags element of the template-set.xml
   */
  TemplateSetTags tags;

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

}
