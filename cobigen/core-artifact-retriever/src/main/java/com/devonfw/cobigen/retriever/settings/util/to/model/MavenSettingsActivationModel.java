package com.devonfw.cobigen.retriever.settings.util.to.model;

import jakarta.xml.bind.annotation.XmlElement;

/**
 * Class, which represents an activation element of maven's setting.xml
 *
 */
public class MavenSettingsActivationModel {

  String activeByDefault;

  /**
   * @return activeByDefault
   */
  public String getActiveByDefault() {

    return this.activeByDefault;
  }

  /**
   * @param activeByDefault new value of {@link #getactiveByDefault}.
   */
  @XmlElement
  public void setActiveByDefault(String activeByDefault) {

    this.activeByDefault = activeByDefault;
  }

}
