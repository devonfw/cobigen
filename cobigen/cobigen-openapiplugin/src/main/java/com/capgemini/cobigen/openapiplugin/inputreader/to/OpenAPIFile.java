package com.capgemini.cobigen.openapiplugin.inputreader.to;

import java.net.URI;
import java.nio.file.Path;

import io.swagger.models.Swagger;

/** Swagger file transfer object which contains the {@link Path} and the name of the file */
public class OpenAPIFile {

    /** {@link Path} to the Swagger file */
    private Path location;

    /** Swagger AST */
    private Swagger swagger;

    /**
     * Creates a new transfer object for a file
     * @param location
     *            {@link URI} to the file
     * @param swagger
     *            {@link Swagger} AST
     */
    public OpenAPIFile(Path location, Swagger swagger) {
        this.location = location;
        this.swagger = swagger;
    }

    /**
     * Returns the file location {@link Path}
     * @return the file location {@link Path}
     */
    public Path getLocation() {
        return location;
    }

    /**
     * Returns the file's name
     * @return the file's name
     */
    public String getFileName() {
        return location.getFileName().toString();
    }

    @Override
    public String toString() {
        return "SwaggerFile[fileName=" + getFileName() + ", location=" + location.toString() + "]";
    }

    /**
     * The swagger AST of the file
     * @return the swagger AST
     */
    public Swagger getSwagger() {
        return swagger;
    }
}
