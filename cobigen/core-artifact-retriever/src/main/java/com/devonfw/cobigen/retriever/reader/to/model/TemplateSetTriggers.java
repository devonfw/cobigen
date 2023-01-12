package com.devonfw.cobigen.retriever.reader.to.model;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;

/**
 *
 * Represents the trigger element of the template-set.xml
 *
 */
public class TemplateSetTriggers {

  /** List of triggers */
  List<TemplateSetTrigger> triggersList;

  /**
   * @return tagsList
   */
  public List<TemplateSetTrigger> getTriggersList() {

    return this.triggersList;
  }

  /**
   * @param triggersList new value of {@link #getTriggersList}.
   */
  @XmlElement(name = "trigger")
  public void setTriggersList(List<TemplateSetTrigger> triggersList) {

    this.triggersList = triggersList;
  }

}
