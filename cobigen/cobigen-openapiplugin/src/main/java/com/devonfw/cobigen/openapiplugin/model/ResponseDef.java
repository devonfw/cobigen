package com.capgemini.cobigen.openapiplugin.model;

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

    private String mediaType;

    public ResponseDef() {
        isArray = false;
        isPaginated = false;
        isVoid = false;
        isEntity = false;
    }

    public boolean getIsArray() {
        return isArray;
    }

    public void setIsArray(boolean isArray) {
        this.isArray = isArray;
    }

    public boolean getIsPaginated() {
        return isPaginated;
    }

    public void setIsPaginated(boolean isPaginated) {
        this.isPaginated = isPaginated;
    }

    public boolean getIsEntity() {
        return isEntity;
    }

    public void setIsEntity(boolean isEntity) {
        this.isEntity = isEntity;
    }

    public boolean getIsVoid() {
        return isVoid;
    }

    public void setIsVoid(boolean isVoid) {
        this.isVoid = isVoid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
}
