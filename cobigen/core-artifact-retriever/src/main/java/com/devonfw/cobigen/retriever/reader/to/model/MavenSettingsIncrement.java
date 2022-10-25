package com.devonfw.cobigen.retriever.reader.to.model;

import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * Increment element of maven's settings.xml
 *
 */
public class MavenSettingsIncrement {

  String description;

  /**
   * @return description
   */
  public String getDescription() {

    return this.description;
  }

  /**
   * @param description new value of {@link #getdescription}.
   */
  @XmlAttribute(name = "description")
  public void setDescription(String description) {

    this.description = description;
  }

}
