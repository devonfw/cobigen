/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.javaplugin.inputreader;

import java.lang.reflect.Modifier;

/**
 * Util class for providing additional functionality for the FreeMarker model
 *
 * @author mbrunnli (12.04.2013)
 */
public class FreeMarkerUtil {

    /**
     * input {@link Class} for the FreeMarker model
     */
    private ClassLoader classLoader;

    /**
     * Creates a new instance of the {@link FreeMarkerUtil} for the given input {@link ClassLoader}.
     * @param inputClassLoader
     *            input {@link ClassLoader} for the FreeMarker model
     * @author mbrunnli (12.04.2013)
     */
    public FreeMarkerUtil(ClassLoader inputClassLoader) {
        this.classLoader = inputClassLoader;
    }

    /**
     * Checks whether the given subType is a sub type of the given super type
     * @param subType
     *            qualified name of the sub type
     * @param superType
     *            qualified name of the super type
     * @return <code>true</code> if the given subtype is a sub type of the given supertype<br>
     *         <code>false</code> otherwise
     * @throws ClassNotFoundException
     *             if one of the given classes could not be found
     * @author mbrunnli (12.04.2013)
     */
    public boolean isSubtypeOf(String subType, String superType) throws ClassNotFoundException {
        return this.classLoader.loadClass(superType).isAssignableFrom(this.classLoader.loadClass(subType));
    }

    /**
     * Checks whether the given class is abstract
     * @param className
     *            class name of the class being checked
     * @return <code>true</code> if the class is abstract<br>
     *         <code>false</code> otherwise
     * @throws ClassNotFoundException
     *             if the class with the given className could not be found in the FreeMarkerUtil's
     *             classLoader
     * @author mbrunnli (12.04.2013)
     */
    public boolean isAbstract(String className) throws ClassNotFoundException {
        return Modifier.isAbstract(this.classLoader.loadClass(className).getModifiers());
    }

}
