package com.devonfw.cobigen.retriever.reader.to.model;

import jakarta.xml.bind.annotation.XmlAttribute;

/**
 *
 * Tag element of maven's settings.xml
 *
 */
public class MavenSettingsTag {

  String name;

  /**
   * @return name
   */
  public String getName() {

    return this.name;
  }

  /**
   * @param name new value of {@link #getname}.
   */
  @XmlAttribute(name = "name")
  public void setName(String name) {

    this.name = name;
  }

}
