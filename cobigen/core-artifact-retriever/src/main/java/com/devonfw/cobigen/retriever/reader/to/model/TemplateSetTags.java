package com.devonfw.cobigen.retriever.reader.to.model;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;

/**
 * Represents the tag element of the template-set.xml
 */
public class TemplateSetTags {

  List<TemplateSetTag> tagsList;

  /**
   * @return tagsList
   */
  public List<TemplateSetTag> getTagsList() {

    return this.tagsList;
  }

  /**
   * @param tagsList new value of {@link #gettagsList}.
   */
  @XmlElement(name = "tag")
  public void setTagsList(List<TemplateSetTag> tagsList) {

    this.tagsList = tagsList;
  }

}
