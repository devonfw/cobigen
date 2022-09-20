package com.devonfw.cobigen.api.to.model;

import javax.xml.bind.annotation.XmlElement;

/**
 * Class, which represents a server element of the settings.xml
 *
 */
public class MavenSettingsServer {

  String id;

  String username;

  String password;

  /**
   * @return id
   */
  @XmlElement(name = "id")
  public String getId() {

    return this.id;
  }

  /**
   * @param id new value of {@link #getid}.
   */
  public void setId(String id) {

    this.id = id;
  }

  /**
   * @return username
   */
  public String getUsername() {

    return this.username;
  }

  /**
   * @param username new value of {@link #getusername}.
   */
  @XmlElement(name = "username")
  public void setUsername(String username) {

    this.username = username;
  }

  /**
   * @return password
   */
  public String getPassword() {

    return this.password;
  }

  /**
   * @param password new value of {@link #getpassword}.
   */
  @XmlElement(name = "password")
  public void setPassword(String password) {

    this.password = password;
  }

}