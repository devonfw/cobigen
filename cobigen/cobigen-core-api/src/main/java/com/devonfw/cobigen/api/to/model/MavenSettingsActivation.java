package com.devonfw.cobigen.api.to.model;

import jakarta.xml.bind.annotation.XmlElement;

/**
 * Class, which represents the activation element of the settings.xml
 *
 */
public class MavenSettingsActivation {

  /**
   * Represents, if the repository is active by default
   */
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
  @XmlElement(name = "activeByDefault")
  public void setActiveByDefault(String activeByDefault) {

    this.activeByDefault = activeByDefault;
  }

}
