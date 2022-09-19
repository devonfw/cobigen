package com.devonfw.cobigen.api.to;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * TODO fberger This type ...
 *
 */

@XmlRootElement(name = "settings")
public class MavenSettingsModel {

  String localRepository;

  /**
   * @return localRepository
   */
  public String getLocalRepository() {

    return this.localRepository;
  }

  /**
   * @param localRepository new value of {@link #getlocalRepository}.
   */
  @XmlElement
  public void setLocalRepository(String localRepository) {

    this.localRepository = localRepository;
  }

}
