package com.devonfw.cobigen.retriever.reader.to.model;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;

/**
 *
 * Represents the tag element of maven's settings.xml
 *
 */

public class MavenSettingsTags {

  List<MavenSettingsTag> tagsList;

  /**
   * @return tagsList
   */
  public List<MavenSettingsTag> getTagsList() {

    return this.tagsList;
  }

  /**
   * @param tagsList new value of {@link #gettagsList}.
   */
  @XmlElement(name = "tag")
  public void setTagsList(List<MavenSettingsTag> tagsList) {

    this.tagsList = tagsList;
  }

}
