package com.devonfw.cobigen.retriever.settings.util.to.model;

import jakarta.xml.bind.annotation.XmlElement;

/**
 * Class, which represents a server element of the settings.xml
 *
 */
public class MavenSettingsServerModel {

  /**
   * Represents the id of a server element in maven's settings.xml
   */
  String id;

  /**
   * Represents the username of a server element in maven's settings.xml
   */
  String username;

  /**
   * Represents the password of a server element in maven's settings.xml
   */
  String password;

  /**
   * Represents the privateKey of a server element in maven's settings.xml
   */
  String privateKey;

  /**
   * Represents the passphrase of a server element in maven's settings.xml
   */
  String passphrase;

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

  /**
   * @return privateKey
   */
  public String getPrivateKey() {

    return this.privateKey;
  }

  /**
   * @param privateKey new value of {@link #getprivateKey}.
   */
  @XmlElement
  public void setPrivateKey(String privateKey) {

    this.privateKey = privateKey;
  }

  /**
   * @return passphrase
   */
  public String getPassphrase() {

    return this.passphrase;
  }

  /**
   * @param passphrase new value of {@link #getpassphrase}.
   */
  @XmlElement
  public void setPassphrase(String passphrase) {

    this.passphrase = passphrase;
  }

}