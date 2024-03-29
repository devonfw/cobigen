package com.devonfw.cobigen.openapiplugin.model;

/**
 * TODO
 */
@SuppressWarnings("javadoc")
public class ParameterDef extends PropertyDef {

  private boolean isSearchCriteria;

  private boolean inPath;

  private boolean inQuery;

  private boolean inHeader;

  private boolean isBody;

  private String mediaType;

  public ParameterDef() {

    super();
    this.isSearchCriteria = false;
    this.inPath = false;
    this.inQuery = false;
    this.inHeader = false;

  }

  public boolean getIsSearchCriteria() {

    return this.isSearchCriteria;
  }

  public void setIsSearchCriteria(boolean isSearchCriteria) {

    this.isSearchCriteria = isSearchCriteria;
  }

  public boolean getInPath() {

    return this.inPath;
  }

  public void setInPath(boolean inPath) {

    this.inPath = inPath;
  }

  public boolean getInQuery() {

    return this.inQuery;
  }

  public void setInQuery(boolean inQuery) {

    this.inQuery = inQuery;
  }

  public boolean getInHeader() {

    return this.inHeader;
  }

  public void setInHeader(boolean inHeader) {

    this.inHeader = inHeader;
  }

  public String getMediaType() {

    return this.mediaType;
  }

  public void setMediaType(String mediaType) {

    this.mediaType = mediaType;
  }

  public boolean getIsBody() {

    return this.isBody;
  }

  public void setIsBody(boolean isBody) {

    this.isBody = isBody;
  }
}
