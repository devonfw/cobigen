package com.devonfw.cobigen.retriever.settings.to.model;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;

/**
 * Class, which represents the proxies element of the settings.xml
 *
 */
public class MavenSettingsProxiesModel {

  /**
   * List of proxy elements contained in the proxies element
   */
  List<MavenSettingsProxyModel> proxyList;

  /**
   * @return proxyList
   */
  public List<MavenSettingsProxyModel> getProxyList() {

    return this.proxyList;
  }

  /**
   * @param proxyList new value of {@link #getproxyList}.
   */
  @XmlElement(name = "proxy")
  public void setProxyList(List<MavenSettingsProxyModel> proxyList) {

    this.proxyList = proxyList;
  }

}
