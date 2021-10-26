package com.devonfw.cobigen.api.externalprocess.to;

/**
 * Contains all the properties needed for requesting a merge on two files. It gets serialized and sent over the network
 */
public class MergeTo {

  /**
   * Content of the base file
   */
  private String baseContent;

  /**
   * Content of the patch file
   */
  private String patchContent;

  /**
   * If true, in case of conflict, we will override base content with patch content. By default it should be false.
   */
  private Boolean patchOverrides;

  /**
   * The constructor.
   *
   * @param baseContent Content of the base file
   * @param patchContent Content of the patch file
   * @param patchOverrides If true, in case of conflict, we will override base content with patch content
   */
  public MergeTo(String baseContent, String patchContent, Boolean patchOverrides) {

    super();
    this.baseContent = baseContent;
    this.patchContent = patchContent;
    this.patchOverrides = patchOverrides;
  }

  /**
   * @return baseContent
   */
  public String getBaseContent() {

    return this.baseContent;
  }

  /**
   * @param baseContent new value of {@link #baseContent}.
   */
  public void setBaseContent(String baseContent) {

    this.baseContent = baseContent;
  }

  /**
   * @return patchContent
   */
  public String getPatchContent() {

    return this.patchContent;
  }

  /**
   * @param patchContent new value of {@link #patchContent}.
   */
  public void setPatchContent(String patchContent) {

    this.patchContent = patchContent;
  }

  /**
   * @return patchOverrides
   */
  public Boolean getPatchOverrides() {

    return this.patchOverrides;
  }

  /**
   * @param patchOverrides new value of {@link #patchOverrides}.
   */
  public void setPatchOverrides(Boolean patchOverrides) {

    this.patchOverrides = patchOverrides;
  }

}
