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
     * Optional {@link ClassLoader} reference for correct parsing of java children of this package (container)
     */
    private ClassLoader classLoader;

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
     * Creates a new transfer object for a package with a {@link ClassLoader} for type resolution while
     * parsing its children.
     * @param location
     *            {@link URI} to the package folder
     * @param packageName
     *            package name
     * @param classLoader
     *            for type resolution while parsing the package children
     * @author mbrunnli (07.06.2014)
     */
    public PackageFolder(URI location, String packageName, ClassLoader classLoader) {
        this.location = location;
        this.packageName = packageName;
        this.classLoader = classLoader;
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
     * Sets the location of the package (last package element)
     * @param location
     *            new value of location
     * @author mbrunnli (17.10.2014)
     */
    public void setLocation(URI location) {
        this.location = location;
    }

    /**
     * Returns the package's name
     * @return the package's name
     * @author mbrunnli (07.06.2014)
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Sets the package's name
     * @param packageName
     *            new value of packageName
     * @author mbrunnli (17.10.2014)
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Returns the field 'classLoader'
     * @return value of classLoader
     * @author mbrunnli (17.10.2014)
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Sets the class loader for type resolving while parsing children
     * @param classLoader
     *            new value of classLoader
     * @author mbrunnli (17.10.2014)
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

}
