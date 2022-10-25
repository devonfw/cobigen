package com.devonfw.cobigen.retriever.settings.to.model;

import jakarta.xml.bind.annotation.XmlElement;

/**
 * Class, which represents a mirror element of the settings.xml
 *
 */
public class MavenSettingsMirrorModel {

  /**
   * Represents the mirrorOf element of a mirror
   */
  String mirrorOf;

  /**
   * Represents the url element of a mirror
   */
  String url;

  /**
   * Represents the id element of a mirror
   */
  String id;

  /**
   * Represents the blocked element of a mirror
   */
  String blocked;

  /**
   * @return mirrorOf
   */
  public String getMirrorOf() {

    return this.mirrorOf;
  }

  /**
   * @param mirrorOf new value of {@link #getmirrorOf}.
   */
  @XmlElement(name = "mirrorOf")
  public void setMirrorOf(String mirrorOf) {

    this.mirrorOf = mirrorOf;
  }

  /**
   * @return url
   */
  public String getUrl() {

    return this.url;
  }

  /**
   * @param url new value of {@link #geturl}.
   */
  @XmlElement(name = "url")
  public void setUrl(String url) {

    this.url = url;
  }

  /**
   * @return id
   */
  public String getId() {

    return this.id;
  }

  /**
   * @param id new value of {@link #getid}.
   */
  @XmlElement(name = "id")
  public void setId(String id) {

    this.id = id;
  }

  /**
   * @return blocked
   */
  public String getBlocked() {

    return this.blocked;
  }

  /**
   * @param blocked new value of {@link #getblocked}.
   */
  @XmlElement(name = "blocked")
  public void setBlocked(String blocked) {

    this.blocked = blocked;
  }

}
