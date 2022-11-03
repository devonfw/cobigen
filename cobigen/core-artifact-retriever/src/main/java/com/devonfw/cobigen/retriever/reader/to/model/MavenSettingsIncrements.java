package com.devonfw.cobigen.retriever.reader.to.model;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;

/**
 *
 * Represents the increment element of the template-set.xml
 *
 */
public class MavenSettingsIncrements {

  List<MavenSettingsIncrement> incrementList;

  /**
   * @return incrementList
   */
  public List<MavenSettingsIncrement> getIncrementList() {

    return this.incrementList;
  }

  /**
   * @param incrementList new value of {@link #getincrementList}.
   */
  @XmlElement(name = "increment")
  public void setIncrementList(List<MavenSettingsIncrement> incrementList) {

    this.incrementList = incrementList;
  }

}
