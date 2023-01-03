package com.devonfw.cobigen.gui.model;

import com.devonfw.cobigen.retriever.reader.to.model.TemplateSet;
import com.devonfw.cobigen.retriever.reader.to.model.TemplateSetConfiguration;

/**
 * TODO alsaad This type ...
 *
 */
public class ModifyableTemplateSet extends TemplateSet {

  /**
   * The constructor.
   *
   * @param templateSetVersion
   * @param templateSetConfiguration
   * @param templateSet name
   */
  public ModifyableTemplateSet(String templateSetVersion, TemplateSetConfiguration templateSetConfiguration,
      String name) {

    super(templateSetVersion, templateSetConfiguration);
    this.name = name;
  }

  private String name;

  /**
   * @return name
   */
  public String getName() {

    return this.name;
  }

  /**
   * @param name new value of {@link #getname}.
   */
  public void setName(String name) {

    this.name = name;
  }

}
