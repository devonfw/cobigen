package com.devonfw.cobigen.retriever.mavensearch.to.model;

/**
 * Class for storing the server credentials
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

  /** The user name to use for the proxy server */
  private String proxyUsername;

  /** The password to use for the proxy server */
  private String proxyPassword;

  /**
   *
   * The constructor.
   *
   * @param baseUrl The maven repository server base URL
   * @param username to use for authentication
   * @param password to use for authentication
   * @param proxyAddress to use for the proxy server
   * @param proxyPort to use for the proxy server
   * @param proxyUsername to use for the proxy server authentication
   * @param proxyPassword to use for the proxy server authentication
   */
  public ServerCredentials(String baseUrl, String username, String password, String proxyAddress, int proxyPort,
      String proxyUsername, String proxyPassword) {

    this.baseUrl = baseUrl;
    this.username = username;
    this.password = password;
    this.proxyAddress = proxyAddress;
    this.proxyPort = proxyPort;
    this.proxyUsername = proxyUsername;
    this.proxyPassword = proxyPassword;
  }

  /**
   * @return baseUrl
   */
  public String getBaseUrl() {

    return this.baseUrl;
  }

  /**
   * Returns the username, if the username settings.xml tag is a placeholder this getter will return null instead
   *
   * @return username String of username
   */
  public String getUsername() {

    if (this.username != null && this.username.startsWith("$["))
      return null;
    return this.username;
  }

  /**
   * Returns the password, if the password settings.xml tag is a placeholder this getter will return null instead
   *
   * @return password String of password
   */
  public String getPassword() {

    if (this.username != null && this.password.startsWith("$["))
      return null;
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

  /**
   * @return proxyUsername
   */
  public String getProxyUsername() {

    return this.proxyUsername;
  }

  /**
   * @return proxyPassword
   */
  public String getProxyPassword() {

    return this.proxyPassword;
  }
}
