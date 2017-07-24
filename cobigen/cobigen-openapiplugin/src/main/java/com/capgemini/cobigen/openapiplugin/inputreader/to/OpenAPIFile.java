package com.capgemini.cobigen.openapiplugin.inputreader.to;

import java.net.URI;

import io.swagger.models.Swagger;

/** Swagger file transfer object which contains the {@link URI} and the name of the file */
public class OpenAPIFile {

    /** {@link URI} to the Swagger file */
    private URI location;

    /** File name */
    private String fileName;

    private Swagger swagger;

    /**
     * Creates a new transfer object for a file
     * @param location
     *            {@link URI} to the file
     * @param fileName
     *            file name
     */
    public OpenAPIFile(URI location, String fileName) {
        this.location = location;
        this.fileName = fileName;
    }

    /**
     * Returns the file location {@link URI}
     * @return the file location {@link URI}
     */
    public URI getLocation() {
        return location;
    }

    /**
     * Sets the location of the file
     * @param location
     *            new value of location
     */
    public void setLocation(URI location) {
        this.location = location;
    }

    /**
     * Returns the file's name
     * @return the file's name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the file name
     * @param fileName
     *            new value of fileName
     */
    public void setPackageName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "SwaggerFile[fileName=" + fileName + ", location=" + location.toString() + "]";
    }

    public Swagger getSwagger() {
        return swagger;
    }

    public void setSwagger(Swagger swagger) {
        this.swagger = swagger;
    }
}
