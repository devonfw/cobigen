package com.capgemini.cobigen.openapiplugin.inputreader.to;

import java.net.URI;
import java.nio.file.Path;

import io.swagger.models.Swagger;

/** Swagger file transfer object which contains the {@link Path} and the name of the file */
public class OpenAPIFile {

    /** {@link Path} to the Swagger file */
    private Path location;

    /** Open API AST */
    private Swagger ast;

    /**
     * Creates a new transfer object for a file
     * @param location
     *            {@link URI} to the file
     * @param ast
     *            {@link Swagger} AST
     */
    public OpenAPIFile(Path location, Swagger ast) {
        this.location = location;
        this.ast = ast;
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
        return getClass().getSimpleName() + "[fileName=" + getFileName() + ", location=" + location.toString() + "]";
    }

    /**
     * The swagger AST of the file
     * @return the swagger AST
     */
    public Swagger getAST() {
        return ast;
    }
}
