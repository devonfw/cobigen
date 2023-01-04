package com.devonfw.cobigen.retriever.reader.to.model;

import jakarta.xml.bind.annotation.XmlElement;

public class TemplatesConfiguration {

  /**
   * Represents the increments element of the template-set.xml
   */
  private TemplateSetIncrements increments;

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
}
