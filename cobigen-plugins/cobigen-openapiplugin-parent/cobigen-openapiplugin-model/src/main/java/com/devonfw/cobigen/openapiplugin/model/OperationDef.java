package com.devonfw.cobigen.openapiplugin.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
@SuppressWarnings("javadoc")
public class OperationDef {

  private String type;

  private List<ParameterDef> parameters;

  private List<ResponseDef> responses;

  private String operationId;

  private String description;

  private String summary;

  private Collection<String> tags;

  public OperationDef(String type) {

    this.type = type;
    this.parameters = new LinkedList<>();
  }

  public String getType() {

    return this.type;
  }

  public void setType(String type) {

    this.type = type;
  }

  public List<ParameterDef> getParameters() {

    return this.parameters;
  }

  public void setParameters(List<ParameterDef> parameters) {

    this.parameters = parameters;
  }

  public List<ResponseDef> getResponses() {

    return this.responses;
  }

  public void setResponses(List<ResponseDef> responses) {

    this.responses = responses;
  }

  public String getOperationId() {

    return this.operationId;
  }

  public void setOperationId(String operationId) {

    this.operationId = operationId;
  }

  public String getDescription() {

    return this.description;
  }

  public void setDescription(String description) {

    this.description = description;
  }

  public String getSummary() {

    return this.summary;
  }

  public void setSummary(String summary) {

    this.summary = summary;
  }

  public Collection<String> getTags() {

    return this.tags;
  }

  public void setTags(Collection<String> tags) {

    this.tags = tags;
  }

}
