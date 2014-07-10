/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.javaplugin.inputreader.to;

import java.net.URI;

/**
 * Package transfer object which contains the {@link URI} of the package folder and the package name
 * @author mbrunnli (03.06.2014)
 */
public class PackageFolder {

    /**
     * {@link URI} to the package folder
     */
    private URI location;

    /**
     * Package name
     */
    private String packageName;

    /**
     * Creates a new transfer object for a package
     * @param location
     *            {@link URI} to the package folder
     * @param packageName
     *            package name
     * @author mbrunnli (07.06.2014)
     */
    public PackageFolder(URI location, String packageName) {
        this.location = location;
        this.packageName = packageName;
    }

    /**
     * Returns the folder location {@link URI} of the package
     * @return the folder location {@link URI} of the package
     * @author mbrunnli (07.06.2014)
     */
    public URI getLocation() {
        return location;
    }

    /**
     * Returns the package's name
     * @return the package's name
     * @author mbrunnli (07.06.2014)
     */
    public String getPackageName() {
        return packageName;
    }

}
