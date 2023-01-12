package com.devonfw.cobigen.retriever.reader.to.model;

/**
 * Class which represents the template set entity which can be used to access the version and
 * {@link TemplateSetConfiguration}
 */
public class TemplateSet {

  /** Version of the template set */
  private String templateSetVersion;

  /** {@link TemplateSetConfiguration} of the template set */
  private TemplateSetConfiguration templateSetConfiguration;

  /**
   * The constructor.
   *
   * @param templateSetVersion the version of the template set
   * @param templateSetConfiguration the {@link TemplateSetConfiguration}
   */
  public TemplateSet(String templateSetVersion, TemplateSetConfiguration templateSetConfiguration) {

    this.templateSetVersion = templateSetVersion;
    this.templateSetConfiguration = templateSetConfiguration;
  }

  /**
   * @return templateSetVersion
   */
  public String getTemplateSetVersion() {

    return this.templateSetVersion;
  }

  /**
   * @param templateSetVersion new value of {@link #gettemplateSetVersion}.
   */
  public void setTemplateSetVersion(String templateSetVersion) {

    this.templateSetVersion = templateSetVersion;
  }

  /**
   * @return templateSetConfiguration
   */
  public TemplateSetConfiguration getTemplateSetConfiguration() {

    return this.templateSetConfiguration;
  }

  /**
   * @param templateSetConfiguration new value of {@link #gettemplateSetConfiguration}.
   */
  public void setTemplateSetConfiguration(TemplateSetConfiguration templateSetConfiguration) {

    this.templateSetConfiguration = templateSetConfiguration;
  }

}
