package com.devonfw.cobigen.openapiplugin.model;

import java.net.URI;
import java.nio.file.Path;

import com.networknt.oas.model.OpenApi3;

/**
 * Swagger file transfer object which contains the {@link Path} and the name of
 * the file
 */
public class OpenAPIFile {

    /** {@link Path} to the Swagger file */
    private Path location;

    /** Open API AST */
    private OpenApi3 ast;

    /**
     * Creates a new transfer object for a file
     *
     * @param location {@link URI} to the file
     * @param ast      {@link OpenApi3} AST
     */
    public OpenAPIFile(Path location, OpenApi3 ast) {

        this.location = location;
        this.ast = ast;
    }

    /**
     * Returns the file location {@link Path}
     *
     * @return the file location {@link Path}
     */
    public Path getLocation() {

        return this.location;
    }

    /**
     * Returns the file's name
     *
     * @return the file's name
     */
    public String getFileName() {

        return this.location.getFileName().toString();
    }

    @Override
    public String toString() {

        return getClass().getSimpleName() + "[fileName=" + getFileName() + ", location=" + this.location.toString()
                + "]";
    }

    /**
     * The swagger AST of the file
     *
     * @return the swagger AST
     */
    public OpenApi3 getAST() {

        return this.ast;
    }
}
