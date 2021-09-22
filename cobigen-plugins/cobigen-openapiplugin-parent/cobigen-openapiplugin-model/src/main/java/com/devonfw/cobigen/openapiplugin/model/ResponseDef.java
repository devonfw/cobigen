package com.devonfw.cobigen.openapiplugin.model;

import java.util.List;

/**
 *
 */
@SuppressWarnings("javadoc")
public class ResponseDef {

  private boolean isArray;

  private boolean isPaginated;

  private boolean isEntity;

  private boolean isVoid;

  private String type;

  private String format;

  private String code;

  private List<String> mediaTypes;

  private String description;

  private EntityDef entityRef;

  public EntityDef getEntityRef() {

    return this.entityRef;
  }

  public void setEntityRef(EntityDef entityRef) {

    this.entityRef = entityRef;
  }

  public ResponseDef() {

    this.isArray = false;
    this.isPaginated = false;
    this.isVoid = false;
    this.isEntity = false;
  }

  public boolean getIsArray() {

    return this.isArray;
  }

  public void setIsArray(boolean isArray) {

    this.isArray = isArray;
  }

  public boolean getIsPaginated() {

    return this.isPaginated;
  }

  public void setIsPaginated(boolean isPaginated) {

    this.isPaginated = isPaginated;
  }

  public boolean getIsEntity() {

    return this.isEntity;
  }

  public void setIsEntity(boolean isEntity) {

    this.isEntity = isEntity;
  }

  public boolean getIsVoid() {

    return this.isVoid;
  }

  public void setIsVoid(boolean isVoid) {

    this.isVoid = isVoid;
  }

  public String getType() {

    return this.type;
  }

  public void setType(String type) {

    this.type = type;
  }

  public String getFormat() {

    return this.format;
  }

  public void setFormat(String format) {

    this.format = format;
  }

  public String getCode() {

    return this.code;
  }

  public void setCode(String code) {

    this.code = code;
  }

  public String getDescription() {

    return this.description;
  }

  public void setDescription(String description) {

    this.description = description;
  }

  public List<String> getMediaTypes() {

    return this.mediaTypes;
  }

  public void setMediaTypes(List<String> mediaTypes) {

    this.mediaTypes = mediaTypes;
  }
}
