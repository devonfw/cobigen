package com.devonfw.cobigen.retriever.reader.to.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Class, which represents the templatesConfiguration of the template-set.xml
 *
 */
@XmlRootElement(name = "templateSetConfiguration")
public class MavenTemplateSetConfiguration {

  /**
   * Represents the increments element of the template-set.xml
   */
  MavenSettingsIncrements increments;

  /**
   * Represents the tags element of the template-set.xml
   */
  MavenSettingsTags tags;

  /**
   * @return increments
   */
  public MavenSettingsIncrements getIncrements() {

    return this.increments;
  }

  /**
   * @param increments new value of {@link #getincrements}.
   */
  @XmlElement(name = "increments")
  public void setIncrements(MavenSettingsIncrements increments) {

    this.increments = increments;
  }

  /**
   * @return tags
   */
  public MavenSettingsTags getTags() {

    return this.tags;
  }

  /**
   * @param tags new value of {@link #gettags}.
   */
  @XmlElement(name = "tags")
  public void setTags(MavenSettingsTags tags) {

    this.tags = tags;
  }

}
