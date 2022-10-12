package com.devonfw.cobigen.retriever.to.model;

import jakarta.xml.bind.annotation.XmlElement;

/**
 * Class, which represents a proxy element of the settings.xml
 *
 */
public class MavenSettingsProxyModel {

  /**
   * Represents the id element of a mirror
   */
  String id;

  /**
   * Represents the active element of a mirror
   */
  String active;

  /**
   * Represents the protocol element of a mirror
   */
  String protocol;

  /**
   * Represents the host element of a mirror
   */
  String host;

  /**
   * Represents the port element of a mirror
   */
  String port;

  /**
   * Represents the nonProxyHosts element of a mirror
   */
  String nonProxyHosts;

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
   * @return active
   */
  public String getActive() {

    return this.active;
  }

  /**
   * @param active new value of {@link #getactive}.
   */
  @XmlElement(name = "active")
  public void setActive(String active) {

    this.active = active;
  }

  /**
   * @return protocol
   */
  public String getProtocol() {

    return this.protocol;
  }

  /**
   * @param protocol new value of {@link #getprotocol}.
   */
  @XmlElement(name = "protocol")
  public void setProtocol(String protocol) {

    this.protocol = protocol;
  }

  /**
   * @return host
   */
  public String getHost() {

    return this.host;
  }

  /**
   * @param host new value of {@link #gethost}.
   */
  @XmlElement(name = "host")
  public void setHost(String host) {

    this.host = host;
  }

  /**
   * @return port
   */
  public String getPort() {

    return this.port;
  }

  /**
   * @param port new value of {@link #getport}.
   */
  @XmlElement(name = "port")
  public void setPort(String port) {

    this.port = port;
  }

  /**
   * @return nonProxyHosts
   */
  public String getNonProxyHosts() {

    return this.nonProxyHosts;
  }

  /**
   * @param nonProxyHosts new value of {@link #getnonProxyHosts}.
   */
  @XmlElement(name = "nonProxyHosts")
  public void setNonProxyHosts(String nonProxyHosts) {

    this.nonProxyHosts = nonProxyHosts;
  }

}