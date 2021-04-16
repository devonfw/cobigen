package com.devonfw.cobigen.javaplugin.inputreader.to;

import java.net.URI;

/** Package transfer object which contains the {@link URI} of the package folder and the package name */
public class PackageFolder {

    /** {@link URI} to the package folder */
    private URI location;

    /** Package name */
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
     */
    public PackageFolder(URI location, String packageName, ClassLoader classLoader) {
        this.location = location;
        this.packageName = packageName;
        this.classLoader = classLoader;
    }

    /**
     * Returns the folder location {@link URI} of the package
     * @return the folder location {@link URI} of the package
     */
    public URI getLocation() {
        return location;
    }

    /**
     * Sets the location of the package (last package element)
     * @param location
     *            new value of location
     */
    public void setLocation(URI location) {
        this.location = location;
    }

    /**
     * Returns the package's name
     * @return the package's name
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Sets the package's name
     * @param packageName
     *            new value of packageName
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Returns the field 'classLoader'
     * @return value of classLoader
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Sets the class loader for type resolving while parsing children
     * @param classLoader
     *            new value of classLoader
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public String toString() {
        return "PackageFolder[packageName=" + packageName + ", location=" + location.toString() + "]";
    }
}
