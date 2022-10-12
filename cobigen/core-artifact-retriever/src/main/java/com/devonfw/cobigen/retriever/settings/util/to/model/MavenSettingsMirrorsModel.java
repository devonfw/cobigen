package com.devonfw.cobigen.retriever.settings.util.to.model;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;

/**
 * Class, which represents the mirrors element of the settings.xml
 *
 */
public class MavenSettingsMirrorsModel {

  List<MavenSettingsMirrorModel> mirrorList;

  /**
   * @return mirrorList
   */
  public List<MavenSettingsMirrorModel> getMirrorList() {

    return this.mirrorList;
  }

  /**
   * @param mirrorList new value of {@link #getmirrorList}.
   */
  @XmlElement(name = "mirror")
  public void setMirrorList(List<MavenSettingsMirrorModel> mirrorList) {

    this.mirrorList = mirrorList;
  }

}