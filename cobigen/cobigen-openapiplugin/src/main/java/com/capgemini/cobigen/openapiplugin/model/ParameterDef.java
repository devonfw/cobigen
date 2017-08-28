package com.capgemini.cobigen.openapiplugin.model;

/**
 * TODO
 */
public class ParameterDef extends PropertyDef {

    private boolean isSearchCriteria;

    private boolean inPath;

    private boolean inQuery;

    private boolean inHeader;

    public ParameterDef() {
        super();
        isSearchCriteria = false;
        inPath = false;
        inQuery = false;
        inHeader = false;

    }

    public boolean getIsSearchCriteria() {
        return isSearchCriteria;
    }

    public void setIsSearchCriteria(boolean isSearchCriteria) {
        this.isSearchCriteria = isSearchCriteria;
    }

    public boolean getInPath() {
        return inPath;
    }

    public void setInPath(boolean inPath) {
        this.inPath = inPath;
    }

    public boolean getInQuery() {
        return inQuery;
    }

    public void setInQuery(boolean inQuery) {
        this.inQuery = inQuery;
    }

    public boolean getInHeader() {
        return inHeader;
    }

    public void setInHeader(boolean inHeader) {
        this.inHeader = inHeader;
    }
}
