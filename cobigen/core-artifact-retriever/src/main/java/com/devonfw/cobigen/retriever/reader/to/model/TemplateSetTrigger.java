package com.devonfw.cobigen.retriever.reader.to.model;

import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * Trigger element of the template-set.xml
 */
public class TemplateSetTrigger {

  /** The id of the trigger */
  String id;

  /** The type of the trigger */
  String type;

  /**
   * @return id
   */
  public String getId() {

    return this.id;
  }

  /**
   * @param id new value of {@link #getid}.
   */
  @XmlAttribute(name = "id")
  public void setId(String id) {

    this.id = id;
  }

  /**
   * @return type
   */
  public String getType() {

    return this.type;
  }

  /**
   * @param type new value of {@link #gettype}.
   */
  @XmlAttribute(name = "type")
  public void setType(String type) {

    this.type = type;
  }

}
