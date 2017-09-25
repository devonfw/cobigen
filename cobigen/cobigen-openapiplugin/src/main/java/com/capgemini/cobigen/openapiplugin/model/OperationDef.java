package com.capgemini.cobigen.openapiplugin.model;

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

    private ResponseDef response;

    private String operationId;

    private String description;

    private String summary;

    private Collection<String> tags;

    public OperationDef(String type) {
        this.type = type;
        parameters = new LinkedList<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ParameterDef> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterDef> parameters) {
        this.parameters = parameters;
    }

    public ResponseDef getResponse() {
        return response;
    }

    public void setResponse(ResponseDef response) {
        this.response = response;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Collection<String> getTags() {
        return tags;
    }

    public void setTags(Collection<String> tags) {
        this.tags = tags;
    }

}
