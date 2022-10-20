package com.devonfw.cobigen.retriever.mavensearch.util.to.model;

/**
 *
 * Class for storing the server credentials
 *
 */
public class ServerCredentials {

  /** The maven repository server base URL */
  private String baseUrl;

  /** The username to use for authentication */
  private String username;

  /** The password to use for authentication */
  private String password;

  /** The address to use for the proxy server */
  private String proxyAddress;

  /** The port to use for the proxy server */
  private int proxyPort;

  /**
   *
   * The constructor.
   *
   * @param baseUrl The maven repository server base URL
   * @param username to use for authentication
   * @param password to use for authentication
   * @param proxyAddress to use for the proxy server
   * @param proxyPort to use for the proxy server
   */
  public ServerCredentials(String baseUrl, String username, String password, String proxyAddress, int proxyPort) {

    this.baseUrl = baseUrl;
    this.username = username;
    this.password = password;
    this.proxyAddress = proxyAddress;
    this.proxyPort = proxyPort;
  }

  /**
   * @return baseUrl
   */
  public String getBaseUrl() {

    return this.baseUrl;
  }

  /**
   * @return username
   */
  public String getUsername() {

    return this.username;
  }

  /**
   * @return password
   */
  public String getPassword() {

    return this.password;
  }

  /**
   * @return proxyAddress
   */
  public String getProxyAddress() {

    return this.proxyAddress;
  }

  /**
   * @return proxyPort
   */
  public int getProxyPort() {

    return this.proxyPort;
  }
}
