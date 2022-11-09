package com.devonfw.cobigen.retriever.settings.to.model;

import jakarta.xml.bind.annotation.XmlElement;

/**
 * Class, which represents an activation element of maven's setting.xml
 */
public class MavenSettingsActivationModel {

  /** Represents the active by default element of an activation model */
  private String activeByDefault;

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
