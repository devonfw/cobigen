package com.devonfw.cobigen.api.to.model;

import java.net.URL;

import jakarta.xml.bind.annotation.XmlElement;

/**
 * Class, which represents a repository element of the settings.xml
 *
 */

public class MavenSettingsRepositoryModel {

  /**
   * Represents the id of a repository element in maven's settings.xml
   */
  String id;

  /**
   * Represents the name of a repository element in maven's settings.xml
   */
  String name;

  /**
   * Represents the url of a repository element in maven's settings.xml
   */
  URL url;

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
   * @return name
   */
  public String getName() {

    return this.name;
  }

  /**
   * @param name new value of {@link #getname}.
   */
  @XmlElement(name = "name")
  public void setName(String name) {

    this.name = name;
  }

  /**
   * @return url
   */
  public URL getUrl() {

    return this.url;
  }

  /**
   * @param url new value of {@link #geturl}.
   */
  @XmlElement(name = "url")
  public void setUrl(URL url) {

    this.url = url;
  }

}
