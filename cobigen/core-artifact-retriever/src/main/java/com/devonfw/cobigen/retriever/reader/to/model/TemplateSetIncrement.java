package com.devonfw.cobigen.retriever.reader.to.model;

import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * Increment element of the template-set.xml
 *
 */
public class TemplateSetIncrement {

  /** Name of increment */
  String name;

  /** Description of increment */
  String description;

  /** Explanation of increment */
  String explanation;

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

  /**
   * @return explanation
   */
  public String getExplanation() {

    return this.explanation;
  }

  /**
   * @param explanation new value of {@link #getexplanation}.
   */
  @XmlAttribute(name = "explanation")
  public void setExplanation(String explanation) {

    this.explanation = explanation;
  }

  /**
   * @return description
   */
  public String getDescription() {

    return this.description;
  }

  /**
   * @param description new value of {@link #getdescription}.
   */
  @XmlAttribute(name = "description")
  public void setDescription(String description) {

    this.description = description;
  }

}
